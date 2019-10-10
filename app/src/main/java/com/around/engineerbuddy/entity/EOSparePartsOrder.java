package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EOSparePartsOrder extends BMAObject {
   public ArrayList<EOModelNumber>modelNumberList=new ArrayList<>();
    public ArrayList<EOPartType>partTypeList=new ArrayList<>();
    public boolean getPartOnModel;
    @SerializedName("spare_parts")
    public EOSpareParts eoSpareParts;


}
