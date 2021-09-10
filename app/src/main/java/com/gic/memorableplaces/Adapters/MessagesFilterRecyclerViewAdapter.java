package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import me.grantland.widget.AutofitTextView;

public class MessagesFilterRecyclerViewAdapter extends RecyclerView.Adapter<MessagesFilterRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";
    //Variables


    private final Context mContext;
    private String TypeOfNotification;
    private int iCount;
    private ArrayList<String> alsDates;
    private LinkedHashMap<String, HashMap<String, String>> hmMessages;
    MiscTools miscTools = new MiscTools();


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public MotionLayout CL_MESSAGES_FILTER;
        public AutofitTextView ATV_TIMESTAMP, ATV_USERNAME, ATV_NUMBER_OF_NEW_MESSAGES;
        public TextView TV_NEW_MESSAGE;
        public RoundedImageView RIV_PP;
        public ImageView IV_NEW_MESSAGE_DOT, IV_NUMBER_OF_MESSAGES_DOT, IV_BORDER;
        public View V_BOTTOM_LINE;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            RIV_PP = itemView.findViewById(R.id.RIV_PP);
            CL_MESSAGES_FILTER = itemView.findViewById(R.id.CL_MESSAGES_FILTER);
            ATV_TIMESTAMP = itemView.findViewById(R.id.ATV_TIMESTAMP);
            ATV_USERNAME = itemView.findViewById(R.id.ATV_USERNAME);
            IV_NEW_MESSAGE_DOT = itemView.findViewById(R.id.IV_New_Message_Dot);
            IV_NUMBER_OF_MESSAGES_DOT = itemView.findViewById(R.id.IV_No_Of_Messages);
            TV_NEW_MESSAGE = itemView.findViewById(R.id.TV_MESSAGE_ON_DISPLAY);
            ATV_NUMBER_OF_NEW_MESSAGES = itemView.findViewById(R.id.ATV_No_Of_Messages);
            V_BOTTOM_LINE = itemView.findViewById(R.id.V_BOTTOM_LINE);
            IV_BORDER = itemView.findViewById(R.id.IV_BORDER);
        }

    }


    public MessagesFilterRecyclerViewAdapter(Context context, LinkedHashMap<String, HashMap<String, String>> Messages, String sTypeOfMessage
            , ArrayList<String> Dates) {

        mContext = context;
        hmMessages = Messages;
        TypeOfNotification = sTypeOfMessage;
        alsDates = Dates;


    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_messages_filter, parent, false);
        return new MainFeedViewHolder(v);


    }


    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        Typeface timestamp = Typeface.createFromAsset(mContext.getAssets(), "fonts/fredoka_one.ttf");
        Typeface username = Typeface.createFromAsset(mContext.getAssets(), "fonts/convergence.ttf");
        holder.ATV_TIMESTAMP.setTypeface(timestamp, Typeface.NORMAL);
        holder.ATV_USERNAME.setTypeface(username, Typeface.NORMAL);


        if (position == (hmMessages.size() - 1)) {
            holder.CL_MESSAGES_FILTER.getTransition(R.id.Transition_BOTTOM_LINE).setEnable(false);
            holder.V_BOTTOM_LINE.setVisibility(View.GONE);
        }

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(estimatedServerTimeMs);
                String TimeStamp = formatter.format(calendar.getTime());
                holder.ATV_TIMESTAMP.setText(miscTools.SetUpDateWidget(alsDates.get(position), false, TimeStamp));


            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        holder.ATV_USERNAME.setText(hmMessages.get(alsDates.get(position)).get(mContext.getString(R.string.field_username)));
        GlideImageLoader.loadImageWithOutTransition(mContext, hmMessages.get(alsDates.get(position)).get(mContext.getString(R.string.field_profile_photo)), holder.RIV_PP);

        holder.TV_NEW_MESSAGE.setText(hmMessages.get(alsDates.get(position)).get(mContext.getString(R.string.field_message_sent)));


        if (hmMessages.get(alsDates.get(position)).get("isSeen").equals("false")) {
            holder.IV_NEW_MESSAGE_DOT.setVisibility(View.VISIBLE);
            holder.IV_NEW_MESSAGE_DOT.setVisibility(View.VISIBLE);
            holder.ATV_NUMBER_OF_NEW_MESSAGES.setVisibility(View.VISIBLE);
            holder.ATV_NUMBER_OF_NEW_MESSAGES.setText("1");
            holder.IV_NUMBER_OF_MESSAGES_DOT.setVisibility(View.VISIBLE);
            holder.ATV_TIMESTAMP.setTextColor(Color.parseColor("#007FFF"));
            holder.TV_NEW_MESSAGE.setTextColor(Color.BLACK);
            holder.IV_BORDER.setBackgroundResource(R.drawable.rounded_dash_border_blue);
            holder.V_BOTTOM_LINE.setBackgroundColor(Color.parseColor("#007FFF"));
        }

    }

    private int convertDpToPixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public int getItemCount() {
        return hmMessages.size();
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

