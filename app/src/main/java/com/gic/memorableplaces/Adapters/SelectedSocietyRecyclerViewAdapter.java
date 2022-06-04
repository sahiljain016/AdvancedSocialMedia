package com.gic.memorableplaces.Adapters;

import android.content.Context;
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
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedSocietyRecyclerViewAdapter extends RecyclerView.Adapter<SelectedSocietyRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SelectedSocietyRecyclerViewAdapter";

    //alsDetailList
    private ArrayList<String> alsDetailList;
    private Context mContext;
    private boolean isMusic;
    private boolean isMovie;
    private boolean isBook;
    private boolean isMMB;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView mItemName, mCreator;
        public CircleImageView mCover;
        public RelativeLayout relativeLayout;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            mCover = itemView.findViewById(R.id.CIV_IMAGE_GAMES);
            mItemName = itemView.findViewById(R.id.TV_NAME_GAMES);
            mCreator = itemView.findViewById(R.id.TV_DESP_GAMES);
           // relativeLayout = itemView.findViewById(R.id.base_result_mmb);

        }
    }

    public SelectedSocietyRecyclerViewAdapter(ArrayList<String> DetailList, Context context, boolean Music, boolean Movie, boolean Book, boolean misMMB) {
        alsDetailList = DetailList;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_games, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        int pos = holder.getBindingAdapterPosition();
        String Name = alsDetailList.get(pos).substring(0, alsDetailList.get(pos).indexOf("$1*"));
        String Desp = alsDetailList.get(pos).substring((alsDetailList.get(pos).indexOf("$1*") + 3), alsDetailList.get(pos).indexOf("$2*"));
        String Cover = alsDetailList.get(pos).substring((alsDetailList.get(pos).indexOf("$2*") + 3), alsDetailList.get(pos).indexOf("$3*"));

//        if ((position % 2) == 0) {
//            holder.relativeLayout.setBackgroundColor(Color.parseColor("#88000000"));
//            holder.mItemName.setTextColor(Color.WHITE);
//
//        } else {
//            holder.relativeLayout.setBackgroundColor(Color.WHITE);
//            holder.mItemName.setTextColor(Color.BLACK);
//
//        }
        holder.mItemName.setText(Name);
//        UniversalImageLoader.setImage(mCoverList.get(position),holder.mCover,null,null);
        if (Cover.length() > 40)
            GlideImageLoader.loadImageWithOutTransition(mContext, Cover, holder.mCover);
        else
            holder.mCover.setImageResource(Integer.parseInt(Cover));


        if (isMMB) {
            if (isMusic)
                holder.mCreator.setText(Html.fromHtml("Artist: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
            if (isMovie)
                holder.mCreator.setText(Html.fromHtml("<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));
            if (isBook)
                holder.mCreator.setText(Html.fromHtml("Author: " + "<B>" + Desp + "</B>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            holder.mCreator.setText(Desp);
        }
    }

    @Override
    public int getItemCount() {
        return alsDetailList.size();
    }


}

