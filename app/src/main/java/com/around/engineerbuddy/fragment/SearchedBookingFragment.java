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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
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

public class SearchedBookingFragment extends BMAFragment {


    TextView tomorrowText, bookingText, countBooking;
    ImageView bookingTypeImage;
    View devider;
    LinearLayout bookingHeaderLayout, noDataToDisplayLayout;
    RecyclerView recyclerView;
    boolean isMissed;
    // ProgressBar progressBar;
    BookingInfo bookingInfo;
    BMARecyclerAdapter bmaRecyclerAdapter;
    ArrayList<EOBooking> searchedBookingList = new ArrayList<>();
    String filterStr;
    //////pk.eyJ1IjoidWtzYWgiLCJhIjoiY2p2cW5iaHZnMHlpYjQ0cWw4M2Y3eDRwcyJ9.MPePwKBaJVKg4BTHhYgMFA


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {

        this.isMissed = getArguments().getBoolean("isMissed");
        this.searchedBookingList = this.getArguments().getParcelableArrayList("searchedBookingList");
        this.filterStr = getArguments().getString("filterStr");
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
        this.noDataToDisplayLayout = this.view.findViewById(R.id.nodataToDisplayLayout);

        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.getBookingList(), recyclerView, this, R.layout.tomorrow_item_row);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);

        this.dataToView();


        return this.view;
    }

    // HttpRequest httpRequest;


    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        RelativeLayout rowLayout = itemView.findViewById(R.id.rowLayout);
        LinearLayout dateLayout = itemView.findViewById(R.id.dateLayout);

        BMAmplitude.saveUserAction("SearchedBooking", "SearchedBooking");
        EOBooking eoBooking = (EOBooking) rowObject;
        dateLayout.setBackgroundColor(eoBooking.internal_status.equalsIgnoreCase(BMAConstants.INTERNAL_SATATUS_CANCELLED) || eoBooking.current_status.equalsIgnoreCase("Cancelled") ? BMAUIUtil.getColor(R.color.blue_black) : BMAUIUtil.getColor(R.color.missedBookingcolor));
        TextView name = itemView.findViewById(R.id.name);
        TextView address = itemView.findViewById(R.id.address);
        TextView brandName = itemView.findViewById(R.id.brandName);
        TextView bookingID = itemView.findViewById(R.id.chargableservice);
        bookingID.setText(eoBooking.bookingID);
        TextView serviceName = itemView.findViewById(R.id.serviceName);
        TextView date = itemView.findViewById(R.id.date);
        TextView distance = itemView.findViewById(R.id.distance);
        TextView chargeableAmount = itemView.findViewById(R.id.chargeableAmount);
        name.setText(eoBooking.name);
        address.setText(eoBooking.bookingAddress);
        brandName.setText(eoBooking.applianceBrand);
        serviceName.setText(eoBooking.services + "-" + eoBooking.requestType);
        date.setText(eoBooking.bookingDate);
        distance.setText(eoBooking.bookingDistance);
        chargeableAmount.setText("" + eoBooking.dueAmount);
        ImageView phone = itemView.findViewById(R.id.phone);
        ImageView mapIcon = itemView.findViewById(R.id.mapLocArrow);
        mapIcon.setTag(eoBooking);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditBooking((EOBooking) v.getTag())) {
                    Fragment fragment = new BMAMapFragment();
                    Bundle bundl = new Bundle();
                    bundl.putParcelable("eoBooking", eoBooking);
                    bundl.putString(BMAConstants.HEADER_TXT, "");
                    fragment.setArguments(bundl);
                    getMainActivity().updateFragment(fragment, true);
                }
            }
        });
        phone.setTag(eoBooking);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditBooking((EOBooking) v.getTag())) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                        BMAAlertDialog dialog = new BMAAlertDialog(getActivity(), true, false) {
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
            }
        });

        rowLayout.setTag(eoBooking);
        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditBooking((EOBooking) v.getTag())) {
                    Fragment fragment = new BookingDetailFragment();
                    Bundle bundl = new Bundle();
                    bundl.putParcelable("booking", eoBooking);
                    bundl.putString(BMAConstants.HEADER_TXT, "");
                    fragment.setArguments(bundl);

                    getMainActivity().updateFragment(fragment, true, null);
                }
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void dataToView() {
        //    this.countBooking.setText("" + (this.getBookingList().size() < 10 ? "0" + this.getBookingList().size() : this.getBookingList().size()));
        this.bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.getBookingArray(), recyclerView, this, R.layout.tomorrow_item_row);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(bmaRecyclerAdapter);
