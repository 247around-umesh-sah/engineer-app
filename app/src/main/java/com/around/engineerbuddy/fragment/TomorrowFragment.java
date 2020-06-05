package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
//import com.around.engineerbuddy.activity.NavigationMapActivity;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.entity.BookingInfo;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOGeoAddressEntity;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;
import com.around.engineerbuddy.util.BMAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


public class TomorrowFragment extends BMAFragment {

    TextView tomorrowText, bookingText, countBooking;
    ImageView bookingTypeImage;
    View devider;
    LinearLayout bookingHeaderLayout,noDataToDisplayLayout;
    RecyclerView recyclerView;
    boolean isMissed;
    // ProgressBar progressBar;
    BookingInfo bookingInfo;
    BMARecyclerAdapter bmaRecyclerAdapter;
    public ArrayList<EOBooking> missedBooking=new ArrayList<>();
    public ArrayList<EOBooking> tomorrowBooking=new ArrayList<>();
    //////pk.eyJ1IjoidWtzYWgiLCJhIjoiY2p2cW5iaHZnMHlpYjQ0cWw4M2Y3eDRwcyJ9.MPePwKBaJVKg4BTHhYgMFA


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {

        this.isMissed = getArguments().getBoolean("isMissed");
        this.missedBooking=getArguments().getParcelableArrayList("missedbooking");
        this.tomorrowBooking=getArguments().getParcelableArrayList("tomorrowBooking");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tomorrow, container, false);

        this.recyclerView = this.view.findViewById(R.id.recyclerView);
        this.countBooking = this.view.findViewById(R.id.count_booking);
        this.tomorrowText = this.view.findViewById(R.id.tomorrowText);
        this.bookingText = this.view.findViewById(R.id.bookingText);
        this.bookingTypeImage = this.view.findViewById(R.id.bookingTypeImage);
        this.devider = this.view.findViewById(R.id.deviderView);
        this.bookingHeaderLayout = this.view.findViewById(R.id.headerLayout);
        this.noDataToDisplayLayout=this.view.findViewById(R.id.nodataToDisplayLayout);

        if (isMissed) {
            tomorrowText.setText("Missed");
            bookingTypeImage.setBackground(getResources().getDrawable(R.drawable.missed_booking));

        }
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.getBookingList(), recyclerView, this, R.layout.tomorrow_item_row);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
      //  if(bookingInfo==null) {
           // loadData();
       // }else{
            this.dataToView(false);
       // }

        return this.view;
    }

   HttpRequest httpRequest;


    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        RelativeLayout rowLayout = itemView.findViewById(R.id.rowLayout);
        if(isMissed) {
            BMAmplitude.saveUserAction("MissedBookingFragment","MissedBookingFragment");
        }else {
            BMAmplitude.saveUserAction("TomorrowFragment","TomorrowFragment");
        }



        EOBooking eoBooking = (EOBooking) rowObject;
        TextView name = itemView.findViewById(R.id.name);
        TextView address = itemView.findViewById(R.id.address);
        TextView brandName = itemView.findViewById(R.id.brandName);
        TextView bookingID=itemView.findViewById(R.id.chargableservice);
        bookingID.setText(eoBooking.bookingID);
        TextView serviceName = itemView.findViewById(R.id.serviceName);
        TextView date = itemView.findViewById(R.id.date);
        TextView distance=itemView.findViewById(R.id.distance);
        TextView chargeableAmount=itemView.findViewById(R.id.chargeableAmount);
        name.setText(eoBooking.name);
        address.setText(eoBooking.bookingAddress);
        brandName.setText(eoBooking.applianceBrand);
        serviceName.setText(eoBooking.services+"-"+eoBooking.requestType);
        date.setText(eoBooking.bookingDate);
        distance.setText(eoBooking.bookingDistance);
        chargeableAmount.setText(""+eoBooking.dueAmount);
        ImageView phone = itemView.findViewById(R.id.phone);
        ImageView mapIcon = itemView.findViewById(R.id.mapLocArrow);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BMAMapFragment();
                Bundle bundl = new Bundle();
                bundl.putParcelable("eoBooking", eoBooking);
                bundl.putString(BMAConstants.HEADER_TXT, "");
                fragment.setArguments(bundl);
                getMainActivity().updateFragment(fragment, true);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                    BMAAlertDialog dialog = new BMAAlertDialog(getActivity(), true,false) {
                        @Override
                        public void onConfirmation() {
                            this.dismiss();
                            doCall(eoBooking.primaryContact);
                        }
                    };
                    dialog.show(getString(R.string.enableCallFeatureValiadtion));
                } else {

                    doCall(eoBooking.primaryContact);
                }

            }
        });

        if (isMissed) {
            itemView.findViewById(R.id.dateLayout).setBackgroundColor(BMAUIUtil.getColor(R.color.missedBookingColor));
        }
        if(eoBooking.covid_zone!=null && eoBooking.covid_zone.size()>0){
            itemView.findViewById(R.id.covidLayout).setVisibility(View.VISIBLE);
            ImageView virusicon=itemView.findViewById(R.id.virusIcon);
            TextView covidArea=itemView.findViewById(R.id.covidArea);
           // covidIcon.setText(R.string.virus_icon);
            TextView zone=itemView.findViewById(R.id.zone);
        //    covidArea.setVisibility(View.VISIBLE);
            String zoneString=eoBooking.covid_zone.get(0).zone_color;
            if(zoneString.contains("Red") ||zoneString.contains("red") ){
                zone.setText(zoneString + " Zone");
                zone.setTextColor(BMAUIUtil.getColor(R.color.red_color));
                virusicon.setBackground(getResources().getDrawable(R.drawable.red_virus));
               // covidIcon.setTextColor(BMAUIUtil.getColor(R.color.red_color));

            }else if(zoneString.contains("Green") ||zoneString.contains("green") ){
                zone.setText(zoneString + " Zone");
                zone.setTextColor(BMAUIUtil.getColor(R.color.green_color));
                virusicon.setBackground(getResources().getDrawable(R.drawable.green_virus));
            }else if(zoneString.contains("Orange") ||zoneString.contains("orange") ){
                zone.setText(zoneString + " Zone");
                zone.setTextColor(BMAUIUtil.getColor(R.color.orange_color));
                virusicon.setBackground(getResources().getDrawable(R.drawable.orange_virus));
            }

        }

        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BookingDetailFragment();
                Bundle bundl = new Bundle();
                bundl.putParcelable("booking", eoBooking);
                bundl.putString(BMAConstants.HEADER_TXT, "");
                fragment.setArguments(bundl);

                getMainActivity().updateFragment(fragment, true, null);
            }
        });
    }



    private String getAddress(Location location) {
        String addressText = null;
        Geocoder geoCoder = new Geocoder(this.getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = String.format(
                        "%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
            }
        } catch (IOException ex) {
        }
        return addressText;
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = TomorrowFragment.this;
        httpRequest.execute(isMissed ? "missedBookings" : "tommorowBookings", MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null),getMainActivity().getPinCode());
    }

    @Override
    public void processFinish(String response) {

       // httpRequest.progress.dismiss();

        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, res);
                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
                    if (this.bookingInfo != null) {
                        this.dataToView(true);
                        if(this.getBookingList().size()==0) {
                            noDataToDisplayLayout.setVisibility(View.VISIBLE);
                        }

                    }


                }
            } catch (JSONException e) {
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                    }
                };
                bmaAlertDialog.show(R.string.loginFailedMsg);
            }
        }else{
            httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };
            bmaAlertDialog.show(getString(R.string.somethingWentWrong));
        }
        //  distance("813208","813209");

