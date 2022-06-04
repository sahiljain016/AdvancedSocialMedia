package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.DataModels.Chat;
import com.gic.memorableplaces.R;

import java.util.List;

public class DynamicLinesRecyclerViewAdapter extends RecyclerView.Adapter<DynamicLinesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";

    //Variables
    private final List<Chat> alsAnchorDetails;
    private final OnAnchorClicked onAnchorClicked;
    private final Context mContext;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView TV_TITLE, TV_MESSAGE;
        public ImageView IV_GO_TO;


        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);


            TV_TITLE = itemView.findViewById(R.id.TV_ANCHOR_TITLE);
            TV_MESSAGE = itemView.findViewById(R.id.TV_ANCHOR_MESSAGE);
            IV_GO_TO = itemView.findViewById(R.id.IV_GO_TO_ANCHOR);


        }

    }

    public DynamicLinesRecyclerViewAdapter(List<Chat> AnchorDetails, OnAnchorClicked onAnchorClicked, Context context) {
        alsAnchorDetails = AnchorDetails;
        this.onAnchorClicked = onAnchorClicked;
        mContext = context;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_2_line_text, parent, false);

        return new MainFeedViewHolder(v);

    }

    public interface OnAnchorClicked {
        void onItemClick(Chat chat);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        int pos = holder.getBindingAdapterPosition();
        Log.d(TAG, "onBindViewHolder: anchor chat: " + alsAnchorDetails.get(pos));

        holder.TV_TITLE.setText(alsAnchorDetails.get(pos).getAnchor_id());
        holder.TV_MESSAGE.setText("Message: "  + alsAnchorDetails.get(pos).getMessage());
        holder.IV_GO_TO.setOnClickListener(v -> onAnchorClicked.onItemClick(alsAnchorDetails.get(pos)));

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: alsDespList.size() " + alsDespList.size());
        return alsAnchorDetails.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

