package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.FilterFriends.FilterFragment;
import com.gic.memorableplaces.R;

import java.util.ArrayList;
import java.util.HashMap;

import me.grantland.widget.AutofitTextView;

import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmSelectedValues;


public class FiltersRecyclerViewAdapter extends RecyclerView.Adapter<FiltersRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "MessagesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> mFilterNameList;
    private ArrayList<String> alsFieldsSelected;
    private ArrayList<Integer> mIconList;
    private Context mContext;
    private HashMap<String, Boolean> mSelectedFiltersHashMap;
    private OnFilterClickListener MyOnFilterClickListener;
    private String CurrentFrag;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnFilterClickListener mOnFilterClickListener;
        public AutofitTextView ATV_FilterName, ATV_NumberOfValues;
        public RelativeLayout relativeLayout;
        public ImageView IV_Icon;
        public View Dot_Filter,V_Extension;

        public MainFeedViewHolder(@NonNull View itemView, OnFilterClickListener onFilterClickListener) {
            super(itemView);
            mOnFilterClickListener = onFilterClickListener;
            ATV_FilterName = itemView.findViewById(R.id.atv_filter_name);
            ATV_NumberOfValues = itemView.findViewById(R.id.ATV_NoOfFilters);
            IV_Icon = itemView.findViewById(R.id.Icon_IV);
            relativeLayout = itemView.findViewById(R.id.RL_FILTER_NAME);
            Dot_Filter = itemView.findViewById(R.id.dot_no_of_filter);
            V_Extension = itemView.findViewById(R.id.IV_Extension);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnFilterClickListener.onItemClick(getAdapterPosition(), ATV_FilterName, ATV_NumberOfValues);
        }
    }

    public interface OnFilterClickListener {
        void onItemClick(int position, AutofitTextView autofitTextView, AutofitTextView NumberOfValues);
    }

    public FiltersRecyclerViewAdapter(String CurrentField, ArrayList<String> filterNameList, ArrayList<Integer> IconList, ArrayList<String> SelectedFields, HashMap<String, Boolean> SelectedFiltersHashMap, Context context, OnFilterClickListener onFilterClickListener) {
        mFilterNameList = filterNameList;
        mIconList = IconList;
        alsFieldsSelected = SelectedFields;
        mContext = context;
        CurrentFrag = CurrentField;
        mSelectedFiltersHashMap = SelectedFiltersHashMap;
        MyOnFilterClickListener = onFilterClickListener;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filters, parent, false);
        return new MainFeedViewHolder(v, MyOnFilterClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        Log.d(TAG, "onBindViewHolder: CurrentFrag: " + CurrentFrag);
        Log.d(TAG, "onBindViewHolder: position: " + mFilterNameList.get(position));
        if(CurrentFrag.equals(mFilterNameList.get(position))){

           holder.V_Extension.setVisibility(View.VISIBLE);
           holder.relativeLayout.setBackgroundResource(R.drawable.rounded_corners_tl_bl);
           holder.IV_Icon.setVisibility(View.VISIBLE);
           holder.IV_Icon.setImageResource(mIconList.get(position));
           holder.ATV_FilterName.setTextColor(Color.WHITE);

        }
        holder.ATV_FilterName.setText(mFilterNameList.get(position));

        Log.d(TAG, String.format("onBindViewHolder: Selected Filter HM %s", mSelectedFiltersHashMap));
        if (mSelectedFiltersHashMap.get(mFilterNameList.get(position))) {
            holder.relativeLayout.setBackgroundResource(R.drawable.rounded_corners_tl_bl);
            holder.ATV_FilterName.setTextColor(Color.WHITE);
            holder.Dot_Filter.setVisibility(View.VISIBLE);
            holder.IV_Icon.setVisibility(View.VISIBLE);
            holder.IV_Icon.setImageResource(mIconList.get(position));
            holder.ATV_NumberOfValues.setVisibility(View.VISIBLE);
            Log.d(TAG, String.format("onBindViewHolder: Selected Values: %s", hmSelectedValues));
            Log.d(TAG, String.format("onBindViewHolder: mFilterNameList: %s", mFilterNameList));
            Log.d(TAG, String.format("onBindViewHolder: position filter: %s", mFilterNameList.get(position)));
            holder.ATV_NumberOfValues.setText(String.valueOf(hmSelectedValues.get(FilterFragment.ConvertFilterNameToField(mFilterNameList.get(position))).size()));

        }
    }

    @Override
    public int getItemCount() {
        return mFilterNameList.size();
    }



}


