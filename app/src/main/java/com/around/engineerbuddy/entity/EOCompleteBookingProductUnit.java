package com.around.engineerbuddy.entity;

import java.util.ArrayList;

public class EOCompleteBookingProductUnit extends BMAObject {
    public String appliance_id;
    public String brand;
    public String partner_id;
    public String service_id;
    public String booking_id;
    public String category;
    public String capacity;
    public String model_number;
    public String description;
    public String purchase_date;
    public String sub_order_id;
    public String brand_id;
    public boolean isProductBroken;


    public ArrayList<EOCompleteProductQuantity> quantity = new ArrayList<>();
}
