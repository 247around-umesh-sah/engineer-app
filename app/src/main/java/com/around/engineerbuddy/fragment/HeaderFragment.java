package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.activity.MainActivity;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.util.BMAConstants;

public class HeaderFragment extends BMAFragment {

    TextView title;
    String headerTxt;
    BMAFontViewField barNavigation,backButton;
    Bundle argument;
    ImageView headerTitleImage;
    public int headerImageDrawable;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.argument=this.getArguments();
        if(this.argument!=null) {
            this.headerTxt = this.argument.getString(BMAConstants.HEADER_TXT, null);
            this.headerImageDrawable=this.argument.getInt("drawable",0);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        this.view= inflater.inflate(R.layout.header, container, false);
        this.title=this.view.findViewById(R.id.title);
        this.title.setText(this.headerTxt!=null ? this.headerTxt : getString(R.string.app_name));
        barNavigation=this.view.findViewById(R.id.barNavigation);
        backButton=this.view.findViewById(R.id.backBtn);
        this.headerTitleImage=this.view.findViewById(R.id.headerTitleImage);
        if(this.headerImageDrawable!=0){
            this.setHeaderTitleImage(this.headerImageDrawable);
        }
        barNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).showSlider();
        }});
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });
        this.changeNavIcon(this.argument);
        return this.view;
    }
        private void changeNavIcon(Bundle bundle){
        if(bundle !=null && bundle.getString(BMAConstants.menu_id)!=null){
            backButton.setVisibility(View.GONE);
            barNavigation.setVisibility(View.VISIBLE);

        }else{
            backButton.setVisibility(View.VISIBLE);
            barNavigation.setVisibility(View.GONE);
        }
    }

    public void setHeaderTitleImage(int imageDrawable) {
        this.headerTitleImage.setBackground(getResources().getDrawable(imageDrawable));
        this.headerTitleImage.setVisibility(View.VISIBLE);
    }
}
