package com.around.engineerbuddy.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.ApiResponse;
import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EOAllBookingTask;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EoWrongPart;
import com.around.engineerbuddy.helper.ApplicationHelper;
import com.around.engineerbuddy.util.BMAConstants;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AllTasksFragment extends BMAFragment implements ApiResponse, View.OnClickListener {

    LinearLayout missedBookingLayout, tomorrowBookingLayout, morningLayout, afternoonLayout, evenningLayout;
    TextView missedBookingCount, tomorrowBookingCount, morningBookingCount, afternoonBookingCount, eveningBookingCount, closureTxtProgress, incentiveProgress, feedbackText;
    RelativeLayout incentiveRelativeLayout;
    EditText searchfield;
    ImageView searchIcon;
    private HttpRequest httpRequest;
    EOAllBookingTask eoAllBookingTask;
    ProgressBar closureProgressBar, incentiveProgressBar, feedBackProgressBar;
    SwipeRefreshLayout swipeRefresh;
    float batteryLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.all_task_fragment, container, false);
        //  BMAmplitude.saveUserAction("AllTaskFragment","AllTaskFragment");
        Log.d("aaaaaa","oncreaterow");
        this.swipeRefresh=this.view.findViewById(R.id.swipeRefresh);
        this.missedBookingLayout = this.view.findViewById(R.id.missedLayout);
        this.tomorrowBookingLayout = this.view.findViewById(R.id.tomorroBookingLayout);
        this.morningLayout = this.view.findViewById(R.id.morningLayout);
        this.afternoonLayout = this.view.findViewById(R.id.afternoonLayout);
        this.evenningLayout = this.view.findViewById(R.id.eveningLayout);
        this.missedBookingCount = this.view.findViewById(R.id.missedBookingCount);
        this.tomorrowBookingCount = this.view.findViewById(R.id.tomorrowBookingCount);
        this.morningBookingCount = this.view.findViewById(R.id.morningBookingCount);
        this.afternoonBookingCount = this.view.findViewById(R.id.afternoonBookingCount);
        this.eveningBookingCount = this.view.findViewById(R.id.eveningBookingCount);
        this.closureProgressBar = this.view.findViewById(R.id.closureProgressBar);
        this.incentiveRelativeLayout=this.view.findViewById(R.id.incentiveRelativeLayout);
        this.searchIcon = this.view.findViewById(R.id.searchIcon);
        this.searchfield = this.view.findViewById(R.id.serachField);
        this.closureProgressBar.setProgress(0);

        //  this.closureProgressBar.setProgress(82);
        this.incentiveProgressBar = this.view.findViewById(R.id.incentiveProgressBar);
        this.incentiveProgressBar.setProgress(0);
        this.feedBackProgressBar = this.view.findViewById(R.id.feedBackProgressBar);
        this.feedBackProgressBar.setProgress(0);
        this.feedBackProgressBar.setSecondaryProgress(0);
        //this.feedBackProgressBar.setProgress(72);
        this.closureTxtProgress = this.view.findViewById(R.id.closureTxtProgress);
        this.incentiveProgress = this.view.findViewById(R.id.incentiveProgress);
        this.feedbackText = this.view.findViewById(R.id.feedbackText);
        // this.closureTxtProgress.setText("80%");
        this.incentiveProgress.setText("0");
        getMainActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // this.feedbackText.setText("4.7");
        // this.closureProgressBar.getIndeterminateDrawable().setColorFilter(BMAUIUtil.getColor(R.color.orange_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        this.addListner();
        //  loadView();


        return view;
    }

    private void addListner() {
        this.missedBookingLayout.setOnClickListener(this);
        this.tomorrowBookingLayout.setOnClickListener(this);
        this.morningLayout.setOnClickListener(this);
        this.afternoonLayout.setOnClickListener(this);
        this.evenningLayout.setOnClickListener(this);
        this.searchIcon.setOnClickListener(this);
        this.incentiveRelativeLayout.setOnClickListener(this);
        this.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequest();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


        if (isVisibleToUser) {
//            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//            Intent batteryStatus = getMainActivity().getApplicationContext().registerReceiver(null, iFilter);
//
//            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
//            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
//
//            float batteryPct = level / (float) scale;
//            batteryLevel=batteryPct*100;
//            Log.d("aaaaaa","SetUserVisible Battery Status  = "+batteryLevel);
            getRequest();
            Log.d("aaaaaa","setUservisible");
               }
    }
    private void getRequest(){
        ConnectionDetector cd=new ConnectionDetector(getMainActivity());

        if(cd.isConnectingToInternet()) {
            String token="";
            ApplicationHelper applicationHelper=MainActivityHelper.applicationHelper();
            if(applicationHelper!=null) {
                SharedPreferences sharedPreferences = applicationHelper.getSharedPrefrences(BMAConstants.NOTIF_INFO);
                String deviceToken = sharedPreferences.getString("device_firebase_token", "devicetoken");
                if (deviceToken == null) {
                    try {
                        FirebaseInstanceId firebaseInstanceId = FirebaseInstanceId.getInstance().getInstance();
                        token = firebaseInstanceId.getToken();

                    } catch (Exception e) {
                    }
                } else {
                    token = deviceToken;
                }
            }
            httpRequest = new HttpRequest(getMainActivity(), true);
            httpRequest.delegate = AllTasksFragment.this;
            this.actionID = "engineerHomeScreen";
            //  Log.d("aaaaaaa"," All Task engineerID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID","abcfegd")+"    service id = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null));
            httpRequest.execute(actionID, MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null), getMainActivity().getPinCode(),token);//,batteryLevel+"");
        }else{
            Misc misc=new Misc(getMainActivity());
            misc.NoConnection();

        }
    }

    private void dataToView() {
        if (this.eoAllBookingTask != null) {
            this.missedBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.missedBooking.size()));
            this.tomorrowBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.tomorrowBooking.size()));
            this.morningBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayMorningBooking.size()));
            this.afternoonBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayAfternoonBooking.size()));
            this.eveningBookingCount.setText(eoAllBookingTask.getBookingCount(this.eoAllBookingTask.todayEveningBooking.size()));
            this.feedbackText.setText(eoAllBookingTask.rating);
            this.feedBackProgressBar.setSecondaryProgress(Integer.valueOf(eoAllBookingTask.rating) * 20);
            this.closureTxtProgress.setText(eoAllBookingTask.sameDayClosure);
            this.closureProgressBar.setProgress(Integer.valueOf(eoAllBookingTask.sameDayClosure));
            if (eoAllBookingTask.incentive != null && eoAllBookingTask.incentive.length() != 0) {
                incentiveProgress.setText(eoAllBookingTask.incentive);
                this.incentiveProgressBar.setProgress(100);
            }

        }
        //LP-4394701910188
    }
