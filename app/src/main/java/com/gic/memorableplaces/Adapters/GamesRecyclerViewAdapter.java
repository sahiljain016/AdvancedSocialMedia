package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;

import java.util.ArrayList;

public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GamesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> alsSelectedList;
    private Context mContext;
    private OnGameRemovedListener MyOnGameRemovedListener;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnGameRemovedListener mOnGameRemovedListener;
        public TextView tGameName;
        public TextView tGamePlatform;
        public ImageView ivGamesImage;
        public ImageView ivStar1;
        public ImageView ivStar2;
        public ImageView ivStar3;
        public ImageView ivStar4;
        public ImageView ivStar5;
        public AppCompatButton ACB_REMOVE;
        // public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView, OnGameRemovedListener onGameRemovedListener) {
            super(itemView);
            mOnGameRemovedListener = onGameRemovedListener;
            ivGamesImage = itemView.findViewById(R.id.IV_GAME_COVER_VG);
            tGameName = itemView.findViewById(R.id.GameName);
            ACB_REMOVE = itemView.findViewById(R.id.ACB_REMOVE_VG);
            tGamePlatform = itemView.findViewById(R.id.GamePlatforms);
            ivStar1 = itemView.findViewById(R.id.star1);
            ivStar2 = itemView.findViewById(R.id.star2);
            ivStar3 = itemView.findViewById(R.id.star3);
            ivStar4 = itemView.findViewById(R.id.star4);
            ivStar5 = itemView.findViewById(R.id.star5);
            ACB_REMOVE.setOnClickListener(this);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }


        @Override
        public void onClick(View v) {
            mOnGameRemovedListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public GamesRecyclerViewAdapter(ArrayList<String> SelectedList, Context context, OnGameRemovedListener onGameRemovedListener) {
        alsSelectedList = SelectedList;
        MyOnGameRemovedListener = onGameRemovedListener;
        mContext = context;

//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    public interface OnGameRemovedListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_games, parent, false);
        return new MainFeedViewHolder(v, MyOnGameRemovedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        int pos = holder.getBindingAdapterPosition();
        String MainString = alsSelectedList.get(pos);
        String Name = MainString.substring(0, MainString.indexOf("$1$"));
        String img = MainString.substring((MainString.indexOf("$1$") + 3), MainString.indexOf("$2$"));
        String rating = MainString.substring((MainString.indexOf("$2$") + 3), MainString.indexOf("$3$"));
        String platforms = MainString.substring((MainString.indexOf("$3$") + 3), MainString.indexOf("$4$"));

        Log.d(TAG, "onBindViewHolder: name: " + Name);
        holder.tGameName.setText(Name);
        if (!img.equals("N/A")) {
            GlideImageLoader.loadImageWithOutTransition(mContext, img, holder.ivGamesImage);
        }
        holder.tGamePlatform.setText(platforms);

        if (!rating.equals("N/A")) {
            float Rating = Float.parseFloat(rating);

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
        return alsSelectedList.size();
    }


}

