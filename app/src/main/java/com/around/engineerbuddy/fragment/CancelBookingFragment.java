package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.BookingGetterSetter;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.CancelBookingAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAOTPDialog;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CancelBookingFragment extends BMAFragment implements View.OnClickListener {
    private String bookingID;
    private ConnectionDetector cd;
    private Misc misc;
    private CancelBookingAdapter mAdapter;
    private final List<BookingGetterSetter> cancellationReason = new ArrayList<>();
    private HttpRequest httpRequest;
    //BookingGetterSetter bookingList;
    EditText reasonForCancel;
    Button submit;
    String actionID;
    String otp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.bookingID = getArguments().getString("bookingID");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.cancel_booking_fragment, container, false);
        BMAmplitude.saveUserAction("CancelBookingFragment", "CancelBookingFragment");
        // cancellationReason.add("Your problem is resolved");
        RecyclerView recyclerView = this.view.findViewById(R.id.recyclerview);
        //Button submit = findViewById(R.id.submit);
        submit = view.findViewById(R.id.submitButton);
        this.submit.setOnClickListener(this);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        TextView bookingIDText = this.view.findViewById(R.id.bookingID);
        TextView services = this.view.findViewById(R.id.services);
        TextView name = this.view.findViewById(R.id.customerName);
        this.reasonForCancel = this.view.findViewById(R.id.Problemdescriptionedittext);


