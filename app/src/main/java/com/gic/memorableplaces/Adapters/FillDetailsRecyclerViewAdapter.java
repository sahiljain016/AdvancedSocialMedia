package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class FillDetailsRecyclerViewAdapter extends RecyclerView.Adapter<FillDetailsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "CoursesRecyclerViewAdapter";

    //Variables
    private Context mContext;

    private ArrayList<String> alsCourseList;

    private OnCoursesClickListener MyOnCoursesClickListener;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnCoursesClickListener mOnCoursesClickListener;

        private AutofitTextView ATV_TITLE;
        public MainFeedViewHolder(@NonNull View itemView, OnCoursesClickListener onCoursesClickListener) {
            super(itemView);
            mOnCoursesClickListener = onCoursesClickListener;
            ATV_TITLE = itemView.findViewById(R.id.ATV_DETAIL_TITLE_FD);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnCoursesClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnCoursesClickListener {
        void onItemClick(int position);
    }

    public FillDetailsRecyclerViewAdapter( Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fill_details, parent, false);
        return new MainFeedViewHolder(v, MyOnCoursesClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called");
        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/CormorantGaramond.ttf");
        holder.ATV_TITLE.setTypeface(face, Typeface.NORMAL);


    }

    @Override
    public int getItemCount() {
        return ( 1);
    }


}

