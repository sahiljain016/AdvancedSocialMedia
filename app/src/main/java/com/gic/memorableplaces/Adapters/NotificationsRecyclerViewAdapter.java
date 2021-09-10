package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";
    //Variables


    private final Context mContext;
    private String TypeOfNotification;
    private int iCount;
    private ArrayList<String> alsLeftDateStamp, alsRightDateStamp;
    private LinkedHashMap<String, HashMap<String, String>> hmLeft, hmRight;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {
        public AutofitTextView ATV_NOTIFICATION_TITLE_LEFT, ATV_NOTIFICATION_TITLE_RIGHT,
                ATV_FILTERS_MATCHED_LEFT, ATV_FILTERS_MATCHED_RIGHT, ATV_TIMESTAMP_LEFT, ATV_TIMESTAMP_RIGHT;
        public AutofitTextView ATV_FILTER1_LEFT, ATV_FILTER2_LEFT, ATV_FILTER3_LEFT, ATV_FILTER4_LEFT, ATV_FILTER5_LEFT;
        public AutofitTextView ATV_FILTER1_RIGHT, ATV_FILTER2_RIGHT, ATV_FILTER3_RIGHT, ATV_FILTER4_RIGHT, ATV_FILTER5_RIGHT;
        public AutofitTextView ATV_UNDO_LEFT, ATV_UNDO_RIGHT;
        public CircleImageView IV_PP_LEFT, IV_PP_RIGHT, IV_BORDER_LEFT, IV_BORDER_RIGHT;
        public CardView CV_NOTIF_RIGHT, CV_NOTIF_LEFT;
        public ImageView IV_DOT_LEFT_1, IV_DOT_LEFT_2, IV_DOT_LEFT_3, IV_DOT_RIGHT_1, IV_DOT_RIGHT_2, IV_DOT_RIGHT_3;
        public RelativeLayout RL_PP;
        public ConstraintLayout CL_NOTIFICATIONS_MAIN;


        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ATV_NOTIFICATION_TITLE_LEFT = itemView.findViewById(R.id.ATV_NOTIFICATION_TITLE_LEFT);
            ATV_NOTIFICATION_TITLE_RIGHT = itemView.findViewById(R.id.ATV_NOTIFICATION_TITLE_RIGHT);
            ATV_FILTERS_MATCHED_LEFT = itemView.findViewById(R.id.ATV_FILTER_MATCHED_LEFT);
            ATV_FILTERS_MATCHED_RIGHT = itemView.findViewById(R.id.ATV_FILTER_MATCHED_RIGHT);
            IV_PP_LEFT = itemView.findViewById(R.id.IV_PP_LEFT);
            IV_PP_RIGHT = itemView.findViewById(R.id.IV_PP_RIGHT);
            CV_NOTIF_RIGHT = itemView.findViewById(R.id.CV_NOTIF_RIGHT);
            CV_NOTIF_LEFT = itemView.findViewById(R.id.CV_NOTIF_LEFT);
            RL_PP = itemView.findViewById(R.id.RL_PP);
            CL_NOTIFICATIONS_MAIN = itemView.findViewById(R.id.CL_NOTIFICATIONS_MAIN);
            ATV_TIMESTAMP_LEFT = itemView.findViewById(R.id.ATV_TIME_STAMP_LEFT);
            ATV_TIMESTAMP_RIGHT = itemView.findViewById(R.id.ATV_TIME_STAMP_RIGHT);
            ATV_FILTER1_LEFT = itemView.findViewById(R.id.ATV_FILTER1_LEFT);
            ATV_FILTER2_LEFT = itemView.findViewById(R.id.ATV_FILTER2_LEFT);
            ATV_FILTER3_LEFT = itemView.findViewById(R.id.ATV_FILTER3_LEFT);
            ATV_FILTER4_LEFT = itemView.findViewById(R.id.ATV_FILTER4_LEFT);
            ATV_FILTER5_LEFT = itemView.findViewById(R.id.ATV_FILTER5_LEFT);
            ATV_FILTER1_RIGHT = itemView.findViewById(R.id.ATV_FILTER1_RIGHT);
            ATV_FILTER2_RIGHT = itemView.findViewById(R.id.ATV_FILTER2_RIGHT);
            ATV_FILTER3_RIGHT = itemView.findViewById(R.id.ATV_FILTER3_RIGHT);
            ATV_FILTER4_RIGHT = itemView.findViewById(R.id.ATV_FILTER4_RIGHT);
            ATV_FILTER5_RIGHT = itemView.findViewById(R.id.ATV_FILTER5_RIGHT);
            ATV_UNDO_LEFT = itemView.findViewById(R.id.ATV_UNDO_LEFT);
            ATV_UNDO_RIGHT = itemView.findViewById(R.id.ATV_UNDO_RIGHT);
            IV_DOT_LEFT_1 = itemView.findViewById(R.id.IV_DOT_LEFT_1);
            IV_DOT_LEFT_2 = itemView.findViewById(R.id.IV_DOT_LEFT_2);
            IV_DOT_LEFT_3 = itemView.findViewById(R.id.IV_DOT_LEFT_3);
            IV_DOT_RIGHT_1 = itemView.findViewById(R.id.IV_DOT_RIGHT_1);
            IV_DOT_RIGHT_2 = itemView.findViewById(R.id.IV_DOT_RIGHT_2);
            IV_DOT_RIGHT_3 = itemView.findViewById(R.id.IV_DOT_RIGHT_3);
            IV_BORDER_LEFT = itemView.findViewById(R.id.IV_BLUE_BORDER_LEFT);
            IV_BORDER_RIGHT = itemView.findViewById(R.id.IV_BLUE_BORDER_RIGHT);
        }

    }


    public NotificationsRecyclerViewAdapter(Context context, ArrayList<String> LeftDateStamps, ArrayList<String> RightDateStamps
            , LinkedHashMap<String, HashMap<String, String>> Left, LinkedHashMap<String, HashMap<String, String>> Right, int count, String Type) {

        mContext = context;
        alsLeftDateStamp = LeftDateStamps;
        alsRightDateStamp = RightDateStamps;
        hmLeft = Left;
        hmRight = Right;
        iCount = count;
        TypeOfNotification = Type;


    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notifications_filter, parent, false);
        return new MainFeedViewHolder(v);


    }


    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);


        InflateNotification(position, true, TypeOfNotification, hmLeft, alsLeftDateStamp, holder.ATV_NOTIFICATION_TITLE_LEFT,
                holder.IV_BORDER_LEFT, holder.IV_DOT_LEFT_1, holder.IV_DOT_LEFT_2, holder.IV_DOT_LEFT_3, holder.IV_PP_LEFT,
                holder.ATV_TIMESTAMP_LEFT, holder.ATV_FILTER1_LEFT, holder.ATV_FILTER2_LEFT, holder.ATV_FILTER3_LEFT,
                holder.ATV_FILTER4_LEFT, holder.ATV_FILTER5_LEFT, holder.ATV_FILTERS_MATCHED_LEFT, holder.ATV_UNDO_LEFT,
                null, null);

        InflateNotification(position, false, TypeOfNotification, hmRight, alsRightDateStamp, holder.ATV_NOTIFICATION_TITLE_RIGHT,
                holder.IV_BORDER_RIGHT, holder.IV_DOT_RIGHT_1, holder.IV_DOT_RIGHT_2, holder.IV_DOT_RIGHT_3, holder.IV_PP_RIGHT,
                holder.ATV_TIMESTAMP_RIGHT, holder.ATV_FILTER1_RIGHT, holder.ATV_FILTER2_RIGHT, holder.ATV_FILTER3_RIGHT,
                holder.ATV_FILTER4_RIGHT, holder.ATV_FILTER5_RIGHT, holder.ATV_FILTERS_MATCHED_RIGHT, holder.ATV_UNDO_RIGHT,
                holder.CV_NOTIF_RIGHT, holder.RL_PP);


        holder.CV_NOTIF_RIGHT.post(() -> {


            int LeftHeight, RightHeight;

            LeftHeight = holder.CV_NOTIF_LEFT.getMeasuredHeight();
            RightHeight = holder.CV_NOTIF_RIGHT.getMeasuredHeight();

            Log.d(TAG, "onTransitionCompleted: LeftHeight: " + LeftHeight);
            Log.d(TAG, "onTransitionCompleted: RightHeight: " + RightHeight);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.CL_NOTIFICATIONS_MAIN);

            if (LeftHeight < RightHeight) {
                constraintSet.connect(R.id.CV_NOTIF_LEFT, ConstraintSet.BOTTOM, R.id.CV_NOTIF_RIGHT, ConstraintSet.BOTTOM, MiscTools.convertDpToPixel(10, mContext));
                constraintSet.constrainDefaultHeight(R.id.CV_NOTIF_LEFT, ConstraintSet.MATCH_CONSTRAINT);
                constraintSet.applyTo(holder.CL_NOTIFICATIONS_MAIN);

                holder.CV_NOTIF_LEFT.getLayoutParams().height = RightHeight;
                holder.CV_NOTIF_LEFT.requestLayout();
            } else if (LeftHeight > RightHeight) {

                holder.CV_NOTIF_RIGHT.getLayoutParams().height = LeftHeight;
                holder.CV_NOTIF_RIGHT.requestLayout();
            }
        });
    }


    public int countChar(String str, char c) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                count++;
        }

        return count;
    }



    @Override
    public int getItemCount() {
        return hmLeft.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void InflateNotification(int position, boolean isLeft, String Type, LinkedHashMap<String, HashMap<String, String>> hm,
                                     ArrayList<String> als, AutofitTextView ATV_NOTIFICATION, CircleImageView IV_BORDER, ImageView IV_DOT1,
                                     ImageView IV_DOT2, ImageView IV_DOT3, CircleImageView IV_PP, AutofitTextView ATV_TIMESTAMP, AutofitTextView ATV_FILTER1,
                                     AutofitTextView ATV_FILTER2, AutofitTextView ATV_FILTER3, AutofitTextView ATV_FILTER4,
                                     AutofitTextView ATV_FILTER5, AutofitTextView ATV_FILTERS_MATCHED, AutofitTextView ATV_UNDO,
                                     CardView CV_NOTIF, RelativeLayout RL_PP) {
        if (!isLeft) {
            if ((position) >= als.size()) {
                CV_NOTIF.setVisibility(View.GONE);
                RL_PP.setVisibility(View.GONE);
                return;
            }
        }
        MiscTools miscTools = new MiscTools();

        Log.d(TAG, "onBindViewHolder: Left Date Stamp: " + als.get(position));
        Log.d(TAG, "onBindViewHolder: hmLeft Details: " + hm.get(als.get(position)));
        ATV_NOTIFICATION.setText(Html.fromHtml(
                "<B>"
                        + hm.get(als.get(position)).get(mContext.getString(R.string.field_username))
                        + "</B>"
                        + "<BR>"
                        + "has followed you!"));

        if (hm.get(als.get(position)).get("isSeen").equals("false")) {
            IV_BORDER.setBorderColor(Color.parseColor("#0023F6"));
            IV_DOT1.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.home_color_2, null)));
            IV_DOT2.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.home_color_2, null)));
            IV_DOT3.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.home_color_2, null)));
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
                ATV_TIMESTAMP.setText(miscTools.SetUpDateWidget(als.get(position), false, TimeStamp));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
