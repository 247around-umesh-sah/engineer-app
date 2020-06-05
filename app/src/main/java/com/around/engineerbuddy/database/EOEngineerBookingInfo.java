//package com.around.engineerbuddy.database;
//
//import android.arch.persistence.room.ColumnInfo;
//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.PrimaryKey;
//
//import com.around.engineerbuddy.entity.BMAObject;
//
//@Entity
//public class EOEngineerBookingInfo extends BMAObject {
//
//    @PrimaryKey(autoGenerate = true)
//    private int id;
//
//    private String engineerLoginID;
//    private String engineerName;
//    private String bookingID;
//    private String engineerLocation;
//    private String actionNameOverBooking;
//
//    private String bookingInfoOfEngineerAction;
//
//    public String getEngineerLoginID() {
//        return engineerLoginID;
//    }
//
//    public void setEngineerLoginID(String engineerLoginID) {
//        this.engineerLoginID = engineerLoginID;
//    }
//
//    public String getEngineerName() {
//        return engineerName;
//    }
//
//    public void setEngineerName(String engineerName) {
//        this.engineerName = engineerName;
//    }
//    public void setBookingInfoOfEngineerAction(String bookingInfoOfEngineerAction) {
//        this.bookingInfoOfEngineerAction = bookingInfoOfEngineerAction;
//    }
//    public String getBookingInfoOfEngineerAction() {
//        return bookingInfoOfEngineerAction;
//    }
//
//    public String getBookingID() {
//        return bookingID;
//    }
//
//    public void setBookingID(String bookingID) {
//        this.bookingID = bookingID;
//    }
//
//    public String getEngineerLocation() {
//        return engineerLocation;
//    }
//
//    public void setEngineerLocation(String engineerLocation) {
//        this.engineerLocation = engineerLocation;
//    }
//
//    public String getActionNameOverBooking() {
//        return actionNameOverBooking;
//    }
//
//    public void setActionNameOverBooking(String actionNameOverBooking) {
//        this.actionNameOverBooking = actionNameOverBooking;
//    }
//
//    public String getTask() {
//        return task;
//    }
//
//    public void setTask(String task) {
//        this.task = task;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public String getFinishBy() {
//        return finishBy;
//    }
//
//    public void setFinishBy(String finishBy) {
//        this.finishBy = finishBy;
//    }
//
//    public boolean isFinished() {
//        return finished;
//    }
//
//    public void setFinished(boolean finished) {
//        this.finished = finished;
//    }
//
//
//    public String getReason() {
//        return reason;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    private String reason;
//
//
//    private String title;
//    private String description;
//
//
////    @ColumnInfo(name = "created_at")
////    @TypeConverters({TimestampConverter.class})
////    private Date createdAt;
////
////    @ColumnInfo(name = "modified_at")
////    @TypeConverters({TimestampConverter.class})
////    private Date modifiedAt;
//
//    private boolean encrypt;
//    private String password;
//    @ColumnInfo(name = "task")
//    private String task;
//
//    @ColumnInfo(name = "desc")
//    private String desc;
//
//    @ColumnInfo(name = "finish_by")
//    private String finishBy;
//
//    @ColumnInfo(name = "finished")
//    private boolean finished;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
////    public Date getCreatedAt() {
////        return createdAt;
////    }
////
////    public void setCreatedAt(Date createdAt) {
////        this.createdAt = createdAt;
////    }
////
////    public Date getModifiedAt() {
////        return modifiedAt;
////    }
////
////    public void setModifiedAt(Date modifiedAt) {
////        this.modifiedAt = modifiedAt;
////    }
//
//    public boolean isEncrypt() {
//        return encrypt;
//    }
//
//    public void setEncrypt(boolean encrypt) {
//        this.encrypt = encrypt;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//}
