package com.around.engineerbuddy.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.around.engineerbuddy.fragment.AfternoonTasksFragment;
import com.around.engineerbuddy.fragment.AllTasksFragment;
import com.around.engineerbuddy.fragment.EveningTasksFragment;
import com.around.engineerbuddy.fragment.MorningTasksFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomFragmentPageAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 4;
    List<Fragment> fragmentList = new ArrayList<>();


    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MorningTasksFragment();
            case 1:
                return new AfternoonTasksFragment();
            case 2:
                return new EveningTasksFragment();
            case 3:
                return new AllTasksFragment();


        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "10:00-01:00";
            case 1:

                return "01:00-04:00";
            case 2:

                return "04:00-07:00";
            case 3:

                return "All Task";
        }
        return null;
    }
}
