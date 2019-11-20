package com.around.engineerbuddy.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.around.engineerbuddy.ApiResponse;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.activity.MainActivity;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.entity.EOBooking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BMAFragment extends Fragment implements ApiResponse, BMARecyclerAdapter.BMAListRowCreator {
    View view;
    private LocationManager mLocationManager;
   // Location currentLocation;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       // mLocationManager = (LocationManager) getMainActivity().getSystemService(Context.LOCATION_SERVICE);
        //  this.hideSoftKeyboard(this.getActivity());
       // savePageNameForAmplitude();

    }

    public void savePageNameForAmplitude(){
        BMAmplitude.saveUserAction(getPageName(),getPageName());
    }
    public String getPageName(){
        return getMainActivity().getPageFragment().getClass().getSimpleName();
    }

    @Override
    public void processFinish(String response) {

    }

    public void loadRecyclerViewAdapter(RecyclerView recyclerView, Context context, BMARecyclerAdapter.BMAListRowCreator bmaListRowCreator, int layoutID) {
//        Context context,List list,RecyclerView recyclerView, BMARecyclerAdapter.BMAListRowCreator
//        bmaListRowCreator,int layoutID
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("aaa");
        stringList.add("bbbbb");
        // recyclerView.setAdapter(new BMARecyclerAdapter(context,stringList,recyclerView,bmaListRowCreator,layoutID));

    }

    public MainActivity getMainActivity() {
        FragmentActivity fragmentActivity = getActivity();
        return fragmentActivity != null ? (MainActivity) fragmentActivity : MainActivityHelper.application();
    }

    @Override
    public <T> void createRow(View convertView, T rowObject) {

    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {

    }

    @Override
    public <T> void createHeader(View convertView, T rowObject) {

    }

    public void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            hideKeyBoard(activity, activity.getCurrentFocus());
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    public void hideKeyBoard(Context context, View v) {
        if (v == null) {
            return;
        }
        getMainActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public Location getLocation(String address) {
        Location location = null;
        address.replaceAll(","," ");
//        Log.d("aaaaa","adress = "+address);
        //String address = "c 68 rama 1 greater noida";// 201310;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 5);
//            Log.d("aaaaa","adressList= "+addressList);
            //Log.d("aaaaaa", "latitude..... = " + addressList.get(0).getLatitude() + "      longitude..#### = " + addressList.get(0).getLongitude());
            if (addressList != null && addressList.size() > 0) {
                Address addressLoc = addressList.get(0);
//                Log.d("aaaaa","addressLoc= "+addressLoc);
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(addressLoc.getLatitude());
                location.setLongitude(addressLoc.getLongitude());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    public float calculatedDistnace(String address) {

//float distance=0.0f;
//        if(this.currentLocation!=null){
        float caldis=0.0f;
//        if(true)
//        return caldis;
        Location destLoc=getLocation(address);


        if (getCurrentLocation() != null && getLocation(address)!=null && destLoc!=null) {
            caldis = this.getCurrentLocation().distanceTo(destLoc)/1000;

        }
       // Log.d("aaaaa", "curntLoc = " + currentLocation + "    caldis = = " + caldis);
        return caldis;
//        }
//        return distance;

    }
    public Location getCurrentLocation(){
        return getMainActivity().getCurrentLocation();
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.stopUpdateDistance();


    }
    Handler timerHandler;
    Runnable timerRunnable;

    public void updateDistance(){
        setDistance();
//        timerHandler= new Handler();
//        timerHandler.postDelayed(this.timerRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    setDistance();
//
//                    timerHandler.postDelayed(BMAFragment.this.timerRunnable, 1000); // run every second
//                } catch (Exception e) {
                   // stopUpdateDistance();
//                }
//            }
//
//        }, 1000);
    }
    private void setDistance(){
        for(EOBooking eoBooking:getBookingList()) {

            String distance = String.format("%.01f", this.calculatedDistnace(eoBooking.bookingAddress+" "+eoBooking.pincode));
            String strArray[]=distance.split(".");
            Log.d("aaaaa","cal= "+distance);
            eoBooking.distnace =distance;
            eoBooking.destLocation=getLocation(eoBooking.bookingAddress+" "+eoBooking.pincode);

        }
      //  this.noitifyDataSetChangedAdapter();
    }
    private void stopUpdateDistance(){
        if (this.timerHandler != null) {
            this.timerHandler.removeCallbacks(this.timerRunnable);
            timerHandler = null;
        }
    }
    public void noitifyDataSetChangedAdapter(){

    }
    public ArrayList<EOBooking> getBookingList(){
        return new ArrayList<>();
    }

    public void startSearch(String filterStr) {

    }
}

