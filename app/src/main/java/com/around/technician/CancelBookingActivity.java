package com.around.technician;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.around.technician.adapters.CancelBookingAdapter;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CancelBookingActivity extends AppCompatActivity implements ApiResponse {
    private String bookingID;
    private ConnectionDetector cd;
    private Misc misc;
    private CancelBookingAdapter mAdapter;
    private final List<BookingGetterSetter> cancellationReason = new ArrayList<>();
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_booking);
        FirebaseCrash.log("Activity created");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cancel Booking");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        bookingID = intent.getStringExtra("bookingID");
        String appliance = intent.getStringExtra("services");
        String customerName = intent.getStringExtra("customerName");
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        //Button submit = findViewById(R.id.submit);

        TextView bookingIDText = findViewById(R.id.bookingID);
        TextView services = findViewById(R.id.services);
        TextView name = findViewById(R.id.customerName);

        bookingIDText.setText(bookingID);
        services.setText(appliance);
        name.setText(customerName);

        cd = new ConnectionDetector(this);
        misc = new Misc(this);

        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            mAdapter = new CancelBookingAdapter(cancellationReason, recyclerView);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cd.isConnectingToInternet()) {
            httpRequest = new HttpRequest(this, true);
            httpRequest.delegate = CancelBookingActivity.this;
            httpRequest.execute("getCancellationReason");
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

    public void processCancelBooking(View view) {
        String selectedReason = "";
        List<BookingGetterSetter> data = mAdapter.getCancellationReason();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCheckedCancellationReason()) {
                selectedReason = data.get(i).getCancellationReason();
                break;
            }
        }

        if (selectedReason.equals("")) {
            Snackbar.make(view, R.string.cancellationReasonRequired, Snackbar.LENGTH_LONG).show();
        } else {
            if (cd.isConnectingToInternet()) {
                String location = misc.getLocation();
                httpRequest = new HttpRequest(this, true);
                httpRequest.delegate = CancelBookingActivity.this;
                httpRequest.execute("cancelBookingByEngineer", bookingID, selectedReason, location);
            } else {
                misc.NoConnection();
            }
        }
    }

    @Override
    public void processFinish(String httpReqResponse) {
//        int maxLogSize = 2000;
//        for (int i = 0; i <= httpReqResponse.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i + 1) * maxLogSize;
//            end = end > httpReqResponse.length() ? httpReqResponse.length() : end;
//            Log.w("Response", httpReqResponse.substring(start, end));
//        }
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    if (jsonObject.has("cancellationReason")) {
                        JSONArray posts = jsonObject.optJSONArray("cancellationReason");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            cancellationReason.add(new BookingGetterSetter(post.optString("reason"), false));
                        }

                        CancelBookingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CancelBookingActivity.this.mAdapter.notifyItemInserted(cancellationReason.size());
                                CancelBookingActivity.this.mAdapter.notifyDataSetChanged();
                                httpRequest.progress.dismiss();

                            }
                        });
                    } else {
                        httpRequest.progress.dismiss();
                        new android.support.v7.app.AlertDialog.Builder(CancelBookingActivity.this)

                                .setMessage(R.string.bookingUpdatedSuccessMsg)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(CancelBookingActivity.this, SearchActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }).show();
                    }

                } else {
                    misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serverConnectionFailedMsg);
                    httpRequest.progress.dismiss();
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
