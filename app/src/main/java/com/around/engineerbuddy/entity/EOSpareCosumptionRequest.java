package com.around.engineerbuddy.entity;


// This entity class denote spare related data key value to parse into json Object
public class EOSpareCosumptionRequest extends BMAObject {
   public String spare_id;
     public String consumed_spare_status_id;
    public String  consumed_spare_tag;
    public String part_name;
    public String inventory_id;
    public String spareConsumptionReason;
    public String remarks;
    public int position;
}
