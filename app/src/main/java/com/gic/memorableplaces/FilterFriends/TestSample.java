

package com.gic.memorableplaces.FilterFriends;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.R;


public class TestSample extends Fragment {
    private static final String TAG = "TestSample";

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_find_friends_results_shrinked_2, container, false);
        Log.d(TAG, "onCreateView: LikesAndMessagesFragment");



        return view;
    }
}

