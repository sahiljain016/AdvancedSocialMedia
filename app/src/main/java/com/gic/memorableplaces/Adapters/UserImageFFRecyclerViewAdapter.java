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

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.grantland.widget.AutofitTextView;

public class UserImageFFRecyclerViewAdapter extends RecyclerView.Adapter<UserImageFFRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "UserImageFFRecyclerViewAdapter";

    //Variables
    private List<String> mUserImagesList;
    private RoundedImageView RIV_MAIN;
    private ImageView IV_BG;
    private boolean IsImages;
    private Context mContext;
    private ArrayList<String> alsFilterName;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {


        public RoundedImageView mUserImage, RIV_BORDER;
        public AutofitTextView ATV_FILTER_NAME;
        public ImageView Icon;
        public RelativeLayout RL_FILTER_IN_RESULT_BG;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                mUserImage = itemView.findViewById(R.id.User_images_Image_view_ff);
                RIV_BORDER = itemView.findViewById(R.id.RIV_BORDER);
            } catch (Exception ignored) {

            }
            try {
                ATV_FILTER_NAME = itemView.findViewById(R.id.ATV_FILTER_NAME);
                RL_FILTER_IN_RESULT_BG = itemView.findViewById(R.id.RL_FILTER_IN_RESULT_BG);
                Icon = itemView.findViewById(R.id.IV_FILTER_ICON);
            } catch (Exception ignored) {

            }

        }
    }

    public UserImageFFRecyclerViewAdapter(List<String> userImagesList, ArrayList<String> FilterName, RoundedImageView PP, ImageView BG, boolean misImages, Context context) {
        mUserImagesList = userImagesList;
        RIV_MAIN = PP;
        IV_BG = BG;
        mContext = context;
        IsImages = misImages;
        alsFilterName = FilterName;

    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (IsImages)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_images_ff, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filter_box_in_result, parent, false);

        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: mUserImagesList " + mUserImagesList);

        if (IsImages) {

            if(position == 0){
                holder.RIV_BORDER.setImageResource(R.color.image_selected);
            }
            if (!mUserImagesList.get(position).equals("N/A")) {
                holder.mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
                GlideImageLoader.loadImageWithOutTransition(mContext, mUserImagesList.get(position), holder.mUserImage);
            } else {
                holder.mUserImage.setImageResource(R.drawable.ic_no_image_present);
            }

            holder.mUserImage.setOnClickListener(v -> {
                holder.RIV_BORDER.setImageResource(R.color.image_selected);
                GlideImageLoader.loadImageWithOutTransition(mContext, mUserImagesList.get(position), RIV_MAIN);
                GlideImageLoader.loadImageWithOutTransition(mContext, mUserImagesList.get(position), IV_BG);

            });
            //notifyDataSetChanged();
        } else {
            String[] ColourList = {"#80CDDBFD", "#80C5FFBE", "#80FFBEBE", "#80FBF4AE", "#80E0AEFB"};

            Random random = new Random();
            int pos = random.nextInt(5);
            holder.RL_FILTER_IN_RESULT_BG.setBackgroundColor(Color.parseColor(ColourList[pos]));
            holder.ATV_FILTER_NAME.setText(mUserImagesList.get(position));
            holder.Icon.setImageResource(MiscTools.GetFilterIcon(alsFilterName.get(position), mContext));
            if (position == 6) {
                holder.Icon.setVisibility(View.GONE);
                holder.ATV_FILTER_NAME.setText("View All");
            }
        }
    }

    @Override
    public int getItemCount() {
        int count;
        if (IsImages)
            count = mUserImagesList.size();
        else {

            if (mUserImagesList.size() > 5) {
                count = 6;
            } else {
                count = mUserImagesList.size();
            }
        }
        Log.d(TAG, "getItemCount: count");

        return count;


    }


}

