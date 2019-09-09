package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EOWarrantyCheckerBookingDetail extends BMAObject {
    @SerializedName("unit_details")
    public ArrayList<EOCompleteBookingProductUnit> unitDetailArray = new ArrayList<>();
    public ArrayList<ArrayList<EOCompleteProductQuantity>> prices = new ArrayList<>();
    @SerializedName("is_repeat")
    public String isRepeat;
}
