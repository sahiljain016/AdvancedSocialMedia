

package com.gic.memorableplaces.FilterFriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.gic.memorableplaces.Adapters.MessagesFilterRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.NotificationsRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.OnSwipeListener;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeSet;

import io.alterac.blurkit.BlurLayout;
import me.grantland.widget.AutofitTextView;


public class LikesAndMessagesFragment extends Fragment {
    private static final String TAG = "LikesAndMessagesFragment";

    private AutofitTextView ATV_RECEIVED, ATV_SENT, ATV_TITLE;
    private ConstraintLayout CL_CUSTOM_SWITCH;
    private PorterShapeImageView IV_BG_IMAGE;
    private ImageView IV_DOT_MESSAGES, IV_DOT_NOTIFICATIONS;
    private BlurLayout BL_CUSTOM_SWITCH;
    private CardView CV_TOP_NOTIF;

    private AnimatedRecyclerView rResults;
    private NotificationsRecyclerViewAdapter notificationsRecyclerViewAdapter;
    private MessagesFilterRecyclerViewAdapter messagesFilterRecyclerViewAdapter;

    private DatabaseReference myRef;

    private Context mContext;
    private boolean isLeft = true;
    private String userUID;

    private LinkedHashMap<String, HashMap<String, String>> hmNotificationsLeft, hmNotificationsRight, hmMessages;
    private ArrayList<String> alsDateStamps;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_filters, container, false);
        Log.d(TAG, "onCreateView: LikesAndMessagesFragment");

        mContext = getActivity();
        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userUID = mAuth.getCurrentUser().getUid();


        hmNotificationsLeft = new LinkedHashMap<>();
        hmMessages = new LinkedHashMap<>();
        hmNotificationsRight = new LinkedHashMap<>();
        alsDateStamps = new ArrayList<>();

