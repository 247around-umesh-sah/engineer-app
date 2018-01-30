package com.around.technician;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhay on 28/12/17.
 */
public class Misc {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Activity activity;
    Context context;

    public Misc(Context context) {

        this.context = context;
        activity = (Activity) context;

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

        int writeStorage = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
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

    public void showDialog(String title, String description) {
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
}
