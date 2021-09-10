package com.gic.memorableplaces.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.SquareImageView;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GamePlatformRecyclerViewAdapter extends RecyclerView.Adapter<GamePlatformRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "GamePlatformRecyclerViewAdapter";

    //Variables
    private ArrayList<String> mNameList;
    private Context mContext;
    public static String SelectedPlatform;
    public static int CurrentVisiblePosition;
    private View vDialog;
    private AlertDialog alertDialog;
    private RecyclerView.Adapter mFinalAdapter;
    private boolean isClicked = false;
    //Firebase

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView mItemName;
        public SquareImageView CoverImageView;
        public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);


            mItemName = itemView.findViewById(R.id.ItemName);
            relativeLayout = itemView.findViewById(R.id.base_result_mmb);
            CoverImageView = itemView.findViewById(R.id.CoverImageView);

        }
    }

    public GamePlatformRecyclerViewAdapter(ArrayList<String> NameList, Context context, AlertDialog mAlertDialog) {
        mNameList = NameList;
        alertDialog = mAlertDialog;
        mContext = context;

    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_mmb, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        CurrentVisiblePosition = position;
        holder.CoverImageView.setVisibility(View.GONE);
        if ((position % 2) == 0) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#CC000000"));
            holder.mItemName.setTextColor(Color.WHITE);

        } else {
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
            holder.mItemName.setTextColor(Color.BLACK);

        }
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) holder.mItemName.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        holder.mItemName.setLayoutParams(layoutParams);
        holder.mItemName.setTextSize(22);
        holder.mItemName.setText(mNameList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: itemview clicked");
                SelectedPlatform = mNameList.get(position);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }


}

