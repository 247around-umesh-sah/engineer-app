package com.around.technician;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by abhay on 28/12/17.
 */
@SuppressWarnings("DefaultFileTemplate")
public class GetToken {

    public Context context;
    SharedPreferences sharedPrefs;
    private Boolean isEmulator = new DeviceInfo(this.context).isEmulator();
    private Boolean isRooted = new DeviceInfo(this.context).isRooted();
    private String osName = new DeviceInfo(this.context).getOs();
    private String version = new DeviceInfo(this.context).getVersion();
    private String model = new DeviceInfo(this.context).getModel();
    private String device = new DeviceInfo(this.context).getDevice();

    public GetToken(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences(SplashActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
    }

    public String getAuthToken(String[] params) throws Exception {

        String subUrl = params[0];

        Map<String, String> urlParameters = new HashMap<>();
        String deviceInfo = getDeviceInfo();
        urlParameters.put("deviceInfo", deviceInfo);
        urlParameters.put("method", "post");
        urlParameters.put("apiPath", "api");

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        assert pInfo != null;
        String appVersion = pInfo.versionName;
        urlParameters.put("app_version", appVersion);

        switch (subUrl) {
            case "engineerLogin":
                engineerLogin(urlParameters, params);
                break;
            case "searchBookingID":
                searchBookingID(urlParameters, params);
                break;
            case "getBookingAppliance":
                getBookingAppliance(urlParameters, params);
                break;
            case "completeBookingByEngineer":
                completeBookingByEngineer(urlParameters, params);
                break;
            case "cancelBookingByEngineer":
                processCancelBooking(urlParameters, params);
                break;
        }

        String token = generateToken(urlParameters, subUrl);
        Map<String, String> jsonData = new HashMap<>();

        String deviceId = new DeviceInfo(this.context).bindIds();

        String requestId = UUID.randomUUID().toString();
        jsonData.put("requestId", requestId);
        jsonData.put("token", token);
        jsonData.put("requestUrl", subUrl);
        jsonData.put("deviceId", deviceId);

        return new Gson().toJson(jsonData);
    }

    public String generateToken(Map<String, String> urlParameters, String subUrl) {
        String jwtToken = "";
        try {
            String json = new Gson().toJson(urlParameters);

            String key = "boloaaka-mobile-application";
            String sharedSecret = JwtBuilder.signHmac256("username", "boloaaka-signup-request");

            jwtToken = JwtBuilder.generateJWTToken(subUrl, json, key, sharedSecret);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return jwtToken;
    }

    public Map<String, String> completeBookingByEngineer(Map<String, String> urlParameters,
                                                         String[] params) {
        try {

            String amount = params[1];
            String SignatureEncode = params[2];

            urlParameters.put("amountDue", amount);
            urlParameters.put("SignatureEncode", SignatureEncode);
            urlParameters.put("amountPaid", params[3]);
            urlParameters.put("UnitArray", params[4]);
            urlParameters.put("bookingID", params[5]);
            urlParameters.put("location", params[6]);
            urlParameters.put("remarks", params[7]);
            urlParameters.put("service_center_id", sharedPrefs.getString("service_center_id", null));
            urlParameters.put("engineer_id", sharedPrefs.getString("engineerID", null));
            urlParameters.put("agent_id", sharedPrefs.getString("agentID", null));
            urlParameters.put("sc_agent_id", sharedPrefs.getString("scAgentID", null));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> processCancelBooking(Map<String, String> urlParameters,
                                                    String[] params) {
        try {
            urlParameters.put("bookingID", params[1]);
            urlParameters.put("cancellationReason", params[2]);
            urlParameters.put("location", params[3]);
            urlParameters.put("service_center_id", sharedPrefs.getString("service_center_id", null));
            urlParameters.put("engineer_id", sharedPrefs.getString("engineerID", null));
            urlParameters.put("agent_id", sharedPrefs.getString("agentID", null));
            urlParameters.put("sc_agent_id", sharedPrefs.getString("scAgentID", null));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> engineerLogin(Map<String, String> urlParameters, String[] params) {
        try {

            String mobile = params[1];
            String password = params[2];

            urlParameters.put("mobile", mobile);
            urlParameters.put("password", password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> getBookingAppliance(Map<String, String> urlParameters, String[] params) {
        try {

            String bookingID = params[1];

            urlParameters.put("bookingID", bookingID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> searchBookingID(Map<String, String> urlParameters, String[] params) {
        try {

            String search_text = params[1];

            urlParameters.put("search_text", search_text);
            urlParameters.put("service_center_id", sharedPrefs.getString("service_center_id", null));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public String getDeviceInfo() {

        HashMap<String, String> dInfo = new HashMap<>();

        dInfo.put("os", osName);
        dInfo.put("platformVersion", version);
        dInfo.put("model", model);
        dInfo.put("modelVersion", device);
        dInfo.put("isRooted", isRooted.toString());
        dInfo.put("isEmulator", isEmulator.toString());

        return new Gson().toJson(dInfo);
    }

}
