package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gic.memorableplaces.R;

import java.util.ArrayList;

public class MyImagesRecyclerViewAdapter extends RecyclerView.Adapter<MyImagesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "MyImagesRecyclerViewAdapter";

    //Variables
    private final ArrayList<String> aluImagesList;
    private final OnMyImageClicked onMyImageClicked;
    private final Context mContext;
    private int pos;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView TV_NUMBER;
        public ImageView IV_IMAGE, IV_BORDER;
        OnMyImageClicked mOnMyImageClicked;


        public MainFeedViewHolder(@NonNull View itemView, OnMyImageClicked onMyImageClicked) {
            super(itemView);

            mOnMyImageClicked = onMyImageClicked;
            TV_NUMBER = itemView.findViewById(R.id.TV_NUMBER_PP);
            IV_IMAGE = itemView.findViewById(R.id.IV_MY_IMAGE);
            IV_BORDER = itemView.findViewById(R.id.IV_BORDER_PP);
            IV_IMAGE.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnMyImageClicked.onItemClick(getBindingAdapterPosition());
        }
    }

    public MyImagesRecyclerViewAdapter(ArrayList<String> ImagesList, OnMyImageClicked onMyImageClicked, Context context) {
        aluImagesList = ImagesList;
        this.onMyImageClicked = onMyImageClicked;
        mContext = context;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pictures, parent, false);

        return new MainFeedViewHolder(v, onMyImageClicked);

    }

    public interface OnMyImageClicked {
        void onItemClick(int pos);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        pos = holder.getBindingAdapterPosition();

        if (aluImagesList.get(pos).equals("N/A")) {
            holder.TV_NUMBER.setVisibility(View.VISIBLE);
            holder.TV_NUMBER.setText(String.valueOf(pos + 1));
            holder.IV_BORDER.setVisibility(View.VISIBLE);
            holder.IV_IMAGE.setVisibility(View.GONE);
        } else {
            holder.TV_NUMBER.setVisibility(View.GONE);
            holder.IV_BORDER.setVisibility(View.GONE);
            holder.IV_IMAGE.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Uri.parse(aluImagesList.get(pos))).into(holder.IV_IMAGE);
        }


    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: alsDespList.size() " + alsDespList.size());
        return aluImagesList.size();
    }

    public int GetPosition() {
        return pos;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

