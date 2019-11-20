package com.around.engineerbuddy.entity;

import java.util.ArrayList;

public class EOCompletedBookingDetail extends BMAObject {

    public ArrayList<EOCompletedCancelledBookingDetail> booking_details = new ArrayList();
    public ArrayList<EOSpareConsumptionStatus> consumption_details = new ArrayList();
}
