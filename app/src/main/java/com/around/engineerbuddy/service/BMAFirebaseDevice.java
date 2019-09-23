package com.around.engineerbuddy.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class BMAFirebaseDevice extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d("zzzzz","onTokenRfresh = "+ FirebaseInstanceId.getInstance().getToken());
    }
}
