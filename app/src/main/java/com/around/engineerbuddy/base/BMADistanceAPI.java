package com.around.engineerbuddy.base;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BMADistanceAPI  {
    String origPinCode, destPinCode;
    public BMADistanceAPI(String origPinCode,String destPinCode){
        this.origPinCode=origPinCode;
        this.destPinCode=destPinCode;
    }

   public void execute(){
       new AsyncTaskExample().execute();
   }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer stringBuffer=null;
            try {
                URL url = null;
                try {
                    // url = new URL("https://maps.googleapis.com/maps/api/distancematrix/xml?origins=813208,india&destinations=813209,india&key=AIzaSyBro5H0vFCkDvaXzigG-yCsYpMkFYnTuk0");//(strings[0]);

                    url=new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins=$city_1,"+origPinCode+",India&destinations=$city_2,+"+destPinCode+",India&mode=driving&language=en-EN&sensor=false&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ");


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                // is = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                stringBuffer= new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    stringBuffer.append(line);
                    // break;
                }

                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuffer.toString();
            //  return is;
        }

        @Override
        protected void onPostExecute(String distance) {
            super.onPostExecute(distance);

        }
    }
    // https://maps.googleapis.com/maps/api/distancematrix/xml?origins=12413&destinations=12211&key=YOUR_API_KEY

}
