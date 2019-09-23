package com.around.engineerbuddy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Misc {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Activity activity;
    Context context;
    ConnectionDetector cd;

    public Misc(Context context) {

        this.context = context;
        activity = (Activity) context;
        cd = new ConnectionDetector(context);

    }

    public boolean checkAndLocationRequestPermissions() {
        int accessFineLocation = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int accessCoarseLocation = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int readPhoneState = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);

        int wifiState = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_WIFI_STATE);

        int readStorage = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStorage = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int accessCamera = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (wifiState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }  if (accessCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public boolean checkPhoneRequestPermissions() {
        int phone = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void NoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String chars = "Internet Connection";
        SpannableString str = new SpannableString(chars);
        str.setSpan(new ForegroundColorSpan(Color.parseColor("#E30D81")), 0, chars.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String message = "Seems you are not connected to the internet.Please connect to internet first";
        SpannableString str1 = new SpannableString(message);
        str1.setSpan(new ForegroundColorSpan(Color.parseColor("#DCDCDC")), 0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(str);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);

    }

//    public void showDialog(String title, String description) {
//        new android.support.v7.app.AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(description)
//                .setCancelable(false)
//                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Whatever...
//                    }
//                }).show();
//    }

    public void showDialog(Integer title, Integer description) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                    }
                }).show();
    }
    public void showDialog(Integer title, String description) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                    }
                }).show();
    }

    /**
     * Get Current location of Mobile
     *
     * @return Address
     */
    public String getLocation() {
        Gson gson = new Gson();
        String arrayString = "";
        if (cd.isConnectingToInternet()) {

            if (checkAndLocationRequestPermissions()) {
                // create class object
                GPSTracker gps = new GPSTracker(context);

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    try {
                        if (!gps.getAddress().getPostalCode().isEmpty()) {

                            Map<String, String> address = new HashMap<>();

                            address.put("pincode", gps.getAddress().getPostalCode());
                            address.put("city", gps.getAddress().getLocality());
                            address.put("address", gps.getAddress().getAddressLine(0));
                            address.put("latitude", gps.getLatitude()+"");
                            address.put("longitude", gps.getLongitude()+"");
                            arrayString = gson.toJson(address);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return arrayString;
    }
    public String getLocationPinCode() {
        String arrayString = "201301";
        if (cd.isConnectingToInternet()) {

            if (checkAndLocationRequestPermissions()) {
                // create class object
                GPSTracker gps = new GPSTracker(context);

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    try {
                        if (!gps.getAddress().getPostalCode().isEmpty()) {

                            Map<String, String> address = new HashMap<>();

                          arrayString= gps.getAddress().getPostalCode();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        return arrayString;
    }
}
