package com.around.engineerbuddy.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.CustomFragmentPageAdapter;


public class FragmentLoader extends BMAFragment {

    private static final String TAG = FragmentLoader.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
public CustomFragmentPageAdapter customFragmentPageAdapter;
public int index=3;


    public FragmentLoader() {

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loader, container, false);

        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.view_pager);

        this.customFragmentPageAdapter=new CustomFragmentPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(this.customFragmentPageAdapter);
        viewPager.setCurrentItem(index);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.morning);
        tabLayout.getTabAt(1).setIcon(R.drawable.afternoon);
        tabLayout.getTabAt(2).setIcon(R.drawable.evening);
        tabLayout.getTabAt(3).setIcon(R.drawable.alltask);
        return view;
    }
    public void loadTab(int index){
        viewPager.setCurrentItem(index);
    }
    public Fragment getCurrentPage(){
        return this.customFragmentPageAdapter.getItem(this.viewPager.getCurrentItem());
        //for (int i = 0; i < tabLayout.getTabCount(); i++) {

        //}
    }

    @Override
    public void startSearch(String filterStr) {
        if (getCurrentFragment() != null) {
            getCurrentFragment().startSearch(filterStr);
        }
    }
    public BMAFragment getCurrentFragment() {
        return this.getFragmentAtIndex(this.viewPager != null ? this.viewPager.getCurrentItem() : 0);
    }

    public BMAFragment getFragmentAtIndex(int index) {
        return this.customFragmentPageAdapter != null ? this.customFragmentPageAdapter.getItem(index) : null;
    }
}
