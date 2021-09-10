package com.gic.memorableplaces.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.SquareImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameResultsRecyclerViewAdapter extends RecyclerView.Adapter<GameResultsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GameResultsRecyclerViewAdapter";

    //Variables
    private Context mContext;
    private ArrayList<String> GameNameList;
    private ArrayList<String> GamePlatformList;
    private ArrayList<String> GameImageUrlList;
    private ArrayList<String> GameRatingList;
    private OnResultsClickListener MyOnResultsClickListener;
    //Firebase

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnResultsClickListener mOnResultsClickListener;
        public TextView GameName, GamePlatform, GameRating;
        public SquareImageView GameImage;
        public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView, OnResultsClickListener onResultsClickListener) {
            super(itemView);
            mOnResultsClickListener = onResultsClickListener;
            GameName = itemView.findViewById(R.id.ItemName);
            GamePlatform = itemView.findViewById(R.id.CreatorName);
            GameRating = itemView.findViewById(R.id.GameRating);
            relativeLayout = itemView.findViewById(R.id.base_result_mmb);
            GameImage = itemView.findViewById(R.id.CoverImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnResultsClickListener.onItemClick(getAdapterPosition());
        }


    }
    public interface OnResultsClickListener {
        void onItemClick(int position);
    }
    public GameResultsRecyclerViewAdapter(ArrayList<String> gameNameList,
                                          ArrayList<String> gamePlatformList, ArrayList<String> imageArray, ArrayList<String> gameRatingList,
                                          Context context,OnResultsClickListener onResultsClickListener) {
        GameNameList = gameNameList;
        GamePlatformList = gamePlatformList;
        GameImageUrlList = imageArray;
        GameRatingList = gameRatingList;
        mContext = context;
        MyOnResultsClickListener = onResultsClickListener;

    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_mmb, parent, false);
        return new MainFeedViewHolder(v,MyOnResultsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);


        holder.relativeLayout.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) holder.GameName.getLayoutParams();
        layoutParams.addRule(RelativeLayout.START_OF, R.id.GameRating);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        holder.GameName.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParamsPlatform =
                (RelativeLayout.LayoutParams) holder.GamePlatform.getLayoutParams();
        layoutParamsPlatform.addRule(RelativeLayout.START_OF, R.id.GameRating);
        layoutParamsPlatform.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        layoutParamsPlatform.addRule(RelativeLayout.BELOW, R.id.ItemName);
        holder.GamePlatform.setLayoutParams(layoutParamsPlatform);

        if (position != GameNameList.size() && position != GamePlatformList.size() && position != GameRatingList.size()) {
            holder.GameName.setText(GameNameList.get(position));
            holder.GamePlatform.setText(GamePlatformList.get(position));
            holder.GameRating.setText(GameRatingList.get(position) + " " + " /5");
        }

        GlideImageLoader.loadImageWithOutTransition(mContext, GameImageUrlList.get(position), holder.GameImage);


    }

    @Override
    public int getItemCount() {
        return GameNameList.size();
    }


}

