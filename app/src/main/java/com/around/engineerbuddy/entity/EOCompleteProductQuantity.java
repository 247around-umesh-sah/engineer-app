package com.around.engineerbuddy.entity;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class EOCompleteProductQuantity extends BMAObject {
    public String unit_id;
    public String id;
    public String pod;
    public String invoice_pod;
    public String price_tags;
    public String customer_total;
    public String serial_number_pic;
    public String around_net_payable;
    public String partner_net_payable;
   // public String customer_net_payable;
    public String customer_paid_basic_charges;
    public String customer_paid_extra_charges;
    public String customer_paid_parts;
    public String booking_status;
    public String partner_paid_basic_charges;
    public String product_or_services;
    public String Service;
    public String serial_number;
    public String around_paid_basic_charges;
    @SerializedName("service_category")
    public String serviceCategory;
    @SerializedName("customer_net_payable")
    public String amountDue;

    public boolean isComplete;

    public boolean isNotComplete;
    public Bitmap serialNoPic;
    public Bitmap invoicePic;
    public EOModelNumber selectedModel;
    public String model_number;
    public String model_number_id;
    public String customerBasicharge="0.00";
    public String customerExtraharge="0.00";
    public String customerPartharge="0.00";
    public String serialNumber;
    public String selectedPOD;
    public String newUnitId;
    public String partner_invoice_id;



//if product_or_services = service and customer netPayeable =0;  =>customer basdic charge hide
//   product_or_services=product ->  means  field hide /
//   product_or_services=product && customer net payable =0 then->  means  field show / parts
//    {
//        "id":"1527", "service_category":"Installation & Demo (Free)",
//            "customer_total":"250.00", "partner_net_payable":"250.00",
//            "customer_net_payable":"0.00", "pod":"1", "is_upcountry":"1", "vendor_basic_percentage":
//        "82.600", "around_net_payable":"0.00", "product_or_services":
//        "Service", "upcountry_customer_price":"0", "upcountry_vendor_price":
//        "0", "upcountry_partner_price":"0", "flat_upcountry":"0"
//    }
}
