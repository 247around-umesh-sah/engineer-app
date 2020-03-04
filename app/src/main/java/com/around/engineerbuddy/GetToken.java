package com.around.engineerbuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.around.engineerbuddy.util.BMAConstants;
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
        sharedPrefs = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);//context.getSharedPreferences(SplashActivity.MyPREFERENCES,
               // Context.MODE_PRIVATE);
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
            case "engineerHomeScreen":
                engineerHomeScreen(urlParameters, params);
                break;
            case "engineerBookingsByStatus":
            case "missedBookings":
                missedBookingList(urlParameters, params);
                break;
            case "tommorowBookings":
                missedBookingList(urlParameters, params);
                break;
            case "searchBookingID":
                searchBookingID(urlParameters, params);
                break;
            case "getBookingAppliance":
                getBookingAppliance(urlParameters, params);
                break;
//            case "completeBookingByEngineer":
//                completeBookingByEngineer(urlParameters, params);
//                break;
            case "cancelBookingByEngineer":
                processCancelBooking(urlParameters, params);
                break;
            case "getCustomerQrCode":
                getCustomerQrCode(urlParameters, params);
                break;
            case "techSupport":
                techSupport(urlParameters, params);
                break;
            case "engineerHeplingDocuments":
                engineerHeplingDocuments(urlParameters, params);
                break;
            case "engineerProfile":
                engineerProfile(urlParameters, params);
                break;
            case "engineerSparePartOrder":
                sparePartOrder(urlParameters,params);
                break;
            case "partTypeOnModelNumber":
                partTypeOnModelNumber(urlParameters,params);
                break;
            case "sparePartName":
                getSparePartName(urlParameters,params);
                break;
            case "submitSparePartsOrder":
                submitSparePartsOrder(urlParameters,params);
                break;
            case "symptomCompleteBooking":
                symptomCompleteBooking(urlParameters,params);
                break;
            case "defectCompleteBooking":
                defectCompleteBooking(urlParameters,params);
                break;
            case "solutionCompleteBooking":
                solutionCompleteBooking(urlParameters,params);
                break;
            case "bookingProductDetails":
                bookingProductDetails(urlParameters,params);
                break;
            case "completeBookingByEngineer":
                completeBookingByEngineer(urlParameters,params);
                break;
            case "updateBookingReasons":
                updateBookingReasons(urlParameters,params);
                break;
            case "updateBookingByEngineer":
                updateBookingByEngineer(urlParameters,params);
                break;
            case "paytmAmountByEngineer":
                paytmAmountByEngineer(urlParameters,params);
                break;
            case "validateSerialNumber":
                validateSerialNumber(urlParameters,params);
                break;

            case "sparePartsWarrantyChecker":
                sparePartsWarrantyChecker(urlParameters,params);
                break;
            case "checkSparePartsOrder":
                checkSparePartsOrder(urlParameters,params);
                break;
            case "warrantyCheckerAndCallTypeData":
                warrantyCheckerAndCallTypeData(urlParameters,params);
                break;
            case "submitWarrantyCheckerAndEditCallType":
                submitWarrantyCheckerAndEditCallType(urlParameters,params);
                break;
            case "spareConsumptionData":
                spareConsumptionData(urlParameters,params);
                break;
            case "wrongSparePartsName":
                wrongSparePartsName(urlParameters,params);
                break;
            case "getBookingDetails":
                getBookingDetails(urlParameters,params);
                break;
            case "searchData":
                searchData(urlParameters,params);
                break;
            case "incentiveEearnedBookings":
                incentiveEearnedBookings(urlParameters,params);
                break;
            case "todaysSlotBookings":
                todaysSlotBookings(urlParameters,params);
                break;
            case "usernotifications":
                usernotifications(urlParameters,params);
                break;
            case "partners":
                partners(urlParameters,params);
                break;
            case "partnerAppliances":
                partnerAppliances(urlParameters,params);
                break;
            case "partnerappliancesModels":
                partnerappliancesModels(urlParameters,params);
                break;
            case "partnerappliancesModelsPartTypes":
                partnerappliancesModelsPartTypes(urlParameters,params);
                break;
            case "partnerappliancesModelsPartTypesInventory":
                partnerappliancesModelsPartTypesInventory(urlParameters,params);
                break;
            case "submitPrevPartsConsumption":
                submitPrevPartsConsumption(urlParameters,params);
                break;






        }

        String token = generateToken(urlParameters, subUrl);
        Map<String, String> jsonData = new HashMap<>();
        Log.d("aaaaa","BEFORE deviceinfo= ");
        DeviceInfo deviceInfo1 = new DeviceInfo(this.context);
        Log.d("aaaaa","deviceinfo= "+deviceInfo1);
        String deviceId=null;
        if(deviceInfo1!=null &&  !(this.context instanceof SplashActivity)) {
            deviceId = deviceInfo1.bindIds();
        }

        String requestId = UUID.randomUUID().toString();

        jsonData.put("requestId", requestId);
        jsonData.put("token", token);
        jsonData.put("requestUrl", subUrl);
        jsonData.put("deviceId", deviceId);
        jsonData.put("app_version", appVersion);

        return new Gson().toJson(jsonData);
    }

    public Map<String, String> getCustomerQrCode(Map<String, String> urlParameters,
                                                    String[] params) {
        try {
            urlParameters.put("bookingID", params[1]);
            urlParameters.put("amountPaid", params[2]);
            urlParameters.put("engineerNo", sharedPrefs.getString("phoneNumber", null));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
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

//    public Map<String, String> completeBookingByEngineer(Map<String, String> urlParameters,
//                                                         String[] params) {
//        try {
//
//            String amount = params[1];
//            String SignatureEncode = params[2];
//
//            urlParameters.put("amountDue", amount);
//            urlParameters.put("SignatureEncode", SignatureEncode);
//            urlParameters.put("amountPaid", params[3]);
//            urlParameters.put("UnitArray", params[4]);
//            urlParameters.put("bookingID", params[5]);
//            urlParameters.put("location", params[6]);
//            urlParameters.put("remarks", params[7]);
//            urlParameters.put("service_center_id", sharedPrefs.getString("service_center_id", null));
//            urlParameters.put("engineer_id", sharedPrefs.getString("engineerID", null));
//            urlParameters.put("agent_id", sharedPrefs.getString("agentID", null));
//            urlParameters.put("sc_agent_id", sharedPrefs.getString("scAgentID", null));
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return urlParameters;
//    }

    public Map<String, String> processCancelBooking(Map<String, String> urlParameters,
                                                    String[] params) {
        try {
            urlParameters.put("bookingID", params[1]);
            urlParameters.put("cancellationReason", params[2]);
            urlParameters.put("location", params[3]);
            urlParameters.put("remarks",params[4]);
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
            String deviceToken = params[3];
            Log.d("zzzzz", " request deviceToken is = " + deviceToken);
            urlParameters.put("mobile", mobile);
            urlParameters.put("password", password);
            urlParameters.put("device_firebase_token", deviceToken);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> techSupport(Map<String, String> urlParameters, String[] params) {
        try {

            String bookingID = params[1];

            urlParameters.put("booking_id", bookingID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> missedBookingList(Map<String, String> urlParameters, String[] params) {
        try {

            String engineerID = params[1];
            String serviceCenetrID = params[2];

            urlParameters.put("service_center_id", serviceCenetrID);
            urlParameters.put("engineer_id", engineerID);

            if(params.length>3 ){

                if(params[3].equalsIgnoreCase("Cancelled") || params[3].equalsIgnoreCase("Completed")) {
                    urlParameters.put("booking_status", params[3]);
                }else{
                    urlParameters.put("engineer_pincode", params[3]);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> engineerHomeScreen(Map<String, String> urlParameters, String[] params) {
        try {

            String engineerID = params[1];
            String serviceCenetrID = params[2];

            urlParameters.put("service_center_id", serviceCenetrID);
            urlParameters.put("engineer_id", engineerID);
            urlParameters.put("engineer_pincode", params[3]);
         //   urlParameters.put("deviceBattery", params[4]);



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
    public Map<String, String> engineerHeplingDocuments(Map<String, String> urlParameters, String[] params) {
        try {

            String bookingID = params[1];
            urlParameters.put("booking_id", bookingID);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> engineerProfile(Map<String, String> urlParameters, String[] params) {
        try {

            String engineerID = params[1];
            urlParameters.put("engineer_id", engineerID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> sparePartOrder(Map<String, String> urlParameters,
                                                 String[] params) {
        try {
            urlParameters.put("partner_id", params[1]);
            urlParameters.put("service_id", params[2]);
            urlParameters.put("booking_id", params[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partTypeOnModelNumber(Map<String, String> urlParameters,
                                              String[] params) {
        try {
            urlParameters.put("model_number_id", params[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> getSparePartName(Map<String, String> urlParameters,
                                                     String[] params) {
        try {
            urlParameters.put("part_type", params[1]);
            urlParameters.put("partner_id", params[2]);
            urlParameters.put("service_id", params[3]);
            if(params.length>4) {
                Log.d("resp","length = "+params.length);
                urlParameters.put("model_number_id", params[4]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> submitSparePartsOrder(Map<String, String> urlParameters,
                                                       String[] params) {
        try {
            Log.d("aaaaaa","aentID GETTOKEN  = "+params[2]);

            urlParameters.put("submitSparePartsOrder", params[1]);
            urlParameters.put("sc_agent_id", params[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> symptomCompleteBooking(Map<String, String> urlParameters,
                                                     String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("service_id", params[2]);
            urlParameters.put("partner_id", params[3]);
            urlParameters.put("request_type", params[4]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> defectCompleteBooking(Map<String, String> urlParameters,
                                                      String[] params) {
        try {

            urlParameters.put("technical_problem", params[1]);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> solutionCompleteBooking(Map<String, String> urlParameters,
                                                     String[] params) {
        try {

            urlParameters.put("technical_symptom", params[1]);
            urlParameters.put("technical_defect", params[2]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }


    public Map<String, String> bookingProductDetails(Map<String, String> urlParameters,
                                                       String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("brand", params[2]);
            urlParameters.put("partner_id", params[3]);
            urlParameters.put("service_id", params[4]);
            urlParameters.put("service_center_id", params[5]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> completeBookingByEngineer(Map<String, String> urlParameters,
                                                     String[] params) {
        try {

            urlParameters.put("completeBookingByEngineer", params[1]);
            urlParameters.put("engineer_id", params[2]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> updateBookingReasons(Map<String, String> urlParameters,
                                                         String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> updateBookingByEngineer(Map<String, String> urlParameters,
                                                    String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("reason", params[2]);
            urlParameters.put("remark", params[3]);
            urlParameters.put("booking_date", params[4]);
            urlParameters.put("partner_id", params[5]);
            urlParameters.put("service_center_id", params[6]);
            urlParameters.put("days", "0");
            urlParameters.put("spare_shipped", true+"");



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }

    public Map<String, String> paytmAmountByEngineer(Map<String, String> urlParameters,
                                                       String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> validateSerialNumber(Map<String, String> urlParameters,
                                                     String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("serial_number", params[2]);
            urlParameters.put("model_number", params[3]);
            urlParameters.put("price_tags", params[4]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> sparePartsWarrantyChecker(Map<String, String> urlParameters,
                                                    String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("partner_id", params[2]);
            urlParameters.put("booking_create_date", params[3]);
            urlParameters.put("request_type", params[4]);
            urlParameters.put("model_number", params[5]);
            urlParameters.put("purchase_date", params[6]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> checkSparePartsOrder(Map<String, String> urlParameters,
                                                         String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> warrantyCheckerAndCallTypeData(Map<String, String> urlParameters,
                                                    String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("partner_id", params[2]);
            urlParameters.put("service_id", params[3]);
            urlParameters.put("primary_contact", params[4]);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> submitWarrantyCheckerAndEditCallType(Map<String, String> urlParameters,
                                                              String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("model_number", params[2]);
            urlParameters.put("purchase_date", params[3]);
            urlParameters.put("prices", params[4]);
            urlParameters.put("request_types", params[5]);
            urlParameters.put("sc_agent_id", params[6]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> spareConsumptionData(Map<String, String> urlParameters,
                                                                    String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            if( params[2]!=null) {
                urlParameters.put("pre_consume_req", params[2]);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> wrongSparePartsName(Map<String, String> urlParameters,
                                                    String[] params) {
        try {

            urlParameters.put("service_id", params[1]);
            urlParameters.put("partner_id", params[2]);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> getBookingDetails(Map<String, String> urlParameters,
                                                   String[] params) {
        try {

            urlParameters.put("booking_id", params[1]);
            urlParameters.put("booking_status", params[2]);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> searchData(Map<String, String> urlParameters,
                                                 String[] params) {
        try {

            urlParameters.put("engineer_id", params[1]);
            urlParameters.put("service_center_id", params[2]);
            urlParameters.put("search_value", params[3]);
            urlParameters.put("engineer_pincode", params[4]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> incentiveEearnedBookings(Map<String, String> urlParameters,
                                          String[] params) {
        try {

            urlParameters.put("engineer_id", params[1]);
            urlParameters.put("service_center_id", params[2]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> todaysSlotBookings(Map<String, String> urlParameters,
                                                        String[] params) {
        try {

            urlParameters.put("engineer_id", params[1]);
            urlParameters.put("service_center_id", params[2]);
            urlParameters.put("engineer_pincode", params[3]);
            urlParameters.put("booking_slot", params[4]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> usernotifications(Map<String, String> urlParameters,
                                                  String[] params) {
        try {

            urlParameters.put("mobile", params[1]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partners(Map<String, String> urlParameters,
                                                 String[] params) {
        try {

            urlParameters.put("mobile", params[1]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partnerAppliances(Map<String, String> urlParameters,
                                        String[] params) {
        try {

            urlParameters.put("partner_id", params[1]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partnerappliancesModels(Map<String, String> urlParameters,
                                                 String[] params) {
        try {
            urlParameters.put("service_id", params[1]);
            urlParameters.put("partner_id", params[2]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partnerappliancesModelsPartTypes(Map<String, String> urlParameters,
                                                       String[] params) {
        try {
            urlParameters.put("service_id", params[1]);
            urlParameters.put("partner_id", params[2]);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> partnerappliancesModelsPartTypesInventory(Map<String, String> urlParameters,
                                                                String[] params) {
        try {
            urlParameters.put("service_id", params[1]);
            urlParameters.put("partner_id", params[2]);

            urlParameters.put("part_type", params[3]);





        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlParameters;
    }
    public Map<String, String> submitPrevPartsConsumption(Map<String, String> urlParameters,
                                                                         String[] params) {
        try {
            urlParameters.put("submitPreviousPartsConsumption", params[1]);






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
