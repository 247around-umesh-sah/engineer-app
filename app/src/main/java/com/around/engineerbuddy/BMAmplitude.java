package com.around.engineerbuddy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amplitude.api.Amplitude;
import com.around.engineerbuddy.helper.ApplicationHelper;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class BMAmplitude {

    static  String trackingUserID;
    static  String userID;


    public static void initializeAmplitude(Context context, Application application) {

        ApplicationHelper applicationHelper = MainActivityHelper.applicationHelper();
        SharedPreferences sharedPrefs=null;
        if (applicationHelper != null) {
            sharedPrefs = applicationHelper.getSharedPrefrences(BMAConstants.LOGIN_INFO);
        }
        ////Uncomment when release upgrading on live
        if (sharedPrefs != null) {
//            trackingUserID = sharedPrefs.getString("phoneNumber", null);
//            Amplitude.getInstance().initialize(context, "b39da78062e88c864e971422bbe0fa2f", trackingUserID).enableForegroundTracking(application);
        }
    }
    public static void saveUserAction(String pageTitle,String clickOnAction){


        JSONObject eventProperties = new JSONObject();
        if(trackingUserID!=null) {
            try {
                eventProperties.put("EngineerLoginID", trackingUserID);
                eventProperties.put("pageTitle", pageTitle);

                eventProperties.put("ActionClickOn", clickOnAction);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//       ///// Amplitude.getInstance().setUserId("7820939469");
//     /////   Amplitude.getInstance().setDeviceId("12345678912345678912345asdfghsdf");
            //////// // Amplitude.getInstance().setUserProperties(eventProperties);

            //Uncomment when we release upgrading on live
//            Amplitude.getInstance().logEvent(eventProperties.toString());
//            Amplitude.getInstance().setUserProperties(eventProperties);

        }

    }
}
