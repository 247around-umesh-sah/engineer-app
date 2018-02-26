package com.around.technician;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.around.technician.adapters.CompleteBookingAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompleteBookingActivity extends AppCompatActivity implements ApiResponse {
    ConnectionDetector cd;
    Misc misc;
    RecyclerView recyclerView;
    CompleteBookingAdapter mAdapter;
    String bookingID, appliance, amountDue, customerName;
    List<BookingGetterSetter> unitList;
    Button submit;

    private List<BookingGetterSetter> unitDetails = new ArrayList<>();
    private HttpRequest httpRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complete_booking);
        Intent intent = getIntent();
        bookingID = intent.getStringExtra("bookingID");
        appliance = intent.getStringExtra("services");
        amountDue = intent.getStringExtra("amountDue");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setTitle(bookingID);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        TextView toolbarServices = findViewById(R.id.toolbarservice);
        toolbarTitle.setText(bookingID);
        toolbarServices.setText(appliance);


        customerName = intent.getStringExtra("customerName");
        recyclerView = findViewById(R.id.appliance_list);

        submit = findViewById(R.id.submit);

        cd = new ConnectionDetector(this);
        misc = new Misc(this);
        misc.checkAndLocationRequestPermissions();
        recyclerView.setNestedScrollingEnabled(false);
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

    }

    /**
     * This is used to check validation whatever Engineer done to completed booking.
     *
     * @param view View
     */
    public void submitProcess(View view) {
        boolean validation = true;
        List<BookingGetterSetter> data = mAdapter.getUnitData();

        if (validation) {

            for (int i = 0; i < data.size(); i++) {

                boolean isDelivered = false;
                for (int k = 0; k < data.get(i).getUnitList().size(); k++) {
                    if (isDelivered = data.get(i).getUnitList().get(k).getDelivered()) {
                        isDelivered = data.get(i).getUnitList().get(k).getDelivered();
                    }
                    if (data.get(i).getUnitList().get(k).getPod().equals("1")) {

                        if (data.get(i).getSerialNo().toString().isEmpty()) {
                            validation = false;
                            // IF Serial No is empty means pict is not set
                            Snackbar.make(view, R.string.serialNumberPictureRequired, Snackbar.LENGTH_LONG).show();
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
                    openAddAmountActivity(data);

                } else {
                    Toast.makeText(CompleteBookingActivity.this, R.string.askGPSPermission, Toast.LENGTH_SHORT).show();
                }

            } else {
                misc.NoConnection();
            }
        }
    }

//    public boolean isNumeric(String s) {
//        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
//    }

    public void openAddAmountActivity(final List<BookingGetterSetter> data){

        @SuppressLint("UseSparseArrays")
        Map<Integer, Map<String, String>> unitParameter = new HashMap<>();
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

        Intent openAddAmount = new Intent(CompleteBookingActivity.this, AmountPaidActivity.class);
        openAddAmount.putExtra("formData", arrayString);
        openAddAmount.putExtra("amountDue", amountDue);
        openAddAmount.putExtra("bookingID", bookingID);
        startActivity(openAddAmount);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data);

    }


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
                            submit.setVisibility(View.VISIBLE);

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

}
