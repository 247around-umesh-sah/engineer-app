package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.entity.BookingInfo;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompletedBookingDetail;
import com.around.engineerbuddy.entity.EOCompletedCancelledBookingDetail;
import com.around.engineerbuddy.entity.EOSpareConsumptionStatus;
import com.around.engineerbuddy.entity.EoWrongPart;

import org.json.JSONException;
import org.json.JSONObject;

public class CompletedBookingDetailFragment extends BMAFragment {

    EOBooking eoBooking;
    HttpRequest httpRequest;
    LinearLayout completedbookingDetailLayout, completedConsumptionbookingDetailLayout;
    private boolean isCancelledBooking;
    EOCompletedBookingDetail eoCompletedBookingDetail;
    LayoutInflater inflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = this.getArguments().getParcelable("eoBooking");
        this.isCancelledBooking = this.getArguments().getBoolean("isCancelledBooking");


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        //getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.view = inflater.inflate(R.layout.booking_detail_fragment, container, false);
        this.view.findViewById(R.id.tileRowLayout).setVisibility(View.GONE);
        this.completedbookingDetailLayout = this.view.findViewById(R.id.completedbookingDetailLayout);
        this.completedConsumptionbookingDetailLayout = this.view.findViewById(R.id.completedConsumptionbookingDetailLayout);
        //   this.dataToView();
        // this.loadData();
        this.getRequest();
        return this.view;
    }

    private void dataToView() {
        TextView name = this.view.findViewById(R.id.name);
        TextView address = this.view.findViewById(R.id.address);
        TextView brandName = this.view.findViewById(R.id.brandName);
        // TextView serviceName=this.view.findViewById(R.id.serviceName);
        TextView bookingDate = this.view.findViewById(R.id.bookingDate);
        TextView requestType = this.view.findViewById(R.id.requestType);
        TextView bookingIdName = this.view.findViewById(R.id.bookingIdName);
        TextView capacityOfApliance = this.view.findViewById(R.id.capacityOfApliance);
        TextView applianceCategory = this.view.findViewById(R.id.applianceCatg);
        TextView applianceName = this.view.findViewById(R.id.applianceName);
        TextView timeSlot = this.view.findViewById(R.id.timeSlot);
        TextView chargeabletext = this.view.findViewById(R.id.chargeabletext);
        this.view.findViewById(R.id.bookingDetai_chargellayout).setVisibility(View.VISIBLE);
      //  this.view.findViewById(R.id.symptomLayout).setVisibility(View.GONE);
//        if (this.eoCompletedBookingDetail == null) {
//            return;
//        }

        name.setText(eoBooking.name);
        EOCompletedCancelledBookingDetail eoCompletedCancelledBookingDetail = this.eoCompletedBookingDetail.booking_details.get(0);
        address.setText(eoCompletedCancelledBookingDetail.booking_address);
        brandName.setText(eoCompletedCancelledBookingDetail.appliance_brand);
        applianceName.setText(eoCompletedCancelledBookingDetail.services);
        capacityOfApliance.setText(eoCompletedCancelledBookingDetail.appliance_capacity);
        if (eoCompletedCancelledBookingDetail.appliance_capacity == null || eoCompletedCancelledBookingDetail.appliance_capacity.length() == 0) {
            this.view.findViewById(R.id.capacityOfAplianceLayout).setVisibility(View.GONE);
        }
        applianceCategory.setText(eoCompletedCancelledBookingDetail.appliance_category);
        //  serviceName.setText(eoBooking.services);
        bookingDate.setText(eoBooking.bookingDate);
        requestType.setText(eoBooking.requestType);
        bookingIdName.setText(eoBooking.bookingID);
        timeSlot.setText(eoCompletedCancelledBookingDetail.booking_timeslot);
        if (eoCompletedCancelledBookingDetail.amount_paid == null) {
            eoCompletedCancelledBookingDetail.amount_paid = "0";
        }
        //this.view.findViewById(R.id.amountChargeLayout).setVisibility(View.GONE);
        chargeabletext.setText("₹ " + eoBooking.dueAmount);
          ImageView symptomIcon=this.view.findViewById(R.id.symptomIcon);
          symptomIcon.setBackground(getResources().getDrawable(R.drawable.chargeable));
          TextView customerPaidAmount=this.view.findViewById(R.id.symptom);
          if(eoCompletedCancelledBookingDetail!=null || eoCompletedCancelledBookingDetail.amount_paid!=null) {
              this.view.findViewById(R.id.symptomLayout).setVisibility(View.VISIBLE);
              customerPaidAmount.setText("paid ₹ " + eoCompletedCancelledBookingDetail.amount_paid);
          }else {
              this.view.findViewById(R.id.symptomLayout).setVisibility(View.GONE);
          }
        this.loadData(eoCompletedCancelledBookingDetail);
    }

    private void getRequest() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = CompletedBookingDetailFragment.this;

        String booking_status = isCancelledBooking ? "Cancelled" : "Completed";
        httpRequest.execute("getBookingDetails", eoBooking.bookingID, booking_status);

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
                    // wrongPartList = BMAGson.store().getList(EoWrongPart.class, jsonObject.getJSONObject("response").getString("wrong_part_list"));


                    this.eoCompletedBookingDetail = BMAGson.store().getObject(EOCompletedBookingDetail.class, res);
                    this.dataToView();

