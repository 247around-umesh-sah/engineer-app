package com.around.engineerbuddy.fragment;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOBookingUpdateReason;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewAppointmentFragment extends BMAFragment implements View.OnClickListener {

    Button submitButton;
   // String bookingID;
    LinearLayout dateLayout,selectReasonlayout;
    TextView morningTimeSlot, afternoonTimeSlot, eveningTimeSlot, timeSlot, selectedDate, selectAppointmentReason;
    BMAFontViewField appointmentDropDownIcon;
    HttpRequest httpRequest;
    EOBooking eoBooking;
    EditText Problemdescriptionedittext;
    String actionID;
    ArrayList<EOBookingUpdateReason> reasonList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      //  this.bookingID = this.getArguments().getString("bookingID");
        this.eoBooking=this.getArguments().getParcelable("eoBooking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.new_appointment_fragment, container, false);
        this.dateLayout = this.view.findViewById(R.id.dateLayout);
        this.selectedDate = this.view.findViewById(R.id.selectedDate);
        this.morningTimeSlot = this.view.findViewById(R.id.morningTimeSlot);
        this.afternoonTimeSlot = this.view.findViewById(R.id.noonTimeSlot);
        this.eveningTimeSlot = this.view.findViewById(R.id.eveningTimeSlot);
        this.timeSlot = this.view.findViewById(R.id.timeSlot);
        this.appointmentDropDownIcon = this.view.findViewById(R.id.appointmentDropDownIcon);
        this.selectAppointmentReason = this.view.findViewById(R.id.selectAppointmentReason);
        this.submitButton=this.view.findViewById(R.id.submitButton);
        this.Problemdescriptionedittext=this.view.findViewById(R.id.Problemdescriptionedittext);
        this.dateLayout.setOnClickListener(this);
        this.morningTimeSlot.setOnClickListener(this);
        this.afternoonTimeSlot.setOnClickListener(this);
        this.eveningTimeSlot.setOnClickListener(this);
        this.selectReasonlayout=this.view.findViewById(R.id.selectReasonlayout);
        this.selectReasonlayout.setOnClickListener(this);
        this.dateLayout.setVisibility(View.GONE);
        this.submitButton.setOnClickListener(this);
        this.setBackgroundToView(morningTimeSlot, R.color.skybluegreen);
        getRequest();
        return this.view;
    }


    private void getRequest() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = NewAppointmentFragment.this;
        actionID="updateBookingReasons";
        httpRequest.execute(actionID, this.eoBooking.bookingID);
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
                    if(actionID.equalsIgnoreCase("updateBookingByEngineer")){
                        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext()) {


                            @Override
                            public void onDefault() {
                                super.onDefault();
                                Intent intent= new Intent();
                                intent.putExtra("completed",true);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, new Intent());
                                getFragmentManager().popBackStack();
                            }


                        };
                        bmaAlertDialog.show(jsonObject.get("result").toString());
                    }else {

                        String res = (new JSONObject(jsonObject.getString("response"))).getString("internal_status");
                        this.reasonList = BMAGson.store().getList(EOBookingUpdateReason.class, res);
                    }
