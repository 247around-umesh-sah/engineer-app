package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMATileView;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUtil;

import org.json.JSONObject;

public class BookingDetailFragment extends BMAFragment implements View.OnClickListener {
    BMATileView tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9;
    EOBooking eoBooking;
    HttpRequest httpRequest;
    Misc misc;
    LinearLayout symptomLayout;
    TextView symptom;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("booking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.booking_detail_fragment, container, false);
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
        this.symptomLayout= this.view.findViewById(R.id.symptomLayout);
        this.symptom= this.view.findViewById(R.id.symptom);

        this.view.findViewById(R.id.bookingDetai_chargellayout).setVisibility(View.VISIBLE);


        name.setText(eoBooking.name);
        address.setText(eoBooking.bookingAddress);
        brandName.setText(eoBooking.applianceBrand);
        applianceName.setText(eoBooking.services);
        capacityOfApliance.setText(eoBooking.applianceCapacity);
        if(eoBooking.applianceCapacity==null || eoBooking.applianceCapacity.length()==0){
            this.view.findViewById(R.id.capacityOfAplianceLayout).setVisibility(View.GONE);
        }
        applianceCategory.setText(eoBooking.applianceCategory);
        //  serviceName.setText(eoBooking.services);
        bookingDate.setText(eoBooking.bookingDate);
        requestType.setText(eoBooking.requestType);
        bookingIdName.setText(eoBooking.bookingID);
        timeSlot.setText(eoBooking.bookingTimeSlot);
        if(this.eoBooking.bookingRemarks!=null && this.eoBooking.bookingRemarks.length()!=0){
            this.symptomLayout.setVisibility(View.VISIBLE);
         this.symptom.setText(this.eoBooking.bookingRemarks);
        }else {
            this.symptomLayout.setVisibility(View.GONE);
        }

        chargeabletext.setText("₹ "+eoBooking.dueAmount);
        this.tile1 = this.view.findViewById(R.id.tile1);
        this.tile2 = this.view.findViewById(R.id.tile2);
        this.tile3 = this.view.findViewById(R.id.tile3);
        this.tile4 = this.view.findViewById(R.id.tile4);
        this.tile5 = this.view.findViewById(R.id.tile5);
        this.tile6 = this.view.findViewById(R.id.tile6);
        this.tile7 = this.view.findViewById(R.id.tile7);
        this.tile8 = this.view.findViewById(R.id.tile8);
        this.tile9 = this.view.findViewById(R.id.tile9);
        this.setTileValue(this.tile9, R.string.cancel_booking, R.drawable.cancel_booking, "Cancel", true);
        this.setTileValue(this.tile5, R.string.comingSoon, R.drawable.brandlogo, "Update", false);
        this.setTileValue(this.tile2, R.string.complete_booking, R.drawable.completing_booking, "Complete", true);

        this.setTileValue(this.tile8, R.string.techsupport, R.drawable.mobile, "Tech Support", false);
        // this.setTileValue(this.tile4, R.string.document, R.drawable.document_file, "Documents", false);
        this.setTileValue(this.tile4, R.string.update_booking, R.drawable.update, "Update", true);
        //this.setTileValue(this.tile6, R.string.techsupport, R.drawable.mobile, "Tech Support", false);
        this.setTileValue(this.tile6, R.string.spareparts, R.drawable.spare_part, "Spare Parts", false);
        // this.setTileValue(this.tile7, R.string.qrcode, R.drawable.scan_booking, "QR-Code", false);
        this.setTileValue(this.tile7, R.string.document, R.drawable.document_file, "Documents", false);
        this.setTileValue(this.tile3, R.string.navigation, R.drawable.navigation_map, "Navigation", false);
        this.setTileValue(this.tile1, R.string.customercall, R.drawable.customer_call, "CustomerCall", true);
//        if(!(eoBooking.requestType.contains("Repair"))){
//            tile6.setOnClickListener(null);
//        }
        this.tile1.setText("Call");
        misc = new Misc(getContext());
      //  this.tile2.setEnabled(eoBooking.complete_allow);
        return this.view;
    }