//                    if (this.bookingInfo != null) {
//                        this.dataToView();
//                        noDataToDisplayLayout.setVisibility(View.GONE);
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

    private void loadData(EOCompletedCancelledBookingDetail eoCompletedCancelledBookingDetail1) {
        if (eoCompletedBookingDetail == null) {
            return;
        }
        if (isCancelledBooking) {
            this.view.findViewById(R.id.cancelLayout).setVisibility(View.VISIBLE);
            TextView cancelReason = this.view.findViewById(R.id.cancelReason);
            cancelReason.setText(eoCompletedCancelledBookingDetail1.cancellation_reason);
            TextView cancelRemarks = this.view.findViewById(R.id.cancelRemarks);
            cancelRemarks.setText(eoCompletedCancelledBookingDetail1.cancellation_remark);
            return;
        }
        int bookingDetailCounter=1;
        for(EOCompletedCancelledBookingDetail eoCompletedCancelledBookingDetail : this.eoCompletedBookingDetail.booking_details) {
            View childView = inflater.inflate(R.layout.completedbookingdetailayout, null, false);
            CheckBox productBorkencheckbox = childView.findViewById(R.id.productBorkencheckbox);
            LinearLayout modelLayout=childView.findViewById(R.id.modelLayout);
            LinearLayout enterSerialNoLayout=childView.findViewById(R.id.enterSerialNoLayout);
            LinearLayout serviceCatgLayout=childView.findViewById(R.id.serviceCatgLayout);
           // LinearLayout amountDueLayout=childView.findViewById(R.id.amountDueLayout);

            TextView selectModel = childView.findViewById(R.id.selectModel);
            EditText enterSerialNumber = childView.findViewById(R.id.enterSerialNumber);
            TextView selectServiceCatg = childView.findViewById(R.id.selectServiceCatg);
            TextView basichChargeEdittext = childView.findViewById(R.id.basichChargeEdittext);
            TextView additionalChargeEdittext = childView.findViewById(R.id.additionalChargeEdittext);
            TextView partsCostEdittext = childView.findViewById(R.id.partsCostEdittext);
            TextView selectSympton = childView.findViewById(R.id.selectSympton);
            TextView selectDefect = childView.findViewById(R.id.selectDefect);
            TextView selectSolution = childView.findViewById(R.id.selectSolution);
            ///
            LinearLayout symptomLayout = childView.findViewById(R.id.symptomLayout);
            LinearLayout defectLayout = childView.findViewById(R.id.defectLayout);
            LinearLayout solutionLayout = childView.findViewById(R.id.solutionLayout);
            LinearLayout symptomRemarksLayout = childView.findViewById(R.id.symptomRemarksLayout);
            TextView remarks = childView.findViewById(R.id.remarks);

            symptomLayout.setVisibility(View.GONE);
            defectLayout.setVisibility(View.GONE);
            solutionLayout.setVisibility(View.GONE);
            symptomRemarksLayout.setVisibility(View.GONE);




            //  TextView amountDue = childView.findViewById(R.id.amountDue);
            childView.findViewById(R.id.amountDueLayout).setVisibility(View.GONE);
            RadioButton radioComplete = childView.findViewById(R.id.radioComplete);
            RadioButton radioNotComplete = childView.findViewById(R.id.radioNotComplete);
            enterSerialNumber.setEnabled(false);
            if(bookingDetailCounter>1){
                productBorkencheckbox.setVisibility(View.GONE);
                modelLayout.setVisibility(View.GONE);
                enterSerialNoLayout.setVisibility(View.GONE);
               // serviceCatgLayout.setVisibility(View.GONE);
            }
            productBorkencheckbox.setEnabled(false);
            productBorkencheckbox.setChecked(eoCompletedCancelledBookingDetail.is_broken!=null && eoCompletedCancelledBookingDetail.is_broken.equalsIgnoreCase("1") ? true : false);
            if (eoCompletedCancelledBookingDetail.booking_status.equalsIgnoreCase("1")) {
                radioComplete.setChecked(true);
                radioNotComplete.setChecked(false);
            } else {
                radioComplete.setChecked(false);
                radioNotComplete.setChecked(true);
            }
            radioComplete.setEnabled(false);
            radioNotComplete.setEnabled(false);

            if(bookingDetailCounter==1) {
                selectModel.setText(eoCompletedCancelledBookingDetail.model_number);
                enterSerialNumber.setText(eoCompletedCancelledBookingDetail.serial_number);
                 selectSympton .setText(eoCompletedCancelledBookingDetail.symptom!=null ? eoCompletedCancelledBookingDetail.symptom :"default");
                 selectDefect .setText(eoCompletedCancelledBookingDetail.defect!=null ? eoCompletedCancelledBookingDetail.defect :"default");
                 selectSolution.setText(eoCompletedCancelledBookingDetail.technical_solution!=null ? eoCompletedCancelledBookingDetail.technical_solution : "default");
                remarks.setText(eoCompletedCancelledBookingDetail.closing_remark);
                symptomLayout.setVisibility(View.VISIBLE);
                defectLayout.setVisibility(View.VISIBLE);
                solutionLayout.setVisibility(View.VISIBLE);
                symptomRemarksLayout.setVisibility(View.VISIBLE);

            }
            selectServiceCatg.setText(eoCompletedCancelledBookingDetail.request_type);
            basichChargeEdittext.setText("₹ "+eoCompletedCancelledBookingDetail.service_charge);
            additionalChargeEdittext.setText("₹ "+eoCompletedCancelledBookingDetail.additional_service_charge);
            partsCostEdittext.setText("₹ "+eoCompletedCancelledBookingDetail.parts_cost);
            // amountDue.setText(eoCompletedCancelledBookingDetail.amount_paid);
            //amount_paid

            this.completedbookingDetailLayout.addView(childView);
            bookingDetailCounter++;
        }

        addConsumptionLayout();

    }

    private void addConsumptionLayout() {
        for (EOSpareConsumptionStatus eoSpareConsumptionStatus : eoCompletedBookingDetail.consumption_details) {
            View childView = inflater.inflate(R.layout.spare_part_consumption_row, null, false);
            TextView selectConsumptionReason = childView.findViewById(R.id.selectConsumptionReason);
            selectConsumptionReason.setHint("");
            selectConsumptionReason.setText(eoSpareConsumptionStatus.consumed_status);
            childView.findViewById(R.id.partNameDropDownIcon).setVisibility(View.GONE);
            LinearLayout wrongPartLayout = childView.findViewById(R.id.wrongPartLayout);
            TextView partNumber = childView.findViewById(R.id.partNumber);
            TextView partName = childView.findViewById(R.id.partName);
            TextView partType = childView.findViewById(R.id.partType);
            TextView status = childView.findViewById(R.id.status);

            partNumber.setText(eoSpareConsumptionStatus.spare_part_number);
            partName.setText(eoSpareConsumptionStatus.spare_parts_requested);
            partType.setText(eoSpareConsumptionStatus.spare_parts_requested_type);
            status.setText(eoSpareConsumptionStatus.spare_status);

            if (eoSpareConsumptionStatus.consumed_status!=null && eoSpareConsumptionStatus.consumed_status.equalsIgnoreCase("Wrong part received")) {
                wrongPartLayout.setVisibility(View.VISIBLE);
                childView.findViewById(R.id.wrongPartDropDownIcon).setVisibility(View.GONE);
                childView.findViewById(R.id.wrongPartNumberlayout).setVisibility(View.GONE);

                EditText selectwrongtpart = childView.findViewById(R.id.selectwrongtpart);
                selectwrongtpart.setEnabled(false);
                selectwrongtpart.setText(eoSpareConsumptionStatus.wrong_part_name);

//                TextView wrongPartNumber = childView.findViewById(R.id.wrongPartNumber);
//                wrongPartNumber.setVisibility(View.GONE);
                // wrongPartNumber.setText("");
                EditText problemdescriptionedittext = childView.findViewById(R.id.Problemdescriptionedittext);
                problemdescriptionedittext.setEnabled(false);
                problemdescriptionedittext.setText(eoSpareConsumptionStatus.wrong_part_remarks);
            }

            //   TextView selectConsumptionReason= childView.findViewById(R.id.selectConsumptionReason);
            this.completedConsumptionbookingDetailLayout.addView(childView);
        }
    }
}
