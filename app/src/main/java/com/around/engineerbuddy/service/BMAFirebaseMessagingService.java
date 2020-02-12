package com.around.engineerbuddy.service;

import android.content.SharedPreferences;
import android.nfc.tech.NfcBarcode;
import android.util.Log;

import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.helper.ApplicationHelper;
import com.around.engineerbuddy.util.BMAConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BMAFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d("zzzzz","Message  = "+remoteMessage.getNotification().getTitle());
       Log.d("zzzzz","Message Form = "+remoteMessage.getNotification());
        Log.d("zzzzz","Message = "+remoteMessage.getData());
        if(remoteMessage.getData().size()>0) {
            Log.d("zzzzz", "Message data= " + remoteMessage.getData().get("data"));
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("zzzzz","Token = "+s);
        ApplicationHelper applicationHelper=MainActivityHelper.applicationHelper();
        if(applicationHelper!=null){
            SharedPreferences.Editor editor= applicationHelper.getSharedPrefrences(BMAConstants.NOTIF_INFO).edit();
            editor.putString("device_firebase_token", s);
            Log.d("zzzzz","device_firebase_token = "+s);
            editor.commit();
        }
       // SharedPreferences.Editor editor = MainActivityHelper.applicationHelper().getSharedPrefrences().edit();

    }
}
