package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//This class hold object list of spare consumption
public class EOConsumption extends BMAObject {
    @SerializedName("spare_parts_details")
   public ArrayList<EOSparePartConsumption>sparePartCosumption=new ArrayList<>();
    @SerializedName("spare_consumed_status")
    public ArrayList<EOSpareConsumptionStatus>spareCosumptionStatus=new ArrayList<>();

}
