package com.around.engineerbuddy;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class BMAApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

           // FirebaseCrash.report("");
            FirebaseCrash.log("");
        Log.d("aaaaa","BMAApplication");
        new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                FirebaseCrash.report(e);
                FirebaseCrash.log(e.toString());

                Log.d("aaaaa","Crash in BMAApplication = "+e.toString());

            }
        };

    }
}
