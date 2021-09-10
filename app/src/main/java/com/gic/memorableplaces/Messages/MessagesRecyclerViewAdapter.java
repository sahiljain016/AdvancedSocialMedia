package com.gic.memorableplaces.Messages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gic.memorableplaces.DataModels.MessagePreview;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "MessagesRecyclerViewAdapter";

    //Variables
    private final ArrayList<MessagePreview> alMessagePreviews;
    private Context mContext;
    private final boolean bIsPinned;
    private Bundle bundle;
    private String sOtherPublicKey;
    private final Activity mActivity;
    private boolean isGroup;
    //Classes
    private final MiscTools miscTools = new MiscTools();
    //Firebase
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public RoundedImageView RIV_PP_BORDER, RIV_PP, RIV_ONLINE, RIV_ONLINE_DOT, RIV_NO_OF_MESSAGES;
        public TextView TV_MESSAGE;
        public AutofitTextView ATV_USERNAME, ATV_TIMESTAMP, ATV_No_Of_Messages;
        public ConstraintLayout CL_IMAGES;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            RIV_PP_BORDER = itemView.findViewById(R.id.RIV_PP_BORDER);
            RIV_PP = itemView.findViewById(R.id.RIV_PP_MESSAGES);
            RIV_ONLINE = itemView.findViewById(R.id.RIV_ONLINE);
            ATV_USERNAME = itemView.findViewById(R.id.ATV_USERNAME_MESSAGES);
            ATV_TIMESTAMP = itemView.findViewById(R.id.ATV_TIMESTAMP_MESSAGES);
            ATV_No_Of_Messages = itemView.findViewById(R.id.ATV_No_Of_Pending_Messages);
            RIV_ONLINE_DOT = itemView.findViewById(R.id.RIV_ONLINE_DOT);
            RIV_NO_OF_MESSAGES = itemView.findViewById(R.id.RIV_NO_OF_MESSAGES);
            TV_MESSAGE = itemView.findViewById(R.id.TV_MESSAGE_ON_DISPLAY);
            CL_IMAGES = itemView.findViewById(R.id.CL_IMAGES_MESSAGES);
//            mMessage = itemView.findViewById(R.id.mMessage);
//            mPing = itemView.findViewById(R.id.mPing);
//            MessageRL = itemView.findViewById(R.id.base_RL_messages);
//            mUnreadMessageDot = itemView.findViewById(R.id.UnredMessage);
//            mGroupPP1 = itemView.findViewById(R.id.GroupProfilePhoto1);
//            mGroupPP2 = itemView.findViewById(R.id.GroupProfilePhoto2);

        }
    }

    public MessagesRecyclerViewAdapter(ArrayList<MessagePreview> messagePreviews, boolean isPinned, Context context, Activity activity) {
        alMessagePreviews = messagePreviews;
        mContext = context;
        mActivity = activity;
        bIsPinned = isPinned;

        // ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {


        //  Log.d(TAG, "onBindViewHolder: binding pos: " + holder.getBindingAdapterPosition());
        //Log.d(TAG, "onBindViewHolder: absolute pos:" + holder.getAbsoluteAdapterPosition());
        int pos = holder.getBindingAdapterPosition();
        //GetSingleUserInfo(alMessagePreviews, holder, pos);
        // Log.d(TAG, "onBindViewHolder: other pp: " + alMessagePreviews.get(pos).getOtherPPUrl());
        //Log.d(TAG, "onBindViewHolder: other pp: " + alMessagePreviews.get(pos).getOtherUsername());
        Glide.with(mContext)
                .load(alMessagePreviews.get(pos).getOther_pp_url())
                .thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.CL_IMAGES.animate().setDuration(200).alpha(1).start();
                        holder.RIV_PP.setBackgroundResource(R.drawable.ic_user_default_profile);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.CL_IMAGES.animate().setDuration(200).alpha(1).start();
                        return false;
                    }
                })
                .into(holder.RIV_PP);

        holder.ATV_USERNAME.setText(alMessagePreviews.get(pos).getOther_display_name());

        holder.TV_MESSAGE.setText(alMessagePreviews.get(pos).getLatest_message());

        if (alMessagePreviews.get(pos).getOnline())
            holder.RIV_ONLINE.setImageResource(R.color.is_online_color_green);
        else
            holder.RIV_ONLINE.setImageResource(R.color.is_not_online_color_yellow);

        GetTimeStamp(alMessagePreviews.get(pos).getString_date(), holder.ATV_TIMESTAMP);
        Log.d(TAG, "onBindViewHolder: pending messages: " + alMessagePreviews.get(pos).getPending_unseen_messages());
        if (alMessagePreviews.get(pos).getPending_unseen_messages() > 0) {

            holder.RIV_PP_BORDER.setVisibility(View.VISIBLE);
            holder.RIV_ONLINE_DOT.setVisibility(View.VISIBLE);
            holder.RIV_NO_OF_MESSAGES.setVisibility(View.VISIBLE);
            holder.ATV_USERNAME.setTypeface(holder.ATV_USERNAME.getTypeface(), Typeface.BOLD);
            holder.TV_MESSAGE.setTextColor(Color.parseColor("#202020"));

            holder.ATV_No_Of_Messages.setText(String.valueOf(alMessagePreviews.get(pos).getPending_unseen_messages()));

            if (bIsPinned) {
                holder.RIV_NO_OF_MESSAGES.setImageResource(R.drawable.gradient_pink_orange);
                holder.RIV_ONLINE_DOT.setImageResource(R.drawable.gradient_pink_orange);
                holder.RIV_PP_BORDER.setImageResource(R.drawable.gradient_pink_orange);
            } else {
                holder.RIV_NO_OF_MESSAGES.setImageResource(R.drawable.gradient_blue_green);
                holder.RIV_ONLINE_DOT.setImageResource(R.drawable.gradient_blue_green);
                holder.RIV_PP_BORDER.setImageResource(R.drawable.gradient_blue_green);
            }


        }

        ChatOnClick(holder, false);


