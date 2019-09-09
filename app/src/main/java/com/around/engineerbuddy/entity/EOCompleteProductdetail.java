package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EOCompleteProductdetail extends BMAObject {
    @SerializedName("model_data")
    public ArrayList<EOModelNumber>modelList = new ArrayList<>();

    @SerializedName("booking_unit_details")
    public EOCompleteBookingProductUnit bookingUnitDetails;

    public ArrayList<EOCompleteProductQuantity>prices = new ArrayList<>();

    EOCompleteProductQuantity eoCompleteProductQuantity;
    @SerializedName("spare_parts")
    public EOSpareParts eoSpareParts;

    public EOCompleteBookingProductUnit getbookingProductUnit(){
        if(bookingUnitDetails==null) {
            bookingUnitDetails = new EOCompleteBookingProductUnit();
        }
        return bookingUnitDetails;

    }
    public EOCompleteProductQuantity eoCompleteProductQuantity(){
        if(eoCompleteProductQuantity==null) {
            eoCompleteProductQuantity = new EOCompleteProductQuantity();
        }
        return eoCompleteProductQuantity;

    }
}
