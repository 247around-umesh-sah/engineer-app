package com.around.engineerbuddy;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOGeoAddressEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DistanceAPI {
    DistanceListner distanceListner;
    ArrayList<EOBooking>bookingList;
    Context context;
    public DistanceAPI(Context context,ArrayList<EOBooking> bookingList,DistanceListner distanceListner){
        this.bookingList=bookingList;
        this.context=context;
        this.distanceListner=distanceListner;

    }


    public void distance(){//String originPincode, String destPincode, int position) {
//        String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india,"+originPincode+"&destinations=india,"+destPincode+"&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
        for(EOBooking eoBooking:this.bookingList){
            String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india," + eoBooking.pincode + "&destinations=india," + "110015" + "&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,distnaceURL);
        }

        //  String distnaceURL="https://maps.googleapis.com/maps/api/distancematrix/json?origins=$city_1,"+originPincode+",India&destinations=$city_2,"+destPincode+",India&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
        //  new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,distnaceURL);

    }

//    @Override
//    public void distnaceCalculate() {
//
//    }

    public class MyAsyncTask extends AsyncTask<String, String, String> {
        String distance;
//        @Override
//        protected String doInBackground(Void... params) {
//
//        }

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
                for(EOBooking eoBooking:bookingList){
                    Log.d("aaaaaaa", "pincode = "+eoBooking.pincode+"        geopin = "+eoGeoAddressEntity.origin_addresses.get(0));
                    if(eoGeoAddressEntity.origin_addresses.get(0).contains(eoBooking.pincode)){
                        if(eoGeoAddressEntity.rows.get(0).elements.get(0).status.equalsIgnoreCase("OK")) {
                            eoBooking.distnace = eoGeoAddressEntity.rows.get(0).elements.get(0).distance.text.split(" ")[0];
                        }
                        Log.d("aaaaaaa", "distance = "+eoBooking.distnace);

                    }
                }

            //distanceListner.distnaceCalculate();
          //  TomorrowFragment.this.bmaRecyclerAdapter.notifyDataSetChanged();
            // Log.d("aaaaaaa", "result = " + result + "\n\n\n");

        }
    }
    public interface DistanceListner{
         void distnaceCalculate(Location location);

    }
//    public Location getLocation() {
//        Location location=null;
//        String address = "c 68 rama 1 greater noida";// 201310;
//        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//        try {
//            List<Address> addressList = geocoder.getFromLocationName(address, 5);
//            Log.d("aaaaaa", "latitude..... = " + addressList.get(0).getLatitude() + "      longitude..#### = " + addressList.get(0).getLongitude());
//       if(addressList!=null && addressList.size()>0){
//           Address addressLoc=addressList.get(0);
//           location=new Location(LocationManager.GPS_PROVIDER);
//           location.setLatitude(addressLoc.getLatitude());
//           location.setLongitude(addressLoc.getLongitude());
//
//       }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return location;
//    }
    //    . = 28.4743879      longitude..#### = 77.50399039999999
//            2019-05-10 11:57:16.641 27185-27185/com.around.engineerbuddy D/aaaaaa: beforeCallSetUserVisi
//2019-05-10 11:57:16.765 27185-27185/com.around.engineerbuddy D/aaaaaa: latitude..... = 28.4743879      longitude..#### = 77.50399039999999

}
