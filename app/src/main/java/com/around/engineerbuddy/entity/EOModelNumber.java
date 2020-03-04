package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

// To hold the data key vakue for Model number related
public class EOModelNumber extends BMAObject {
    public String id;
    @SerializedName("model_number")
    public String modelNumber;
}
