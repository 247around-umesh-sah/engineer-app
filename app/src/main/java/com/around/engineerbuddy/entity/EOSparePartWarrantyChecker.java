package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EOSparePartWarrantyChecker extends BMAObject {
    @SerializedName("model_number_list")
    public ArrayList<EOModelNumber> modelList = new ArrayList<>();

    @SerializedName("booking_details")
    public EOWarrantyCheckerBookingDetail eoWarrantyCheckerBookingDetail;

    @SerializedName("spare_parts")
    public EOSpareParts eoSpareParts;
}
