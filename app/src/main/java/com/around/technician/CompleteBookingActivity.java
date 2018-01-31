package com.around.technician;

import android.annotation.SuppressLint;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.technician.adapters.CompleteBookingAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("ALL")
public class CompleteBookingActivity extends AppCompatActivity implements ApiResponse {
    ConnectionDetector cd;
    Misc misc;
    RecyclerView recyclerView;
    CompleteBookingAdapter mAdapter;
    String bookingID, appliance, amountDue, customerName;
    List<BookingGetterSetter> unitList;
    Button mClear, save, mCancel, signatureButton, submit;
    TextView customerPaid;
    File file, serialFile;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;
    EditText amountPaidInput;
    FileOutputStream mFileOutStream;
    // GPSTracker class
    GPSTracker gps;
    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundDigitSign/";
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    //    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = "";

    private List<BookingGetterSetter> unitDetails = new ArrayList<>();
    private HttpRequest httpRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complete_booking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Complete Booking");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        bookingID = intent.getStringExtra("bookingID");
        appliance = intent.getStringExtra("services");
        amountDue = intent.getStringExtra("amountDue");

        customerName = intent.getStringExtra("customerName");
        recyclerView = (RecyclerView) findViewById(R.id.appliance_list);

        gps = new GPSTracker(this);


        TextView bookingIDText = (TextView) findViewById(R.id.bookingID);
        TextView services = (TextView) findViewById(R.id.services);
        TextView name = (TextView) findViewById(R.id.customerName);
        TextView amountDueText = (TextView) findViewById(R.id.amountDue);
        submit = (Button) findViewById(R.id.submit);
        signatureButton = (Button) findViewById(R.id.signature);
        amountPaidInput = (EditText) findViewById(R.id.amountPaidInput);

        bookingIDText.setText(bookingID);
        services.setText(appliance);
        String amount_due_with_icon = "\u20B9" + amountDue;
        amountDueText.setText(amount_due_with_icon);
        name.setText(customerName);


