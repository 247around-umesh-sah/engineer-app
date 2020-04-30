package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.DirectionAPI;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOLatLong;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BMAMapFragment extends BMAFragment implements OnMapReadyCallback, LocationListener {

    public GoogleMap mMap;
    LocationManager locationManager;
    LinearLayout bbokingMapInfo;
    EOBooking eoBooking;
    boolean isNavigation;
    TextView distance;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.map_fragment, container, false);
      //  SupportMapFragment mapFragment = this.getChildFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment= (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        BMAmplitude.saveUserAction("BMAMapFragment","BMAMapFragment");
        this.bbokingMapInfo = this.view.findViewById(R.id.bookingMapInfoLayout);
        mapFragment.getMapAsync(this);
        View chileView = inflater.inflate(R.layout.tomorrow_item_row, null, false);
        TextView name = chileView.findViewById(R.id.name);
        chileView.findViewById(R.id.covidLayout).setVisibility(View.VISIBLE);
        TextView address = chileView.findViewById(R.id.address);
        TextView brandName = chileView.findViewById(R.id.brandName);
        TextView serviceName = chileView.findViewById(R.id.serviceName);
        TextView date = chileView.findViewById(R.id.date);
        this.distance = chileView.findViewById(R.id.distance);
        TextView bookingID=chileView.findViewById(R.id.chargableservice);

        if (eoBooking != null) {
            name.setText(eoBooking.name);
            address.setText(eoBooking.bookingAddress);
            brandName.setText(eoBooking.applianceBrand);
            serviceName.setText(eoBooking.services);
            date.setText(eoBooking.bookingDate);
            distance.setText(eoBooking.distnace);
            bookingID.setText(eoBooking.bookingID);
        }
        ImageView phone = chileView.findViewById(R.id.phone);
        ImageView mapIcon = chileView.findViewById(R.id.mapLocArrow);

        mapIcon.setBackground(getResources().getDrawable(R.drawable.direction_nav));

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), true, false) {
                    @Override
                    public void onConfirmation() {
                        super.onConfirmation();

                        if ( ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getMainActivity(), new String[]{Manifest.permission.CALL_PHONE}, 12);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + eoBooking.primaryContact));
                            startActivity(intent);
                        }

                    }
                };
                bmaAlertDialog.show(getString(R.string.callConfirmationMessage));


            }
        });
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                if(eoBooking.destLocation==null) {
                    eoBooking.destLocation = getLocation(eoBooking.bookingAddress + " " + eoBooking.pincode);
                }
                if(eoBooking.destLocation==null){
                    eoBooking.destLocation = getLocation(eoBooking.pincode);
                }
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+eoBooking.destLocation.getLatitude()+","+eoBooking.destLocation.getLongitude()+"&mode=l");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        this.bbokingMapInfo.addView(chileView);
        return this.view;
    }
    Runnable timeRun;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = this.mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        this.mMap.setMyLocationEnabled(true);
        this.locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            BMAAlertDialog dialog = new BMAAlertDialog(getActivity(), true, false) {
                @Override
                public void onConfirmation() {
                    this.dismiss();
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            };
            dialog.show(getString(R.string.enableLocation));
        }
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 100, BMAMapFragment.this);

       // startNavigation();
        if(getCurrentLocation()!=null) {
            Log.d("aaaaaa","getCurrent LOCC");

            showDestinationLocation();
            showCurrentLocation(11.5f);
            drawPath();
            showCovidLocation();

        }else {

            Handler timerHandler = new Handler();
            timerHandler.postDelayed(timeRun = new Runnable() {
                @Override
                public void run() {
                    try {
                        if(getCurrentLocation()!=null){

                            Log.d("aaaaaa","timehandler");

                            showDestinationLocation();
                            showCurrentLocation(11.5f);
                            drawPath();
                            showCovidLocation();
                            timerHandler.removeCallbacks(timeRun);
                        }
                        timerHandler.postDelayed(BMAMapFragment.this.timerRunnable, 1000); // run every second
                    } catch (Exception e) {
                        timerHandler.removeCallbacks(timeRun);
                    }
                }

            }, 0);

        }


    }
    ProgressDialog progressBar;
    private void showCovidLocation(){
//        ArrayList<String>cityList=new ArrayList<>();
//
//        cityList.add("Delhi");
//        cityList.add("Noida");
//        cityList.add("Tundla");
//        cityList.add("Kanpur");
//        cityList.add("Prayagraj");
//        cityList.add("Patna");

        progressBar=new ProgressDialog(getContext());
        progressBar.show();

        for (EOLatLong eoLatLong:eoBooking.covid_corrdinates){
            Log.d("aaaaa","for loop = "+eoLatLong.lat);
            showCovidPlace(eoLatLong.lat,eoLatLong.longi);
        }
        Handler timerHandler = new Handler();

        timerHandler.postDelayed(timeRun = new Runnable() {
            @Override
            public void run() {
                try {
                   progressBar.dismiss();
                    //timerHandler.postDelayed(BMAMapFragment.this.timerRunnable, 1000); // run every second
                } catch (Exception e) {
                    timerHandler.removeCallbacks(timeRun);
                }
            }

        }, 2000);
       // progressBar.dismiss();
    }
    MarkerOptions markerOptions=null;
    private void showCovidPlace(String latitude,String longitude){

       // this.mMap.clear();

            if(markerOptions==null) {
            markerOptions=new MarkerOptions();
            }
            //   markerOptions.title(getAddress(getDestinationLatLng()));
          //  markerOptions.title("COVID-19 Effected Area");
            LatLng positionLatlong=new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));//getCovidDestinationLatLng(city);
