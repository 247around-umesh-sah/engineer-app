package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.BookingInfo;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CancelledBookingFragment extends BMAFragment {
    TextView tomorrowText, bookingText, countBooking;
    ImageView bookingTypeImage;
    View devider;
    LinearLayout bookingHeaderLayout, noDataToDisplayLayout;
    RecyclerView recyclerView;
    BookingInfo bookingInfo;
    HttpRequest httpRequest;
    private boolean isCancelledBooking;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.isCancelledBooking = this.getArguments().getBoolean("isCancelledBooking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        BMAmplitude.saveUserAction("CancelledBookingFragment", "CancelledBookingFragment");
        this.recyclerView = this.view.findViewById(R.id.recyclerView);
        this.countBooking = this.view.findViewById(R.id.count_booking);
        this.tomorrowText = this.view.findViewById(R.id.tomorrowText);
        this.bookingText = this.view.findViewById(R.id.bookingText);
        this.bookingTypeImage = this.view.findViewById(R.id.bookingTypeImage);
        this.devider = this.view.findViewById(R.id.deviderView);
        this.bookingHeaderLayout = this.view.findViewById(R.id.headerLayout);
        this.bookingHeaderLayout.setVisibility(View.GONE);
        this.noDataToDisplayLayout = this.view.findViewById(R.id.nodataToDisplayLayout);
        this.noDataToDisplayLayout.setVisibility(View.VISIBLE);
        this.loadData();
        return this.view;
    }

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CancelledBookingFragment.this;
        String status = isCancelledBooking ? "Cancelled" : "Completed";
        httpRequest.execute("engineerBookingsByStatus", MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null), status);


    }

    private void dataToView() {

        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.bookingInfo.cancelledBookings, recyclerView, this, R.layout.booking_detail_fragment);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);


    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {

        EOBooking eoBooking = (EOBooking) rowObject;
        LinearLayout rowLayout = itemView.findViewById(R.id.rowLayout);
        itemView.findViewById(R.id.tileRowLayout).setVisibility(View.GONE);
        itemView.findViewById(R.id.customerDetail).setVisibility(View.GONE);
        itemView.findViewById(R.id.bookingDetail).setVisibility(View.GONE);
        itemView.findViewById(R.id.customerDetailLayout).setVisibility(View.GONE);

        itemView.findViewById(R.id.bookingStatusLayout).setVisibility(View.VISIBLE);
        TextView chargeableAmount = itemView.findViewById(R.id.chargableservice);
        chargeableAmount.setText(eoBooking.bookingID);
        itemView.findViewById(R.id.dateCalenderLayout).setVisibility(View.GONE);

        itemView.findViewById(R.id.symptomLayout).setVisibility(View.GONE);

        LinearLayout dateLayout = itemView.findViewById(R.id.dateLayout);
        dateLayout.setVisibility(View.VISIBLE);
        dateLayout.setBackgroundColor(this.isCancelledBooking ? BMAUIUtil.getColor(R.color.blue_black) : BMAUIUtil.getColor(R.color.missedBookingcolor));
        itemView.findViewById(R.id.name).setVisibility(View.GONE);
        TextView name = itemView.findViewById(R.id.bookingDeatilName);
        name.setVisibility(View.VISIBLE);

        TextView bookingDate = itemView.findViewById(R.id.headerDate);
        TextView requestType = itemView.findViewById(R.id.requestType);
        TextView bookingIdName = itemView.findViewById(R.id.bookingIdName);
        TextView chargeabletext = itemView.findViewById(R.id.chargeabletext);
        TextView bookingStatus=itemView.findViewById(R.id.bookingStatus);
        itemView.findViewById(R.id.chargeableAmount).setVisibility(View.GONE);

        name.setText(eoBooking.name);
        bookingDate.setText(eoBooking.bookingDate);
        requestType.setText(eoBooking.requestType);
        bookingIdName.setText(eoBooking.bookingID);
        chargeabletext.setText("₹ " + eoBooking.dueAmount);
        bookingStatus.setText("Status - "+eoBooking.booking_close_status);
        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("eoBooking", eoBooking);

            }
        });


    }

    private void updateFragment(Bundle bundle, String headerText) {
        CompletedBookingDetailFragment completedBookingDetailFragment = new CompletedBookingDetailFragment();
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        completedBookingDetailFragment.setArguments(bundle);
        getMainActivity().updateFragment(completedBookingDetailFragment, true);
    }

    @Override
    public void processFinish(String response) {

        httpRequest.progress.dismiss();

        Log.d("response", " response = " + response);

        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, res);
                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
                    if (this.bookingInfo != null) {
                        this.dataToView();
                        noDataToDisplayLayout.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                    }
                };
                bmaAlertDialog.show(R.string.loginFailedMsg);
            }
        } else {
            httpRequest.progress.dismiss();
        }
    }
}
