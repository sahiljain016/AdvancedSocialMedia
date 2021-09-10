package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SocietiesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> mGamesName;
    private ArrayList<String> mGamesPlatform;
    private ArrayList<String> mGamesRating;
    private ArrayList<String> mGamesImgUrl;
    private Context mContext;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView tGameName;
        public TextView tGamePlatform;
        public RoundedImageView ivGamesImage;
        public ImageView ivStar1;
        public ImageView ivStar2;
        public ImageView ivStar3;
        public ImageView ivStar4;
        public ImageView ivStar5;
        // public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivGamesImage = itemView.findViewById(R.id.GameImage);
            tGameName = itemView.findViewById(R.id.GameName);
            tGamePlatform = itemView.findViewById(R.id.GamePlatforms);
            ivStar1 = itemView.findViewById(R.id.star1);
            ivStar2 = itemView.findViewById(R.id.star2);
            ivStar3 = itemView.findViewById(R.id.star3);
            ivStar4 = itemView.findViewById(R.id.star4);
            ivStar5 = itemView.findViewById(R.id.star5);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }
    }

    public GamesRecyclerViewAdapter(ArrayList<String> NameList, ArrayList<String> CreatorList,
                                    ArrayList<String> CoverList, ArrayList<String> GamesRating, Context context) {
        mGamesName = NameList;
        mGamesPlatform = CreatorList;
        mGamesImgUrl = CoverList;
        mContext = context;
        mGamesRating = GamesRating;

        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_games, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        //AutofitHelper.create(holder.tSocietyName);
//        holder.tSocietyName.setAnimateType(HTextViewType.EVAPORATE);
//        holder.tSocietyName.animateText(mSocietyName.get(position));

        holder.tGameName.setText(mGamesName.get(position));
//        UniversalImageLoader.setImage(mCoverList.get(position),holder.mCover,null,null);
        if (position != mGamesImgUrl.size() && position < mGamesImgUrl.size()) {
            GlideImageLoader.loadImageWithOutTransition(mContext, mGamesImgUrl.get(position), holder.ivGamesImage);
            // GlideImageLoader.loadImageWithOutTransition(mContext, mSocietyCover.get(position), holder.ivSocietyCover);
        } else {
            GlideImageLoader.loadImageWithOutTransition(mContext, null, holder.ivGamesImage);
        }
        if (position != mGamesPlatform.size() && position < mGamesPlatform.size()) {
            holder.tGamePlatform.setText(mGamesPlatform.get(position));
        } else {
            holder.tGamePlatform.setText("Content Not Available");

        }
        if (position != mGamesRating.size() && position < mGamesRating.size()) {
            float Rating = Float.parseFloat(mGamesRating.get(position));

            if (Rating < 1.00 && Rating > 0) {
                holder.ivStar1.setImageResource(R.drawable.ic_half_filled_star);
            } else if ((Rating == 1.00 || Rating > 1.00) && Rating < 1.5) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
            } else if ((Rating == 1.50 || Rating > 1.50) && Rating < 2.0) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_half_filled_star);
            } else if ((Rating == 2.00 || Rating > 2.00) && Rating < 2.50) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
            } else if ((Rating == 2.50 || Rating > 2.50) && Rating < 3.00) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_half_filled_star);
            } else if ((Rating == 3.00 || Rating > 3.00) && Rating < 3.50) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star);
            } else if ((Rating == 3.50 || Rating > 3.50) && Rating < 4.00) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar4.setImageResource(R.drawable.ic_half_filled_star);
            } else if ((Rating == 4.00 || Rating > 4.00) && Rating < 4.50) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar4.setImageResource(R.drawable.ic_filled_star);
            } else if ((Rating == 4.50 || Rating > 4.50) && Rating < 5.00) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar4.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar5.setImageResource(R.drawable.ic_half_filled_star);
            } else if (Rating == 5.00) {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar4.setImageResource(R.drawable.ic_filled_star);
                holder.ivStar5.setImageResource(R.drawable.ic_filled_star);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mGamesName.size();
    }


}