//        distance < 10 ? ("0" + distance ): distance
    }
    private void dataToView(boolean isSetDistance) {
    //    this.countBooking.setText("" + (this.getBookingList().size() < 10 ? "0" + this.getBookingList().size() : this.getBookingList().size()));
        this.bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.getBookingArray(), recyclerView, this, R.layout.tomorrow_item_row);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(bmaRecyclerAdapter);
//        if(isSetDistance) {
//            this.updateDistance();
//        }
      //  this.httpRequest.progress.dismiss();

    }


    public void noitifyDataSetChangedAdapter(){
       // httpRequest.progress.dismiss();
        this.bmaRecyclerAdapter.notifyDataSetChanged();
    }




    public ArrayList<EOBooking> getBookingList() {
       return isMissed ? this.missedBooking!=null ? this.missedBooking : new ArrayList<>() : this.tomorrowBooking!=null ? this.tomorrowBooking : new ArrayList<>();
                // return this.bookingInfo != null ?    isMissed ? this.bookingInfo.missedBooking : this.bookingInfo.tomorrowBooking : new ArrayList<>();

    }
    private ArrayList<EOBooking> getBookingArray(){
        for(EOBooking eoBooking:getBookingList()){
            eoBooking.name=BMAUtil.getTitleString(eoBooking.name);
        }
        return getBookingList();
    }


    private void doCall(String mobNumber) {

        //By Nitin Sir
        BMAAlertDialog bmaAlertDialog=new BMAAlertDialog(getContext(),true,false){
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobNumber));
                startActivity(intent);
            }
        };
        bmaAlertDialog.show(getString(R.string.callConfirmationMessage));

    }
int position;
    public void distance(){//String originPincode, String destPincode, int position) {
//        String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india,"+originPincode+"&destinations=india,"+destPincode+"&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
        for(EOBooking eoBooking:this.getBookingList()){
            String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india," + eoBooking.pincode + "&destinations=india," + "110015" + "&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,distnaceURL);
        }

         //  String distnaceURL="https://maps.googleapis.com/maps/api/distancematrix/json?origins=$city_1,"+originPincode+",India&destinations=$city_2,"+destPincode+",India&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
        //  new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,distnaceURL);

    }

    public class MyAsyncTask extends AsyncTask<String, String, String> {
        String distance;

        @Override
        protected String doInBackground(String... strings) {
            String distnaceUrl = strings[0];
            try {
                URL url = new URL(distnaceUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                 conn.setReadTimeout(50000 /* milliseconds */);
//                 conn.setConnectTimeout(50000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        stringBuffer.append(line);
                        // break;
                    }

                    in.close();
                    return stringBuffer.toString();

                } else {
                    return "false : " + responseCode;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            distance = result;
            EOGeoAddressEntity eoGeoAddressEntity=BMAGson.store().getObject(EOGeoAddressEntity.class,result);
            if(eoGeoAddressEntity.status.equalsIgnoreCase("OK"))
            for(EOBooking eoBooking:getBookingList()){
                if(eoGeoAddressEntity.origin_addresses.get(0).contains(eoBooking.pincode)){
                    if(eoGeoAddressEntity.rows.get(0).elements.get(0).status.equalsIgnoreCase("OK")) {
                        eoBooking.distnace = eoGeoAddressEntity.rows.get(0).elements.get(0).distance.text.split(" ")[0];
                    }

                }
            }

            TomorrowFragment.this.bmaRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null ) {
            return;
        }

        if(data.getBooleanExtra("isCancelled",false)) {
//            if (this.getBookingList().size() == 1) {

            if(getTargetFragment()!=null) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, new Intent());
                if(getFragmentManager()!=null) {
                    getFragmentManager().popBackStack();
                }
            }
//            } else {
//                //loadData();
//            }
        }
    }
}
