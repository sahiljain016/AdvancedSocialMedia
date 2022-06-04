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
import java.util.Locale;

public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GamesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> alsSelectedList;
    private Context mContext;
    private boolean bIsGame;
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
        public TextView TV_CITY_COUNTRY, TV_NO;
        public ImageView IV_FLAG;
        // public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView, boolean isGame, OnGameRemovedListener onGameRemovedListener) {
            super(itemView);
            mOnGameRemovedListener = onGameRemovedListener;
            if (isGame) {
                ivGamesImage = itemView.findViewById(R.id.IV_GAME_COVER_VG);
                tGameName = itemView.findViewById(R.id.GameName);
                tGamePlatform = itemView.findViewById(R.id.GamePlatforms);
                ivStar1 = itemView.findViewById(R.id.star1);
                ivStar2 = itemView.findViewById(R.id.star2);
                ivStar3 = itemView.findViewById(R.id.star3);
                ivStar4 = itemView.findViewById(R.id.star4);
                ivStar5 = itemView.findViewById(R.id.star5);
                ACB_REMOVE = itemView.findViewById(R.id.ACB_REMOVE_VG);
            } else {
                ACB_REMOVE = itemView.findViewById(R.id.ACB_CVTV);
                TV_CITY_COUNTRY = itemView.findViewById(R.id.TV_CVTV);
                IV_FLAG = itemView.findViewById(R.id.IV_CVTV);
                TV_NO = itemView.findViewById(R.id.TV_NO_CVTV);
            }
            ACB_REMOVE.setOnClickListener(this);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }


        @Override
        public void onClick(View v) {
            mOnGameRemovedListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public GamesRecyclerViewAdapter(ArrayList<String> SelectedList, boolean isGame, Context context, OnGameRemovedListener onGameRemovedListener) {
        alsSelectedList = SelectedList;
        MyOnGameRemovedListener = onGameRemovedListener;
        bIsGame = isGame;
        mContext = context;

//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    public interface OnGameRemovedListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (bIsGame) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_games, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_view_textview, parent, false);
        }
        return new MainFeedViewHolder(v, bIsGame, MyOnGameRemovedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        int pos = holder.getBindingAdapterPosition();
        String MainString = alsSelectedList.get(pos);
        Log.d(TAG, "onBindViewHolder: alsSelctedList: " + alsSelectedList);
        if (bIsGame) {
            if (MainString.contains("$1$")) {
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
                } else {
                    holder.tGameName.setText("Error! Please load again.");

                }
            }
        } else {
            if (!MainString.equals(mContext.getString(R.string.not_available))) {
                String name = alsSelectedList.get(position);
                String url = "https://flagcdn.com/256x192/" + (name.substring((name.length() - 3), (name.length() - 1))).toLowerCase(Locale.ROOT) + ".png";
                Log.d(TAG, "onBindViewHolder: name: " + name);
                holder.TV_CITY_COUNTRY.setText(name);
                GlideImageLoader.loadImageWithOutTransition(mContext, url, holder.IV_FLAG);
            } else {
                holder.TV_CITY_COUNTRY.setText("No selection made");
                holder.IV_FLAG.setImageResource(R.drawable.ic_filter_places);
            }

            holder.TV_NO.setText(String.valueOf(pos + 1));
        }
    }

    @Override
    public int getItemCount() {
        return alsSelectedList.size();
    }


}

