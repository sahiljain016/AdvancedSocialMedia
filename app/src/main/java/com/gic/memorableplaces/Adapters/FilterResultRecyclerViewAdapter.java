package com.gic.memorableplaces.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

import static android.view.View.GONE;

public class FilterResultRecyclerViewAdapter extends RecyclerView.Adapter<FilterResultRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "Filter1RecyclerViewAdapter";

    //Variables
    private final ArrayList<String> mValueList;
    private final HashMap<String, ArrayList<String>> SelectedValuesHM, HM_VALUES;
    private final OnValueClickListener MyOnValueClickListener;
    private final boolean mIsFiltered;
    private String SelectedField;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnValueClickListener mOnValueClickListener;
        //        public TextView mValueName;
//        public ImageView ValueTick;
//        public CardView CardView_Filter;
        public AutofitTextView ATV_filter_value, ATV_NoOfResults;
        public ImageView IV_checked_filter, IV_unchecked_filter;
        public RelativeLayout RL_filter;

        public MainFeedViewHolder(@NonNull View itemView, OnValueClickListener onValueClickListener) {
            super(itemView);
            mOnValueClickListener = onValueClickListener;
//            mValueName = itemView.findViewById(R.id.ValueName);
//            ValueTick = itemView.findViewById(R.id.ValueTick);
//            CardView_Filter = itemView.findViewById(R.id.CardView_Filter);
            ATV_filter_value = itemView.findViewById(R.id.ATV_Filter_value);
            ATV_NoOfResults = itemView.findViewById(R.id.ATV_NoOfFiltersResult);
            IV_checked_filter = itemView.findViewById(R.id.IV_filter_value_checked);
            IV_unchecked_filter = itemView.findViewById(R.id.IV_filter_value_unchecked);
            ATV_NoOfResults = itemView.findViewById(R.id.ATV_NoOfFiltersResult);
            RL_filter = itemView.findViewById(R.id.RL_FILTER);

            IV_unchecked_filter.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnValueClickListener.onItemClick(getAdapterPosition(), RL_filter, IV_unchecked_filter, IV_checked_filter, ATV_filter_value);
        }
    }

    public interface OnValueClickListener {
        void onItemClick(int position, RelativeLayout relativeLayout, ImageView UnChecked, ImageView Tick, AutofitTextView Value);
    }

    public FilterResultRecyclerViewAdapter(String CurrentField, ArrayList<String> ValueList, HashMap<String, ArrayList<String>> HM_Values, HashMap<String, ArrayList<String>> hmSelectedValues, OnValueClickListener onValueClickListener, boolean IsFiltered) {
        mValueList = ValueList;
        MyOnValueClickListener = onValueClickListener;
        mIsFiltered = IsFiltered;
        HM_VALUES = HM_Values;
        SelectedValuesHM = hmSelectedValues;
        SelectedField = CurrentField;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filter_1_values, parent, false);
        return new MainFeedViewHolder(v, MyOnValueClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        if (!mValueList.get(position).equals("No Results Available")) {


            if (mIsFiltered) {
                holder.RL_filter.setBackgroundResource(R.drawable.blue_green_gradient);
                holder.IV_checked_filter.setVisibility(View.VISIBLE);
                holder.itemView.setClickable(false);
            }
            Log.d(TAG, String.format("onBindViewHolder: Selected Values Hash Map: %s", SelectedValuesHM));

            if (!SelectedValuesHM.isEmpty()) {
                if (Objects.requireNonNull(SelectedValuesHM.get(SelectedField)).contains(mValueList.get(position))) {
                    holder.RL_filter.setBackgroundResource(R.drawable.blue_green_gradient);
                    holder.IV_checked_filter.setVisibility(View.VISIBLE);
                }
            }

            holder.ATV_filter_value.setText(mValueList.get(position));
            holder.ATV_NoOfResults.setText("Results: " + HM_VALUES.get(mValueList.get(position)).size());
        } else {
            holder.ATV_filter_value.setText("No Results Available");
            holder.IV_checked_filter.setVisibility(GONE);
            holder.ATV_NoOfResults.setVisibility(View.INVISIBLE);
            holder.IV_unchecked_filter.setVisibility(GONE);

        }
    }

    @Override
    public int getItemCount() {
        return (mValueList.size());
    }

}

