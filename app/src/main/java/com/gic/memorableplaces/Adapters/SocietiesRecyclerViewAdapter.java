package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;

public class SocietiesRecyclerViewAdapter extends RecyclerView.Adapter<SocietiesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SocietiesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> als, alsSelectedList;
    private Context mContext;

    private OnSocietyClicked MyOnSocietyClicked;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {
        OnSocietyClicked mOnSocietyClicked;
        public ImageView IV_SOCIETY, IV_ADD_SOCIETY, IV_COVER, IV_DESP;
        public TextView TV_DESP;
        //public AutofitTextView ATV_NAME;
        public TextView TV_SOCIETY;
        public ConstraintLayout CL_ADD_SOCIETY;

        public MainFeedViewHolder(@NonNull View itemView, OnSocietyClicked onSocietyClicked) {
            super(itemView);

            mOnSocietyClicked = onSocietyClicked;
            IV_SOCIETY = itemView.findViewById(R.id.IV_SOCIETY);
            IV_ADD_SOCIETY = itemView.findViewById(R.id.IV_ADD_SOCIETY);
            TV_DESP = itemView.findViewById(R.id.TV_DESP_SIC);
            IV_DESP = itemView.findViewById(R.id.IV_DESP_SIC);
            // ATV_NAME = itemView.findViewById(R.id.ATV_SOCIETY_NAME);
            IV_COVER = itemView.findViewById(R.id.IV_COVER);
            CL_ADD_SOCIETY = itemView.findViewById(R.id.CL_ADD_SOCIETY);
            TV_SOCIETY = itemView.findViewById(R.id.TV_SOCIETY);

        }

//        @Override
//        public void onClick(View v) {
//            mOnSocietyClicked.onItemClick(getBindingAdapterPosition(), IV_ADD_SOCIETY, TV_SOCIETY, CL_ADD_SOCIETY);
//        }

    }

    public interface OnSocietyClicked {
        void onItemClick(int position, String color, ImageView IV, TextView TV, ConstraintLayout CL);
    }

    public SocietiesRecyclerViewAdapter(ArrayList<String> NameList, ArrayList<String> SelectedList, OnSocietyClicked onSocietyClicked, Context context) {
        als = NameList;
        alsSelectedList = SelectedList;
        MyOnSocietyClicked = onSocietyClicked;
        mContext = context;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_societies, parent, false);
        return new MainFeedViewHolder(v, MyOnSocietyClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {


        int pos = holder.getBindingAdapterPosition();

        String Name = als.get(pos).substring(0, als.get(pos).indexOf("$1*"));
        int icon = Integer.parseInt(als.get(pos).substring(als.get(pos).indexOf("$2*") + 3, als.get(pos).indexOf("$3*")));
        String color = als.get(pos).substring((als.get(pos).indexOf("$3*") + 3));

//        Log.d(TAG, "onBindViewHolder: alsSelectedList: " + alsSelectedList);
//        Log.d(TAG, "onBindViewHolder: als: " + als);
        Log.d(TAG, "onBindViewHolder: ");
        if (alsSelectedList.contains(als.get(pos))) {
            holder.IV_ADD_SOCIETY.setImageResource(R.drawable.ic_correct);
            holder.TV_SOCIETY.setText("Added");
            holder.CL_ADD_SOCIETY.setTag("selected");

            Log.d(TAG, "onBindViewHolder: reached 1");
            if (color.equals("#FFFFFF")) {

                Log.d(TAG, "onBindViewHolder: reached 1 a");
                holder.IV_ADD_SOCIETY.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                holder.TV_SOCIETY.setTextColor(Color.parseColor("#000000"));
                holder.IV_COVER.setBackgroundColor(Color.parseColor("#000000"));
                holder.TV_DESP.setBackgroundColor(Color.parseColor("#E6FFFFFF"));
                holder.TV_DESP.setTextColor(Color.parseColor("#000000"));
                holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.rounded_white_background);
                holder.CL_ADD_SOCIETY.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            } else {

                Log.d(TAG, "onBindViewHolder: reached 1 b");
                holder.IV_ADD_SOCIETY.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                holder.TV_SOCIETY.setTextColor(Color.parseColor("#FFFFFF"));
                holder.IV_COVER.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.TV_DESP.setBackgroundColor(Color.parseColor("#E6000000"));
                holder.TV_DESP.setTextColor(Color.parseColor("#FFFFFF"));
                holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.rounded_white_background);
                holder.CL_ADD_SOCIETY.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            }
        } else {
            Log.d(TAG, "onBindViewHolder: reached 2");
            holder.IV_ADD_SOCIETY.setImageResource(R.drawable.ic_plus_sign_white);
            holder.TV_SOCIETY.setText("Select");
            holder.CL_ADD_SOCIETY.setTag("unselected");
            if (color.equals("#FFFFFF")) {
                Log.d(TAG, "onBindViewHolder: reached 2 a");

                holder.IV_ADD_SOCIETY.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                holder.TV_SOCIETY.setTextColor(Color.parseColor("#FFFFFF"));
                holder.IV_COVER.setBackgroundColor(Color.parseColor("#000000"));
                holder.TV_DESP.setBackgroundColor(Color.parseColor("#E6FFFFFF"));
                holder.TV_DESP.setTextColor(Color.parseColor("#000000"));
                holder.CL_ADD_SOCIETY.setBackgroundTintList(null);
                holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
            } else {
                Log.d(TAG, "onBindViewHolder: reached 2 b");

                holder.IV_ADD_SOCIETY.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                holder.TV_SOCIETY.setTextColor(Color.parseColor("#000000"));
                holder.IV_COVER.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.TV_DESP.setBackgroundColor(Color.parseColor("#E6000000"));
                holder.TV_DESP.setTextColor(Color.parseColor("#FFFFFF"));
                holder.CL_ADD_SOCIETY.setBackgroundTintList(null);
                holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.border_rounded_black_20sdp);
            }
        }

        Log.d(TAG, "onBindViewHolder: Name: " + Name);


//        if (color.equals("#000000")) {
//            holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.border_rounded_black_20sdp);
//            holder.IV_COVER.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        } else {
//            holder.CL_ADD_SOCIETY.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
//            holder.IV_COVER.setBackgroundColor(Color.parseColor("#000000"));
//        }
        holder.IV_SOCIETY.setImageResource(icon);


        holder.IV_DESP.setOnClickListener(v -> {
            if (holder.IV_DESP.getTag().equals("gone")) {
                String desp = als.get(pos).substring(als.get(pos).indexOf("$1*") + 3, als.get(pos).indexOf("$2*"));
                holder.TV_DESP.setText("About " + Name + ":\n\n" + desp);
                holder.TV_DESP.setVisibility(View.VISIBLE);
                holder.IV_DESP.setTag("visible");
            } else {
                holder.TV_DESP.setVisibility(View.GONE);
                holder.IV_DESP.setTag("gone");
            }
        });

        holder.CL_ADD_SOCIETY.setOnClickListener(v -> MyOnSocietyClicked.onItemClick(pos, color, holder.IV_ADD_SOCIETY, holder.TV_SOCIETY, holder.CL_ADD_SOCIETY));
    }

    @Override
    public int getItemCount() {
        return als.size();
    }


}

