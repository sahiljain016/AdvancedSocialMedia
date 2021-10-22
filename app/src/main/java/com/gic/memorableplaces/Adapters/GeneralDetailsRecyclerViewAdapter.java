package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class GeneralDetailsRecyclerViewAdapter extends RecyclerView.Adapter<GeneralDetailsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GeneralDetailsRecyclerViewAdapter";

    //Variables
    private final ArrayList<StringBuilder> alsGeneralDetails;
    private Context mContext;
    private final OnGeneralDetailsListener MyOnGeneralDetailsListener;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnGeneralDetailsListener mOnGeneralDetailsListener;
        private ImageView IV_ARROW, IV;
        private ConstraintLayout CL;
        private AutofitTextView ATV;

        public MainFeedViewHolder(@NonNull View itemView, OnGeneralDetailsListener onValueClickListener) {
            super(itemView);
            mOnGeneralDetailsListener = onValueClickListener;
            IV = itemView.findViewById(R.id.IV_GD);
            IV_ARROW = itemView.findViewById(R.id.IV_ARROW_GD);
            CL = itemView.findViewById(R.id.CL_GD_RV);
            ATV = itemView.findViewById(R.id.ATV_GD);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnGeneralDetailsListener.onItemClick(getAdapterPosition(), CL);
        }
    }

    public interface OnGeneralDetailsListener {
        void onItemClick(int position, ConstraintLayout CL);
    }

    public GeneralDetailsRecyclerViewAdapter(OnGeneralDetailsListener onGeneralDetailsListener, Context context, ArrayList<StringBuilder> generalDetails) {
        alsGeneralDetails = generalDetails;
        mContext = context;
        MyOnGeneralDetailsListener = onGeneralDetailsListener;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_general_details, parent, false);
        return new MainFeedViewHolder(v, MyOnGeneralDetailsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        int pos = holder.getBindingAdapterPosition();
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        holder.ATV.setTypeface(cap);
        Log.d(TAG, "onBindViewHolder: icon: " + alsGeneralDetails.get(pos).substring(alsGeneralDetails.get(pos).indexOf(",") + 1));
        Log.d(TAG, "onBindViewHolder: icon: " + alsGeneralDetails.get(pos).substring(0, alsGeneralDetails.get(pos).indexOf(",")));
        holder.ATV.setText(alsGeneralDetails.get(pos).substring(0, alsGeneralDetails.get(pos).indexOf(",")));
        holder.IV.setImageResource(Integer.parseInt(alsGeneralDetails.get(pos).substring(alsGeneralDetails.get(pos).indexOf(",") + 1)));
    }

    @Override
    public int getItemCount() {
        return (alsGeneralDetails.size());
    }

}

