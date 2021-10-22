package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class FillDetailsRecyclerViewAdapter extends RecyclerView.Adapter<FillDetailsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "CoursesRecyclerViewAdapter";

    //Variables
    private Context mContext;

    private ArrayList<String> alsFilterDetails;
    private OnDetailFillClicked MyOnDetailFillClicked;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
OnDetailFillClicked mOnDetailFillClicked;
        private AutofitTextView ATV_TITLE;
        private ConstraintLayout CL;
        private ImageView IV_ICON;
        private RelativeLayout RL_BOTTOM;

        public MainFeedViewHolder(@NonNull View itemView,OnDetailFillClicked onDetailFillClicked) {
            super(itemView);
            mOnDetailFillClicked = onDetailFillClicked;
            ATV_TITLE = itemView.findViewById(R.id.ATV_DETAIL_TITLE_FD);
            CL = itemView.findViewById(R.id.CL_FD);
            IV_ICON = itemView.findViewById(R.id.IV_ICON_FD);
            RL_BOTTOM = itemView.findViewById(R.id.RL_BOTTOM_FILL_IN_FD);
            RL_BOTTOM.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mOnDetailFillClicked.onItemClick(getBindingAdapterPosition(),RL_BOTTOM);
        }


//        @Override
//        public void onClick(View v) {
//
//        }
    }

    public interface OnDetailFillClicked {
        void onItemClick(int position,RelativeLayout relativeLayout);
    }

    public FillDetailsRecyclerViewAdapter(ArrayList<String> FilterDetails, OnDetailFillClicked onDetailFillClicked, Context context) {
        alsFilterDetails = FilterDetails;
        MyOnDetailFillClicked = onDetailFillClicked;
        mContext = context;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fill_details, parent, false);
        return new MainFeedViewHolder(v,MyOnDetailFillClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called");
        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/CormorantGaramond.ttf");
        holder.ATV_TITLE.setTypeface(face, Typeface.NORMAL);

        int pos = holder.getBindingAdapterPosition();

        String details = alsFilterDetails.get(pos);
//        Log.d(TAG, "onBindViewHolder: bg: " + );
//        Log.d(TAG, "onBindViewHolder: icon: " + details.substring(details.indexOf(',') + 1,details.indexOf('*')));
//        Log.d(TAG, "onBindViewHolder: name: " + details.substring(details.indexOf('*') + 1));

        holder.CL.setBackgroundResource(Integer.parseInt(details.substring(0,details.indexOf(','))));
        holder.IV_ICON.setImageResource(Integer.parseInt(details.substring(details.indexOf(',') + 1,details.indexOf('*'))));
        holder.ATV_TITLE.setText(details.substring(details.indexOf('*') + 1));



    }

    @Override
    public int getItemCount() {
        return ( alsFilterDetails.size());
    }


}

