package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.SquareImageView;

import java.util.ArrayList;

public class ZodiacArrayAdapter extends ArrayAdapter<String> {
    private static final String TAG = "SpinnerAdapter";
    private Context ctx;
    private ArrayList<String> contentArray;
    private ArrayList<Integer> imageArray;


    public ZodiacArrayAdapter(Context context, ArrayList<String> objects,
                              ArrayList<Integer> imageArray) {
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
        View row = inflater.inflate(R.layout.layout_result_games, parent, false);

        TextView ZodiacName = row.findViewById(R.id.TV_NAME_GAMES);
        SquareImageView ZodiacImage = row.findViewById(R.id.CIV_IMAGE_GAMES);

        ZodiacName.setText(contentArray.get(position));
        if(imageArray.get(position) != 0) {
            ZodiacImage.setImageResource(imageArray.get(position));
        }

        return row;
    }

}
