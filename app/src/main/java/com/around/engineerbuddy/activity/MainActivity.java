package com.around.engineerbuddy.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.LoginActivity;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.fragment.BMAFragment;
import com.around.engineerbuddy.fragment.BMANotificationFragment;
import com.around.engineerbuddy.fragment.CancelledBookingFragment;
import com.around.engineerbuddy.fragment.CheckSparePartPrice;
import com.around.engineerbuddy.fragment.CovidFragment;
import com.around.engineerbuddy.fragment.FragmentLoader;
import com.around.engineerbuddy.fragment.HeaderFragment;
import com.around.engineerbuddy.fragment.ProfileFragment;
import com.around.engineerbuddy.helper.ApplicationHelper;
import com.around.engineerbuddy.util.BMAConstants;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LocationListener {


    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    public String engineerID, serviceCenterId, scAgentId;
    public ArrayList<EOBooking> todayMorningBooking = new ArrayList<>();
   public ArrayList<EOBooking> todayAfternoonBooking = new ArrayList<>();
   public ArrayList<EOBooking> todayEveningBooking = new ArrayList<>();
    public SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    private LocationManager mLocationManager;
    private Location currentLocation;
    String agentName;
    Misc misc;

    public FirebaseAnalytics mfireBaseAnalytics;

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfireBaseAnalytics=FirebaseAnalytics.getInstance(this);
//        mfireBaseAnalytics.setUserId(getIntent().getStringExtra("userId"));
//        Bundle bundleumesh=new Bundle();
//        bundleumesh.putString("U_umesh"," Kumar ah");
//        mfireBaseAnalytics.logEvent("logUmeshSah",bundleumesh);
//        mfireBaseAnalytics.setUserProperty("heloUmesh ","sendlogUmesh");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        BMAmplitude.initializeAmplitude(this, getApplication());
        //  Amplitude.getInstance().initialize(this, "918de58d720e22307e6021fb157c964e").enableForegroundTracking(getApplication());

        MainActivityHelper.setApplicationObj(this);
        this.engineerID = getIntent().getStringExtra("engineerID");
        this.serviceCenterId = getIntent().getStringExtra("service_center_id");
        this.scAgentId = getIntent().getStringExtra("scAgentID");
        if(MainActivityHelper.applicationHelper()!=null) {
            sharedPrefs = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragmentManager = getSupportFragmentManager();
        fragment = new FragmentLoader();
        Bundle bundle = new Bundle();
        bundle.putString(BMAConstants.menu_id, "home");
        fragment.setArguments(bundle);
        this.updateFragment(fragment);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView profileImageName = headerView.findViewById(R.id.profileImageName);
        // SharedPreferences sharedPreferences= MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO);
         if(sharedPrefs!=null) {
             this.agentName = sharedPrefs.getString("agent_name", "");
         }
         if(agentName!=null) {
             profileImageName.setText(agentName);
         }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {
                    fragment = new FragmentLoader();
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment);
                } else if (id == R.id.cancelled_booking) {
                    fragment = new CancelledBookingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.cancelledbooking));
                    bundle.putString(BMAConstants.menu_id, getString(R.string.cancelledbooking));
                    bundle.putBoolean("isCancelledBooking", true);
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);

                } else if (id == R.id.complete_booking) {
                    fragment = new CancelledBookingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.completedbooking));
                    bundle.putString(BMAConstants.menu_id, getString(R.string.completedbooking));
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);

                } else if (id == R.id.profile) {
                    fragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.menu_id, getString(R.string.completedbooking));
                    bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.profile));
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);

                } else if (id == R.id.nav_logout) {
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(MainActivity.this, true, false) {
                        @Override
                        public void onConfirmation() {
                            resetDeviceData();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            MainActivity.this.finish();
                        }
                    };
                    bmaAlertDialog.show(getString(R.string.logoutConfirmationMsg));
                }else  if (id == R.id.notification) {
                    fragment = new BMANotificationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.notification));
                    bundle.putString(BMAConstants.menu_id, getString(R.string.notification));
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);
                }else  if (id == R.id.checkSparePrice) {
                    fragment = new CheckSparePartPrice();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.checkSparePrice));
                    bundle.putString(BMAConstants.menu_id, getString(R.string.checkSparePrice));
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);
                }else  if (id == R.id.covid) {
                    fragment = new CovidFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BMAConstants.HEADER_TXT, "Covid 19");
                    bundle.putString(BMAConstants.menu_id,"Covid19");
                    fragment.setArguments(bundle);
                    MainActivity.this.updateFragment(fragment, true);
                }



                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void resetDeviceData() {
        String deviceToken= MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.NOTIF_INFO).getString("device_firebase_token",null);
        MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).edit().clear().commit();
        SharedPreferences.Editor editor= MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.NOTIF_INFO).edit();
        editor.putString("device_firebase_token", deviceToken);
        editor.commit();
    }

    public boolean closeDrawers() {
        if (this.drawerLayoutID() != null && this.drawerLayoutID().isDrawerOpen(GravityCompat.START)) {
            this.drawerLayoutID().closeDrawers();
            return true;
        }
        return false;
    }

    long previousTime;

    public void showSlider() {
        if (drawerLayoutID().isDrawerOpen(GravityCompat.START)) {
            drawerLayoutID().closeDrawer(GravityCompat.START);
        } else {
            drawerLayoutID().openDrawer(GravityCompat.START);
        }
    }

    public DrawerLayout drawerLayoutID() {
        return findViewById(R.id.drawer_layout);
    }


    @Override
    protected void onStart() {
        Log.d("aaaaaa","onStartMainActivity");
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            BMAAlertDialog dialog = new BMAAlertDialog(this, true, false) {
                @Override
                public void onConfirmation() {
                    this.dismiss();
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            };
            dialog.show(getString(R.string.enableLocation));
        }
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        misc=new Misc(this);
        if(misc.checkAndLocationRequestPermissions()) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 10, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, this);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onBackPressed() {
        Log.d("aaaaa","MainActivity onback pressesd= "+getPageFragment());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.getPageFragment() != null) {//&& this.getPageFragment().onBackPressed()) {

                if (this.getPageFragment() instanceof FragmentLoader) {
                    FragmentLoader fragmentLoader = (FragmentLoader) this.getPageFragment();

                    if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
                        if (fragmentLoader.getCurrentPage().getClass().getSimpleName().equalsIgnoreCase("AllTasksFragment")) {
                            if (2000 + previousTime > (previousTime = System.currentTimeMillis())) {
                                moveTaskToBack(true);
                            } else {
                                Snackbar.make(findViewById(dataContainerResID()), getString(R.string.exitConfirmInfo), Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            this.redirectToHomeMenu();
                            //   super.onBackPressed();
                        }
                    } else {

                        if (fragmentLoader.getCurrentPage().getClass().getSimpleName().equalsIgnoreCase("AllTasksFragment")) {
                            if (2000 + previousTime > (previousTime = System.currentTimeMillis())) {
                                moveTaskToBack(true);
                            } else {
                                Snackbar.make(findViewById(dataContainerResID()), getString(R.string.exitConfirmInfo), Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            this.redirectToHomeMenu();
                        }
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public String getServiceCenterId() {
        return MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", "");

    }

    public String getEngineerId() {
        return MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", "");

    }

    public void redirectToHomeMenu() {
        //  this.closeDrawers();
        clearBackStack();
        FragmentLoader fragmentLoader = new FragmentLoader();
        Bundle bundle = new Bundle();
        bundle.putString(BMAConstants.menu_id, "home");
        fragmentLoader.setArguments(bundle);
        this.updateFragment(fragmentLoader, true, null);
    }

    public void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateFragment(Fragment fragment, Integer drawable) {
        this.updateFragment(fragment, false, drawable);

    }

    public void updateFragment(Fragment fragment) {
        this.updateFragment(fragment, false);

    }

    public void updateFragment(Fragment fragment, boolean isAddToBackStack) {
        this.updateFragment(fragment, isAddToBackStack, null);

    }

    public void updateFragment(Fragment fragment, boolean isAddToBackStack, Integer headerImageDrawable) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!(fragment instanceof FragmentLoader))
            fragment.setTargetFragment(this.getPageFragment(), 101);
        fragmentTransaction.replace(dataContainerResID(), fragment);

        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }


        Bundle fragmentBundle = fragment.getArguments();
        if (headerImageDrawable != null)
            fragmentBundle.putInt("drawable", headerImageDrawable);
        String headerTitleText;
        HeaderFragment headerFragment = new HeaderFragment();

        headerFragment.setArguments(fragmentBundle);
        fragmentTransaction.replace(R.id.headerFragmentConatiner, headerFragment);
        fragmentTransaction.commit();

    }

    public BMAFragment getPageFragment() {
        Fragment fragment = this.findFragmentById(dataContainerResID());
        return fragment != null ? (BMAFragment) fragment : null;
    }

    public Fragment findFragmentById(int fragmentContainerID) {
        return this.getSupportFragmentManager().findFragmentById(fragmentContainerID);
    }

    @IdRes
    protected int dataContainerResID() {
        try {
            return R.id.container;
        } catch (NoSuchFieldError e) {
            return 0;
        }
    }

    public void loadtabFragment(int index) {
        if (this.fragment != null && this.fragment instanceof FragmentLoader) {
            ((FragmentLoader) fragment).loadTab(index);
        }

    }

    public Fragment getAllTaskFragment() {
        return fragment != null && this.fragment instanceof FragmentLoader ? ((FragmentLoader) fragment).customFragmentPageAdapter.getItem(3) : null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("aaaaa","MAinactivity onRequestPermissionsResult");
        if (getPageFragment() != null)
            getPageFragment().onRequestPermissionsResult( requestCode,permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaaaa","MainActivity0");
        if (getPageFragment() != null)
            getPageFragment().onActivityResult(requestCode, resultCode, data);
    }

    public Location getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            currentLocation = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null) {
                currentLocation = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } else {
            currentLocation = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return currentLocation;
    }

    public String getPinCode() {
        if (misc == null)
            misc = new Misc(this);
        return misc.getLocationPinCode();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public void startSearch(String filterStr) {
        if (getPageFragment() != null) {
            getPageFragment().startSearch(filterStr);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("aaaaa","Onresume");
        if(MainActivityHelper.applicationHelper()==null) {
            MainActivityHelper.setApplicationHelper(new ApplicationHelper(getApplicationContext()));
        }
    }
}
