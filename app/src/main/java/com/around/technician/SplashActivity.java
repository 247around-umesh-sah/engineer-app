package com.around.technician;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.firebase.crash.FirebaseCrash;

public class SplashActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedPrefs;
    ImageView scooter;
    ImageView building;
    ImageView poles;
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseCrash.log("Activity created");
        sharedPrefs = getSharedPreferences(SplashActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        building = (ImageView) findViewById(R.id.building);
        scooter = (ImageView) findViewById(R.id.scooter);
        poles = (ImageView) findViewById(R.id.poles);

        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0.0f, 4.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(100);  // animation duration
        animation.setRepeatCount(1000);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);


        scooter.startAnimation(animation);
        TranslateAnimation animation3 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, (float)
                1.0, Animation.RELATIVE_TO_PARENT, (float) -0.2, Animation.RELATIVE_TO_PARENT, (float)
                0.0, Animation.RELATIVE_TO_PARENT, (float) 0.0);
        animation3.setDuration(2000);  // animation duration
        animation3.setRepeatCount(SPLASH_TIME_OUT);  // animation repeat count
        animation3.setRepeatMode(1);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);

        //imgLogo.startAnimation(animation);  // start animation
        poles.startAnimation(animation3);

        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                poles.setVisibility(poles.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if (sharedPrefs.contains("isLogin")) {
                        Intent search = new Intent(SplashActivity.this, SearchActivity.class);
                        startActivity(search);
                        finish();
                    } else {
                        Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(login);
                        finish();
                    }
                }
            }
        };
        timerThread.start();

    }
}
