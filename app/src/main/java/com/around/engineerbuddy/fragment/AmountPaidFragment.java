package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.around.engineerbuddy.BookingGetterSetter;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class AmountPaidFragment extends BMAFragment {

    private HttpRequest httpRequest;
    ConnectionDetector cd;
    Misc misc;
//    String bookingID;
//    String amountDue;
    String formData;
    EditText amountPaidInput, amountDueInput;
    EditText remarks;
    String amountPaid ="", enRemarks = "";
    RadioGroup paymentGroup;
    Button submit;
    BookingGetterSetter bookingList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.bookingList= getArguments().getParcelable("bookingList");
        this.formData=getArguments().getString("formData");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.amount_paid_fragment,container,false);
        this.init();
        return this.view;
    }
    private void init(){
        cd = new ConnectionDetector(getContext());
        misc = new Misc(getContext());
        misc.checkAndLocationRequestPermissions();
        amountPaidInput = this.view.findViewById(R.id.amountPaid);
        amountDueInput = this.view.findViewById(R.id.amountDue);
        remarks = this.view.findViewById(R.id.remarks);
        this.submit=this.view.findViewById(R.id.submitButton);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProcess(v);
            }
        });

        this.submit.setText("Proceed");
        amountDueInput.setText(this.bookingList.amountDue);
        amountDueInput.setFocusable(false);

        paymentGroup = this.view.findViewById(R.id.paymentGroup);

    }

    public void submitProcess(View view) {
        boolean validation = true;

        amountPaid = amountPaidInput.getText().toString();
        enRemarks = remarks.getText().toString();

        if (Integer.parseInt(this.bookingList.amountDue) > 0) {
            if (amountPaid.isEmpty()) {
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();
            } else if (Integer.parseInt(amountPaid) == 0) {
                validation = false;
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();

            }
        }

        if(enRemarks.isEmpty()){
            validation = false;
            Snackbar.make(view, R.string.remarksRequired, Snackbar.LENGTH_LONG).show();
        }

        if (paymentGroup.getCheckedRadioButtonId() == -1) {
            validation = false;
            Snackbar.make(view, R.string.choose_payment_method, Snackbar.LENGTH_LONG).show();

        } else {
            // one of the radio buttons is checked
            RadioButton rb = (RadioButton) paymentGroup.findViewById(paymentGroup.getCheckedRadioButtonId());

            if (rb.getText().toString().equals(getResources().getString(R.string.payment_method_cash))) {
                if(validation){
                    cashOnDelivery();
                }

            } else if (rb.getText().toString().equals(getResources().getString(R.string.payment_method_paytm))) {

                if(validation){
                    paymentThroughPaytm();
                }
            } else {
                validation = false;
                Snackbar.make(view, R.string.choose_payment_method, Snackbar.LENGTH_LONG).show();
            }
        }



    }

    public void cashOnDelivery() {
        DigitalSignature digitalSignature=new DigitalSignature();
        Bundle bundle=new Bundle();
        bundle.putParcelable("bookingList",this.bookingList);
        bundle.putString("formData",this.formData);
        bundle.putString("amountPaid", amountPaid);
        bundle.putString("remarks", enRemarks);
        bundle.putString("paymentMethod", getResources().getString(R.string.payment_method_cash));
        bundle.putString(BMAConstants.HEADER_TXT,"DigitalSignature");
        digitalSignature.setArguments(bundle);
        getMainActivity().updateFragment(digitalSignature,true);
    }

    public void paymentThroughPaytm() {
        if (cd.isConnectingToInternet()) {
            httpRequest = new HttpRequest(getContext(), true);
            httpRequest.delegate = AmountPaidFragment.this;
            httpRequest.execute("getCustomerQrCode", this.bookingList.bookingID, amountPaid);

        } else {
            misc.NoConnection();
        }
    }

    @Override
    public void processFinish(String httpReqResponse) {
        Log.w("AmountResponse", httpReqResponse);
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                switch (statusCode) {
                    case "0000":
                        httpRequest.progress.dismiss();
                        PaytmQRFragment paytmQRFragment=new PaytmQRFragment();
                        Bundle bundle=new Bundle();
                        bundle.putParcelable("bookingList",this.bookingList);
                        bundle.putString("formData",this.formData);
                        bundle.putString("amountPaid", amountPaid);
                        bundle.putString("remarks", enRemarks);
                        bundle.putString("qrUrl",jsonObject.getString("QrImageUrl"));
                        bundle.putString("paymentMethod", getResources().getString(R.string.payment_method_cash));
                        bundle.putString(BMAConstants.HEADER_TXT,getString(R.string.payThroughPaytm));
                        paytmQRFragment.setArguments(bundle);
                        getMainActivity().updateFragment(paytmQRFragment,true);
//

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
