package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.BookingInfo;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EoWrongPart;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;
import com.around.engineerbuddy.util.BMAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MorningTasksFragment extends BMAFragment {
    RecyclerView recyclerView;
    LinearLayout nodataToDisplayLayout;
    BMARecyclerAdapter bmaRecyclerAdapter;
    HttpRequest httpRequest;
   // public ArrayList<EOBooking> todayMorningBooking = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        this.recyclerView=this.view.findViewById(R.id.recyclerView);
        this.view.findViewById(R.id.headerLayout).setVisibility(View.GONE);
        this.nodataToDisplayLayout=this.view.findViewById(R.id.nodataToDisplayLayout);
        BMAmplitude.saveUserAction("MorningTask","MorningTask");
        loadRecyclerView();
        if(getMainActivity().todayMorningBooking.size()==0){
            nodataToDisplayLayout.setVisibility(View.VISIBLE);
        }else {
            nodataToDisplayLayout.setVisibility(View.GONE);
        }
////            bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(),getBookingArray(), recyclerView, this, R.layout.tomorrow_item_row);
////            recyclerView.setHasFixedSize(true);
////            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////            recyclerView.setAdapter(bmaRecyclerAdapter);
//            loadRecyclerView();
//            //this.updateDistance();
//        }

        return this.view;
    }
    private void loadRecyclerView(){
        if( recyclerView!=null) {
            bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), getBookingArray(), recyclerView, this, R.layout.tomorrow_item_row);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(bmaRecyclerAdapter);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
//
//
//            if (getMainActivity().todayMorningBooking.size() == 0) {
//                if(nodataToDisplayLayout!=null)
//                    nodataToDisplayLayout.setVisibility(View.VISIBLE);
//            }else if(recyclerView!=null) {
            if(nodataToDisplayLayout!=null) {
                if (getMainActivity().todayMorningBooking.size() == 0) {
                    nodataToDisplayLayout.setVisibility(View.VISIBLE);
                }else{
                    nodataToDisplayLayout.setVisibility(View.GONE);
                }
            }
                loadRecyclerView();
//
//                //this.updateDistance();
//            }
         //   getRequest();
        }
    }

//    private void getRequest(){
//        httpRequest = new HttpRequest(getMainActivity(), true);
//        httpRequest.delegate = MorningTasksFragment.this;
//       // this.actionID = "engineerHomeScreen";
//        //  Log.d("aaaaaaa"," All Task engineerID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("engineerID","abcfegd")+"    service id = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("service_center_id", null));
//        httpRequest.execute("todaysSlotBookings", MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("engineerID", null), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("service_center_id", null), getMainActivity().getPinCode(),"10AM-1PM");
//
//    }

//    @Override
//    public void processFinish(String response) {
//        httpRequest.progress.dismiss();
//        if (response.contains("data")) {
//            JSONObject jsonObjectHttpReq;
//
//            try {
//                jsonObjectHttpReq = new JSONObject(response);
//
//                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
//                String statusCode = jsonObject.getString("code");
//                if (statusCode.equals("0000")) {
//                    httpRequest.progress.dismiss();
//                    String res = jsonObject.getString("response");
//                    this.todayMorningBooking = BMAGson.store().getList(EOBooking.class, res);
//                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
//                    if(this.todayMorningBooking!=null && this.todayMorningBooking.size()>0) {
//                        loadRecyclerView();
//                    }else {
//                        nodataToDisplayLayout.setVisibility(View.VISIBLE);
//                    }
//
//
//                }
//            } catch (JSONException e) {
//                httpRequest.progress.dismiss();
//                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
//
//
//                    @Override
//                    public void onWarningDismiss() {
//                        super.onWarningDismiss();
//                    }
//                };
//                bmaAlertDialog.show(R.string.loginFailedMsg);
//            }
//        }else{
//            httpRequest.progress.dismiss();
//            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
//
//
//                @Override
//                public void onWarningDismiss() {
//                    super.onWarningDismiss();
//                }
//            };
//            bmaAlertDialog.show(getString(R.string.somethingWentWrong));
//        }
//    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOBooking eoBooking= (EOBooking) rowObject;
        itemView.findViewById(R.id.dateLayout).setBackgroundColor(BMAUIUtil.getColor(R.color.loginButtonColor));
        RelativeLayout rowLayout = itemView.findViewById(R.id.rowLayout);
        ImageView phone=itemView.findViewById(R.id.phone);
        ImageView mapIcon=itemView.findViewById(R.id.mapLocArrow);
        TextView name=itemView.findViewById(R.id.name);
        TextView address=itemView.findViewById(R.id.address);
        TextView brandName=itemView.findViewById(R.id.brandName);
        TextView serviceName=itemView.findViewById(R.id.serviceName);
        TextView date = itemView.findViewById(R.id.date);
        TextView distance=itemView.findViewById(R.id.distance);
        TextView chargeableAmount=itemView.findViewById(R.id.chargeableAmount);
        TextView bookingID=itemView.findViewById(R.id.chargableservice);
        bookingID.setText(eoBooking.bookingID);
        date.setText(eoBooking.bookingDate);
        distance.setText(eoBooking.bookingDistance);
        chargeableAmount.setText(""+eoBooking.dueAmount);
        name.setText(eoBooking.name);
        address.setText(eoBooking.bookingAddress);
        brandName.setText(eoBooking.applianceBrand);
        serviceName.setText(eoBooking.services+"-"+eoBooking.requestType);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new BMAMapFragment();
                Bundle bundl = new Bundle();
                bundl.putParcelable("eoBooking", eoBooking);
                bundl.putString(BMAConstants.HEADER_TXT, "");
                fragment.setArguments(bundl);
                getMainActivity().updateFragment(fragment, true);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                    BMAAlertDialog dialog = new BMAAlertDialog(getActivity(), true,false) {
                        @Override
                        public void onConfirmation() {
                            this.dismiss();
//                            Intent myIntent = new Intent(Intent.ACTION_CALL);
//                            startActivity(myIntent);
                             doCall(eoBooking.primaryContact);
                        }
                    };
                    dialog.show(getString(R.string.enableCallFeatureValiadtion));
                }
                else
                {

                    doCall(eoBooking.primaryContact);
                }

            }
        });
        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BookingDetailFragment();
                Bundle bundl = new Bundle();
                bundl.putParcelable("booking", eoBooking);
                bundl.putString(BMAConstants.HEADER_TXT, " ");
                fragment.setArguments(bundl);

                getMainActivity().updateFragment(fragment, true, null);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // doCall();
                }
                return;
            }
        }
    }
    private void doCall(String number){
        BMAAlertDialog bmaAlertDialog=new BMAAlertDialog(getContext(),true,false){
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        };
        bmaAlertDialog.show(getString(R.string.callConfirmationMessage));


    }
    public void noitifyDataSetChangedAdapter(){
        this.bmaRecyclerAdapter.notifyDataSetChanged();
    }




    public ArrayList<EOBooking> getBookingList() {
        return getMainActivity().todayMorningBooking;
        //return this.todayMorningBooking;

    }
    private ArrayList<EOBooking> getBookingArray(){
        for(EOBooking eoBooking:getBookingList()){
            eoBooking.name=BMAUtil.getTitleString(eoBooking.name);
        }
        return getBookingList();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null ) {
            return;
        }

        if(data.getBooleanExtra("isCancelled",false)) {
          //  if (this.getBookingList().size() == 1) {

            getMainActivity().redirectToHomeMenu();
//                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, new Intent());
//                getFragmentManager().popBackStack();
//            } else {
//                getRequest();
//            }
        }
    }


}