//        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        UniversalImageLoader.setImage(hmLeft.get(alsLeftDateStamp.get(position)).get(mContext.getString(R.string.field_profile_photo)), IV_PP, null, "");
        if (!hm.get(als.get(position)).get(mContext.getString(R.string.field_profile_photo)).equals("N/A"))
            GlideImageLoader.loadImageWithOutTransition(mContext, hm.get(als.get(position)).get(mContext.getString(R.string.field_profile_photo)), IV_PP);
        else
            IV_PP.setImageResource(R.drawable.default_user_profile);


        if (!hm.get(als.get(position)).get(mContext.getString(R.string.field_filters_matched)).equals(mContext.getString(R.string.field_random_match))) {
            String filters = hm.get(als.get(position)).get(mContext.getString(R.string.field_filters_matched));
            if (Type.equals(mContext.getString(R.string.field_received))) {

                int commas = countChar(filters, ',');
                String[] FiltersList = new String[0];
                if (commas != 0) {
                    FiltersList = filters.substring(filters.indexOf('[') + 1, filters.indexOf(']')).split(",");
                }
                if (commas == 0) {
                    ATV_FILTER1.setVisibility(View.VISIBLE);
                    ATV_FILTER1.setText(filters.substring(filters.indexOf('[') + 1, filters.indexOf(']')));
                } else if (commas == 1) {
                    ATV_FILTER1.setVisibility(View.VISIBLE);
                    ATV_FILTER2.setVisibility(View.VISIBLE);
                    ATV_FILTER1.setText(FiltersList[0]);
                    ATV_FILTER2.setText(FiltersList[1]);
                } else if (commas == 2) {
                    ATV_FILTER1.setVisibility(View.VISIBLE);
                    ATV_FILTER2.setVisibility(View.VISIBLE);
                    ATV_FILTER3.setVisibility(View.VISIBLE);
                    ATV_FILTER1.setText(FiltersList[0]);
                    ATV_FILTER2.setText(FiltersList[1]);
                    ATV_FILTER3.setText(FiltersList[2]);
                } else if (commas == 3) {
                    ATV_FILTER1.setVisibility(View.VISIBLE);
                    ATV_FILTER2.setVisibility(View.VISIBLE);
                    ATV_FILTER3.setVisibility(View.VISIBLE);
                    ATV_FILTER4.setVisibility(View.VISIBLE);
                    ATV_FILTER1.setText(FiltersList[0]);
                    ATV_FILTER2.setText(FiltersList[1]);
                    ATV_FILTER3.setText(FiltersList[2]);
                    ATV_FILTER4.setText(FiltersList[3]);
                } else {
                    ATV_FILTER1.setVisibility(View.VISIBLE);
                    ATV_FILTER2.setVisibility(View.VISIBLE);
                    ATV_FILTER3.setVisibility(View.VISIBLE);
                    ATV_FILTER4.setVisibility(View.VISIBLE);
                    ATV_FILTER5.setVisibility(View.VISIBLE);
                    ATV_FILTER1.setText(FiltersList[0]);
                    ATV_FILTER2.setText(FiltersList[1]);
                    ATV_FILTER3.setText(FiltersList[2]);
                    ATV_FILTER4.setText(FiltersList[3]);
                    String[] finalFiltersList = FiltersList;
                    ATV_FILTER5.setOnClickListener(v -> {
                        StringBuilder MoreFilters = new StringBuilder();
                        for (int i = 4; i < finalFiltersList.length; i++) {
                            if (i == finalFiltersList.length - 1)
                                MoreFilters.append(finalFiltersList[i]);
                            else
                                MoreFilters.append(finalFiltersList[i]).append(",");
                        }

                        miscTools.InflateBalloonTooltip(mContext, MoreFilters.toString(), 0, ATV_FILTER5);

                    });
                }
            } else {
                ATV_FILTERS_MATCHED.setText(mContext.getString(R.string.field_filters_matched));
                ATV_FILTERS_MATCHED.setBackgroundResource(R.drawable.gradient_rounded_purple_pink);
                ATV_FILTERS_MATCHED.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 10);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                ATV_FILTERS_MATCHED.setLayoutParams(lp);
                miscTools.InflateBalloonTooltip(mContext, filters, 0, ATV_FILTER5);

                ATV_UNDO.setVisibility(View.VISIBLE);
                FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);

                ATV_UNDO.setOnClickListener(v -> {
                    mFirebaseMethods.removeFollowerAndFollowing(hm.get(als.get(position)).get(mContext.getString(R.string.field_user_id)));
                    mFirebaseMethods.DeleteFollowFromFilterList(hm.get(als.get(position)).get(mContext.getString(R.string.field_user_id)));
                    hm.remove(als.get(position));
                    als.remove(position);
                    iCount -= iCount;
                    notifyDataSetChanged();
                });
            }
        } else {
            ATV_FILTERS_MATCHED.setText(hm.get(als.get(position)).get(mContext.getString(R.string.field_filters_matched)));
            ATV_FILTERS_MATCHED.setBackgroundResource(R.drawable.gradient_rounded_purple_pink);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 10, 0, 10);
            lp.gravity = Gravity.CENTER;
            ATV_FILTERS_MATCHED.setLayoutParams(lp);
            ATV_FILTERS_MATCHED.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if (!Type.equals(mContext.getString(R.string.field_received))) {
                ATV_UNDO.setVisibility(View.VISIBLE);
                FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);
                ATV_UNDO.setOnClickListener(v -> {
                    mFirebaseMethods.removeFollowerAndFollowing(hm.get(als.get(position)).get(mContext.getString(R.string.field_user_id)));
                    mFirebaseMethods.DeleteFollowFromFilterList(hm.get(als.get(position)).get(mContext.getString(R.string.field_user_id)));
                    hm.remove(als.get(position));
                    als.remove(position);
                    iCount -= iCount;
                    notifyDataSetChanged();
                });
            }

        }
    }


}