// After search on home screen is taking time to change page.
    @Override
    public void processFinish(String response) {
        // super.processFinish(response);
        Log.d("bbbbbbb", "response  = " + response);


        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    //  JSONObject responseData = new JSONObject(jsonObject.getString("response"));
                    if (this.actionID.equalsIgnoreCase("engineerHomeScreen")) {
                        String res = jsonObject.getString("response");
                        eoAllBookingTask = BMAGson.store().getObject(EOAllBookingTask.class, res);
                        getMainActivity().todayMorningBooking = eoAllBookingTask.todayMorningBooking;
                        getMainActivity().todayAfternoonBooking = eoAllBookingTask.todayAfternoonBooking;
                        getMainActivity().todayEveningBooking = eoAllBookingTask.todayEveningBooking;
                        this.dataToView();
                    } else {
                        ArrayList<EOBooking> searchedBookingList = BMAGson.store().getList(EOBooking.class, jsonObject.getJSONObject("response").getString("Bookings"));
                        openSearchedBookingPage(searchedBookingList);
                        this.closureProgressBar.setProgress(0);
                        this.feedBackProgressBar.setProgress(0);
                        this.incentiveProgressBar.setProgress(0);
                    }


                } else if (this.actionID.equalsIgnoreCase("searchData")) {
                    //  if(statusCode.equals("0061")){
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                        @Override
                        public void onWarningDismiss() {
                            super.onWarningDismiss();
                        }
                    };
                    String result = jsonObject.getString("result");
                    if (result == null || result.length() == 0) {
                        result = "Data not found";
                    }
                    bmaAlertDialog.show(result);
                    //   }
                }
            } catch (Exception e) {

            }
        }else{
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };

            bmaAlertDialog.show(getString(R.string.serverError));
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();


        switch (id) {
            case R.id.missedLayout:
                if (this.eoAllBookingTask == null || this.eoAllBookingTask.missedBooking.size() == 0) {
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.missedBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                this.closureProgressBar.setProgress(0);
                this.feedBackProgressBar.setProgress(0);
                this.incentiveProgressBar.setProgress(0);
                BMAmplitude.saveUserAction("MissedBooking", "MissedBookingLayout");
                bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.missedBooking));
                bundle.putBoolean("isMissed", true);
                bundle.putParcelableArrayList("missedbooking",this.eoAllBookingTask.missedBooking);
                TomorrowFragment tomorrowFragment1 = new TomorrowFragment();
                tomorrowFragment1.setArguments(bundle);
                getMainActivity().updateFragment(tomorrowFragment1, true);
                break;
            case R.id.tomorroBookingLayout:
                if (this.eoAllBookingTask == null || this.eoAllBookingTask.tomorrowBooking.size() == 0) {
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noTomorrowBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                this.closureProgressBar.setProgress(0);
                this.feedBackProgressBar.setProgress(0);
                this.incentiveProgressBar.setProgress(0);
                BMAmplitude.saveUserAction("TomorrowBooking", "TomorrowBookingLayout");
                bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.tomorrowBooking));
                bundle.putParcelableArrayList("tomorrowBooking",this.eoAllBookingTask.tomorrowBooking);
                TomorrowFragment tomorrowFragment = new TomorrowFragment();
                tomorrowFragment.setArguments(bundle);
                getMainActivity().updateFragment(tomorrowFragment, true);
                break;
            case R.id.morningLayout:
                if (this.eoAllBookingTask == null || this.eoAllBookingTask.todayMorningBooking.size() == 0) {
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noMorningBooking), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                this.closureProgressBar.setProgress(0);
                this.feedBackProgressBar.setProgress(0);
                this.incentiveProgressBar.setProgress(0);
                FragmentLoader fragmentLoader = new FragmentLoader();
                fragmentLoader.index = 0;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader, true);
                break;
            case R.id.afternoonLayout:
                if (this.eoAllBookingTask == null || this.eoAllBookingTask.todayAfternoonBooking.size() == 0) {
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noNoonBooking), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                this.closureProgressBar.setProgress(0);
                this.feedBackProgressBar.setProgress(0);
                this.incentiveProgressBar.setProgress(0);
                FragmentLoader fragmentLoader1 = new FragmentLoader();
                fragmentLoader1.index = 1;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader1.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader1, true);
                // getMainActivity().loadtabFragment(1);
                break;
            case R.id.eveningLayout:
                if (this.eoAllBookingTask == null || this.eoAllBookingTask.todayEveningBooking.size() == 0) {
                    Snackbar.make(this.view.findViewById(R.id.allTaskfragment), getString(R.string.noEveningBookingValidation), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                this.closureProgressBar.setProgress(0);
                this.feedBackProgressBar.setProgress(0);
                this.incentiveProgressBar.setProgress(0);
                FragmentLoader fragmentLoader2 = new FragmentLoader();
                fragmentLoader2.index = 2;
                bundle.putString(BMAConstants.menu_id, "home");
                fragmentLoader2.setArguments(bundle);
                getMainActivity().updateFragment(fragmentLoader2, true);
                break;
            case R.id.searchIcon:
                String inputValue=this.searchfield.getText().toString().trim();
                if(inputValue!=null && inputValue.length()>0) {
                    if(inputValue.length()>=8) {
                        this.startSearch(inputValue);
                    }else{
                        Toast.makeText(getContext(),getString(R.string.enterValidCharString),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),getString(R.string.eneterBookingIDValiadation),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.incentiveRelativeLayout:
                if(this.eoAllBookingTask!=null && this.eoAllBookingTask.incentive!=null && this.eoAllBookingTask.incentive.length()>0 && Integer.valueOf(this.eoAllBookingTask.incentive)>0){
                openIncentivePage();
                    this.closureProgressBar.setProgress(0);
                    this.feedBackProgressBar.setProgress(0);
                    this.incentiveProgressBar.setProgress(0);
            }
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        this.closureProgressBar.setProgress(0);
//        this.feedBackProgressBar.setProgress(0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    String actionID;

    @Override
    public void startSearch(String filterStr) {
        Log.d("aaaaa", "search data = " + filterStr);

        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = AllTasksFragment.this;
        this.actionID = "searchData";
        //  Log.d("aaaaaaa"," All Task engineerID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID","abcfegd")+"    service id = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null));
        httpRequest.execute(this.actionID, MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null), filterStr, getMainActivity().getPinCode());//,batteryLevel+"");

    }

    private void openSearchedBookingPage(ArrayList<EOBooking> searchedBookingList) {
        // searchedBookingList
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("searchedBookingList", searchedBookingList);
        SearchedBookingFragment searchedBookingFragment = new SearchedBookingFragment();
        bundle.putString(BMAConstants.HEADER_TXT, "Searched Bookings");
        bundle.putString("filterStr", searchfield.getText().toString().trim());
        searchedBookingFragment.setArguments(bundle);
        searchfield.setText("");
        getMainActivity().updateFragment(searchedBookingFragment, true);

    }
    private void openIncentivePage(){
        Bundle bundle=new Bundle();
        bundle.putString(BMAConstants.HEADER_TXT, "Earnings");
        bundle.putString("totalIncentiveAmount",eoAllBookingTask.incentive);
        IncentiveFragment incentiveFragment = new IncentiveFragment();
        incentiveFragment.setArguments(bundle);
        getMainActivity().updateFragment(incentiveFragment, true);
    }


}
