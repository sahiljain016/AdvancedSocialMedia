package com.gic.memorableplaces.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gic.memorableplaces.CustomLibs.LoadingViewLib.LVBlazeWood;
import com.gic.memorableplaces.R;
import com.github.siyamed.shapeimageview.RoundedImageView;


public class GlideImageLoader {

    public static void loadImageWithTransition(Context mContext, String imageUrl, ImageView image, final ProgressBar progressBar, final LVBlazeWood fireGrid) {
       if(fireGrid != null){
        fireGrid.startAnim(1000);}
        Glide.with(mContext)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(new RequestOptions()
                .placeholder(R.drawable.rounded_corners_imageview))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        if(progressBar!=null)
//                        progressBar.setVisibility(View.GONE);
                        if(fireGrid != null){
                            fireGrid.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if(progressBar!=null)
//                        progressBar.setVisibility(View.GONE);
                        if(fireGrid != null){
                            fireGrid.setVisibility(View.GONE);
                        }
                        return false;
                    }
                }).into(image);
    }


    public static void loadImageWithOutTransition(Context mContext, String imageUrl, ImageView image) {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_default_image_share))
                .into(image);
    }
    public static void loadRoundedImageWithOutTransition(Context mContext, String imageUrl, RoundedImageView image) {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_default_image_share))
                .into(image);
    }


}
