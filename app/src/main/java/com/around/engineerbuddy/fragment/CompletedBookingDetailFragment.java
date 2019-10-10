package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.entity.EOBooking;

public class CompletedBookingDetailFragment extends BMAFragment {

    EOBooking eoBooking;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = this.getArguments().getParcelable("eoBooking");



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.booking_detail_fragment, container, false);
        this.view.findViewById(R.id.tileRowLayout).setVisibility(View.GONE);
        this.dataToView();
        return this.view;
    }
    private void dataToView(){
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
        chargeabletext.setText("₹ "+eoBooking.dueAmount);
    }
}