//        Log.d("aaaaa","covid loc = "+positionLatlong);
//        Log.d("aaaaa","covid loc Address  = "+getAddress(positionLatlong));

            markerOptions.position(positionLatlong);
            // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.source_hue_blue_map_icon));
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addCircle(new CircleOptions()
                    .center(positionLatlong)
                    .radius(3)
                    .strokeColor(Color.RED)
                    .fillColor(Color.RED)).setStrokeColor(Color.RED);
            mMap.addMarker(markerOptions).showInfoWindow();

         //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatlong,12.0f ));



    }
    LatLng covidLatlng;

    public LatLng getCovidDestinationLatLng(String city) {
//        if (covidLatlng == null)
            covidLatlng = new LatLng(getCovidLoaction(city).getLatitude(), getCovidLoaction(city).getLongitude());
        return covidLatlng;
    }

    private Location getCovidLoaction(String city){
        Location covidLocation=null;
                if(city!=null) {
                    covidLocation = this.getLocation(city);
                }
                Log.d("aaaaa","covid Locaion === "+covidLocation);
            return covidLocation;
    }

    //28.608025
    //77.367628
    Polyline polyline = null;

    @Override
    public void onLocationChanged(Location location) {


    }

    Handler timerHandler;
    Runnable timerRunnable;

    public void drawPath() {
        new DirectionAPI(getMainActivity(), mMap, sourceLatLng(getCurrentLocation()), getDestinationLatLng());
    }

    public void updateDistance() {

        timerHandler = new Handler();
        timerHandler.postDelayed(this.timerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mMap.clear();
                    showCovidLocation();
                    showDestinationLocation();
                    showCurrentLocation(14.0f);
                    drawPath();
                       BMAMapFragment.this.distance.setText(String.format("%.01f", (getCurrentLocation().distanceTo(desLocation)) / 1000));
                    timerHandler.postDelayed(BMAMapFragment.this.timerRunnable, 30000); // run every second
                } catch (Exception e) {
                    stopUpdateDistance();
                }
            }

        }, 0);
    }

    private void stopUpdateDistance() {
        if (this.timerHandler != null) {
            this.timerHandler.removeCallbacks(this.timerRunnable);
            timerHandler = null;
        }
    }

    Location desLocation;

    public Location getDestinationLoc() {
        if (desLocation == null) {
            if(eoBooking.bookingAddress!=null) {
                desLocation = this.getLocation(eoBooking.bookingAddress + eoBooking.pincode);
            }else {
                desLocation = this.getLocation( eoBooking.pincode);
            }
            if(desLocation==null){
                desLocation = this.getLocation(eoBooking.pincode);
            }
        }
        return desLocation;
    }

    LatLng desLatlng;

    public LatLng getDestinationLatLng() {
        if (desLatlng == null)
            desLatlng = new LatLng(getDestinationLoc().getLatitude(), getDestinationLoc().getLongitude());
        return desLatlng;
    }

    public LatLng sourceLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void startNavigation() {
       // updateDistance();

    }

    private void showDestinationLocation() {
        if(desLocation==getCurrentLocation()){
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(getAddress(getDestinationLatLng()));
       // markerOptions.title("COVID-19 Effected Area");
        markerOptions.position(getDestinationLatLng());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.source_hue_blue_map_icon));
