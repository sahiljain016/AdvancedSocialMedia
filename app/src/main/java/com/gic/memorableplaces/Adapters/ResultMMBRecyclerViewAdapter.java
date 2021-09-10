package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.SquareImageView;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ResultMMBRecyclerViewAdapter extends RecyclerView.Adapter<ResultMMBRecyclerViewAdapter.MainFeedViewHolder> {

    //Variables
    private final ArrayList<String> mNameList;
    private final ArrayList<String> mCreatorList;
    private final ArrayList<String> mCoverList;
    private final OnMMBResultsClickListener MyOnMMBResultsClickListener;
    private final Context mContext;
    private final boolean isMusic;
    private final boolean isMovie;
    private final boolean isBook;
    //Firebase

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnMMBResultsClickListener mOnMMBResultsClickListener;
        public TextView mItemName, mCreator;
        public SquareImageView mCover;
        public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView, OnMMBResultsClickListener OnMMBResultsClickListener) {
            super(itemView);
            mOnMMBResultsClickListener = OnMMBResultsClickListener;
            mCover = itemView.findViewById(R.id.CoverImageView);
            mItemName = itemView.findViewById(R.id.ItemName);
            mCreator = itemView.findViewById(R.id.CreatorName);
            relativeLayout = itemView.findViewById(R.id.base_result_mmb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnMMBResultsClickListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnMMBResultsClickListener {
        void onItemClick(int position);
    }
    public ResultMMBRecyclerViewAdapter(ArrayList<String> NameList, ArrayList<String> CreatorList, ArrayList<String> CoverList,
                                        Context context, boolean Music, boolean Movie, boolean Book,OnMMBResultsClickListener onMMBResultsClickListener) {
        mNameList = NameList;
        mCreatorList = CreatorList;
        mCoverList = CoverList;
        mContext = context;
        isMusic = Music;
        isMovie = Movie;
        isBook = Book;
        MyOnMMBResultsClickListener = onMMBResultsClickListener;
        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_mmb, parent, false);
        return new MainFeedViewHolder(v,MyOnMMBResultsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        if ((position % 2) == 0) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#CC000000"));
            holder.mItemName.setTextColor(Color.WHITE);

        } else {
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
            holder.mItemName.setTextColor(Color.BLACK);

        }
        holder.mItemName.setText(mNameList.get(position));

        if (mCoverList.size() > position) {
            if (mCoverList.get(position) != null) {
                GlideImageLoader.loadImageWithOutTransition(mContext, mCoverList.get(position), holder.mCover);
            }
        } else {
            holder.mCover.setImageResource(R.drawable.ic_default_image_share);
        }
        if (isMusic)
            holder.mCreator.setText(Html.fromHtml("Artist: " + "<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (isMovie)
            holder.mCreator.setText(Html.fromHtml("Director: " + "<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
        if (isBook)
            holder.mCreator.setText(Html.fromHtml("Author: " + "<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }


}

