package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TechSupportFragment extends BMAFragment implements View.OnClickListener {

    TextView noDataToDisplay;
    boolean isTechSupoort;
    EOBooking eoBooking;
    ArrayList<String> techSupportList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.isTechSupoort = getArguments().getBoolean("isTechSupoort");
        this.eoBooking = getArguments().getParcelable("eoBooking");
    }

    LinearLayout techSupportLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.tech_support_fragment, container, false);
        BMAmplitude.saveUserAction("TechSupportFragment", isTechSupoort ? "TechSupportFragment" :"Customer Call");
        this.techSupportLayout = this.view.findViewById(R.id.techSupportLayout);
        CardView techSupportCardView = this.view.findViewById(R.id.tech_support_header);
        techSupportCardView.setVisibility(isTechSupoort ? View.GONE : View.VISIBLE);
        this.noDataToDisplay = this.view.findViewById(R.id.noDataToDisplay);
        this.techSupportLayout.removeAllViews();
        if (isTechSupoort) {
            this.loadData();
        } else {
            TextView techSupportName = this.view.findViewById(R.id.techSupportName);
            TextView techSupportNumber = this.view.findViewById(R.id.techSupportNumber);
            techSupportName.setText(eoBooking.name);
            techSupportNumber.setText(eoBooking.bookingAddress);
            techSupportList.add(eoBooking.primaryContact);
            this.dataToView();
        }


        return this.view;

    }

    HttpRequest httpRequest;

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = TechSupportFragment.this;
        httpRequest.execute("techSupport", eoBooking.bookingID);
    }


    @Override
    public void processFinish(String response) {

        Log.d("response", " response = " + response);
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    // String res = jsonObject.getString("response");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    //  JSONArray jsonArray=jsonObject.getJSONArray("response");

                    if (jsonObject1 == null) {
                        return;
                    }
                    //  JSONObject jsonObject1=jsonArray.getJSONObject(0);
                    String serviceManager = jsonObject1.getString("service_manager");
                    if (serviceManager != null) {
                        techSupportList.add("Service Manager (SF)_" + serviceManager);
                    }
                    String accountManager = jsonObject1.getString("account_manager");
                    if (accountManager != null) {
                        techSupportList.add("Account Manager_" + accountManager);
                    }
                    String tollFreeNumber = jsonObject1.getString("toll_free_number");
                    if (tollFreeNumber != null) {
                        techSupportList.add("Toll Free Number_" + tollFreeNumber);
                    }
                    this.dataToView();
                    httpRequest.progress.dismiss();
//                    this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, res);
//                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
//                    if (this.bookingInfo != null) {
//                        this.dataToView();
//                        noDataToDisplayLayout.setVisibility(View.GONE);
//                    }


                } else {
                    httpRequest.progress.dismiss();

                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                        @Override
                        public void onWarningDismiss() {
                            super.onWarningDismiss();
                            noDataToDisplay.setVisibility(View.VISIBLE);
                        }
                    };
                    bmaAlertDialog.show(jsonObject.get("result").toString());
                }
            } catch (JSONException e) {
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                        noDataToDisplay.setVisibility(View.VISIBLE);
                    }
                };
                bmaAlertDialog.show(R.string.loginFailedMsg);
            }
        } else {
            noDataToDisplay.setVisibility(View.VISIBLE);
            httpRequest.progress.dismiss();
        }
        //  distance("813208","813209");


    }

    private void dataToView() {
        for (String techSupport : this.techSupportList) {
            View positionView = LayoutInflater.from(this.getActivity()).inflate(R.layout.tech_support, null, false);
            TextView techSupportName = positionView.findViewById(R.id.techSupportName);
            TextView techSupportNumber = positionView.findViewById(R.id.techSupportNumber);
            CardView calliconLayoutView = positionView.findViewById(R.id.calliconLayoutView);
            CardView callIconLayout = positionView.findViewById(R.id.call_iconLayoutView);

            ImageView callIcon = positionView.findViewById(R.id.callIcon);
            ImageView call = positionView.findViewById(R.id.call_Icon);
            callIcon.setOnClickListener(this);
            //callIcon.setTag(techSupport);
            call.setOnClickListener(this);
            // call.setTag(techSupport);


            if (isTechSupoort) {

                techSupportName.setText(techSupport.split("_")[0]);
                if (techSupport.split("_").length == 2) {
                    techSupportNumber.setText(techSupport.split("_")[1]);
                    call.setTag(techSupport.split("_")[1]);
                    callIcon.setTag(techSupport.split("_")[1]);
                } else {
                    techSupportName.setVisibility(View.GONE);
                }

            } else {
                calliconLayoutView.setVisibility(View.GONE);
                techSupportName.setVisibility(View.GONE);
                callIconLayout.setVisibility(View.VISIBLE);
                techSupportNumber.setTextColor(BMAUIUtil.getColor(R.color.violate));
                techSupportNumber.setText(techSupport);
                call.setTag(techSupport);
                callIcon.setTag(techSupport);
                positionView.findViewById(R.id.image).setVisibility(View.GONE);
            }
            this.techSupportLayout.addView(positionView);
        }
    }

    @Override
    public void onClick(View v) {
        doCall((String) v.getTag());
    }

    private void doCall(String number) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
            BMAAlertDialog dialog = new BMAAlertDialog(getActivity(), true, false) {
                @Override
                public void onConfirmation() {
                    this.dismiss();
                    call(number);
                }
            };
            dialog.show(getString(R.string.enableCallFeatureValiadtion));
        } else {

            call(number);
        }

    }

    private void call(String number) {
        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), true, false) {
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));//change the number
                startActivity(callIntent);
            }
        };
        bmaAlertDialog.show(getString(R.string.callConfirmationMessage));

    }
}
