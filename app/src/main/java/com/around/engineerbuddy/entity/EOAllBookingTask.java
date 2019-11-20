package com.around.engineerbuddy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EOAllBookingTask extends BMAObject {

    public int missedBookingsCount;
    public int tomorrowBookingsCount;
    public ArrayList<EOBooking> todayMorningBooking=new ArrayList<>();
    public ArrayList<EOBooking> todayAfternoonBooking=new ArrayList<>();
    public ArrayList<EOBooking> todayEveningBooking=new ArrayList<>();
    public String rating;
    public String incentive;
     @SerializedName("same_day_closure")
    public String sameDayClosure;



    public String getBookingCount(int bookingCount){
        return bookingCount>=10 ? bookingCount+"" : "0"+bookingCount;

    }
}
