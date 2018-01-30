package com.around.technician;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class HttpRequest extends AsyncTask<String, Void, String> {
    public static String base_url = "https://aroundhomzapp.com/api";
    public ApiResponse delegate = null;
    public ProgressDialog progress;
    boolean is_progressbar;
    private Context context;
    private JSONObject postDataParams;
    private URL url;

    /**
     * If do not want to show loading while request on server then use false as parameter
     * @param c context
     * @param is_progressbar boolean
     */
    public HttpRequest(Context c, boolean is_progressbar) {

        this.context = c;
        this.is_progressbar = is_progressbar;
    }

    /**
     * This is used to post data to server from Asynchronous
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

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(25000 /* milliseconds */);
            conn.setConnectTimeout(25000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getToken.getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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

        delegate.processFinish(result);

    }


}
