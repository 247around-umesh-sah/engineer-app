package com.around.technician;

import android.app.Application;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhay on 29/12/17.
 */
public class BookingGetterSetter extends Application implements Serializable {
    private static final long serialVersionUID = 1L;

    String bookingID, services, customerName,
            primaryContactNo, address,
            current_status, amountDue;
    List<BookingGetterSetter> unitList = new ArrayList<BookingGetterSetter>();

    String brand, category, capacity, partnerID, serviceID, modelNumber, requestType;

    String unitID, pod, priceTags, customerNetPayable, serialNo, serialNoUrl, cancellationReason;

    Bitmap serialNoBitmap;

    boolean applianceBroken, isDelivered, checkedCancellationReason, repairBooking;

    public BookingGetterSetter(String bookingID,
                               String services,
                               String customerName,
                               String primaryContactNo,
                               String address,
                               String current_status,
                               String amountDue) {

        this.bookingID = bookingID;
        this.services = services;
        this.customerName = customerName;
        this.primaryContactNo = primaryContactNo;
        this.address = address;
        this.current_status = current_status;
        this.amountDue = amountDue;

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

    public String getCustomerNetPayable(){ return customerNetPayable;}


}

