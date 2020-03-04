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
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EOAllBookingTask;
import com.around.engineerbuddy.entity.EOBooking;

import org.json.JSONObject;

import java.util.ArrayList;

public class IncentiveFragment extends BMAFragment {

    TextView totalIncentiveAmount;
    RecyclerView recyclerView;
    HttpRequest httpRequest;
    String incentiveAmount;
    ArrayList<EOBooking>incentiveList=new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        incentiveAmount=getArguments().getString("totalIncentiveAmount");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.incentive_fragment, container, false);
        this.recyclerView=this.view.findViewById(R.id.recyclerView);
        this.totalIncentiveAmount=this.view.findViewById(R.id.totalIncentiveAmount);
        this.totalIncentiveAmount.setText("₹ "+incentiveAmount);
        getRequest();
        return this.view;
    }

    private void getRequest() {
        this.httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = IncentiveFragment.this;
        httpRequest.execute("incentiveEearnedBookings", getMainActivity().getEngineerId(), getMainActivity().getServiceCenterId());

    }

    @Override
    public void processFinish(String response) {
        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    //  JSONObject responseData = new JSONObject(jsonObject.getString("response"));

                      incentiveList = BMAGson.store().getList(EOBooking.class, jsonObject.getString("response"));
                      this.dataToView();


                }
            } catch (Exception e) {

            }
        }else{
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };

            bmaAlertDialog.show("Server Error");
        }
    }
    private void dataToView(){
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.incentiveList, recyclerView, this, R.layout.incentive_row);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(bmaRecyclerAdapter);
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOBooking eoBooking= (EOBooking) rowObject;
        TextView bookingId = itemView.findViewById(R.id.bookingId);
        TextView serviceType = itemView.findViewById(R.id.serviceType);
        TextView requestType = itemView.findViewById(R.id.requestType);
        TextView earnings = itemView.findViewById(R.id.earnings);
        TextView earningsStatus = itemView.findViewById(R.id.earningsStatus);
        if(eoBooking.is_paid.equalsIgnoreCase("1")){
            earningsStatus.setText("PAID");
            earningsStatus.setTextColor(getResources().getColor(R.color.white));
        }else{
            earningsStatus.setVisibility(View.INVISIBLE);
        }

        bookingId.setText(eoBooking.bookingID);
        serviceType.setText(eoBooking.services);
        requestType.setText(eoBooking.requestType);
        earnings.setText("₹ "+eoBooking.partner_incentive);


    }



}