//        View v_CENTER_DIVIDER = view.findViewById(R.id.V_CENTER_DIVIDER);
//        View v_WHITE_HIGHLIGHT = view.findViewById(R.id.V_WHITE_HIGHLIGHT);
        ATV_RECEIVED = view.findViewById(R.id.ATV_RECEIVED);
        ATV_SENT = view.findViewById(R.id.ATV_SENT);
        rResults = view.findViewById(R.id.ARV_NOTIF_AND_MESSAGES);
        CL_CUSTOM_SWITCH = view.findViewById(R.id.CL_TOP_SWITCH);
        BL_CUSTOM_SWITCH = view.findViewById(R.id.BL_CUSTOM_SWITCH);
        IV_BG_IMAGE = view.findViewById(R.id.PSIV_NOTIFICATION_MESSAGES);
        IV_DOT_NOTIFICATIONS = view.findViewById(R.id.IV_DOT_NOTIFICATIONS);
        IV_DOT_MESSAGES = view.findViewById(R.id.IV_DOT_MESSAGES);
        ATV_TITLE = view.findViewById(R.id.ATV_TITLE_NOTIFCATION_MESSAGES);
        CV_TOP_NOTIF = view.findViewById(R.id.CV_TOP_NOTIF);

        CV_TOP_NOTIF.setBackgroundResource(R.drawable.rounded_rect_tl_tr_white);
        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/fredoka_one.ttf");
        ATV_TITLE.setTypeface(title, Typeface.NORMAL);
        IV_BG_IMAGE.setTag("Notifications");

        view.setOnTouchListener(new OnSwipeListener(mContext) {
            @Override
            public void onSwipeRight() {

                if (IV_BG_IMAGE.getTag().equals("Notifications")) {

                    IV_BG_IMAGE.setTag("Messages");
                    ATV_TITLE.setText("Messages");
                    IV_BG_IMAGE.setImageResource(R.drawable.il_messages_filter);
                    BL_CUSTOM_SWITCH.invalidate();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0, 60, 0, 0);
                    layoutParams.addRule(RelativeLayout.BELOW,R.id.CV_CUSTOM_SWITCH);
                    rResults.setLayoutParams(layoutParams);

                    IV_DOT_MESSAGES.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    IV_DOT_NOTIFICATIONS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6C6C6C")));
                    ATV_RECEIVED.performClick();

                }
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                if (IV_BG_IMAGE.getTag().equals("Messages")) {
                    IV_BG_IMAGE.setTag("Notifications");
                    ATV_TITLE.setText("Notifications");

                    IV_BG_IMAGE.setImageResource(R.drawable.il_notifications_filter);
                    BL_CUSTOM_SWITCH.invalidate();

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0, 60, 0, 0);
                    layoutParams.addRule(RelativeLayout.BELOW,R.id.CV_CUSTOM_SWITCH);
                    rResults.setLayoutParams(layoutParams);

                    IV_DOT_MESSAGES.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6C6C6C")));
                    IV_DOT_NOTIFICATIONS.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    ATV_RECEIVED.performClick();
                }

                super.onSwipeLeft();
            }
        });


        ATV_SENT.setOnClickListener(v -> {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(CL_CUSTOM_SWITCH);
            constraintSet.connect(R.id.V_WHITE_HIGHLIGHT, ConstraintSet.END, R.id.CL_TOP_SWITCH, ConstraintSet.END, 0);
            constraintSet.connect(R.id.V_WHITE_HIGHLIGHT, ConstraintSet.START, R.id.V_CENTER_DIVIDER, ConstraintSet.END, 0);
            constraintSet.applyTo(CL_CUSTOM_SWITCH);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(6, 3, 3, 3);
            ATV_SENT.setTextColor(Color.parseColor("#DA0000"));
            ATV_RECEIVED.setTextColor(Color.BLACK);

            Log.d(TAG, "onCreateView: sent value: " + ATV_SENT.getText().toString().substring(ATV_SENT.getText().length() - 2, ATV_SENT.getText().length()));
            if (!ATV_SENT.getText().toString().substring(ATV_SENT.getText().length() - 2, ATV_SENT.getText().length()).equals("0")) {

                if (IV_BG_IMAGE.getTag().equals("Notifications")) {
                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(mContext.getString(R.string.field_follow_list_for_notifications))
                            .child(userUID)
                            .child(mContext.getString(R.string.field_sent_count))
                            .setValue(0);
                } else {
                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(mContext.getString(R.string.field_message_list_for_notifications))
                            .child(userUID)
                            .child(mContext.getString(R.string.field_sent_count))
                            .setValue(0);
                }
            }
            ClearAllList();

            if (IV_BG_IMAGE.getTag().equals("Notifications"))
                GetDetailsForNotifications(mContext.getString(R.string.field_sent), mContext.getString(R.string.field_follow_list_for_notifications));
            else
                GetDetailsForNotifications(mContext.getString(R.string.field_sent), mContext.getString(R.string.field_message_list_for_notifications));


        });

        ATV_RECEIVED.setOnClickListener(v -> {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(CL_CUSTOM_SWITCH);
            constraintSet.connect(R.id.V_WHITE_HIGHLIGHT, ConstraintSet.START, R.id.CL_TOP_SWITCH, ConstraintSet.START, 0);
            constraintSet.connect(R.id.V_WHITE_HIGHLIGHT, ConstraintSet.END, R.id.V_CENTER_DIVIDER, ConstraintSet.START, 0);
            constraintSet.applyTo(CL_CUSTOM_SWITCH);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(3, 3, 6, 3);
            ATV_SENT.setTextColor(Color.BLACK);
            ATV_RECEIVED.setTextColor(Color.parseColor("#DA0000"));
            Log.d(TAG, "onCreateView: recevied value: " + ATV_RECEIVED.getText().toString().substring(ATV_RECEIVED.getText().length() - 2, ATV_RECEIVED.getText().length()));
            if (!ATV_RECEIVED.getText().toString().substring(ATV_RECEIVED.getText().length() - 2, ATV_RECEIVED.getText().length()).equals("0")) {
                if (IV_BG_IMAGE.getTag().equals("Notifications")) {
                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(mContext.getString(R.string.field_follow_list_for_notifications))
                            .child(userUID)
                            .child(mContext.getString(R.string.field_received_count))
                            .setValue(0);
                } else {
                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(mContext.getString(R.string.field_message_list_for_notifications))
                            .child(userUID)
                            .child(mContext.getString(R.string.field_received_count))
                            .setValue(0);
                }

            }

            Log.d(TAG, "onCreateView: tag: " + IV_BG_IMAGE.getTag());

            if (IV_BG_IMAGE.getTag().equals("Notifications"))
                GetDetailsForNotifications(mContext.getString(R.string.field_received), mContext.getString(R.string.field_follow_list_for_notifications));
            else
                GetDetailsForNotifications(mContext.getString(R.string.field_received), mContext.getString(R.string.field_message_list_for_notifications));


        });

        GetDetailsForNotifications(mContext.getString(R.string.field_received), mContext.getString(R.string.field_follow_list_for_notifications));


        return view;
    }

    private void ClearAllList() {
        hmNotificationsLeft.clear();
        hmNotificationsRight.clear();
        hmMessages.clear();
        alsDateStamps.clear();
        isLeft = true;
    }


    private void GetDetailsForNotifications(String TypeOfNotification, String NotificationOrMessage) {
        ClearAllList();
        Query query = myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(NotificationOrMessage)
                .child(userUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                ArrayList<String> RightDateStamps = null, LeftDateStamps = null;
                if (NotificationOrMessage.equals(mContext.getString(R.string.field_follow_list_for_notifications))) {
                    LeftDateStamps = new ArrayList<>();
                    RightDateStamps = new ArrayList<>();
                }
                ATV_RECEIVED.setText("RECEIVED" + "(" + snapshot.child(mContext.getString(R.string.field_received_count)).getValue() + ")");
                ATV_SENT.setText("SENT" + "(" + snapshot.child(mContext.getString(R.string.field_sent_count)).getValue() + ")");

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(NotificationOrMessage)
                        .child(userUID)
                        .child(mContext.getString(R.string.field_received_count))
                        .setValue(0);


                for (DataSnapshot UIDs : snapshot.child(TypeOfNotification).getChildren()) {
                    alsDateStamps.add(UIDs.child(mContext.getString(R.string.field_date_added)).getValue().toString());
                }
                if (alsDateStamps.isEmpty()) {

                    if (NotificationOrMessage.equals(mContext.getString(R.string.field_message_list_for_notifications)))
                        notificationsRecyclerViewAdapter.notifyDataSetChanged();
                    else
                        messagesFilterRecyclerViewAdapter.notifyDataSetChanged();

                    return;
                }
                ArrayList<String> SortedList = new ArrayList<>(new TreeSet<>(alsDateStamps));
                Collections.reverse(SortedList);
                Log.d(TAG, String.format("onDataChange: sortedList: %s", SortedList));
                for (int i = 0; i < SortedList.size(); i++) {
                    for (DataSnapshot UIDs : snapshot.child(TypeOfNotification).getChildren()) {
                        HashMap<String, String> TempHM = new HashMap<>();
                        TempHM.put(mContext.getString(R.string.field_user_id), UIDs.getKey());
                        if (UIDs.child(mContext.getString(R.string.field_date_added)).getValue().toString().equals(SortedList.get(i))) {
                            for (DataSnapshot NotifChildren : UIDs.getChildren()) {
                                String Value = "";
                                String Field = "";
                                if (NotifChildren.getKey().equals(mContext.getString(R.string.field_username))) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = mContext.getString(R.string.field_username);
                                } else if (NotifChildren.getKey().equals(mContext.getString(R.string.field_filters_matched))) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = mContext.getString(R.string.field_filters_matched);
                                } else if (NotifChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = mContext.getString(R.string.field_profile_photo);
                                } else if (NotifChildren.getKey().equals("isSeen")) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = "isSeen";
                                    if (NotificationOrMessage.equals(mContext.getString(R.string.field_follow_list_for_notifications))) {
                                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                                .child(NotificationOrMessage)
                                                .child(userUID)
                                                .child(TypeOfNotification)
                                                .child(Objects.requireNonNull(UIDs.getKey()))
                                                .child("isSeen")
                                                .setValue(true);
                                    }
                                } else if (NotifChildren.getKey().equals(mContext.getString(R.string.field_message_sent))) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = mContext.getString(R.string.field_message_sent);
                                } else if (NotifChildren.getKey().equals(mContext.getString(R.string.field_animation_type_filter_message))) {
                                    Value = NotifChildren.getValue().toString();
                                    Field = mContext.getString(R.string.field_animation_type_filter_message);
                                }

                                if (!TextUtils.isEmpty(Field)) {
                                    TempHM.put(Field, Value);
                                }
                            }

                            Log.d(TAG, String.format("onDataChange: SortedList Before: %s", SortedList));
                            if (NotificationOrMessage.equals(mContext.getString(R.string.field_follow_list_for_notifications))) {
                                if (isLeft) {
                                    hmNotificationsLeft.put(SortedList.get(i), TempHM);
                                    LeftDateStamps.add(SortedList.get(i));
                                    isLeft = false;
                                } else {
                                    hmNotificationsRight.put(SortedList.get(i), TempHM);
                                    RightDateStamps.add(SortedList.get(i));
                                    isLeft = true;
                                }
                            } else {
                                hmMessages.put(SortedList.get(i), TempHM);
                            }
                            Log.d(TAG, String.format("onDataChange: SortedList After: %s", SortedList));
                            Log.d(TAG, String.format("onDataChange: hmMessages: %s", hmMessages));

                        }

                    }
                }
                //reverse array here

                if (NotificationOrMessage.equals(mContext.getString(R.string.field_follow_list_for_notifications))) {
                    notificationsRecyclerViewAdapter = new NotificationsRecyclerViewAdapter(mContext, LeftDateStamps, RightDateStamps,
                            hmNotificationsLeft, hmNotificationsRight, hmNotificationsLeft.size(), TypeOfNotification);
                    rResults.setAdapter(notificationsRecyclerViewAdapter);
                    notificationsRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    messagesFilterRecyclerViewAdapter = new MessagesFilterRecyclerViewAdapter(mContext, hmMessages, TypeOfNotification,
                            SortedList);
                    rResults.setAdapter(messagesFilterRecyclerViewAdapter);
                    messagesFilterRecyclerViewAdapter.notifyDataSetChanged();
                }
                rResults.setItemAnimator(new DefaultItemAnimator());
                rResults.scheduleLayoutAnimation();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}