        cd = new ConnectionDetector(this);
        misc = new Misc(this);
        misc.checkAndLocationRequestPermissions();
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            mAdapter = new CompleteBookingAdapter(unitDetails, recyclerView);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cd.isConnectingToInternet()) {
            httpRequest = new HttpRequest(this, true);
            httpRequest.delegate = CompleteBookingActivity.this;
            httpRequest.execute("getBookingAppliance", bookingID);
        } else {
            misc.NoConnection();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        serialFile = new File(SN_DIRECTORY);
        if (!serialFile.exists()) {
            serialFile.mkdir();
        }

        // Dialog Function
        dialog = new Dialog(this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);
        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountPaidInput.getText().toString();

                if (Float.parseFloat(amountDue) > 0) {
                    if (amount.isEmpty()) {
                        Snackbar.make(v, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();
                    } else {
                        if (Float.parseFloat(amount) > 0) {
                            // Function call for Digital Signature
                            dialog_action();
                        } else {
                            Snackbar.make(v, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();
                        }
                    }


                } else {
                    // Function call for Digital Signature
                    dialog_action();
                }

            }
        });

    }

    /**
     * Get Current location of Mobile
     *
     * @return Address
     */
    public String getLocation() {
        Gson gson = new Gson();
        String arrayString = "";
        if (cd.isConnectingToInternet()) {

            if (misc.checkAndLocationRequestPermissions()) {
                // create class object
                gps = new GPSTracker(CompleteBookingActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    try {
                        if (!gps.getAddress().getPostalCode().isEmpty()) {

                            Map<String, String> address = new HashMap<>();

                            address.put("pincode", gps.getAddress().getPostalCode());
                            address.put("city", gps.getAddress().getLocality());
                            address.put("address", gps.getAddress().getAddressLine(0));
                            arrayString = gson.toJson(address);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return arrayString;
    }

    /**
     * This is used to check validation whatever Engineer done to completed booking.
     *
     * @param view View
     */
    public void submitProcess(View view) {
        boolean validation = true;
        List<BookingGetterSetter> data = mAdapter.getUnitData();

        if (Integer.parseInt(amountDue) > 0) {
            String amountPaid = amountPaidInput.getText().toString();
            if (amountPaid.isEmpty()) {
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();
            } else if (Integer.parseInt(amountPaid) == 0) {
                validation = false;
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();

            }
        }
        if (StoredPath.equals("")) {
            validation = false;
            Snackbar.make(view, R.string.signatureRequired, Snackbar.LENGTH_LONG).show();

        }
        if (validation) {

            for (int i = 0; i < data.size(); i++) {

                boolean isDelivered = false;
                for (int k = 0; k < data.get(i).getUnitList().size(); k++) {
                    if (isDelivered = data.get(i).getUnitList().get(k).getDelivered()) {
                        isDelivered = data.get(i).getUnitList().get(k).getDelivered();
                    }
                    if (data.get(i).getUnitList().get(k).getPod().equals("1")) {
                        if (data.get(i).getSerialNo().isEmpty()) {
                            validation = false;
                            Snackbar.make(view, R.string.serialNumberRequiredMsg, Snackbar.LENGTH_LONG).show();
                            break;
                        }

                        if (data.get(i).getSerialNoBitmap().equals(null)) {
                            validation = false;
                            Snackbar.make(view, R.string.serialNumberPictureRequired, Snackbar.LENGTH_LONG).show();
                            break;
                        }
                    }
                }

                if (!data.get(i).getApplianceBroken()) {
                    if (!isDelivered) {
                        validation = false;
                        Snackbar.make(view, R.string.priceTagsCheckboxRequired, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                }

            }
        }

        if (validation) {
            if (cd.isConnectingToInternet()) {
                if (misc.checkAndLocationRequestPermissions()) {


                    @SuppressLint("UseSparseArrays") Map<Integer, Map<String, String>> unitParameter = new HashMap<>();
                    int z = 0;
                    for (int i = 0; i < data.size(); i++) {


                        for (int k = 0; k < data.get(i).getUnitList().size(); k++) {
                            Map<String, String> subParameter = new HashMap<>();
                            List<String> list = new ArrayList<>();
                            if (data.get(i).getUnitList().get(k).getPod().equals("1") ||
                                    data.get(i).getSerialNoUrl().isEmpty()) {
                                subParameter.put("serialNo", data.get(i).getSerialNo());
                                subParameter.put("serialNoImage", data.get(i).getSerialNoUrl());

                            }

                            subParameter.put("bookingID", bookingID);
                            subParameter.put("pod", data.get(i).getUnitList().get(k).getPod());
                            subParameter.put("unitID", data.get(i).getUnitList().get(k).getUnitID());
                            subParameter.put("applianceBroken", data.get(i).getUnitList().get(k).getApplianceBroken() + "");
                            subParameter.put("isDelivered", data.get(i).getUnitList().get(k).getDelivered() + "");


                            unitParameter.put(z, subParameter);

                            z++;
                        }
                    }

                    Gson gson = new Gson();
                    String arrayString = gson.toJson(unitParameter);


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    //compress the image to PNG format
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                    String signatureURL = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    String location = getLocation();
                    httpRequest = new HttpRequest(this, true);
                    httpRequest.delegate = CompleteBookingActivity.this;
                    httpRequest.execute("completeBookingByEngineer", amountDue, signatureURL,
                            amountPaidInput.getText().toString(), arrayString, bookingID, location);

                } else {
                    Toast.makeText(CompleteBookingActivity.this, R.string.askGPSPermission, Toast.LENGTH_SHORT).show();
                }

            } else {
                misc.NoConnection();
            }
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data);

    }

//    private void onSelectFromGalleryResult(Intent data) {
//        Uri selectedImageUri = data.getData();
//        String[] projection = { MediaStore.MediaColumns.DATA };
//        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
//                null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        cursor.moveToFirst();
//        String selectedImagePath = cursor.getString(column_index);
//        Bitmap thumbnail;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(selectedImagePath, options);
//        final int REQUIRED_SIZE = 200;
//        int scale = 1;
//        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//            scale *= 2;
//        options.inSampleSize = scale;
//        options.inJustDecodeBounds = false;
//        thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);
//
//    }


    @Override
    public void processFinish(String httpReqResponse) {
//        int maxLogSize = 2000;
//        for (int i = 0; i <= httpReqResponse.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i + 1) * maxLogSize;
//            end = end > httpReqResponse.length() ? httpReqResponse.length() : end;
//           // Log.w("Response", httpReqResponse.substring(start, end));
//        }
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                switch (statusCode) {
                    case "0000":
                        if (jsonObject.has("unitDetails")) {

                            JSONArray posts = jsonObject.optJSONArray("unitDetails");

                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject post = posts.optJSONObject(i);

                                JSONArray unitArray = post.optJSONArray("quantity");
                                unitList = new ArrayList<>();
                                unitList.clear();
                                String pod = "0";
                                String repair = "Repair";
                                String repeat = "Repeat";
                                boolean repairBooking = false;
                                for (int k = 0; k < unitArray.length(); k++) {
                                    JSONObject unit = unitArray.optJSONObject(k);
                                    // Check POD
                                    if (unit.optString("pod").equals("1")) {

                                        pod = "1";
                                    }

                                    //IF price tag string contains Repair or Repeat then we will not disable line item
                                    if ((unit.optString("price_tags").toLowerCase().contains(repair.toLowerCase()))
                                            || (unit.optString("price_tags").toLowerCase().contains(repeat.toLowerCase()))) {

                                        repairBooking = true;

                                    }


                                    unitList.add(new BookingGetterSetter(unit.optString("unit_id"),
                                            unit.optString("pod"), unit.optString("price_tags"),
                                            unit.optString("customer_net_payable"), unit.optString("product_or_services"), false, false
                                    ));

                                }

                                unitDetails.add(new BookingGetterSetter(post.optString("booking_id"), post.optString("brand"),
                                        post.optString("partner_id"),
                                        post.optString("service_id"),
                                        post.optString("category"),
                                        post.optString("capacity"),
                                        post.optString("model_number"),
                                        unitList, pod, "", "", null, repairBooking));
                            }

                            CompleteBookingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    CompleteBookingActivity.this.mAdapter.notifyItemInserted(unitDetails.size());
                                    CompleteBookingActivity.this.mAdapter.notifyDataSetChanged();
                                    httpRequest.progress.dismiss();

                                }
                            });
                        } else {
                            httpRequest.progress.dismiss();
                            new android.support.v7.app.AlertDialog.Builder(CompleteBookingActivity.this)

                                    .setMessage(R.string.bookingUpdatedSuccessMsg)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(CompleteBookingActivity.this, SearchActivity.class);
                                            startActivity(intent);
                                        }
                                    }).show();
                        }

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
     * Save Signature of Customer in phone
     */
    public void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        customerPaid = (TextView) dialog.findViewById(R.id.customerPaid);

        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        save = (Button) dialog.findViewById(R.id.save);
        save.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;
        String amountPaidTitle = getResources().getString(R.string.showAmountOnSigDialoag) + "\u20B9" + amountPaidInput.getText().toString();
        customerPaid.setText(amountPaidTitle);

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
                dialog.dismiss();

                Toast.makeText(v.getContext(), R.string.signature_saved, Toast.LENGTH_SHORT).show();

            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mSignature.clear();
                dialog.dismiss();

            }
        });
        dialog.show();
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
