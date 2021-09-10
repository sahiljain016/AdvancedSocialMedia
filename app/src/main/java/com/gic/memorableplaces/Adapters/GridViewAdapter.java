package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gic.memorableplaces.CustomLibs.LoadingViewLib.LVBlazeWood;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MediaStorePaths;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int layoutResource;
    private String append;


    public GridViewAdapter(@NonNull Context context, int resource, String append, ArrayList<String> imagePath) {
        super(context, resource, imagePath);
        mContext = context;
        layoutResource = resource;
        this.append = append;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);

        }

        ImageView gridImage = convertView.findViewById(R.id.grid_imageView1);
        ImageView ic_video = convertView.findViewById(R.id.ic_video);
//        ProgressBar progressBar = convertView.findViewById(R.id.grid_progress);
        LVBlazeWood fireGrid = convertView.findViewById(R.id.fire_grid);
        String url = getItem(position);

        if (url != null && (!MediaStorePaths.isVideo(url))) {

            if (isFirebaseVideo(url)) {
                ic_video.setVisibility(View.VISIBLE);
            } else {
                ic_video.setVisibility(View.GONE);
            }

        } else {
            ic_video.setVisibility(View.VISIBLE);
        }

        GlideImageLoader.loadImageWithTransition(mContext, url, gridImage, null, fireGrid);

//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage(append + url, gridImage);

        return convertView;
    }


    private boolean isFirebaseVideo(String url) {

        return (url.contains("video") && url.contains("videos"));
    }


}