//                    this.eoDocument = BMAGson.store().getList(EODocument.class, res);
//                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
//                    if (this.eoDocument != null) {
//                        this.setView();
//                        noData.setVisibility(View.GONE);
//                    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateLayout:
                this.openCalender();
                break;
            case R.id.morningTimeSlot:
                setBackgroundToView(v, R.color.skybluegreen);
                timeSlot.setText(morningTimeSlot.getText().toString());
                setBackgroundToView(afternoonTimeSlot, R.color.SilverWhite);
                setBackgroundToView(eveningTimeSlot, R.color.SilverWhite);
                break;
            case R.id.noonTimeSlot:
                setBackgroundToView(v, R.color.skybluegreen);
                timeSlot.setText(afternoonTimeSlot.getText().toString());
                setBackgroundToView(morningTimeSlot, R.color.SilverWhite);
                setBackgroundToView(eveningTimeSlot, R.color.SilverWhite);
                break;
            case R.id.eveningTimeSlot:
                setBackgroundToView(v, R.color.skybluegreen);
                timeSlot.setText(eveningTimeSlot.getText().toString());
                setBackgroundToView(morningTimeSlot, R.color.SilverWhite);
                setBackgroundToView(afternoonTimeSlot, R.color.SilverWhite);
                break;
            case R.id.selectReasonlayout:
                openReasonSelectionPopUp();
                break;
            case R.id.submitButton:
                submitRequest();
                break;

        }
    }

    Calendar mcalendar;
    private int day, month, year;

    private void openCalender() {

        mcalendar = Calendar.getInstance();
        day = mcalendar.get(Calendar.DAY_OF_MONTH);
        year = mcalendar.get(Calendar.YEAR);
        month = mcalendar.get(Calendar.MONTH);


        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                selectedDate.setText(getDate(year, month+1, dayOfMonth));
            }

        };
        DatePickerDialog dpDialog = new DatePickerDialog(getContext(), listener, year, month, day);
       // dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        long now = System.currentTimeMillis() - 1000;
        dpDialog.getDatePicker().setMinDate(now);
        dpDialog.getDatePicker().setMaxDate(now+(1000*60*60*24*14));
        // dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+25920000000l );
        dpDialog.show();

    }

    private String getDate(int year, int month, int dayOfMonth) {
        return (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth) + "-" + (month <= 9 ? "0" + month : month) + "-" + year;
    }

    private void setBackgroundToView(View v, int color) {
        v.setBackgroundColor(BMAUIUtil.getColor(color));

    }
    private void openReasonSelectionPopUp( ) {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getReasonList()) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectAppointmentReason.setText(selectedItems.getDetail1());
                    EOBookingUpdateReason eoBookingUpdateReason= (EOBookingUpdateReason) selectedItems.tag;
                    selectAppointmentReason.setTag(eoBookingUpdateReason);
                    if(eoBookingUpdateReason.calender_flag==1){
                        dateLayout.setVisibility(View.VISIBLE);
                    }else {
                        dateLayout.setVisibility(View.GONE);
                    }

                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectReason));
        bmaSelectionDialog.show();
    }

    private ArrayList<BMAUiEntity> getReasonList() {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.reasonList == null) {
            return bmaUiEntityArrayList;
        }
        for (EOBookingUpdateReason eoBookingUpdateReason : this.reasonList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoBookingUpdateReason.reason);
            bmaUiEntity.tag = eoBookingUpdateReason;
            if (selectAppointmentReason.getTag() != null && ((EOBookingUpdateReason) selectAppointmentReason.getTag()).reason.equalsIgnoreCase(eoBookingUpdateReason.reason)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }
   // HttpRequest httpRequest;
//   {"booking_id":"SY-16565919051030","reason":"Customer asked to reschedule","remark":"test kal","booking_date":"2019-07-18","partner_id":"247010","service_center_id":"1","days":0,"spare_shipped":true}
//    Api key -  updateBookingByEngineer
    private void submitRequest(){
        if(!isSelectAllFields()){
            return;
        }

        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = NewAppointmentFragment.this;
        this.actionID="updateBookingByEngineer";
        httpRequest.execute(this.actionID,this.eoBooking.bookingID,selectAppointmentReason.getText().toString().trim(),Problemdescriptionedittext.getText().toString().trim(),selectedDate.getText().toString().trim(), this.eoBooking.partnerID, MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null));

    }
    private boolean isSelectAllFields(){
        if(this.selectAppointmentReason.getText().toString().trim().length()==0){
            Toast.makeText(getContext(),getString(R.string.appointmentReasonValidation),Toast.LENGTH_SHORT).show();
            return false;
        }else if(Problemdescriptionedittext.getText().toString().trim().length()==0){
            Toast.makeText(getContext(),getString(R.string.ramarksValidation),Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            EOBookingUpdateReason eoBookingUpdateReason= (EOBookingUpdateReason) this.selectAppointmentReason.getTag();
            if(eoBookingUpdateReason!=null && eoBookingUpdateReason.calender_flag==1 && selectedDate.getText().toString().trim().length()==0){
                Toast.makeText(getContext(),getString(R.string.apppintmentDateValidation),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