//        bookingIDText.setText(bookingList.bookingID);
//        services.setText(bookingList.services);
//        name.setText(bookingList.customerName);

        cd = new ConnectionDetector(getContext());
        misc = new Misc(getContext());

        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            mAdapter = new CancelBookingAdapter(cancellationReason, recyclerView);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cd.isConnectingToInternet()) {
            httpRequest = new HttpRequest(getContext(), true);
            httpRequest.delegate = this;
            actionID = "getCancellationReason";
            httpRequest.execute("getCancellationReason");
        } else {
            misc.NoConnection();
        }

        return this.view;
    }

  //  String selectedReason = "";
  String selectedReasonID = "";
    private boolean isSelectAllField(){
        selectedReasonID = "";
        List<BookingGetterSetter> data = mAdapter.getCancellationReason();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCheckedCancellationReason()) {
                selectedReasonID = data.get(i).getCancellationReasonID();
                break;
            }
        }

        if (selectedReasonID.equals("")) {
            Snackbar.make(view, R.string.cancellationReasonRequired, Snackbar.LENGTH_LONG).show();
            return false;
        } else if (this.reasonForCancel.getText().toString().trim().length() == 0) {
            Snackbar.make(view, R.string.remarksRequired, Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public void processCancelBooking(View view) {

            String location = misc.getLocation();
            if (cd.isConnectingToInternet()) {

                httpRequest = new HttpRequest(getContext(), true);
                httpRequest.delegate = this;
                actionID="cancelBookingByEngineer";
                // Log.d("aaaaaa","booking id = "+bookingID)
                httpRequest.execute("cancelBookingByEngineer", bookingID, selectedReasonID, location, this.reasonForCancel.getText().toString().trim());
            } else {


//
//                    EngineerBookingDao eodao = DataBaseClient.getInstance(getMainActivity().getApplicationContext()).getAppDatabase().engineerBookingInfoDao();
//                    List<EOEngineerBookingInfo> bookingList = eodao.getAll();
//                    for (EOEngineerBookingInfo eoEngineerBookingInfo : bookingList) {
////                        HashMap<String, Object> requestBookingData = new HashMap<>();
////                        requestBookingData = BMAGson.store().getObject(HashMap.class, eoEngineerBookingInfo.getbookingInfoOfEngineerAction());
////                        String bookingID = (String) requestBookingData.get("booking_id");
//                        if (eoEngineerBookingInfo.getActionNameOverBooking().equalsIgnoreCase("cancelBookingByEngineer") && eoEngineerBookingInfo.getBookingID() != null && eoEngineerBookingInfo.getBookingID().equalsIgnoreCase(bookingID) ) {
//                            //  isSameBookingID = true;
//                            eodao.delete(eoEngineerBookingInfo);
//                            break;
//                        }
//                    }
////            if (isSameBookingID) {
////                misc.NoConnection();
////                return;
////            }
//                    EOEngineerBookingInfo eoEngineerBookingInfo = new EOEngineerBookingInfo();
//                    eoEngineerBookingInfo.setActionNameOverBooking("cancelBookingByEngineer");
//                    eoEngineerBookingInfo.setReason(this.reasonForCancel.getText().toString().trim());
//                    eoEngineerBookingInfo.setBookingID(bookingID);
//                    eoEngineerBookingInfo.setDescription(selectedReason);
//                    eoEngineerBookingInfo.setEngineerLocation(location);
//                    new SaveEngineerBookingAction(getContext(), eoEngineerBookingInfo);
                misc.NoConnection();
                return;
//                EOEngineerBookingInfo eoEngineerBookingInfo=new EOEngineerBookingInfo();
//                eoEngineerBookingInfo.setActionNameOverBooking("cancelBookingByEngineer");
//                eoEngineerBookingInfo.setReason( this.reasonForCancel.getText().toString().trim());
//                eoEngineerBookingInfo.setBookingID(bookingID);
//                eoEngineerBookingInfo.setDescription(selectedReason);
//                eoEngineerBookingInfo.setActionNameOverBooking(location);
//                new SaveEngineerBookingAction(getContext(),eoEngineerBookingInfo);
//                misc.NoConnection();

        }
    }

    @Override
    public void processFinish(String response) {
//        Log.d("aaaaaaa", "response = cancel = " + response);
//        Toast.makeText(getContext(), "rsponse = " + response, Toast.LENGTH_SHORT).show();

        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {

                    if (jsonObject.has("cancellationReason")) {
                        JSONArray posts = jsonObject.optJSONArray("cancellationReason");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            cancellationReason.add(new BookingGetterSetter(post.optString("reason"), false,post.optString("id")));
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CancelBookingFragment.this.mAdapter.notifyItemInserted(cancellationReason.size());
                                CancelBookingFragment.this.mAdapter.notifyDataSetChanged();
                                httpRequest.progress.dismiss();

                            }
                        });
                    } else if (this.actionID.equalsIgnoreCase("sendCancelRescheduleOTP")) {
                       // this.actionID = null;
                       // Toast.makeText(getContext(), "rsponse = " + response, Toast.LENGTH_LONG).show();
                        this.otp=jsonObject.getString("response");
                        httpRequest.progress.dismiss();
                        showOTPDialog();

                    } else {
//                        String rsult= jsonObject.getString("result");
//                        if(rsult==null){
//                            rsult="Booking Cancel Successfully";
//                        }
                        httpRequest.progress.dismiss();
                        new android.support.v7.app.AlertDialog.Builder(getContext())

                                .setMessage("Booking Cancel Successfully")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //  getMainActivity().updateFragment(new FragmentLoader(), false);
                                        Intent intent = new Intent();
                                        intent.putExtra("isCancelled", true);
                                        getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                                        getFragmentManager().popBackStack();

//                                        Intent intent = new Intent(getContext(), SearchActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
                                    }
                                }).show();
                    }

                } else if (statusCode.equalsIgnoreCase("0019")) {
                    httpRequest.progress.dismiss();
                    String rsult = jsonObject.getString("result");
                    if (rsult == null) {
                        rsult = "Booking can't be cancel";
                    }
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
                        @Override
                        public void onCancelConfirmation() {
                            super.onCancelConfirmation();
                        }
                    };
                    bmaAlertDialog.show(rsult);

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

    private void getOTP() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = this;
        actionID = "sendCancelRescheduleOTP";
        httpRequest.execute("sendCancelRescheduleOTP", this.bookingID,"CANCEL");
    }

    View selectedView;

    @Override
    public void onClick(View v) {
        this.selectedView = v;
        if(isSelectAllField()) {
            processCancelBooking(v);
            //getOTP();
        }


    }
    BMAAlertDialog bmaAlertDialog;
    private void showOTPDialog() {
         bmaAlertDialog = new BMAAlertDialog(getContext(), true, false) {

            @Override
            public void onConfirmation(String inputValue) {
                super.onConfirmation(inputValue);
                if(inputValue.trim().length()>0){
                    if (inputValue.equalsIgnoreCase(otp)) {
                        bmaAlertDialog.dismiss();
                        processCancelBooking(selectedView);
                    }else{
                        Toast.makeText(getContext(), getString(R.string.correctOTPValidation), Toast.LENGTH_SHORT).show();
                    }
                }else {
//                if (inputValue.trim().length() == 0) {
//                    Toast.makeText(getContext(), "Please enter OTP", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (inputValue.equalsIgnoreCase(otp)) {
//                    //Log.d("aaaaa","selected Otp = "+inputValue+"         OTP ="+otp);
                    // cancel heat
                    bmaAlertDialog.dismiss();
                    processCancelBooking(selectedView);
                }

//                } else {
//                    Toast.makeText(getContext(), "Wrong OTP entered", Toast.LENGTH_SHORT).show();
//                }

            }
        };
        bmaAlertDialog.show();
        bmaAlertDialog.setTitle("Enter OTP");
        // bmaAlertDialog.fillInputField(totalAmount+"");
        bmaAlertDialog.showInputField("Enter OTP");
    }
}
