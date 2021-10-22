package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameResultsRecyclerViewAdapter extends RecyclerView.Adapter<GameResultsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GameResultsRecyclerViewAdapter";

    //Variables
    private Context mContext;
    private ArrayList<String> alsGameDetails;
    private String sColor;
    private OnGameSelectedListener MyOnResultsClickListener;
    //Firebase

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnGameSelectedListener mOnResultsClickListener;
        public TextView GameName, GamePlatform, GameRating;
        public CircleImageView GameImage;
        // public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView, OnGameSelectedListener onResultsClickListener) {
            super(itemView);
            mOnResultsClickListener = onResultsClickListener;
            GameName = itemView.findViewById(R.id.TV_NAME_GAMES);
            GamePlatform = itemView.findViewById(R.id.TV_DESP_GAMES);
            GameRating = itemView.findViewById(R.id.TV_RATING_GAMES);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);
            GameImage = itemView.findViewById(R.id.CIV_IMAGE_GAMES);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnResultsClickListener.onItemClick(getAdapterPosition());
        }


    }

    public interface OnGameSelectedListener {
        void onItemClick(int position);
    }

    public GameResultsRecyclerViewAdapter(ArrayList<String> alsGameDetails, String Color,
                                          Context context, OnGameSelectedListener onResultsClickListener) {
        this.alsGameDetails = alsGameDetails;
        mContext = context;
        sColor = Color;
        MyOnResultsClickListener = onResultsClickListener;

    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_games, parent, false);
        return new MainFeedViewHolder(v, MyOnResultsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        int pos = holder.getBindingAdapterPosition();
        String MainString = alsGameDetails.get(pos);
        String Name = MainString.substring(0, MainString.indexOf("$1$"));
        String img = MainString.substring((MainString.indexOf("$1$") + 3), MainString.indexOf("$2$"));
        String rating = MainString.substring((MainString.indexOf("$2$") + 3), MainString.indexOf("$3$"));
        String platforms = MainString.substring((MainString.indexOf("$3$") + 3), MainString.indexOf("$4$"));


        holder.GameName.setText(Name);
        holder.GamePlatform.setText(platforms);
        holder.GameRating.setText(rating);

        holder.GameImage.setBorderColor(Color.parseColor(sColor));
        GlideImageLoader.loadImageWithOutTransition(mContext, img, holder.GameImage);


    }

    @Override
    public int getItemCount() {
        return alsGameDetails.size();
    }


}