//        if ((position % 2) == 0) {
//            holder.MessageRL.setBackgroundColor(Color.BLACK);
//            holder.mUsername.setTextColor(Color.WHITE);
//            holder.mProfilePhoto.setBorderColor(Color.WHITE);
//            holder.mPing.setImageResource(R.drawable.ic_ping_white);
//            myRef.child(mContext.getString(R.string.field_chats))
//                    .child(requireNonNull(mAuth.getCurrentUser()).getUid())
//                    .child(mUserIDList.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        if (Objects.equals(dataSnapshot.getKey(), "isSeen")) {
//                            if (Objects.equals(dataSnapshot.getValue(), "false")) {
//                                holder.mMessage.setTextColor(Color.WHITE);
//                                holder.mMessage.setTypeface(holder.mMessage.getTypeface(), Typeface.BOLD);
//                                holder.mUnreadMessageDot.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        } else {
//            holder.MessageRL.setBackgroundColor(Color.WHITE);
//            holder.mUsername.setTextColor(Color.BLACK);
//            holder.mProfilePhoto.setBorderColor(Color.BLACK);
//            holder.mPing.setImageResource(R.drawable.ic_ping);
//            myRef.child(mContext.getString(R.string.field_chats))
//                    .child(requireNonNull(mAuth.getCurrentUser()).getUid())
//                    .child(mUserIDList.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        if (Objects.equals(dataSnapshot.getKey(), "isSeen")) {
//                            if (Objects.equals(dataSnapshot.getValue(), "false")) {
//                                holder.mMessage.setTextColor(Color.BLACK);
//                                holder.mMessage.setTypeface(holder.mMessage.getTypeface(), Typeface.BOLD);
//                                holder.mUnreadMessageDot.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//
//        GetUserInfo(mUserIDList.get(position), holder.mUsername, holder.mProfilePhoto, holder.mGroupPP1, holder.mGroupPP2,holder.itemView,position);
//        getLatestMessage(mUserIDList.get(position), holder.mMessage);
        // Log.d(TAG, "onDataChange: isGroup " + isGroup);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bundle = new Bundle();
