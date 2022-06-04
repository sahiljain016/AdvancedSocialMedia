package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;

import java.util.ArrayList;

public class SelectedMMBRVAdapter extends RecyclerView.Adapter<SelectedMMBRVAdapter.MainFeedViewHolder> {
    private static final String TAG = "SelectedMMBRecyclerViewAdapter";

    //alsDetailList
    private ArrayList<String> alsDetailList;
    private Context mContext;
    private String sType;
    private final OnMMBRemovedListener MyOnMMBRemovedListener;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnMMBRemovedListener mOnMMBRemovedListener;
        public TextView TV_TITLE, TV_DESP;
        public ImageView IV_COVER;
        public AppCompatButton ACB_REMOVE;

        public MainFeedViewHolder(@NonNull View itemView, OnMMBRemovedListener OnMMBRemovedListener) {
            super(itemView);
            mOnMMBRemovedListener = OnMMBRemovedListener;
            IV_COVER = itemView.findViewById(R.id.IV_COVER_MMB);
            TV_TITLE = itemView.findViewById(R.id.TV_TITLE_MMB);
            TV_DESP = itemView.findViewById(R.id.TV_DESP_MMB);
            ACB_REMOVE = itemView.findViewById(R.id.ACB_REMOVE_MMB);

            ACB_REMOVE.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnMMBRemovedListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface OnMMBRemovedListener {
        void onItemClick(int position);
    }

    public SelectedMMBRVAdapter(ArrayList<String> DetailList, Context context, String Type,OnMMBRemovedListener onMMBRemovedListener) {
        alsDetailList = DetailList;
        mContext = context;
        MyOnMMBRemovedListener = onMMBRemovedListener;
        sType = Type;

//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_selected_mmb, parent, false);
        return new MainFeedViewHolder(v,MyOnMMBRemovedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        String result = alsDetailList.get(holder.getBindingAdapterPosition());
        String Name = result.substring(0, result.indexOf("$1$"));
        String Desp = result.substring(result.indexOf("$1$") + 3, result.indexOf("$2$"));
        String Cover = result.substring(result.indexOf("$2$") + 3, result.indexOf("$3$"));
        holder.TV_TITLE.setText(Name);

        if (!Cover.equals("N/A"))
            GlideImageLoader.loadImageWithOutTransition(mContext, Cover, holder.IV_COVER);


        if (sType.equals(mContext.getString(R.string.field_music)))
            holder.TV_DESP.setText(Html.fromHtml("Artist: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (sType.equals(mContext.getString(R.string.field_movie)))
            holder.TV_DESP.setText(Html.fromHtml("Overview: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (sType.equals(mContext.getString(R.string.field_books)))
            holder.TV_DESP.setText(Html.fromHtml("Author: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));

    }

    @Override
    public int getItemCount() {
        return alsDetailList.size();
    }


}

