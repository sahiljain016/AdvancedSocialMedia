package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gic.memorableplaces.R;

import org.jetbrains.annotations.NotNull;

import java.net.URLConnection;
import java.util.ArrayList;

import xyz.neocrux.suziloader.SuziLoader;

public class FontsSpinnerAdapter extends ArrayAdapter<String> {
    private static final String TAG = "FontsSpinnerAdapter";
    private Context ctx;
    private ArrayList<String> contentArray;
    Typeface face;

    public FontsSpinnerAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, R.layout.custom_spinner, R.id.FontFace, objects);
        this.ctx = context;
        this.contentArray = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_spinner, parent, false);
        TextView textView = row.findViewById(R.id.FontFace);

        switch (position) {

            case 0:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/Arial.ttf");

                break;
            case 1:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/akaDora.ttf");
                textView.setTextSize(26);
                break;
            case 2:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/Eutemia.ttf");
                textView.setTextSize(40);
                break;
            case 3:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/GREENPIL.ttf");
                textView.setTextSize(26);
                break;
            case 4:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/Grinched.ttf");
                textView.setTextSize(26);
                break;
            case 5:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/Helvetica.ttf");
                textView.setTextSize(26);
                break;
            case 6:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/Libertango.ttf");

                break;
            case 7:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/MetalMacabre.ttf");
                break;
            case 8:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/ParryHotter.ttf");
                textView.setTextSize(26);
                break;
            case 9:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/TheGodfather_v2.ttf");
                textView.setTextSize(35);
                break;
            case 10:
                face = Typeface.createFromAsset(ctx.getAssets(), "fonts/waltograph42.ttf");
                textView.setTextSize(35);
                break;

        }
        textView.setTypeface(face);
        textView.setText(contentArray.get(position));
        return row;
    }
}
