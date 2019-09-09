package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BookingGetterSetter;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.util.BMAConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class PaytmQRFragment extends BMAFragment implements View.OnClickListener {
    String bookingID, remarks;
    String amountDue, amountPaid;
    String formData, qrUrl;
    BookingGetterSetter bookingList;
    Button confirm;
    private HttpRequest httpRequest;
    ConnectionDetector cd;
    Misc misc;
    TextView noDataToDisplay,paymentReceive;
    BMAFontViewField checkAmountDropDownIcon;
    EditText receiveAmount;
    String actionID;
    int isAmountDoneByPaytm;
         //


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Bundle bundle=this.getArguments();
       this.bookingID= this.getArguments().getString("bookingID");
//
//        this.formData= bundle.getString("formData");
//        this.amountPaid= bundle.getString("amountPaid");
//        this.remarks=bundle.getString("remarks");
//        this.qrUrl=bundle.getString("qrUrl");

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.paytm_qr_fragment,container,false);
        this.noDataToDisplay=this.view.findViewById(R.id.noDataToDisplay);
        this.view.findViewById(R.id.progressBar1).setVisibility(View.GONE);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.confirm=this.view.findViewById(R.id.submitButton);
        this.checkAmountDropDownIcon=this.view.findViewById(R.id.checkAmountDropDownIcon);
        this.receiveAmount=this.view.findViewById(R.id.receiveAmount);
        this.paymentReceive=this.view.findViewById(R.id.paymentReceive);
        this.receiveAmount.setEnabled(false);
        this.checkAmountDropDownIcon.setOnClickListener(this);
        this.confirm.setOnClickListener(this);
        this.init();
        return this.view;
    }
    private void init(){
        cd = new ConnectionDetector(getContext());
        misc = new Misc(getContext());
        misc.checkAndLocationRequestPermissions();
        this.loadQRCode();
//        this.submit=this.view.findViewById(R.id.submitButton);
//        this.submit.setText("Confirmed");
//        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
//        ImageView qrUrlImage = this.view.findViewById(R.id.qrImage);
//        final ProgressBar progressBar = this.view.findViewById(R.id.progressBar1);
//
//        Picasso.with(getContext()).load(qrUrl)
//                .fit().centerInside()
//                .into(qrUrlImage, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//        this.submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitProcess(v);
//            }
//        });
    }
    private void showQRCode() {
        ImageView qrUrlImage = this.view.findViewById(R.id.qrImage);
           final ProgressBar progressBar = this.view.findViewById(R.id.progressBar1);

        Picasso.with(getContext()).load(qrUrl)
                .fit().centerInside()
                .into(qrUrlImage, new Callback() {
                    @Override
                    public void onSuccess() {
                         progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        qrUrlImage.setBackground(getResources().getDrawable(R.drawable.noimage));
                    }
                });
    }
    public void loadQRCode() {
        if (cd.isConnectingToInternet()) {
            httpRequest = new HttpRequest(getContext(), true);
            httpRequest.delegate = PaytmQRFragment.this;
            this.actionID="getCustomerQrCode";
            httpRequest.execute(this.actionID, this.bookingID);

        } else {
            misc.NoConnection();
        }
    }

    public void submitProcess(View view){
        Intent intent = new Intent(getContext(), DigitalSignature.class);
        intent.putExtra("formData", formData);
        intent.putExtra("amountDue", amountDue);
        intent.putExtra("bookingID", bookingID);
        intent.putExtra("amountPaid", amountPaid);
        intent.putExtra("remarks", remarks);
        intent.putExtra("paymentMethod", getResources().getString(R.string.payment_method_paytm));
        startActivity(intent);

        DigitalSignature digitalSignature=new DigitalSignature();
        Bundle bundle=new Bundle();
        bundle.putString("formData", formData);
        bundle.putParcelable("bookingList",this.bookingList);
        bundle.putString("remarks", remarks);
        bundle.putString("paymentMethod", getResources().getString(R.string.payment_method_paytm));
        digitalSignature.setArguments(bundle);
        getMainActivity().updateFragment(digitalSignature,true);
    }
    @Override
    public void processFinish(String httpReqResponse) {
        Log.d("AmountResponse", httpReqResponse);
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                switch (statusCode) {
                    case "0000":
                        if(actionID.equalsIgnoreCase("paytmAmountByEngineer")){

                            JSONObject jsonObject1=jsonObject.getJSONObject("response");
                            this.receiveAmount.setText(jsonObject1.getString("amount"));
                            this.isAmountDoneByPaytm= (int) jsonObject1.get("amount_flag");
                            if(this.isAmountDoneByPaytm!=0){
                                paymentReceive.setText("Amount Recieved");
                            }
                            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext()) {


                                @Override
                                public void onDefault() {
                                    super.onDefault();
                                }


                            };
                            bmaAlertDialog.show(jsonObject.getString("result"));
                        }else {
                            noDataToDisplay.setVisibility(View.GONE);
                            this.qrUrl = jsonObject.getString("QrImageUrl");
                            showQRCode();
                        }
                        httpRequest.progress.dismiss();

//                        PaytmQRFragment paytmQRFragment=new PaytmQRFragment();
//                        Bundle bundle=new Bundle();
//                        bundle.putParcelable("bookingList",this.bookingList);
//                        bundle.putString("formData",this.formData);
//                        bundle.putString("amountPaid", amountPaid);
//                        bundle.putString("remarks", enRemarks);
//                        bundle.putString("qrUrl",jsonObject.getString("QrImageUrl"));
//                        bundle.putString("paymentMethod", getResources().getString(R.string.payment_method_cash));
//                        bundle.putString(BMAConstants.HEADER_TXT,"Pay Through Paytm");
//                        paytmQRFragment.setArguments(bundle);
//                        getMainActivity().updateFragment(paytmQRFragment,true);
//                        Intent intent = new Intent(getContext(), PaytmQrActivity.class);
//                        intent.putExtra("formData", formData);
//                        intent.putExtra("amountDue", amountDue);
//                        intent.putExtra("bookingID", bookingID);
//                        intent.putExtra("amountPaid", amountPaid);
//                        intent.putExtra("remarks", enRemarks);
//                        intent.putExtra("qrUrl",jsonObject.getString("QrImageUrl"));
//                        intent.putExtra("paymentMethod", getResources().getString(R.string.payment_method_paytm));
//                        startActivity(intent);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkAmountDropDownIcon:
                checkAmount();
                break;
            case R.id.submitButton:
                if(isAmountDoneByPaytm==0){
                    Toast.makeText(getContext(),getString(R.string.transctionCompletevalidation),Toast.LENGTH_SHORT).show();
                    return;
                }
                //Log.d("aaaaaa","fragment Name "+getMainActivity().getSupportFragmentManager().findFragmentByTag("CompleteBookingCategoryFragment"));
               Intent intent=new Intent();
                intent.putExtra("paymentsAmount", this.receiveAmount.getText().toString().trim());
                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                getFragmentManager().popBackStack();
                break;
        }
//        Fragment fragment = getFragmentManager().findFragmentByTag(CompleteBookingCategoryFragment.class.getName());
//        fragment.onActivityResult(BMAConstants.requestCode, BMAConstants.requestCode, null);
//       getFragmentManager().popBackStack(CompleteBookingCategoryFragment.class.getName(), 0);
//      //  getMainActivity().getSupportFragmentManager().popBackStack();


    }
    private void checkAmount(){
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = PaytmQRFragment.this;
        this.actionID="paytmAmountByEngineer";
        httpRequest.execute(actionID, this.bookingID);
    }
}
