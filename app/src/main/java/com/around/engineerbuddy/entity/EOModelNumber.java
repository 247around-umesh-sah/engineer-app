package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

public class EOModelNumber extends BMAObject {
    public String id;
    @SerializedName("model_number")
    public String modelNumber;
}
