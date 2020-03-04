package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

public class EODocumentType extends BMAObject{
    @SerializedName("document_type")
    public String documentType;
    @SerializedName("document_description")
    public String documentDescription;
    public String file;
    public String brand;
    public String request_type;
}
