package com.around.engineerbuddy.entity;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

public class EOBooking extends BMAObject {
    @SerializedName("booking_id")
    public String bookingID;
    public String name;
    @SerializedName("booking_address")
    public String bookingAddress;
    public String state;
    @SerializedName("appliance_brand")
    public String applianceBrand;
    public String services;
    @SerializedName("request_type")
    public String requestType;
    @SerializedName("booking_date")
    public String bookingDate;
    @SerializedName("booking_pincode")
    public String pincode;
    public String distnace="0.0";
    @SerializedName("booking_timeslot")
    public String bookingTimeSlot;
    @SerializedName("tech_support_number")
    public String techSupportNum;
    @SerializedName("amount_due")
    public String dueAmount;
    @SerializedName("appliance_capacity")
    public String applianceCapacity;
    @SerializedName("appliance_category")
    public String applianceCategory;
    @SerializedName("booking_primary_contact_no")
    public String primaryContact;
    @SerializedName("partner_id")
    public String partnerID;
    @SerializedName("service_id")
    public String serviceID;
    @SerializedName("booking_remarks")
    public String bookingRemarks;
    @SerializedName("booking_distance")
    public String bookingDistance;
    @SerializedName("create_date")
    public String bookingCreateDate;
    public String symptom;
    public String booking_close_status;
    public Location destLocation;
    public String current_status;
    public String internal_status;
    public String partner_incentive;
    public String service_center_booking_action_status;
    public boolean allow_reshedule=true;
    public boolean complete_allow=true;
    public boolean cancel_allow=true;
    public String alternate_phone_number;
   // public String booking_remarks;

    public Integer spare_eligibility;
       public String message;


}
