package com.around.engineerbuddy.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.around.engineerbuddy.ApiResponse;
import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.entity.EOAllBookingTask;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AllTasksFragment extends BMAFragment implements ApiResponse, View.OnClickListener {

    LinearLayout missedBookingLayout,tomorrowBookingLayout,morningLayout,afternoonLayout,evenningLayout;
    TextView missedBookingCount,tomorrowBookingCount,morningBookingCount,afternoonBookingCount,eveningBookingCount,closureTxtProgress,incentiveProgress,feedbackText;

    private HttpRequest httpRequest;
    EOAllBookingTask eoAllBookingTask;
    ProgressBar closureProgressBar,incentiveProgressBar,feedBackProgressBar;
    float batteryLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.all_task_fragment, container, false);
      //  BMAmplitude.saveUserAction("AllTaskFragment","AllTaskFragment");
        this.missedBookingLayout=this.view.findViewById(R.id.missedLayout);
        this.tomorrowBookingLayout=this.view.findViewById(R.id.tomorroBookingLayout);
        this.morningLayout=this.view.findViewById(R.id.morningLayout);
        this.afternoonLayout=this.view.findViewById(R.id.afternoonLayout);
        this.evenningLayout=this.view.findViewById(R.id.eveningLayout);
        this.missedBookingCount=this.view.findViewById(R.id.missedBookingCount);
        this.tomorrowBookingCount=this.view.findViewById(R.id.tomorrowBookingCount);
        this.morningBookingCount=this.view.findViewById(R.id.morningBookingCount);
        this.afternoonBookingCount=this.view.findViewById(R.id.afternoonBookingCount);
        this.eveningBookingCount=this.view.findViewById(R.id.eveningBookingCount);
        this.closureProgressBar=this.view.findViewById(R.id.closureProgressBar);
        this.closureProgressBar.setProgress(0);
     //  this.closureProgressBar.setProgress(82);
        this.incentiveProgressBar=this.view.findViewById(R.id.incentiveProgressBar);
        this.incentiveProgressBar.setProgress(60);
        this.feedBackProgressBar=this.view.findViewById(R.id.feedBackProgressBar);
        this.feedBackProgressBar.setProgress(0);
        this.feedBackProgressBar.setSecondaryProgress(0);
        //this.feedBackProgressBar.setProgress(72);
        this.closureTxtProgress=this.view.findViewById(R.id.closureTxtProgress);
        this.incentiveProgress=this.view.findViewById(R.id.incentiveProgress);
        this.feedbackText=this.view.findViewById(R.id.feedbackText);
       // this.closureTxtProgress.setText("80%");
        this.incentiveProgress.setText("Coming Soon");
       // this.feedbackText.setText("4.7");
       // this.closureProgressBar.getIndeterminateDrawable().setColorFilter(BMAUIUtil.getColor(R.color.orange_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        this.addListner();
      //  loadView();







        return view;
    }
    private void addListner(){
        this.missedBookingLayout.setOnClickListener(this);
        this.tomorrowBookingLayout.setOnClickListener(this);
        this.morningLayout.setOnClickListener(this);
        this.afternoonLayout.setOnClickListener(this);
        this.evenningLayout.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


        if(isVisibleToUser){
//            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//            Intent batteryStatus = getMainActivity().getApplicationContext().registerReceiver(null, iFilter);
//
//            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
//            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
//
//            float batteryPct = level / (float) scale;
//            batteryLevel=batteryPct*100;
//            Log.d("aaaaaa","SetUserVisible Battery Status  = "+batteryLevel);
            httpRequest = new HttpRequest(getMainActivity(), true);
            httpRequest.delegate = AllTasksFragment.this;
          //  Log.d("aaaaaaa"," All Task engineerID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID","abcfegd")+"    service id = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null));
            httpRequest.execute("engineerHomeScreen", MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null),getMainActivity().getPinCode());//,batteryLevel+"");
        }
    }

    private void dataToView(){
        if(this.eoAllBookingTask!=null){
            this.missedBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.missedBookingsCount));
            this.tomorrowBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.tomorrowBookingsCount));
            this.morningBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayMorningBooking.size()));
            this.afternoonBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayAfternoonBooking.size()));
            this.eveningBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayEveningBooking.size()));
            this.feedbackText.setText(eoAllBookingTask.rating);
            this.feedBackProgressBar.setSecondaryProgress(Integer.valueOf(eoAllBookingTask.rating)*20);
            this.closureTxtProgress.setText(eoAllBookingTask.sameDayClosure);
            this.closureProgressBar.setProgress(Integer.valueOf(eoAllBookingTask.sameDayClosure));

        }
    }
    @Override
    public void processFinish(String response) {
       // super.processFinish(response);
        Log.d("bbbbbbb","response  = "+response);


        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                  //  JSONObject responseData = new JSONObject(jsonObject.getString("response"));
                    String res=jsonObject.getString("response");
                     eoAllBookingTask= BMAGson.store().getObject(EOAllBookingTask.class,res);
                     getMainActivity().todayMorningBooking=eoAllBookingTask.todayMorningBooking;
                     getMainActivity().todayAfternoonBooking=eoAllBookingTask.todayAfternoonBooking;
                     getMainActivity().todayEveningBooking=eoAllBookingTask.todayEveningBooking;
                     this.dataToView();


                }
            }
            catch (Exception e){

            }
        }

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        Bundle bundle=new Bundle();

        switch (id){
            case R.id.missedLayout:
                if(this.eoAllBookingTask==null || this.eoAllBookingTask.missedBookingsCount==0){
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.missedBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                BMAmplitude.saveUserAction("MissedBooking","MissedBookingLayout");
                bundle.putString(BMAConstants.HEADER_TXT,getString(R.string.missedBooking));
                bundle.putBoolean("isMissed",true);
                TomorrowFragment tomorrowFragment1=new TomorrowFragment();
                tomorrowFragment1.setArguments(bundle);
                getMainActivity().updateFragment(tomorrowFragment1,true);
                break;
            case R.id.tomorroBookingLayout:
                if( this.eoAllBookingTask==null || this.eoAllBookingTask.tomorrowBookingsCount==0){
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noTomorrowBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                BMAmplitude.saveUserAction("TomorrowBooking","TomorrowBookingLayout");
                bundle.putString(BMAConstants.HEADER_TXT,getString(R.string.tomorrowBooking));
                TomorrowFragment tomorrowFragment=new TomorrowFragment();
                tomorrowFragment.setArguments(bundle);
                getMainActivity().updateFragment(tomorrowFragment,true);
                break;
            case R.id.morningLayout:
                if( this.eoAllBookingTask==null || this.eoAllBookingTask.todayMorningBooking.size()==0){
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noMorningBooking), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                FragmentLoader fragmentLoader=new FragmentLoader();
                fragmentLoader.index=0;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader,false);
               break;
            case R.id.afternoonLayout:
                if( this.eoAllBookingTask==null || this.eoAllBookingTask.todayAfternoonBooking.size()==0){
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noNoonBooking), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                FragmentLoader fragmentLoader1=new FragmentLoader();
                fragmentLoader1.index=1;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader1.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader1,false);
               // getMainActivity().loadtabFragment(1);
                break;
            case R.id.eveningLayout:
                if( this.eoAllBookingTask==null || this.eoAllBookingTask.todayEveningBooking.size()==0){
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noEveningBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                FragmentLoader fragmentLoader2=new FragmentLoader();
                fragmentLoader2.index=2;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader2.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader2,false);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.closureProgressBar.setProgress(0);
        this.feedBackProgressBar.setProgress(0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