//                bundle.putString(mContext.getString(R.string.field_user_id), mUserIDList.get(position));
//              //  Log.d(TAG, "onClick: isGroup " + isGroup);
//                bundle.putBoolean("isGroup",isGroup);
//                ChatFragment fragment = new ChatFragment();
//                fragment.setArguments(bundle);
//                FragmentTransaction Transaction = ((MessageActivity) mActivity).getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.FL_messages, fragment);
//                Transaction.addToBackStack(mContext.getString(R.string.new_chat_fragment));
//                Transaction.commit();
//
//                myRef.child(mContext.getString(R.string.dbname_chats))
//                        .child(mAuth.getCurrentUser().getUid())
//                        .child(mUserIDList.get(position))
//                        .child("isSeen")
//                        .setValue("true");
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return alMessagePreviews.size();
    }


    private void GetSingleUserInfo(final ArrayList<MessagePreview> almp, MainFeedViewHolder holder, int position) {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(almp.get(position).getOther_user_uid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(mContext.getString(R.string.field_profile_photo)).getValue() != null) {

                }

                if (snapshot.child(mContext.getString(R.string.field_display_name)).getValue() != null)
                    holder.ATV_USERNAME.setText(snapshot.child(mContext.getString(R.string.field_display_name)).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void GetTimeStamp(String TimeStamp, AutofitTextView ATV_TIMESTAMP) {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault());

                Calendar CurrentCalendar = Calendar.getInstance();
                CurrentCalendar.setTimeInMillis(estimatedServerTimeMs);
                String CurrentTimeStamp = formatter.format(CurrentCalendar.getTime());

             //   calendar.setTimeInMillis(chat.getDate_messaged());

               // chat.setString_date(formatter.format(calendar.getTime()));


                try {

                    Calendar TimeStampCalender = Calendar.getInstance();
                    Date CurrentDate = formatter.parse(CurrentTimeStamp);
                    Date TimeStampDate = formatter.parse(TimeStamp);

                    if (CurrentDate != null) {
                        CurrentCalendar.setTime(CurrentDate);
                    }
                    if (TimeStampDate != null) {
                        TimeStampCalender.setTime(TimeStampDate);
                    }

                    if (CurrentCalendar.get(Calendar.YEAR) == TimeStampCalender.get(Calendar.YEAR) &&
                            CurrentCalendar.get(Calendar.WEEK_OF_YEAR) == TimeStampCalender.get(Calendar.WEEK_OF_YEAR)) {

                        //Log.d(TAG, "onDataChange: Current DAY OF YEAR: " + CurrentCalendar.get(Calendar.DAY_OF_YEAR));
                        //Log.d(TAG, "onDataChange: Time Stamp DAY OF YEAR: " + TimeStampCalender.get(Calendar.DAY_OF_YEAR));

                        if (CurrentCalendar.get(Calendar.DAY_OF_YEAR) == TimeStampCalender.get(Calendar.DAY_OF_YEAR)) {

                            ATV_TIMESTAMP.setText(TimeStampCalender.get(Calendar.HOUR) + ":" + TimeStampCalender.get(Calendar.MINUTE) + " " + ((TimeStampCalender.get(Calendar.AM_PM) == 0) ? "A.M" : "P.M"));
                        } else if ((CurrentCalendar.get(Calendar.DAY_OF_YEAR) - 1) == TimeStampCalender.get(Calendar.DAY_OF_YEAR)) {
                            ATV_TIMESTAMP.setText("yesterday");
                        } else {
                            // Log.d(TAG, "onDataChange: Day OF WEEK: " + TimeStampCalender.get(Calendar.DAY_OF_WEEK));
                            if (TimeStampDate != null) {
                                ATV_TIMESTAMP.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(TimeStampDate));
                            }
                        }

                    } else {
                        //Log.d(TAG, "onDataChange: Time Stamp month: " + TimeStampCalender.get(Calendar.MONTH));
                        ATV_TIMESTAMP.setText(TimeStampCalender.get(Calendar.YEAR) + "/" + (TimeStampCalender.get(Calendar.MONTH) + 1) + "/" + TimeStampCalender.get(Calendar.DATE));
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }

    //
//    private void GetUserInfo(final ArrayList<MessagePreview> almp, MainFeedViewHolder holder, int position) {
//
//
//        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                .child(UserID);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //Log.d(TAG, "Entered on Data Changed");
//                if (dataSnapshot.exists()) {
//                    Log.d(TAG, "onDataChange: SINGLE CHAT MESSAGE");
//                    isGroup = false;
//                    ChatOnClick(ItemView, position, false);
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        if (requireNonNull(ds.getKey()).equals(mContext.getString(R.string.field_profile_photo))) {
//                            GlideImageLoader.loadImageWithOutTransition(mContext, requireNonNull(ds.getValue()).toString(), profilePhoto);
//
//                        }
//                        if (ds.getKey().equals(mContext.getString(R.string.field_display_name))) {
//                            full_name.setText(requireNonNull(ds.getValue()).toString());
//
//                        }
//                    }
//                } else {
//                    //isGroup = true;
//                    // Log.d(TAG, "onDataChange: isGroup belwo " + isGroup);
//                    Log.d(TAG, "onDataChange: Group CHAT MESSAGE");
//                    mGroupPP1.setVisibility(View.VISIBLE);
//                    mGroupPP2.setVisibility(View.VISIBLE);
//                    ChatOnClick(ItemView, position, true);
//                    Query query1 = myRef.child(mContext.getString(R.string.dbname_chats))
//                            .child(requireNonNull(mAuth.getCurrentUser()).getUid())
//                            .child(UserID);
//
//                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            int count = 0;
//                            for (DataSnapshot ds : snapshot.getChildren()) {
//
//                                if (Objects.equals(ds.getKey(), "group_name")) {
//                                    full_name.setText(requireNonNull(ds.getValue()).toString());
//                                }
//                                if (Objects.equals(ds.getKey(), "members")) {
//                                    for (DataSnapshot memberSnapshot : ds.getChildren()) {
//                                        count++;
//                                        if (count > 2) {
//                                            break;
//                                        }
//                                        Query query2 = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                                                .child(requireNonNull(requireNonNull(memberSnapshot.child("user_id").getValue()).toString()));
//
//                                        final int finalCount = count;
//                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                for (DataSnapshot ProfilePhoto : snapshot.getChildren()) {
//                                                    if (requireNonNull(ProfilePhoto.getKey()).equals(mContext.getString(R.string.field_profile_photo))) {
//                                                        if (finalCount == 1) {
//
//                                                            GlideImageLoader.loadImageWithOutTransition(mContext, requireNonNull(ProfilePhoto.getValue()).toString(), mGroupPP1);
//                                                        }
//                                                        if (finalCount == 2) {
//
//                                                            GlideImageLoader.loadImageWithOutTransition(mContext, requireNonNull(ProfilePhoto.getValue()).toString(), mGroupPP2);
//
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    private void ChatOnClick(MainFeedViewHolder holder, final boolean isGroup) {
        holder.itemView.setOnClickListener(v -> {
            bundle = new Bundle();
            Log.d(TAG, String.format("ChatOnClick: alMessagePreviews: %s", alMessagePreviews));
            Log.d(TAG, "onBindViewHolder: binding pos: " + holder.getBindingAdapterPosition());
            Log.d(TAG, "ChatOnClick: pos: " + holder.getBindingAdapterPosition());
            int pos = holder.getBindingAdapterPosition();
            bundle.putString(mContext.getString(R.string.field_user_id), alMessagePreviews.get(pos).getOther_user_uid());
            bundle.putString(mContext.getString(R.string.field_username), alMessagePreviews.get(pos).getOther_display_name());
            bundle.putString(mContext.getString(R.string.field_profile_photo), alMessagePreviews.get(pos).getOther_pp_url());
            bundle.putString(mContext.getString(R.string.field_chat_uid), alMessagePreviews.get(pos).getChat_uid());
            bundle.putBoolean(mContext.getString(R.string.field_is_message_pinned), bIsPinned);
            bundle.putBoolean("isGroup", isGroup);
            if (alMessagePreviews.get(pos).getLatest_sender_id().equals(alMessagePreviews.get(pos).getOther_user_uid())) {
                bundle.putString(mContext.getString(R.string.field_other_public_key), alMessagePreviews.get(pos).getSender_public_key());
                GetPublicKey(bundle, mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_my_public_key));
            } else {
                bundle.putString(mContext.getString(R.string.field_my_public_key), alMessagePreviews.get(pos).getSender_public_key());
                GetPublicKey(bundle, alMessagePreviews.get(pos).getOther_user_uid(), mContext.getString(R.string.field_other_public_key));
            }


        });
    }

    private void GetPublicKey(Bundle bundle, String UID, String Key) {
        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(UID)
                .child(mContext.getString(R.string.field_public_key));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    bundle.putString(Key, snapshot.getValue().toString());
                }

                ChatFragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                FragmentTransaction Transaction = ((NewMessageActivity) mActivity).getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FL_messages, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.chat_fragment));
                Transaction.commit();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void getLatestMessage(String UserID, final TextView message) {
//
//        Query query = myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(UserID);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if (Objects.equals(dataSnapshot.getKey(), "latest_message"))
//                        message.setText(requireNonNull(dataSnapshot.getValue()).toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }


}

