package com.gic.memorableplaces.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csguys.viewmoretextview.ViewMoreTextView;
import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

public class SocietiesRecyclerViewAdapter extends RecyclerView.Adapter<SocietiesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SocietiesRecyclerViewAdapter";

    //Variables
    private ArrayList<String> mSocietyName;
    private ArrayList<String> mSocietyDesp;
    private ArrayList<String> mSocietyCover;
    private Context mContext;
    private boolean isMusic;
    private boolean isMovie;
    private boolean isBook;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView tSocietyName;
        public ViewMoreTextView tSocietyDesp;
        public RoundedImageView ivSocietyCover;
        // public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivSocietyCover = itemView.findViewById(R.id.SocietyImage);
            tSocietyName = itemView.findViewById(R.id.GameName);
            tSocietyDesp = itemView.findViewById(R.id.GamePlatform);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }
    }

    public SocietiesRecyclerViewAdapter(ArrayList<String> NameList, ArrayList<String> CreatorList, ArrayList<String> CoverList,
                                        Context context) {
        mSocietyName = NameList;
        mSocietyDesp = CreatorList;
        mSocietyCover = CoverList;
        mContext = context;

        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_societies, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        AutofitHelper.create(holder.tSocietyName);
//        holder.tSocietyName.setAnimateType(HTextViewType.EVAPORATE);
//        holder.tSocietyName.animateText(mSocietyName.get(position));

        holder.tSocietyName.setText(mSocietyName.get(position));
//        UniversalImageLoader.setImage(mCoverList.get(position),holder.mCover,null,null);
        if (position != mSocietyCover.size() && position < mSocietyCover.size()) {
            GlideImageLoader.loadImageWithOutTransition(mContext, mSocietyCover.get(position), holder.ivSocietyCover);
            // GlideImageLoader.loadImageWithOutTransition(mContext, mSocietyCover.get(position), holder.ivSocietyCover);
        } else {
            GlideImageLoader.loadImageWithOutTransition(mContext, null, holder.ivSocietyCover);
        }
        if (position != mSocietyDesp.size() && position < mSocietyDesp.size()) {
            holder.tSocietyDesp.setText(mSocietyDesp.get(position));
        } else {
            holder.tSocietyDesp.setText("Content Not Available");

        }
        holder.tSocietyDesp.setOnClickListener(v -> {

            Log.d(TAG, "onClick: society desp");
            if (position != mSocietyDesp.size() && position < mSocietyDesp.size()) {
                Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                window.setGravity(Gravity.CENTER);
                window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                window.setAttributes(wlp);
                dialog.setContentView(R.layout.dialog_society_desp);
                TyperTextView heading = dialog.findViewById(R.id.Society_name);
                AutofitHelper.create(heading);
                TextView desp = dialog.findViewById(R.id.society_desp_text);
                desp.setMovementMethod(new ScrollingMovementMethod());
                heading.animateText(mSocietyName.get(position));
                desp.setText(mSocietyDesp.get(position));
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSocietyName.size();
    }


}

