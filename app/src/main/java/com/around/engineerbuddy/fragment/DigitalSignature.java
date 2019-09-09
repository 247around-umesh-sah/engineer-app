package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.BookingGetterSetter;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.GPSTracker;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DigitalSignature extends BMAFragment {

    GPSTracker gps;
    ConnectionDetector cd;
    Misc misc;
   // String bookingID;
    //String amountDue;
    String formData;
    Button mClear, save;
    TextView customerPaid;
    File file, serialFile;
    LinearLayout mContent;
    View drawView;
    DigitalSignature.signature mSignature;
    Bitmap bitmap;
    FileOutputStream mFileOutStream;
    private HttpRequest httpRequest;
    String amountPaid = "", enRemarks = "";
    BookingGetterSetter bookingList;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundDigitSign/";
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    String StoredPath = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle=this.getArguments();
        this.bookingList=bundle.getParcelable("bookingList");
        this.formData=bundle.getString("formData");
        this.amountPaid=bundle.getString("amountPaid");
        this.enRemarks=bundle.getString("remarks");
       // bundle.putString("paymentMethod", getResources().getString(R.string.payment_method_cash));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.digital_signature_fragment, container, false);
        BMAmplitude.saveUserAction("DigitalSignature","DigitalSignature");
        this.init();
        return this.view;
    }

    public void init() {
        gps = new GPSTracker(getContext());
        misc = new Misc(getContext());
        misc.checkAndLocationRequestPermissions();
        cd = new ConnectionDetector(getContext());



        mContent = this.view.findViewById(R.id.linearLayout);
        //customerPaid = this.view.findViewById(R.id.customerPaid);

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        serialFile = new File(SN_DIRECTORY);
        if (!serialFile.exists()) {
            serialFile.mkdir();
        }

        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = this.view.findViewById(R.id.clear);
        save = this.view.findViewById(R.id.save);
        save.setEnabled(false);

        drawView = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mSignature.clear();
                save.setEnabled(false);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                drawView.setDrawingCacheEnabled(true);
                String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                StoredPath = DIRECTORY + bookingList.bookingID + "_" + pic_name + ".png";

                mSignature.save(drawView, StoredPath);


                Toast.makeText(v.getContext(), R.string.signature_saved, Toast.LENGTH_SHORT).show();

                SendRequestToServer();

            }
        });

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

    public void SendRequestToServer() {
        if (cd.isConnectingToInternet()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to PNG format
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
            String signatureURL = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            String location = misc.getLocation();
            httpRequest = new HttpRequest(getContext(), true);
            httpRequest.delegate = DigitalSignature.this;
            httpRequest.execute("completeBookingByEngineer", this.bookingList.amountDue, signatureURL,
                    amountPaid, formData, this.bookingList.bookingID, location,
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
                        new android.support.v7.app.AlertDialog.Builder(getContext())

                                .setMessage(R.string.bookingUpdatedSuccessMsg)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getMainActivity().updateFragment(new FragmentLoader(),false);
//                                        Intent intent = new Intent(getContext(), SearchActivity.class);
//                                        startActivity(intent);
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

}
