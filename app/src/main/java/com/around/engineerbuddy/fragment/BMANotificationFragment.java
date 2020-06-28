package com.around.engineerbuddy.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.BookingInfo;
import com.around.engineerbuddy.entity.EONotification;
import com.around.engineerbuddy.entity.EONotificationMessage;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BMANotificationFragment extends BMAFragment {
    RecyclerView recyclerView;
    HttpRequest httpRequest;
    EONotification eoNotification;
    TextView noDataToDisplay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.notification, container, false);
        this.recyclerView = this.view.findViewById(R.id.notificationRecyclerView);
        this.noDataToDisplay=this.view.findViewById(R.id.noDataToDisplay);
        loadRecyclerView();
        getUserNotifications();
        return this.view;
    }
    private void loadRecyclerView(){

        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(),getNotificationList(), recyclerView, this, R.layout.notification_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
    }
    private ArrayList<EONotificationMessage> getNotificationList(){
        return this.eoNotification!=null ? this.eoNotification.notifications : new ArrayList<>();
    }
    private void getUserNotifications(){
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = BMANotificationFragment.this;
        String mobile=MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("phoneNumber",null);
        Log.d("aaaaa","mobile no = "+mobile);
        httpRequest.execute("usernotifications",  mobile);

    }
    private void dataToView(){
        loadRecyclerView();
    }

    @Override
    public void processFinish(String response) {
        super.processFinish(response);
        this.httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    this.eoNotification = BMAGson.store().getObject(EONotification.class, res);
                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
                    if (this.eoNotification != null && this.eoNotification.notifications.size()>0) {
                        this.noDataToDisplay.setVisibility(View.GONE);
                        this.dataToView();
                       // if(this.getBookingList().size()==0) {
                           // noDataToDisplayLayout.setVisibility(View.VISIBLE);
                       // }

                    }else{
                        this.noDataToDisplay.setVisibility(View.VISIBLE);
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
        }else{
            httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };
            bmaAlertDialog.show(getString(R.string.serverError));
        }
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {

        EONotificationMessage eoNotificationMessage= (EONotificationMessage) rowObject;
        LinearLayout rowLayout=itemView.findViewById(R.id.rowLayout);
        TextView text=itemView.findViewById(R.id.notificationText);
        TextView dateTime=itemView.findViewById(R.id.dateTime);
//        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy hh:mm a");
//        String date = format.format(Date.parse(eoNotificationMessage.created_on));
        dateTime.setText(parseDateToddMMyyyy(eoNotificationMessage.created_on));
        text.setText(eoNotificationMessage.message);
        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogToDetailMessage(eoNotificationMessage.message);
            }
        });

    }


    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    private void openDialogToDetailMessage(String message){
        final Dialog d = new Dialog(getContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        d.setContentView(R.layout.imagedialog);
        ImageView imageview= d.findViewById(R.id.showImage);
        imageview.setVisibility(View.GONE);
        TextView notifText=d.findViewById(R.id.notifText);
        notifText.setText(message);
        notifText.setVisibility(View.VISIBLE);
        notifText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
}
