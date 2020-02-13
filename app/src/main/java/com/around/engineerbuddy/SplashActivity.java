package com.around.engineerbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.activity.MainActivity;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.helper.ApplicationHelper;
import com.around.engineerbuddy.util.BMAConstants;
import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedPrefs;
    ImageView scooter;
    ImageView building;
    ImageView poles;
    TextView versionCode;
    private static int SPLASH_TIME_OUT = 500;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseCrash.log("Activity created");
        MainActivityHelper.setApplicationHelper(new ApplicationHelper(getApplicationContext()));
//        sharedPrefs = getSharedPreferences(SplashActivity.MyPREFERENCES,
//                Context.MODE_PRIVATE);
        sharedPrefs=MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);

        building = findViewById(R.id.building);
        scooter = findViewById(R.id.scooter);
        poles =  findViewById(R.id.poles);
        try {
            this.versionCode=findViewById(R.id.versionCode);
                 this.versionCode.setText("Version : "+getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.context=this;

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
        new VersionCodeTask().execute();
        //checkUserLogin();



    }
    class VersionCodeTask extends AsyncTask<Void,String,String>{
     //   final String url = "https://androidquery.appspot.com/api/market?app="+getPackageName();

        private String newVersion;

        @Override
        protected String doInBackground(Void... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".IxB2fe .hAyfc:nth-child(4) .htlgb span")
                        .get(0)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }

//LP-2576461910426/videocon


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                new android.support.v7.app.AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Network Error")
                        .setMessage("press 'ok' to continue ")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                                new VersionCodeTask().execute();
                            }
                        }).show();
                return;

            }
           // Log.d("aaaaaa","  BEFORE  playversion = "+s);
            Double playStoreVersion=Double.parseDouble(s);
            String playVersion=s;
            String versionCode="";

            try {
                PackageInfo packageInfo=getPackageManager().getPackageInfo(getPackageName(),0);
                versionCode=packageInfo.versionName;
              //  Log.d("aaaaaa","versionNAME NAME = "+packageInfo.versionName);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
           // Log.d("aaaaaa","versionCode = "+versionCode+"    playversion = "+playStoreVersion);

            if(playVersion.equalsIgnoreCase(versionCode)){
                checkUserLogin();
            }else {
                new android.support.v7.app.AlertDialog.Builder(SplashActivity.this)
                        .setTitle("New version available,\nPlease Update.")
                        .setMessage("247Around Engineer buddy")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                               // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

                            }
                        })
//                        .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                               checkUserLogin();
//                            }
//                        }
//                )
                        .show();
            }
//                BMAAlertDialog bmaAlertDialog=new BMAAlertDialog(getApplicationContext(),true,false){
//                    @Override
//                    public void onConfirmation() {
//                        super.onConfirmation();
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
//                    }
//                    };
//                bmaAlertDialog.show("New version available");
//                }
            }

 //Log.d("aaaaaa","versionCode = "+newVersion);       }
    }
    private void checkUserLogin(){
        String phonenumbersp=sharedPrefs.getString("phoneNumber",null);
        String engineerIDSP = sharedPrefs.getString("engineerID",null);
        Log.d("aaaaaa","userLogin un = "+phonenumbersp);
        Log.d("aaaaaa","userLogin ei = "+engineerIDSP);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Log.d("aaaaaa","issharepreef = "+sharedPrefs.contains("isLogin"));
                    if (sharedPrefs.contains("isLogin") && phonenumbersp!=null) {
                        Intent search = new Intent(SplashActivity.this, MainActivity.class);//SearchActivity.class);
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
