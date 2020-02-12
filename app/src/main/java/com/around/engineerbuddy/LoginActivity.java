package com.around.engineerbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.activity.MainActivity;
import com.around.engineerbuddy.component.BMAUrlSelectionDialog;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity implements ApiResponse {
    //Mobile Number validation Pattern
    private static final Pattern sPattern = Pattern.compile("^[6-9]{1}[0-9]{9}$");
    public ConnectionDetector cd;
    public EditText phone_number, password;
    public Button login_button;
    public Misc misc;
    public SharedPreferences sharedPrefs;
    public SharedPreferences.Editor editor;
    private HttpRequest httpRequest;
    ImageView loginAppLogo;
    private int ttlTapToOpenAppUrlDlg = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //MainActivityHelper.setApplicationObj(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPrefs = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);//getSharedPreferences(SplashActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        cd = new ConnectionDetector(this);
        misc = new Misc(this);
        misc.checkAndLocationRequestPermissions();

        phone_number = (EditText) findViewById(R.id.phonenumber);
        password = (EditText) findViewById(R.id.password);
        this.loginAppLogo = findViewById(R.id.loginAppLogo);

        login_button = (Button) findViewById(R.id.login_button);
        BMAUIUtil.setBackgroundRect(login_button, getResources().getColor(R.color.colorPrimary), R.dimen._50dp);
        phone_number.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String mobileNo = phone_number.getText().toString();
                // IF Mobile Number Validation Pass then change Color of submit button

                //  if (isValidPhone(mobileNo)) {

//                    if (phone_number.getText().length() > 9 && password.getText().length() > 6) {
//
//                        login_button.setEnabled(true);
//                        login_button.setTextColor(Color.parseColor("#FFFFFF"));
//                        login_button.setBackgroundColor(Color.parseColor("#32475A"));
//                    }
//
//                } else {
//                    login_button.setEnabled(false);
//                    login_button.setTextColor(Color.parseColor("#32475A"));
//                    login_button.setBackgroundColor(Color.parseColor("#D6D7D7"));
//                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String mobileNo = phone_number.getText().toString();
                // IF Mobile Number Validation Pass then change Color of submit button

//                if (isValidPhone(mobileNo)) {
//
//                    if (phone_number.getText().length() > 9 && password.getText().length() > 6) {
//
//                        login_button.setEnabled(true);
//                        login_button.setTextColor(Color.parseColor("#FFFFFF"));
//                        login_button.setBackgroundColor(Color.parseColor("#32475A"));
//                    }
//
//                } else {
//                    login_button.setEnabled(false);
//                    login_button.setTextColor(Color.parseColor("#32475A"));
//                    login_button.setBackgroundColor(Color.parseColor("#D6D7D7"));
//                }
            }
        });
        this.loginAppLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttlTapToOpenAppUrlDlg++;
                if (ttlTapToOpenAppUrlDlg >= 10) {
                    openAppUrlDialog();
                }
            }
        });

    }

    /**
     * Validate Mobile Number through Regex
     *
     * @param s phone number
     * @return boolean
     */
    private boolean isValidPhone(String s) {
        return sPattern.matcher(s).matches();
    }

    /**
     * Call to api server to match Password and Mobile Number of any Engineer
     *
     * @param view View
     */
    public void loginProcess(View view) {
        if (phone_number.getText().toString().trim().length() == 0) {
            Toast.makeText(LoginActivity.this, getString(R.string.userIDValidation), Toast.LENGTH_SHORT).show();
            return;

        }
        if (password.getText().toString().trim().length() == 0) {
            Toast.makeText(LoginActivity.this, getString(R.string.passwordValidation), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isValidPhone(phone_number.getText().toString())) {
            String passwordText = password.getText().toString();
            if (!passwordText.isEmpty()) {
                if (cd.isConnectingToInternet()) {
                    boolean permissionGrant = misc.checkAndLocationRequestPermissions();
                    if (permissionGrant) {

                        String deviceToken = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.NOTIF_INFO).getString("device_firebase_token", "devicetoken");
                        Log.d("zzzzz", "deviceToken is = " + deviceToken);
                        httpRequest = new HttpRequest(LoginActivity.this, true);
                        httpRequest.delegate = LoginActivity.this;
                        httpRequest.execute("engineerLogin", phone_number.getText().toString(), password.getText().toString(),deviceToken);
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.askGPSPermission, Toast.LENGTH_SHORT).show();
                    }


                } else {
                    misc.NoConnection();

                }
            } else {
                Toast.makeText(LoginActivity.this, R.string.askPassword, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(LoginActivity.this, R.string.phoneValidation, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get Response from Server as string.
     * If status code is 0000 means Mobile Number and password exist and Engineer able to logged in App.
     *
     * @param httpReqResponse Api response
     */
    @Override
    public void processFinish(String httpReqResponse) {
        Log.d("vvvvvvvv", "response ...... " + httpReqResponse);
        if (httpReqResponse.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(httpReqResponse);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    httpRequest.progress.dismiss();

                    JSONObject response = new JSONObject(jsonObject.getString("response"));
                    //Log.w(TAG, response.getString("agent_id"));
                    //Internally store data in mobile
                    editor = sharedPrefs.edit();
                    editor.putString("isLogin", "1");
                    editor.putString("agentID", response.getString("agent_id"));
                    editor.putString("agentName", response.getString("agent_name"));
                    editor.putString("scAgentID", response.getString("sc_agent_id"));
                    editor.putString("service_center_id", response.getString("service_center_id"));
                    editor.putString("engineerID", response.getString("entity_id"));
                    editor.putString("phoneNumber", response.getString("user_id"));

                    //editor.commit();
                    editor.apply();

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, response.getString("agent_id"));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, response.getString("agent_name"));
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    SharedPreferences.Editor editor = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).edit();
                    editor.putString("service_center_id", response.getString("service_center_id"));
                    editor.putString("engineerID", response.getString("entity_id"));
                    editor.putString("agent_name", response.getString("agent_name"));
                    editor.putString("scAgentID", response.getString("sc_agent_id"));
                    editor.commit();
                    intent.putExtra("service_center_id", response.getString("service_center_id"));
                    intent.putExtra("agent_name", response.getString("agent_name"));
                    intent.putExtra("engineerID", response.getString("entity_id"));
                    intent.putExtra("scAgentID", response.getString("sc_agent_id"));
                    intent.putExtra("userId", phone_number.getText().toString().trim());
                    startActivity(intent);
                    finish();

                } else {

                    // String result=jsonObject.getString("result");

                    misc.showDialog(R.string.loginFailedTitle, jsonObject.getString("result"));//;R.string.passwordNotMatch);
                    httpRequest.progress.dismiss();
                }
            } catch (JSONException e) {
                misc.showDialog(R.string.loginFailedTitle, R.string.loginFailedMsg);
                httpRequest.progress.dismiss();
            }
        } else {
            misc.showDialog(R.string.serverConnectionFailedTitle, R.string.serverConnectionFailedMsg);
            httpRequest.progress.dismiss();
        }
    }

    private void openAppUrlDialog() {
        BMAUrlSelectionDialog bmaUrlSelectionDialog = new BMAUrlSelectionDialog(this);
        bmaUrlSelectionDialog.show();
        ttlTapToOpenAppUrlDlg = 0;
    }
}
