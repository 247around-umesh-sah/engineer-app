package com.around.technician;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DigitalSignatureActivity extends AppCompatActivity implements ApiResponse {

    // GPSTracker class
    GPSTracker gps;
    ConnectionDetector cd;
    Misc misc;
    String bookingID;
    String amountDue;
    String formData;
    Button mClear, save;
    TextView customerPaid;
    File file, serialFile;
    LinearLayout mContent;
    View view;
    DigitalSignatureActivity.signature mSignature;
    Bitmap bitmap;
    FileOutputStream mFileOutStream;
    private HttpRequest httpRequest;
    String amountPaid ="", enRemarks = "";

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundDigitSign/";
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    String StoredPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_signature);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gps = new GPSTracker(this);
        misc = new Misc(this);
        misc.checkAndLocationRequestPermissions();
        cd = new ConnectionDetector(this);

        Intent intent = getIntent();

        bookingID = intent.getStringExtra("bookingID");
        formData = intent.getStringExtra("formData");
        amountDue = intent.getStringExtra("amountDue");
        amountPaid = intent.getStringExtra("amountPaid");
        enRemarks = intent.getStringExtra("remarks");


        mContent = findViewById(R.id.linearLayout);
        customerPaid = findViewById(R.id.customerPaid);

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        serialFile = new File(SN_DIRECTORY);
        if (!serialFile.exists()) {
            serialFile.mkdir();
        }

        mSignature = new DigitalSignatureActivity.signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = findViewById(R.id.clear);
        save = findViewById(R.id.save);
        save.setEnabled(false);

        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mSignature.clear();
                save.setEnabled(false);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                view.setDrawingCacheEnabled(true);
                String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                StoredPath = DIRECTORY + bookingID + "_" + pic_name + ".png";

                mSignature.save(view, StoredPath);


                Toast.makeText(v.getContext(), R.string.signature_saved, Toast.LENGTH_SHORT).show();

                SendRequestToServer();

            }
        });

    }

    public void SendRequestToServer(){
        if (cd.isConnectingToInternet()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to PNG format
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
            String signatureURL = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            String location = misc.getLocation();
            httpRequest = new HttpRequest(DigitalSignatureActivity.this, true);
            httpRequest.delegate = DigitalSignatureActivity.this;
            httpRequest.execute("completeBookingByEngineer", amountDue, signatureURL,
                    amountPaid, formData, bookingID, location,
                    enRemarks);
        } else {
            misc.NoConnection();
        }

    }

    @Override
    public void processFinish(String httpReqResponse) {
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                switch (statusCode) {
                    case "0000":
                        httpRequest.progress.dismiss();
                        new android.support.v7.app.AlertDialog.Builder(DigitalSignatureActivity.this)

                                .setMessage(R.string.bookingUpdatedSuccessMsg)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(DigitalSignatureActivity.this, SearchActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();


                        break;
                    case "0018":
                        httpRequest.progress.dismiss();
                        misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serialNumberRequiredMsg);
                        break;
                    default:
                        misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serverConnectionFailedMsg);
                        httpRequest.progress.dismiss();
                        break;
                }
            } catch (JSONException e) {
                misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serverConnectionFailedMsg);
                e.printStackTrace();

                httpRequest.progress.dismiss();
            }
        } else {

            misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serverConnectionFailedMsg);
            httpRequest.progress.dismiss();
        }
    }

    /**
     * Making a platform where customer doing signature
     */
    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final RectF dirtyRect = new RectF();
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            // Log.v("tag", "Width: " + v.getWidth());
            //  Log.v("tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);

                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            save.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}
