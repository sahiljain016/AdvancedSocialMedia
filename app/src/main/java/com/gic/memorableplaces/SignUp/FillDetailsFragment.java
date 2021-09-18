package com.gic.memorableplaces.SignUp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.FillDetailsRecyclerViewAdapter;
import com.gic.memorableplaces.R;

import me.grantland.widget.AutofitTextView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class FillDetailsFragment extends Fragment {
    private static final String TAG = "FillDetailsFragment";

    private RecyclerView arvCourses;
    private AutofitTextView ATV_TITLE;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_details, container, false);
        Log.d(TAG, "onCreateView: PrepareCardFragment");

        arvCourses = view.findViewById(R.id.RV_DETAILS_FD);
        ATV_TITLE = view.findViewById(R.id.ATV_TITLE_FD);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Abril_fatface.ttf");
        Typeface tf1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Capriola.ttf");
        ATV_TITLE.setTypeface(tf, Typeface.NORMAL);

        ZoomRecyclerLayout mLayoutManager;
        mLayoutManager = new ZoomRecyclerLayout(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setStackFromEnd(true);
        arvCourses.setHasFixedSize(true);
        arvCourses.setLayoutManager(mLayoutManager);

        FillDetailsRecyclerViewAdapter aCourses = new FillDetailsRecyclerViewAdapter(getActivity());

        arvCourses.setAdapter(aCourses);
        aCourses.notifyDataSetChanged();

        return view;
    }


}