//        markerOptions.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

//        this.mMap.addCircle(new CircleOptions()
//                .center(getDestinationLatLng())
//                .radius(300)
//                .strokeColor(Color.RED)
//                .fillColor(Color.RED)).setStrokeColor(Color.RED);
        mMap.addMarker(markerOptions).showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getDestinationLatLng(), 12.0f));

    }

    private void showCurrentLocation(float zoomvalu) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng sourceLatLng = sourceLatLng(getCurrentLocation());
        // markerOptions.title(getAddress(getDestinationLatLng()));
        markerOptions.position(sourceLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_hue_map_icon));
        mMap.addMarker(markerOptions).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getDestinationLatLng(), zoomvalu));

    }


    public void setCurrentLocation(Location location) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng sourceLatLng = sourceLatLng(location);
        markerOptions.position(sourceLatLng);
        // markerOptions.title(getAddress(latLng));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(desLocation != null ? R.drawable.source_hue_blue_map_icon : R.drawable.dest_hue_map_icon));
        mMap.addMarker(markerOptions);//.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, 12.5f));

//        if(desLocation!=null) {
//
//            //  PolylineOptions polylineOptions = new PolylineOptions();
////            LatLng destinationLatlng = new LatLng(desLocation.getLatitude(), desLocation.getLongitude());
//            //  polylineOptions.add(latLng).add(desLatlng);
//            MarkerOptions markerOptions1 = new MarkerOptions();
//            markerOptions1.title(getAddress(desLatlng));
//            markerOptions1.position(desLatlng);
//            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_hue_map_icon));
//            mMap.addMarker(markerOptions1).showInfoWindow();
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng,12.0f));
//
//            //if(isNavigation) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng,13.5f));
//
////            }else {
////                PolylineOptions polylineOptions = new PolylineOptions();
////                //// LatLng destinationLatlng = new LatLng(desLocation.getLatitude(), desLocation.getLongitude());
////                polylineOptions.add(latLng).add(desLatlng);
////                //// mMap.addPolyline(directionAPI.polylineOptions);
////                polyline= mMap.addPolyline(polylineOptions);
////
////            }
//        }
        //eoBooking.distnace = String.format("%.01f", (location.distanceTo(desLocation))/1000);
        if (desLocation != null)
            this.distance.setText(String.format("%.01f", (location.distanceTo(desLocation)) / 1000));
    }
    //AIzaSyC8jsv9tB5UYUUPyXPdim5PShenA0WASkg

    private String getAddress(LatLng latLng) {
        String addressText = "";
        Geocoder geocoder = new Geocoder(getMainActivity(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (address != null && address.size() > 0) {
                Address address1 = address.get(0);
                address1.getPostalCode();
                addressText = String.format("%s", address1.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressText;
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
}
