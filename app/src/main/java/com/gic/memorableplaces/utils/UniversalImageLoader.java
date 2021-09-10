package com.gic.memorableplaces.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.gic.memorableplaces.R;

public class UniversalImageLoader {
    private static final int defaultImage = R.drawable.default_user_profile;
    private static Context mContext;

    public UniversalImageLoader(Context context) {
        mContext = context;
    }

    public static ImageLoaderConfiguration getConfig(Context context, int DefaultImageSource) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(DefaultImageSource)
                .showImageForEmptyUri(DefaultImageSource)
                .showImageOnFail(DefaultImageSource)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.MAX_PRIORITY)
                .writeDebugLogs()
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        return configuration;
    }

    // THIS METHOD CAN ONLY BE USED TO SET IMAGES THAT ARE STATIC. I CAN'T USE IT IF THE IMAGES
    //ARE BEING CHANGED IN THE FRAGMENT/ACTIVITY OR IF THEY ARE BEING SET IN GRID VIEW
    public static void setImage(String imgURL, ImageView image, final ProgressBar mProgressBar, String append) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);

                }
            }
        });

    }
}
