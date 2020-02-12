package com.around.engineerbuddy.component;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.adapters.CancelBookingAdapter;
import com.around.engineerbuddy.entity.EOUrl;
import com.around.engineerbuddy.fragment.BMAFragment;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAFileUtil;

import java.util.ArrayList;

public class BMAUrlSelectionDialog extends Dialog implements BMARecyclerAdapter.BMAListRowCreator {

    RecyclerView urlselectionRceyclerView;
    BMARecyclerAdapter mAdapter;
    ArrayList<EOUrl> urlList;
    Button cancelButton, submitButton;
    SharedPreferences.Editor editor;

    public BMAUrlSelectionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.url_selection_dialog);
        this.cancelButton = findViewById(R.id.cancelButton);
        this.submitButton = findViewById(R.id.submitButton);
        this.setCanceledOnTouchOutside(false);
        editor = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).edit();
        //SharedPreferences.Editor editor = MainActivityHelper.applicationHelper().getSharedPrefrences().edit();

//        if (this.getWindow() != null) {
//            this.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
//        }
        String urlPrimaryKey = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("urlKey", null);
        urlList= BMAFileUtil.objectListFromFile("ssoconfig.json",EOUrl.class);

        if(urlPrimaryKey!=null && urlPrimaryKey.length()>0) {
            for (EOUrl eoUrl : urlList) {

                if (urlPrimaryKey.equalsIgnoreCase(eoUrl.primaryKey)) {
                    eoUrl.isChecked = true;

                } else {
                    eoUrl.isChecked = false;
                }
            }
        }else {
            for (EOUrl eoUrl : urlList) {
                if(eoUrl.isChecked) {
                    editor.putString("urlKey", eoUrl.primaryKey);
                    editor.commit();
                }
            }
        }
//        urlList = new ArrayList<>();
//        EOUrl eoUrl = new EOUrl();
//        eoUrl.url = "http://testapp.247around.com/engineerApi";
//        eoUrl.name = "Test";
//        eoUrl.primaryKey = "1";
//        if (urlPrimaryKey != null && urlPrimaryKey.equalsIgnoreCase("1")) {
//            eoUrl.ischecked = true;
//        }
//        urlList.add(eoUrl);
//        EOUrl eoUrl1 = new EOUrl();
//        eoUrl1.url = "https://aroundhomzapp.com/engineerApi";
//        eoUrl1.name = "Live";
//        eoUrl1.primaryKey = "2";
//        if (urlPrimaryKey != null && urlPrimaryKey.equalsIgnoreCase("2")) {
//            eoUrl1.ischecked = true;
//        }
//        urlList.add(eoUrl1);
        this.urlselectionRceyclerView = findViewById(R.id.urlselectionRceyclerView);
        try {
            urlselectionRceyclerView.setLayoutManager(new LinearLayoutManager(urlselectionRceyclerView.getContext()));
            mAdapter = new BMARecyclerAdapter(getContext(), urlList, urlselectionRceyclerView, this, R.layout.cancel_status_list);
            urlselectionRceyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BMAUrlSelectionDialog.this.dismiss();
                Log.d("aaaaaa", "cancelPK = " + MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("urlKey", null));
            }
        });
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.commit();
                BMAUrlSelectionDialog.this.dismiss();
            }
        });

    }


    @Override
    public <T> void createRow(View convertView, T rowObject) {


    }
    public String urlPrimaryKey;
    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOUrl eoUrl = (EOUrl) rowObject;

        Log.d("aaaaaa", "utrlcheck "+eoUrl.EnvironmentID+"      ischecked = "+eoUrl.isChecked);
        RelativeLayout radioButtonLayout = itemView.findViewById(R.id.radioButtonLayout);
        TextView status = itemView.findViewById(R.id.staus);
        RadioButton radioButton = itemView.findViewById(R.id.radioButton);
        status.setText(eoUrl.EnvironmentID);
        radioButton.setChecked(eoUrl.isChecked);
        radioButton.setClickable(false);
        radioButtonLayout.setTag(eoUrl);
        radioButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EOUrl eoUrl1 = ((EOUrl) v.getTag());
                urlPrimaryKey=eoUrl1.primaryKey;
                editor.putString("urlKey", eoUrl1.primaryKey);
                Log.d("aaaaaa", "primakey =  " + eoUrl1.primaryKey);
                for (EOUrl eoUrl2 : urlList) {
                    eoUrl2.isChecked = false;
                    if (eoUrl1.primaryKey == eoUrl2.primaryKey) {
                        eoUrl2.isChecked = true;
                    } else {
                        eoUrl2.isChecked = false;
                    }

                }

                //  eoUrl1.ischecked=eoUrl1.ischecked;
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public <T> void createHeader(View convertView, T rowObject) {

    }
}
