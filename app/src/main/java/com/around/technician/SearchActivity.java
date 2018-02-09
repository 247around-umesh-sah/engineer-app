package com.around.technician;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.around.technician.adapters.SearchAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ApiResponse {
    AppCompatEditText searchBooking;
    ProgressBar progressBar;
    ConnectionDetector cd;
    Misc misc;
    TextView bookingNotFound;
    RecyclerView recyclerView;
    SearchAdapter mAdapter;
    private List<BookingGetterSetter> bookingList = new ArrayList<>();
    private HttpRequest httpRequest;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseCrash.log("Activity created");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        searchBooking = findViewById(R.id.searchBooking);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        bookingNotFound = findViewById(R.id.bookingNotFound);
        recyclerView = findViewById(R.id.recyclerview);
        cd = new ConnectionDetector(this);
        misc = new Misc(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.showSoftInput(searchBooking, InputMethodManager.SHOW_IMPLICIT);

        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            mAdapter = new SearchAdapter(bookingList, recyclerView);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        searchBooking.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //We will sent auto request after 9 character of string
                if (s.length() > 9) {
                    mFirebaseAnalytics.setUserProperty("Searched Booking ID", s.toString());
                    searchBookingID(s);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        moveTaskToBack(true);
                    }
                }).create().show();
    }

    /**
     * Send api request to server to search booking by Mobile Number or Booking ID
     *
     * @param searchedText CharSequence
     */
    public void searchBookingID(CharSequence searchedText) {
        if (cd.isConnectingToInternet()) {

            progressBar.setVisibility(View.VISIBLE);
            bookingNotFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            bookingList.clear();
            mAdapter.notifyItemInserted(bookingList.size());
            mAdapter.notifyDataSetChanged();

            httpRequest = new HttpRequest(this, false);
            httpRequest.delegate = SearchActivity.this;
            httpRequest.execute("searchBookingID", searchedText.toString());


        } else {
            misc.NoConnection();

        }
    }

    /**
     * Get Response from Search.
     * If booking Exist then we will show it as list
     *
     * @param httpReqResponse String
     */

    @Override
    public void processFinish(String httpReqResponse) {
        Log.w("Response", httpReqResponse);
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {

                    JSONArray posts = jsonObject.optJSONArray("bookingDetails");
                    bookingList.clear();
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.optJSONObject(i);

                        bookingList.add(new BookingGetterSetter(post.optString("booking_id"), post.optString("services"),
                                post.optString("customername"),
                                post.optString("booking_primary_contact_no"),
                                post.optString("booking_address"),
                                post.optString("current_status"),
                                post.optString("amount_due"),
                                post.optString("booking_remarks")));
                    }

                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);

                            SearchActivity.this.mAdapter.notifyItemInserted(bookingList.size());
                            SearchActivity.this.mAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            httpRequest.progress.dismiss();
                            progressBar.setVisibility(View.GONE);
                            bookingNotFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    bookingNotFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    httpRequest.progress.dismiss();
                }
            } catch (JSONException e) {

                e.printStackTrace();

                httpRequest.progress.dismiss();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            bookingNotFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            //misc.showDialog("OPPS!", "System Failure. Please try Again");
            httpRequest.progress.dismiss();
        }
    }
}