    private void setTileValue(BMATileView fnTileView, @StringRes int title, @DrawableRes int imgRes, String tag, boolean isVisible) {
        fnTileView.setTitle(title);
        fnTileView.setImageResource(imgRes);
        fnTileView.setTag(tag);
        fnTileView.setVisibility(isVisible);

        fnTileView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tile9:
                if(eoBooking.cancel_allow) {
                    bundle.putString("bookingID", eoBooking.bookingID);
                    this.updateFragment(bundle, new CancelBookingFragment(), getString(R.string.cancelBooking));
                }else{
                    Toast.makeText(getContext(), "You have already cancelled this booking", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.tile5:
//                bundle.putParcelable("eoBooking", eoBooking);
//                this.updateFragment(bundle, new UpdateBookingFragment(), "Update Booking");
                break;
            case R.id.tile6:
                if(canSparePartOrder()){
                    openEditWarranty();
                }else{
                    showSpareRequestValidationMessage(this.eoBooking.message);
                }
              //  csanSparePartOrder();
//                if(!(eoBooking.requestType.contains("Repair"))){
//                   Toast.makeText(getContext(),getString(R.string.requestPartValidation),Toast.LENGTH_SHORT).show();
//                   return;
//                }
//                bundle.putParcelable("eoBooking", eoBooking);
//                this.updateFragment(bundle, new SparePartsOrderFragment(), getString(R.string.spareParts));
                break;
            case R.id.tile2:
//                if(!(this.eoBooking.pincode.equalsIgnoreCase(misc.getLocationPinCode()))){
//                    Toast.makeText(getContext(), "You can not complete booking from out side of booking pincode", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(eoBooking.complete_allow) {
                    bundle.putParcelable("eoBooking", eoBooking);
                    this.updateFragment(bundle, new CompleteBookingCategoryFragment(), getString(R.string.completeBooking));
                }else{
                    Toast.makeText(getContext(), "You are not allow to complete this booking", Toast.LENGTH_SHORT).show();
//
                }
                break;
            case R.id.tile4:
                //bundle.putString(BMAConstants.HEADER_TXT, "New Appointment");

                if(eoBooking.service_center_booking_action_status!=null && !eoBooking.service_center_booking_action_status.equalsIgnoreCase("InProcess")) {
                    NewAppointmentFragment newAppointmentFragment = new NewAppointmentFragment();
                    bundle.putParcelable("eoBooking", eoBooking);
                    bundle.putString("bookingID", eoBooking.bookingID);
                    this.updateFragment(bundle, newAppointmentFragment, getString(R.string.newAppointment));
                }else{
                    Toast.makeText(getContext(), "You can not Reshedule this booking due to "+eoBooking.service_center_booking_action_status, Toast.LENGTH_SHORT).show();

                }
//                bundle.putString("bookingID",eoBooking.bookingID);
//                this.updateFragment(bundle, new DocumentCategory(), "Documents");
                break;
            case R.id.tile8:
                bundle.putParcelable("eoBooking", eoBooking);
                bundle.putBoolean("isTechSupoort", true);
                this.updateFragment(bundle, new TechSupportFragment(), getString(R.string.techSupport));
                break;
            case R.id.tile7:
                bundle.putString("bookingID", eoBooking.bookingID);
                this.updateFragment(bundle, new DocumentCategory(), getString(R.string.Documents));
                break;
            case R.id.tile3:
                bundle.putParcelable("eoBooking", eoBooking);
                this.updateFragment(bundle, new BMAMapFragment(), "");// removed By nitin Sir // "Geo Location");
                break;
            case R.id.tile1:
                bundle.putBoolean("isTechSupoort", false);
                bundle.putParcelable("eoBooking", eoBooking);
                this.updateFragment(bundle, new TechSupportFragment(), getString(R.string.customerCall));
                break;


        }
    }

    public void updateFragment(Bundle bundle, Fragment fragment, String headerText) {
        this.updateFragment(bundle, fragment, headerText, null);
    }

    public void updateFragment(Bundle bundle, Fragment fragment, String headerText, Integer imageDrawable) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true, imageDrawable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaaaaa","BookingDEtail onActivityResult = "+data);
        if (data == null) {
            return;
        }
        Log.d("aaaaaa","BookingDEtail onActivityResult completed  = "+ data.getBooleanExtra("completed",false));
        if(data.getBooleanExtra("isCancelled",false) || data.getBooleanExtra("completed",false)){
            Log.d("aaaaaa","IF inside BookingDEtail onActivityResult = ");
            Intent intent=new Intent();
            intent.putExtra("isCancelled",true);
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        }
    }

    private boolean canSparePartOrder(){
        if(this.eoBooking.spare_eligibility!=null && this.eoBooking.spare_eligibility==0){
            return false;
        }
            return true;

    }

    private void csanSparePartOrder(){

        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = BookingDetailFragment.this;
       // this.actionID = "engineerSparePartOrder";
        httpRequest.execute("checkSparePartsOrder", eoBooking.bookingID);
    }
    //0= not,1=yes
    @Override
    public void processFinish(String response) {
        // super.processFinish(response);
        httpRequest.progress.dismiss();
        Log.d("aaaaaa","response = "+response);
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {

                //    =  (new JSONObject(jsonObject.get("response").toString()));
                    JSONObject responseObject=jsonObject.getJSONObject("response");
                    String spareFlag= responseObject.getString("spare_flag");
                    if(spareFlag!=null && spareFlag.equalsIgnoreCase("0")){

                       showSpareRequestValidationMessage(responseObject.get("message").toString());
                       // Toast.makeText(getContext(),getString(R.string.requestPartValidation),Toast.LENGTH_SHORT).show();
                    }else {
                       openEditWarranty();
                       // this.updateFragment(bundle, new SparePartsOrderFragment(), getString(R.string.spareParts));
                    }
                }
            } catch (Exception e) {

            }
        }
    }
    private void openEditWarranty(){
        Bundle bundle=new Bundle();
        bundle.putParcelable("eoBooking", eoBooking);
        this.updateFragment(bundle, new EditWarrantyBooking(),"Check Warranty");
    }
    private void showSpareRequestValidationMessage(String message){
        BMAAlertDialog bmaAlertDialog=new BMAAlertDialog(getContext(),false,true){
            @Override
            public void onDefault() {
                super.onDefault();

            }
        };
        bmaAlertDialog.show(message);
    }


    private void setEOBookingTileView(){
        //this.view.findViewById(R.id.partName).setBackgroundColor(getResources().getD);

    }



}
