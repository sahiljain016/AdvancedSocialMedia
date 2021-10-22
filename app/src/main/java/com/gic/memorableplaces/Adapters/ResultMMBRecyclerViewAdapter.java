package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;

import java.util.ArrayList;

public class ResultMMBRecyclerViewAdapter extends RecyclerView.Adapter<ResultMMBRecyclerViewAdapter.MainFeedViewHolder> {

    //Variables
    private final ArrayList<String> alsLeftResultsList;
    private final ArrayList<String> alsRightResultsList;
    private final String sType;
    private final OnMMBResultsClickListener MyOnMMBResultsClickListener;
    private final Context mContext;
    //Firebase

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {
        OnMMBResultsClickListener mOnMMBResultsClickListener;
        public TextView TV_TITLE_LEFT, TV_DESP_LEFT, TV_TITLE_RIGHT, TV_DESP_RIGHT;
        public ImageView IV_COVER_LEFT, IV_COVER_RIGHT;
        public CardView CV_LEFT, CV_RIGHT;

        public MainFeedViewHolder(@NonNull View itemView, OnMMBResultsClickListener OnMMBResultsClickListener) {
            super(itemView);
            mOnMMBResultsClickListener = OnMMBResultsClickListener;
            IV_COVER_LEFT = itemView.findViewById(R.id.IV_COVER_LEFT_MMB);
            TV_TITLE_LEFT = itemView.findViewById(R.id.TV_TITLE_LEFT_MMB);
            TV_DESP_LEFT = itemView.findViewById(R.id.TV_DESP_LEFT_MMB);
            IV_COVER_RIGHT = itemView.findViewById(R.id.IV_COVER_RIGHT_MMB);
            TV_TITLE_RIGHT = itemView.findViewById(R.id.TV_TITLE_RIGHT_MMB);
            TV_DESP_RIGHT = itemView.findViewById(R.id.TV_DESP_RIGHT_MMB);
            CV_LEFT = itemView.findViewById(R.id.CV_RESULT_LEFT_MMB);
            CV_RIGHT = itemView.findViewById(R.id.CV_RESULT_RIGHT_MMB);
            //relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }


    }

    public interface OnMMBResultsClickListener {
        void onItemClick(int position, String result, CardView cv);
    }

    public ResultMMBRecyclerViewAdapter(Context context, ArrayList<String> LeftResultsList, ArrayList<String> RightResultsList, String type, OnMMBResultsClickListener onMMBResultsClickListener) {
        alsLeftResultsList = LeftResultsList;
        alsRightResultsList = RightResultsList;
        mContext = context;
        sType = type;
        MyOnMMBResultsClickListener = onMMBResultsClickListener;
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_mmb, parent, false);
        return new MainFeedViewHolder(v, MyOnMMBResultsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {


        String result = alsLeftResultsList.get(holder.getBindingAdapterPosition());
        String Name = result.substring(0, result.indexOf("$1$"));
        String Desp = result.substring(result.indexOf("$1$") + 3, result.indexOf("$2$"));
        String Cover = result.substring(result.indexOf("$2$") + 3, result.indexOf("$3$"));
        holder.TV_TITLE_LEFT.setText(Name);

        if (!Cover.equals("N/A"))
            GlideImageLoader.loadImageWithOutTransition(mContext, Cover, holder.IV_COVER_LEFT);
        else
            holder.IV_COVER_LEFT.setImageResource(R.drawable.ic_default_image_share);

        if (sType.equals(mContext.getString(R.string.field_music)))
            holder.TV_DESP_LEFT.setText(Html.fromHtml("Artist: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (sType.equals(mContext.getString(R.string.field_movie)))
            holder.TV_DESP_LEFT.setText(Html.fromHtml("Director: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (sType.equals(mContext.getString(R.string.field_books)))
            holder.TV_DESP_LEFT.setText(Html.fromHtml("Author: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));

        holder.CV_LEFT.setOnClickListener(v -> holder.mOnMMBResultsClickListener.onItemClick(holder.getBindingAdapterPosition(), result, holder.CV_LEFT));

        if ((holder.getBindingAdapterPosition()) < alsRightResultsList.size()) {
            String rResult = alsRightResultsList.get((holder.getBindingAdapterPosition()));
            String rName = rResult.substring(0, rResult.indexOf("$1$"));
            String rDesp = rResult.substring(rResult.indexOf("$1$") + 3, rResult.indexOf("$2$"));
            String rCover = rResult.substring(rResult.indexOf("$2$") + 3, rResult.indexOf("$3$"));
            holder.TV_TITLE_RIGHT.setText(rName);
            Log.d("result", "onBindViewHolder: rResult: " + rResult);
            if (!Desp.equals("N/A"))
                GlideImageLoader.loadImageWithOutTransition(mContext, rCover, holder.IV_COVER_RIGHT);
            else
                holder.IV_COVER_RIGHT.setImageResource(R.drawable.ic_default_image_share);

            if (sType.equals(mContext.getString(R.string.field_music)))
                holder.TV_DESP_RIGHT.setText(Html.fromHtml("Artist: " + "<B>" + rDesp + "</B>", Html.FROM_HTML_MODE_LEGACY));
            if (sType.equals(mContext.getString(R.string.field_movie)))
                holder.TV_DESP_RIGHT.setText(Html.fromHtml("Director: " + "<B>" + rDesp + "</B>", Html.FROM_HTML_MODE_LEGACY));
            if (sType.equals(mContext.getString(R.string.field_books)))
                holder.TV_DESP_RIGHT.setText(Html.fromHtml("Author: " + "<B>" + rDesp + "</B>", Html.FROM_HTML_MODE_LEGACY));

            holder.CV_RIGHT.setOnClickListener(v -> holder.mOnMMBResultsClickListener.onItemClick(holder.getBindingAdapterPosition(), rResult, holder.CV_RIGHT));
        }
    }

    @Override
    public int getItemCount() {

        return alsLeftResultsList.size();
    }


}

