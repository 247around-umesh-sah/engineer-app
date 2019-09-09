package com.around.engineerbuddy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.EOProfile;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends BMAFragment {
    HttpRequest httpRequest;
    TextView profileName, appliance, idType, idNumber, otherID, mobile;
    LinearLayout otherIdLayout, adharLayout, mobileNumberLayout;
    //    LinearLayout
    EOProfile eoProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.profile, container, false);
        this.profileName = this.view.findViewById(R.id.profileName);
        this.appliance = this.view.findViewById(R.id.appliance);
        this.idType = this.view.findViewById(R.id.idType);
        this.idNumber = this.view.findViewById(R.id.idNumber);
        this.otherID = this.view.findViewById(R.id.otherID);
        this.mobile = this.view.findViewById(R.id.mobile);
        this.otherIdLayout= this.view.findViewById(R.id.otherIdLayout);
        this.adharLayout= this.view.findViewById(R.id.adharLayout);
        this.mobileNumberLayout= this.view.findViewById(R.id.mobileNumberLayout);
        this.loadData();
        return this.view;
    }

    private void dataToView() {
        this.profileName.setText(this.eoProfile.name);
        this.appliance.setText(this.eoProfile.appliances);
        if(!this.eoProfile.identityProofType.equalsIgnoreCase("0")) {
            this.idType.setText(this.eoProfile.identityProofType);
        }
        this.idNumber.setText(this.eoProfile.identityProofNumber);

        this.mobile.setText(this.eoProfile.phone);
        this.otherIdLayout.setVisibility(View.GONE);
//        this.adharLayout= this.view.findViewById(R.id.mobile);
//        this.mobileNumberLayout= this.view.findViewById(R.id.mobile);

    }

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = ProfileFragment.this;
        httpRequest.execute("engineerProfile", MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID", null));
    }

    @Override
    public void processFinish(String response) {


        Log.d("response", " response = " + response);

        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    this.eoProfile = BMAGson.store().getObject(EOProfile.class, res);
                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
                    if (this.eoProfile != null) {
                        this.dataToView();
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
        //  distance("813208","813209");


    }
}
