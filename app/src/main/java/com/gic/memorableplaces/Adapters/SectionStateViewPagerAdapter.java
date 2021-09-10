package com.gic.memorableplaces.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStateViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();

    public SectionStateViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    //RESPONSIBLE FOR ADDING DESIRED FRAGMENT
    public void AddFragments(Fragment fragment, String fragmentName) {
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size() - 1);
        mFragmentNumbers.put(fragmentName, mFragmentList.size() - 1);
        mFragmentNames.put(mFragmentList.size() - 1, fragmentName);

    }

    // RETURNS FRAGMENT NUMBER IF YOU INPUT FRAGMENT NAME
    public Integer getFragmentNumber(String FragmentName) {
        if (mFragmentNumbers.containsKey(FragmentName)) {
            return mFragmentNumbers.get(FragmentName);
        } else {
            return null;
        }
    }
    //OVERLOADING FUNCTION

    //RETURNS FRAGMENT NUMBER IF YOU INPUT FRAGMENT
    public Integer getFragmentNumber(Fragment fragment) {
        if (mFragmentNumbers.containsKey(fragment)) {
            return mFragmentNumbers.get(fragment);
        } else {
            return null;
        }
    }

    //RETURNS FRAGMENT Name IF YOU INPUT Number
    public String getFragmentName(Integer fragmentNumber) {
        if (mFragmentNames.containsKey(fragmentNumber)) {
            return mFragmentNames.get(fragmentNumber);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
