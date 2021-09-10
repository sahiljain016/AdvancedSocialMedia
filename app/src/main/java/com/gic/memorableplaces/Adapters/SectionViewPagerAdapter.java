package com.gic.memorableplaces.Adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

// CLASS THAT STORES FRAGMENTS FOR TABS

public class SectionViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "Section View Pager Adapter";
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SectionViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "getItem: " + position);
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void AddFragment(Fragment fragment){

        mFragmentList.add(fragment);
        Log.d(TAG, "AddFragment: " + mFragmentList);
    }
}
