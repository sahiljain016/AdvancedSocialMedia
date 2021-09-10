package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.URLConnection;
import java.util.ArrayList;

import xyz.neocrux.suziloader.SuziLoader;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private static final String TAG = "SpinnerAdapter";
    private Context ctx;
    private ArrayList<String> contentArray;
    private ArrayList<String> imageArray;
    private String mPath;
    private String mAppend = "file:/";
    private int BITMAP_QUALITY = 50;

    public SpinnerAdapter(Context context, int resource, ArrayList<String> objects,
                          ArrayList<String> imageArray) {
        super(context, R.layout.spinner_layout, R.id.albumName, objects);
        this.ctx = context;
        this.contentArray = objects;
        this.imageArray = imageArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_layout, parent, false);

        TextView textView = row.findViewById(R.id.albumName);
        textView.setText(contentArray.get(position));

        ImageView imageView = row.findViewById(R.id.iconSpiner);
        String mPath = imageArray.get(position);
        if (isVideoFile(mPath)) {
            SuziLoader loader = new SuziLoader(); //Create it for once
            loader.with(ctx) //Context
                    .load(mPath) //Video path
                    .into(imageView) // imageview to load the thumbnail
                    .type("mini") // mini or micro
                    .show(); // to show the thumbnail

        } else {
           // fileCompressor.compressImage(albumBitmap.toString());
            GlideImageLoader.loadImageWithOutTransition(ctx,mPath,imageView);

            // ImageLoader.getInstance().init(UniversalImageLoader.getConfig(ctx));

            // Bitmap albumBitmap = ImageLoader.getInstance().loadImageSync(imageArray.get(position));
            //imageView.setImageBitmap(albumBitmap);
            //  UniversalImageLoader.setImage(albumBitmap.toString(), imageView, null, mAppend);
        }

        return row;
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
