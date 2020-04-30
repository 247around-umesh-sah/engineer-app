package com.around.engineerbuddy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.around.engineerbuddy.LoginActivity;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.SplashActivity;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import java.util.ArrayList;

public class CovidActivity extends AppCompatActivity implements BMARecyclerAdapter.BMAListRowCreator {
    RecyclerView covidRecyclerView;
    Button nextButton;
    boolean isAlreadylogin;
    private SharedPreferences sharedPrefs;
    Runnable timerRunnable;
    Handler timerHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.covid_activity);
        sharedPrefs= MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);
        this.nextButton=findViewById(R.id.next);

        this.covidRecyclerView = findViewById(R.id.covidRecyclerView);
        BMAUIUtil.setBackgroundRect(nextButton,R.color.colorPrimary, R.dimen._50dp);
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(this, this.getCovidMessageList(), covidRecyclerView, this, R.layout.covid_item);
        covidRecyclerView.setHasFixedSize(true);
        covidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        covidRecyclerView.setAdapter(bmaRecyclerAdapter);
        this.nextButton.setVisibility(View.GONE);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setEnabled(false);
                checkUserLogin();
            }
        });

        this.timerHandler = new Handler();
        timerHandler.postDelayed(this.timerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    CovidActivity.this.updateView();
                    CovidActivity.this.timerHandler.postDelayed(CovidActivity.this.timerRunnable, refreshViewInterval()); // run every second
                } catch (Exception e) {
                    CovidActivity.this.stopViewUpdateTimer();
                }
            }

        }, initialRefreshDelay());
      //  nextButton.setVisibility(View.VISIBLE);


    }
    protected void stopViewUpdateTimer() {
        if (this.timerHandler != null) {
            this.timerHandler.removeCallbacks(this.timerRunnable);
            timerHandler = null;
        }
    }
    protected long initialRefreshDelay() {
        return 10000;
    }
    protected long refreshViewInterval() {
        return 10000;
    }

    private void updateView(){
        this.nextButton.setVisibility(View.VISIBLE);
    }

    public ArrayList<String> getCovidMessageList() {
        ArrayList<String> covidMessageList = new ArrayList<>();

        covidMessageList.add("Technician Temperature to be checked before issuing calls.");
        covidMessageList.add("Face mask, Hand Gloves, Hand sanitizer are mandatory.");
        covidMessageList.add("No Sign to be taken on any document.");
        covidMessageList.add(" Call Customer on phone from door. Do not use Door bell.");
        covidMessageList.add("Wash hands before work start, Wash hands after work finishes.");
        covidMessageList.add("If customer looks unwell (Cough, fever) no work to be done just apologise and leave.");
        covidMessageList.add(" Customer to stand at a safe distance 3 feet from technician and helper.");
        covidMessageList.add("Leave all your belongings like helmet etc outside the customer house.");
        covidMessageList.add("Helper to follow same guidelines and technician to make sure all the above for helper.");
        return covidMessageList;
    }

    @Override
    public <T> void createRow(View convertView, T rowObject) {

    }
int counter=1;
    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        String message= (String) rowObject;
        TextView count=itemView.findViewById(R.id.count);
        TextView messagetext=itemView.findViewById(R.id.covidmessage);
        messagetext.setText(message);
        count.setText(position+1+". ");

    }

    @Override
    public <T> void createHeader(View convertView, T rowObject) {

    }
    private void checkUserLogin(){
        stopViewUpdateTimer();
    String phonenumbersp=sharedPrefs.getString("phoneNumber",null);
//        String engineerIDSP = sharedPrefs.getString("engineerID",null);


                   // Log.d("aaaaaa","issharepreef = "+sharedPrefs.contains("isLogin"));
                    if (sharedPrefs.contains("isLogin") && phonenumbersp!=null) {
                        Intent search = new Intent(CovidActivity.this, MainActivity.class);//SearchActivity.class);
                        startActivity(search);
                        finish();
                    } else {
                        Intent login = new Intent(CovidActivity.this, LoginActivity.class);
                        startActivity(login);
                        finish();


            }

    }
}
