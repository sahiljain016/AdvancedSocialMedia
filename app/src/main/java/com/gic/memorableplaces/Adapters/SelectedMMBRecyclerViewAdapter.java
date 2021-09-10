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

public class SelectedMMBRecyclerViewAdapter extends RecyclerView.Adapter<SelectedMMBRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SelectedMMBRecyclerViewAdapter";

    //Variables
    private ArrayList<String> mNameList;
    private ArrayList<String> mCreatorList;
    private ArrayList<String> mCoverList;
    private Context mContext;
    private boolean isMusic;
    private boolean isMovie;
    private boolean isBook;
    private boolean isMMB;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView mItemName, mCreator;
        public SquareImageView mCover;
        public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            mCover = itemView.findViewById(R.id.CoverImageView);
            mItemName = itemView.findViewById(R.id.ItemName);
            mCreator = itemView.findViewById(R.id.CreatorName);
            relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }
    }

    public SelectedMMBRecyclerViewAdapter(ArrayList<String> NameList, ArrayList<String> CreatorList, ArrayList<String> CoverList,
                                          Context context, boolean Music, boolean Movie, boolean Book, boolean misMMB) {
        mNameList = NameList;
        mCreatorList = CreatorList;
        mCoverList = CoverList;
        mContext = context;
        isMusic = Music;
        isMovie = Movie;
        isBook = Book;
        isMMB = misMMB;


        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_mmb, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        if ((position % 2) == 0) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#88000000"));
            holder.mItemName.setTextColor(Color.WHITE);

        } else {
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
            holder.mItemName.setTextColor(Color.BLACK);

        }
        holder.mItemName.setText(mNameList.get(position));
//        UniversalImageLoader.setImage(mCoverList.get(position),holder.mCover,null,null);
        if (position != mCoverList.size() && position < mCoverList.size()) {
            GlideImageLoader.loadImageWithOutTransition(mContext, mCoverList.get(position), holder.mCover);
        }
        if (isMMB) {
            if (position != mCreatorList.size() && position < mCreatorList.size()) {
                if (isMusic)
                    holder.mCreator.setText(Html.fromHtml("Artist: " + "<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
                if (isMovie)
                    holder.mCreator.setText(Html.fromHtml("<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
                if (isBook)
                    holder.mCreator.setText(Html.fromHtml("Author: " + "<B>" + mCreatorList.get(position) + "</B>", Html.FROM_HTML_MODE_LEGACY));
            }
        } else {
            if (position != mCreatorList.size() && position < mCreatorList.size()) {
                holder.mCreator.setText(mCreatorList.get(position));

            }
        }
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }


}

