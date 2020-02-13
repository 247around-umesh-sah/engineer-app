package com.around.engineerbuddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.activity.MainActivity;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


@SuppressWarnings("ALL")
public class HttpRequest extends AsyncTask<String, Void, String> {

    //stag url
    //public static String base_url = "http://stag.aroundhomzapp.com/engineerApi";

    //Testing URL
   // public static String base_url = "http://testapp.247around.com/engineerApi";

    ///Live Url
    static String base_url = "https://aroundhomzapp.com/engineerApi";


    //Kenstar
    //public static String base_url="http://www.kenstar.aroundhomzapp.com/engineerApi";
    public ApiResponse delegate = null;
    public ProgressDialog progress;
    public JSONObject postDataParams;
    public URL url;
    boolean is_progressbar;
    private Context context;

    /**
     * If do not want to show loading while request on server then use false as parameter
     *
     * @param c              context
     * @param is_progressbar boolean
     */
    public HttpRequest(Context c, boolean is_progressbar) {

        this.context = c;
        this.is_progressbar = is_progressbar;
    }

    /**
     * This is used to post data to server from Asynchronous
     *
     * @param params String Array
     * @return String
     */
    protected String doInBackground(String... params) {
        // Generate Token
        GetToken getToken = new GetToken(context);

        try {

            String requestData = getToken.getAuthToken(params);

            url = new URL(base_url);

            postDataParams = new JSONObject();
            postDataParams.put("request", requestData);
            // Log.d("aaaaaa","HTTPREQUESTDATA  = "+requestData);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(50000 /* milliseconds */);
            conn.setConnectTimeout(50000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getToken.getPostDataString(postDataParams));

            //  Log.d("aaaaa","getPostData = "+getToken.getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    stringBuffer.append(line);
                    break;
                }

                in.close();
                return stringBuffer.toString();

            } else {
                return "false : " + responseCode;
            }
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String urlkey = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("urlKey", null);
        Log.d("aaaaa", "URL: KEY = " + urlkey);
        if (urlkey != null) {
            if (urlkey.equalsIgnoreCase("1")) {
                base_url = "http://testapp.247around.com/engineerApi";
            } else {
                base_url = "https://aroundhomzapp.com/engineerApi";
            }
        }
        Log.d("aaaaa", "URL: = " + base_url);
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.loading));
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        if (is_progressbar) {
            progress.show();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        response.contains("data")
        Log.d("aaaaaa", "httpResponse = = = " + result);
        if (result.contains("data")) {
            delegate.processFinish(result);
        } else if (result != null) {
            String[] serverResp = result.split(":");
            String serverResponse = "Server Error";// "Something Went Wrong";
            if (serverResponse != null && serverResp.length > 0 && serverResp.length > 1) {
                serverResponse = "Server Error : " + serverResp[1];
            }
            if (this.context instanceof MainActivity) {
                delegate.processFinish(result);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setTitle("Error");

                //Setting message manually and performing action on button click
                builder.setMessage(serverResponse).setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                progress.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                // alert.setTitle("AlertDialogExample");
                alert.show();
            }

        }

    }

    private void submitRequest() {
        ArrayList<String> partnerPaidBasicCharge = new ArrayList<>();

    }

}
