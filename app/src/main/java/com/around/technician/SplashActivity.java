package com.around.technician;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefs = getSharedPreferences(SplashActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    /**
                     * IF user has logged in then we will open SearchActivity otherwise login page.
                     */
                    if (sharedPrefs.contains("isLogin")) {
                        Intent login = new Intent(SplashActivity.this, SearchActivity.class);
                        startActivity(login);
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
