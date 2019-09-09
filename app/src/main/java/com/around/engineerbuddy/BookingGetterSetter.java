package com.around.engineerbuddy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.around.engineerbuddy.entity.BMAObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhay on 29/12/17.
 */
@SuppressWarnings("ALL")
@SuppressLint("Registered")
public class BookingGetterSetter extends BMAObject {
    public static final long serialVersionUID = 1L;
    boolean applianceBroken, isDelivered, checkedCancellationReason, repairBooking;
    public String bookingID;
    public String services;
    public String customerName;
    public String primaryContactNo;
    public String address;
    public String current_status;
    public String amountDue;
    public List<BookingGetterSetter> unitList = new ArrayList<BookingGetterSetter>();
    public String brand;
    public String category;
    public String capacity;
    public String partnerID;
    public String serviceID;
    public String modelNumber;
    public String requestType;
    public String unitID;
    public String pod;
    public String priceTags;
    public String customerNetPayable;
    public String serialNo;
    public String serialNoUrl;
    public String cancellationReason;
    public Bitmap serialNoBitmap;
    public String bookingRemarks;

    public BookingGetterSetter(String bookingID,
                               String services,
                               String customerName,
                               String primaryContactNo,
                               String address,
                               String current_status,
                               String amountDue,
                               String bookingRemarks) {

        this.bookingID = bookingID;
        this.services = services;
        this.customerName = customerName;
        this.primaryContactNo = primaryContactNo;
        this.address = address;
        this.current_status = current_status;
        this.amountDue = amountDue;
        this.bookingRemarks = bookingRemarks;

    }

    public BookingGetterSetter(String bookingID,
                               String brand,
                               String partnerID,
                               String serviceID,
                               String category,
                               String capacity,
                               String modelNumber,
                               List<BookingGetterSetter> unitList,
                               String pod,
                               String serialNo,
                               String serialNoUrl,
                               Bitmap serialNoBitmap,
                               boolean repairBooking) {
        this.bookingID = bookingID;
        this.brand = brand;
        this.partnerID = partnerID;
        this.serviceID = serviceID;
        this.category = category;
        this.capacity = capacity;
        this.modelNumber = modelNumber;
        this.unitList = unitList;
        this.pod = pod;
        this.serialNo = serialNo;
        this.serialNoUrl = serialNoUrl;
        this.serialNoBitmap = serialNoBitmap;
        this.repairBooking = repairBooking;
    }

    public BookingGetterSetter(String unitID,
                               String pod,
                               String priceTags,
                               String customerNetPayable,
                               String requestType,
                               boolean isDelivered,
                               boolean applianceBroken) {

        this.unitID = unitID;
        this.pod = pod;
        this.priceTags = priceTags;
        this.customerNetPayable = customerNetPayable;
        this.isDelivered = isDelivered;
        this.applianceBroken = applianceBroken;
        this.requestType = requestType;

    }

    public BookingGetterSetter(String cancellationReason, boolean checkedCancellationReason) {
        this.cancellationReason = cancellationReason;
        this.checkedCancellationReason = checkedCancellationReason;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getServices() {
        return services;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPrimaryContactNo() {
        return primaryContactNo;
    }

    public String getAddress() {
        return address;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public String getAmountDue() {
        return amountDue;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getUnitID() {
        return unitID;
    }

    public String getPod() {
        return pod;
    }

    public String getPriceTags() {
        return priceTags;
    }

    public List<BookingGetterSetter> getUnitList() {
        return unitList;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSerialNoUrl() {
        return serialNoUrl;
    }

    public void setSerialNoUrl(String serialNoUrl) {
        this.serialNoUrl = serialNoUrl;
    }

    public Bitmap getSerialNoBitmap() {
        return serialNoBitmap;
    }

    public void setSerialNoBitmap(Bitmap serialNoBitmap) {
        this.serialNoBitmap = serialNoBitmap;
    }

    public boolean getApplianceBroken() {
        return applianceBroken;
    }

    public void setApplianceBroken(boolean applianceBroken) {
        this.applianceBroken = applianceBroken;
    }

    public boolean getDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public boolean getCheckedCancellationReason() {
        return checkedCancellationReason;
    }

    public void setCheckedCancellationReason(boolean checkedCancellationReason) {
        this.checkedCancellationReason = checkedCancellationReason;
    }

    public String getRequestType() {
        return requestType;
    }

    public boolean getRepairBooking() {
        return repairBooking;
    }

    public String getCustomerNetPayable() {
        return customerNetPayable;
    }

    public String getBookingRemarks(){ return bookingRemarks;}


}