//        if(isSetDistance) {
//            this.updateDistance();
//        }
        //   this.httpRequest.progress.dismiss();

    }


    public void noitifyDataSetChangedAdapter() {
        //  httpRequest.progress.dismiss();
        this.bmaRecyclerAdapter.notifyDataSetChanged();
    }


    public ArrayList<EOBooking> getBookingList() {
        return this.searchedBookingList != null ? searchedBookingList : new ArrayList<>();

    }

    private ArrayList<EOBooking> getBookingArray() {
        for (EOBooking eoBooking : getBookingList()) {
            eoBooking.name = BMAUtil.getTitleString(eoBooking.name);
        }
        return getBookingList();
    }


    private void doCall(String mobNumber) {

        //By Nitin Sir
        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), true, false) {
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

    public void distance() {//String originPincode, String destPincode, int position) {
//        String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india,"+originPincode+"&destinations=india,"+destPincode+"&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
        for (EOBooking eoBooking : this.getBookingList()) {
            String distnaceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=india," + eoBooking.pincode + "&destinations=india," + "110015" + "&key=AIzaSyDYYGttub8nTWcXVZBG9iMuQwZfFaBNcbQ";
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distnaceURL);
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
            EOGeoAddressEntity eoGeoAddressEntity = BMAGson.store().getObject(EOGeoAddressEntity.class, result);
            if (eoGeoAddressEntity.status.equalsIgnoreCase("OK"))
                for (EOBooking eoBooking : getBookingList()) {
                    if (eoGeoAddressEntity.origin_addresses.get(0).contains(eoBooking.pincode)) {
                        if (eoGeoAddressEntity.rows.get(0).elements.get(0).status.equalsIgnoreCase("OK")) {
                            eoBooking.distnace = eoGeoAddressEntity.rows.get(0).elements.get(0).distance.text.split(" ")[0];
                        }

                    }
                }

            SearchedBookingFragment.this.bmaRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (data.getBooleanExtra("isCancelled", false)) {
          //  if (this.getBookingList().size() == 1) {

                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, new Intent());
                getFragmentManager().popBackStack();
//            } else {
//                getSearchedData();
//            }
        }
    }

    public boolean isEditBooking(EOBooking eoBooking) {

        if(eoBooking.internal_status.equalsIgnoreCase(BMAConstants.INTERNAL_SATATUS_CANCELLED)){
            Toast.makeText(getContext(), "You can not edit this booking due to " + eoBooking.current_status + " status ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eoBooking.current_status.equalsIgnoreCase("Pending") || eoBooking.current_status.equalsIgnoreCase("Rescheduled")) {
            return true;
        }
        Toast.makeText(getContext(), "You can not edit this booking due to " + eoBooking.current_status + " status ", Toast.LENGTH_SHORT).show();
        return false;

    }

    HttpRequest httpRequest;

    private void getSearchedData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SearchedBookingFragment.this;
        // this.actionID = "searchData";
        //  Log.d("aaaaaaa"," All Task engineerID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID","abcfegd")+"    service id = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null));
        httpRequest.execute("searchData", MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null), filterStr, getMainActivity().getPinCode());//,batteryLevel+"");

    }


}
