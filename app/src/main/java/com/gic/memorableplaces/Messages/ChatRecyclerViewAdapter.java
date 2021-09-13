package com.gic.memorableplaces.Messages;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.DataModels.Chat;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "ChatRecyclerViewAdapter";
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    //Variables
    private ArrayList<Chat> mChatList;
    private ArrayList<String> mSeenList;
    private HashMap<String, String> mUsernameList;
    private Context mContext;
    private String profileImgUrl, sUserID, sMyUID;
    private OnChatClicked MyOnChatClicked;
    private boolean isLeftChat, mIsGroup, bIsPinned;
    //Classes
    private MiscTools miscTools = new MiscTools();
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    ;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        OnChatClicked mOnChatClicked;
        public CircleImageView CIV_PP;
        public ImageView IV_ANCHOR;
        public TextView TV_MESSAGE;
        public AutofitTextView ATV_DAY_STAMP;
        public TextView /*mMessageStatus, mSenderUsername,*/ TV_TIMESTAMP, TV_MESSAGE_STATUS/*, mDateMessaged*/;
        /* public ImageView mDividerLeft, mDividerRight;*/
        // public View V_BG;
        public ConstraintLayout CL_CHAT;

        public MainFeedViewHolder(@NonNull View itemView, boolean isLeftChat, OnChatClicked onChatClicked) {
            super(itemView);

            mOnChatClicked = onChatClicked;

            // Log.d(TAG, "MainFeedViewHolder: isLeftChat: " + isLeftChat);
            if (isLeftChat) {
                CIV_PP = itemView.findViewById(R.id.CIV_PP_LEFT_CHAT);
                //mSenderUsername = itemView.findViewById(R.id.SenderUsername);
            } else {
                //  mDateMessaged = itemView.findViewById(R.id.Day_messaged);
                //  mDividerLeft = itemView.findViewById(R.id.divider_left);
                //  mDividerRight = itemView.findViewById(R.id.divider_right);
                // ML = itemView.findViewById(R.id.ML_CHAT_MESSAGE);
            }

            try {
                TV_MESSAGE = itemView.findViewById(R.id.TV_MESSAGE);
                TV_TIMESTAMP = itemView.findViewById(R.id.TV_TIMESTAMP_CHAT);
                ATV_DAY_STAMP = itemView.findViewById(R.id.ATV_DAY_STAMP);
                TV_MESSAGE_STATUS = itemView.findViewById(R.id.TV_MESSAGE_STATUS);
                CL_CHAT = itemView.findViewById(R.id.CL_CHAT_MESSAGE);
                IV_ANCHOR = itemView.findViewById(R.id.IV_ANCHOR_MARK);
                // mMessageStatus = itemView.findViewById(R.id.deliveredOrSeen);
                // V_BG = itemView.findViewById(R.id.V_BG_CHAT);


                //LeftMessageRL = itemView.findViewById(R.id.)
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            itemView.setOnLongClickListener(this);
            //MessageRL = itemView.findViewById(R.id.MessageRL);
        }

        @Override
        public boolean onLongClick(View v) {
            mOnChatClicked.onItemClick(getBindingAdapterPosition(), CL_CHAT, IV_ANCHOR);
            return false;
        }
    }

    public interface OnChatClicked {

        void onItemClick(int position, ConstraintLayout constraintLayout, ImageView imageView);
    }

    public ChatRecyclerViewAdapter(Context context, ArrayList<Chat> ChatList, String ProfilePhotoURL, String rUserID, boolean isGroup,
                                   boolean isPinned, HashMap<String, String> UsernameList, OnChatClicked onChatClicked) {
        mChatList = ChatList;
        mContext = context;
        MyOnChatClicked = onChatClicked;
        mFirebaseMethods = new FirebaseMethods(mContext);
        profileImgUrl = ProfilePhotoURL;
        sUserID = rUserID;
        mIsGroup = isGroup;
        bIsPinned = isPinned;
        mUsernameList = UsernameList;

        //ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
        //gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MSG_TYPE_LEFT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_left_chat, parent, false);
            //v.animate().alpha(0);
            //v.animate().alpha(1).setStartDelay(500);
            isLeftChat = true;
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_right_chat, parent, false);
            isLeftChat = false;
        }
        return new MainFeedViewHolder(v, isLeftChat, MyOnChatClicked);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        holder.CL_CHAT.setMaxWidth((int) (widthPixels / 1.5));
        holder.TV_MESSAGE.setMaxWidth((int) (widthPixels / 1.5));

        isLeftChat = !sMyUID.equals(mChatList.get(position).getSender());

        int pos = holder.getBindingAdapterPosition();
        final Chat chat = mChatList.get(pos);

        holder.TV_MESSAGE.setText(chat.getMessage());

        //Log.d(TAG, "onBindViewHolder: pp url: " + profileImgUrl);
        if (isLeftChat && holder.CIV_PP != null) {
            GlideImageLoader.loadImageWithOutTransition(
                    mContext,
                    profileImgUrl,
                    holder.CIV_PP);
        }

        if (chat.getAnchor_id() != null) {
            holder.IV_ANCHOR.setVisibility(View.VISIBLE);
        } else {
            holder.IV_ANCHOR.setVisibility(View.GONE);
        }
        Log.d(TAG, "onBindViewHolder: bubble color: " + chat);
        if (!chat.getBubbleColor().equals("0")) {
            Log.d(TAG, "onBindViewHolder: bubble color: " + chat);

            SetBubbleDrawable(holder, pos, chat);
            //  Log.d(TAG, "onBindViewHolder: sBubbleColor: " + chat.getBubbleColor());

//            drawable = (GradientDrawable) holder.CL_CHAT.getBackground();
            int[] colors;
            if (chat.isGradient()) {
                colors = new int[]{Lighten(Integer.parseInt(chat.getBubbleColor()))
                        , Integer.parseInt(chat.getBubbleColor())
                        , Darken(Integer.parseInt(chat.getBubbleColor()), 0.2)};
            } else {
                colors = new int[]{Integer.parseInt(chat.getBubbleColor()), Integer.parseInt(chat.getBubbleColor()), Integer.parseInt(chat.getBubbleColor())};
            }
            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
            GradientDrawable get = (GradientDrawable) holder.CL_CHAT.getBackground();

            drawable.setCornerRadii(get.getCornerRadii());
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            holder.CL_CHAT.setBackground(drawable);

        } else {
            holder.CL_CHAT.setBackground(null);
            holder.CL_CHAT.setBackgroundResource(0);
            SetBubbleDrawable(holder, pos, chat);
        }

        GetTimeStamp(chat.getString_date(), holder.TV_TIMESTAMP);

    }

    private void SetBubbleDrawable(MainFeedViewHolder holder, int pos, Chat chat) {
        if ((pos + 1) == mChatList.size()) {
            holder.TV_MESSAGE_STATUS.setVisibility(View.VISIBLE);
            setMessageStatus(holder.TV_MESSAGE_STATUS, false);
        } else {
            holder.TV_MESSAGE_STATUS.setVisibility(View.GONE);
        }

        if (mChatList.size() == 1) {
            holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
            AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
            if (isLeftChat) {
                holder.CIV_PP.onVisibilityAggregated(true);
                GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);

                // GetTimeStamp(chat.getString_date(), holder.TV_TIMESTAMP, false);
            } else {
                holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
            }
        } else if (mChatList.size() == 2) {
            if (pos == 0) {
                holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
                AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
                if (isLeftChat) {
                    holder.CIV_PP.onVisibilityAggregated(true);
                    GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                    holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                } else {
                    holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                }
            } else {
                if (!chat.getString_date().substring(0, 10).equals(mChatList.get(pos - 1).getString_date().substring(0, 10))) {
                    holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
                    AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
                    if (isLeftChat) {
                        holder.CIV_PP.onVisibilityAggregated(true);
                        GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                        holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                    } else {
                        holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                    }
                } else {
                    holder.ATV_DAY_STAMP.setVisibility(View.GONE);
                    if (isLeftChat) {
                        if (mChatList.get(0).getSender().equals(sMyUID)) {
                            holder.CIV_PP.onVisibilityAggregated(true);
                            GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);

                        } else {
                            holder.CIV_PP.setVisibility(View.GONE);
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_top_pink_orange : R.drawable.rounded_rect_chat_top_green_blue);

                        }
                    } else {
                        if (mChatList.get(0).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_top_grey);

                        } else {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                        }
                    }
                }

            }
        } else {
            if (pos == 0) {
                holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
                AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
                if (isLeftChat) {
                    if (mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                        holder.CIV_PP.onVisibilityAggregated(true);
                        GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                    } else {
                        holder.CIV_PP.setVisibility(View.GONE);
                    }
                    holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);

                    // GetTimeStamp(chat.getString_date(), holder.TV_TIMESTAMP, false);
                } else {
                    holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                }
            } else if ((pos + 1) == mChatList.size()) {
                if (!chat.getString_date().substring(0, 10).equals(mChatList.get(pos - 1).getString_date().substring(0, 10))) {
                    holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
                    AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
                    if (isLeftChat) {
                        holder.CIV_PP.onVisibilityAggregated(true);
                        GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                        holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);

                        // GetTimeStamp(chat.getString_date(), holder.TV_TIMESTAMP, false);
                    } else {
                        holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                    }
                } else {
                    holder.ATV_DAY_STAMP.setVisibility(View.GONE);
                    if (isLeftChat) {

                        holder.CIV_PP.onVisibilityAggregated(true);
                        GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);

                        } else {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_top_pink_orange : R.drawable.rounded_rect_chat_top_green_blue);

                        }
                    } else {
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_top_grey);

                        } else {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);

                        }
                    }

                }
            } else {
                if (!chat.getString_date().substring(0, 10).equals(mChatList.get(pos - 1).getString_date().substring(0, 10))) {
                    holder.ATV_DAY_STAMP.setVisibility(View.VISIBLE);
                    AddDayStamp(chat.getString_date(), holder.ATV_DAY_STAMP);
                    if (isLeftChat) {
                        holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                        if (!chat.getString_date().substring(0, 10).equals(mChatList.get(pos + 1).getString_date().substring(0, 10))) {
                            holder.CIV_PP.setVisibility(View.VISIBLE);
                            GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                        } else {
                            if (mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                                holder.CIV_PP.setVisibility(View.VISIBLE);
                                GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                            }
                        }
                    } else {
                        holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                    }
                } else if (!chat.getString_date().substring(0, 10).equals(mChatList.get(pos + 1).getString_date().substring(0, 10))) {
                    holder.ATV_DAY_STAMP.setVisibility(View.GONE);
                    if (isLeftChat) {
                        holder.CIV_PP.setVisibility(View.VISIBLE);
                        GlideImageLoader.loadImageWithOutTransition(mContext, profileImgUrl, holder.CIV_PP);
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                        } else {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_top_pink_orange : R.drawable.rounded_rect_chat_top_green_blue);

                        }
                    } else {
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_top_grey);
                        } else {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                        }
                    }
                } else {
                    holder.ATV_DAY_STAMP.setVisibility(View.GONE);

                    if (isLeftChat) {
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID) && mChatList.get(pos + 1).getSender().equals(sUserID)) {
                            holder.CIV_PP.setVisibility(View.GONE);
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                        } else if (mChatList.get(pos - 1).getSender().equals(sUserID) && mChatList.get(pos + 1).getSender().equals(sUserID)) {
                            holder.CIV_PP.setVisibility(View.GONE);
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_middle_pink_orange : R.drawable.rounded_rect_chat_middle_green_blue);
                        } else if (mChatList.get(pos - 1).getSender().equals(sMyUID) && mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_bottom_pink_orange : R.drawable.rounded_rect_chat_bottom_blue_green);
                        } else if (mChatList.get(pos - 1).getSender().equals(sUserID) && mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(bIsPinned ? R.drawable.rounded_rect_chat_top_pink_orange : R.drawable.rounded_rect_chat_top_green_blue);
                        }
                    } else {
                        if (mChatList.get(pos - 1).getSender().equals(sMyUID) && mChatList.get(pos + 1).getSender().equals(sUserID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_top_grey);
                        } else if (mChatList.get(pos - 1).getSender().equals(sUserID) && mChatList.get(pos + 1).getSender().equals(sUserID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                        } else if (mChatList.get(pos - 1).getSender().equals(sMyUID) && mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_middle_grey);
                        } else if (mChatList.get(pos - 1).getSender().equals(sUserID) && mChatList.get(pos + 1).getSender().equals(sMyUID)) {
                            holder.CL_CHAT.setBackgroundResource(R.drawable.rounded_rect_chat_bottom_grey);
                        }
                    }
                }
            }

        }
    }

    private int Darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private int Lighten(int color){

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = lightenColor(red);
        green = lightenColor(green);
        blue = lightenColor(blue);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    private  int darkenColor(int color, double fraction) {
        return (int)Math.max(color - (color * fraction), 0);
    }

    private  int lightenColor(int color) {
        return (int) Math.min(color + (color * 1.5), 255);
    }

    private void GetTimeStamp(String TimeStamp, TextView TV_TIMESTAMP) {
        // Log.d(TAG, "GetTimeStamp: Time Hours: " + Integer.parseInt(TimeStamp.substring(11, 13)));
        String time = "";
        int hours = Integer.parseInt(TimeStamp.substring(11, 13));
        if (hours < 12) {
            if (hours == 0) {
                time = "12" + TimeStamp.substring(13, 16) + " A.M";

            } else {
                time = TimeStamp.substring(11, 16) + " A.M";
            }
        } else {
            if (hours != 12) {
                time = (hours - 12) + TimeStamp.substring(13, 16) + " P.M";

            } else {
                time = TimeStamp.substring(11, 16) + " P.M";
            }
        }
        TV_TIMESTAMP.setText(time);
    }


    private void setMessageStatus(final TextView TV_TIMESTAMP, boolean isGroup) {
        // Log.d(TAG, "setMessageStatus: enetered ");
        if (!isGroup) {
            Query query = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                    .child(sUserID)
                    .child(sMyUID)
                    .child("isSeen");
            //  Log.d(TAG, "onBindViewHolder: sLatestMessageID " + sLatestMessageID);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //  Log.d(TAG, "onDataChange: message stuts snapshot: " + snapshot);
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue().toString().equals("true"))
                            TV_TIMESTAMP.setText("Seen");
                        else
                            TV_TIMESTAMP.setText("Delivered");
                    } else {
                        TV_TIMESTAMP.setText("Delivered");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            //mMessageStatus.setVisibility(View.VISIBLE);
            mSeenList = new ArrayList<>();
            StringBuilder mStringBuilder = new StringBuilder();
            mUsernameList.remove(sMyUID);
            for (int i = 0; i < mUsernameList.size(); i++) {

                final ArrayList<String> usernameList = new ArrayList<>(mUsernameList.keySet());
                //Log.d(TAG, "setMessageStatus: usernameList " + usernameList.get(i));
                Query query = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(usernameList.get(i))
                        .child(sUserID);

                final int finalI = i;
                query.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (requireNonNull(snapshot.child("isSeen").getValue()).toString().equals("true")) {
                            mSeenList.add(mUsernameList.get(usernameList.get(finalI)));
                        }
                        // Log.d(TAG, "onDataChange: mSeenList size " + mSeenList.size());
                        // Log.d(TAG, "onDataChange: mSeenList " + mSeenList);
                        String LikesString = "";
                        if (mSeenList.size() == 0) {
                            LikesString = "Delivered";
                        } else {
                            int length = mSeenList.size();

                            //Log.d(TAG, "onDataChange: length " + length);
                            if (length == 1) {
                                LikesString = "Seen by "
                                        + "<B>"
                                        + mSeenList.get(0)
                                        + "</B>";
                            } else if (length == 2) {
                                LikesString = "Seen by "
                                        + "<B>"
                                        + mSeenList.get(0)
                                        + "</B>"
                                        + " and "
                                        + "<B>"
                                        + mSeenList.get(1)
                                        + "</B>";
                            } else if (length == 3) {
                                LikesString = "Seen by "
                                        + "<B>"
                                        + mSeenList.get(0)
                                        + "</B>"
                                        + ", "
                                        + "<B>"
                                        + mSeenList.get(1)
                                        + "</B>"
                                        + " and "
                                        + "<B>"
                                        + mSeenList.get(2)
                                        + "</B>";
                            } else if (length > 3) {
                                //Log.d(TAG, "onDataChange: singleSnapshot.getChildrenCount() " + snapshot.getChildrenCount());
                                LikesString = "Seen by "
                                        + "<B>"
                                        + mSeenList.get(0)
                                        + "</B>"
                                        + ", "
                                        + "<B>"
                                        + mSeenList.get(1)
                                        + "</B>"
                                        + " and "
                                        + "<B>"
                                        + (length - 2)
                                        + " others"
                                        + "</B>";
                            }
                            // Log.d(TAG, "onDataChange:LikesString " + LikesString);
                        }
                        // Log.d(TAG, "onDataChange:LikesString " + LikesString);
                        //mMessageStatus.setText(Html.fromHtml(LikesString, Html.FROM_HTML_MODE_LEGACY));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            // Log.d(TAG, String.format("setMessageStatus: mSeenList %s", mSeenList));
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    private void AddDayStamp(String PrevDate, AutofitTextView ATV_DAY_STAMP) {


        //   calendar.setTimeInMillis(chat.getDate_messaged());

        // chat.setString_date(formatter.format(calendar.getTime()));

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
                    Calendar PreviousCalender = Calendar.getInstance();

                    Date CurrentDate = formatter.parse(CurrentTimeStamp);
                    Date PreviousDate = formatter.parse(PrevDate);

                    if (CurrentDate != null) {
                        CurrentCalendar.setTime(CurrentDate);
                    }
                    if (PreviousDate != null) {
                        PreviousCalender.setTime(PreviousDate);
                    }

                    if (CurrentCalendar.get(Calendar.YEAR) == PreviousCalender.get(Calendar.YEAR) &&
                            CurrentCalendar.get(Calendar.WEEK_OF_YEAR) == PreviousCalender.get(Calendar.WEEK_OF_YEAR)) {

                        if (CurrentCalendar.get(Calendar.DAY_OF_YEAR) == PreviousCalender.get(Calendar.DAY_OF_YEAR)) {
                            ATV_DAY_STAMP.setText("Today");
                        } else if ((CurrentCalendar.get(Calendar.DAY_OF_YEAR) - 1) == PreviousCalender.get(Calendar.DAY_OF_YEAR)) {
                            ATV_DAY_STAMP.setText("Yesterday");
                        } else {
                            ATV_DAY_STAMP.setText((new SimpleDateFormat("EEEE", Locale.getDefault()).format(PreviousDate))
                                    + ", "
                                    + PreviousCalender.get(Calendar.DAY_OF_MONTH)
                                    + " "
                                    + (new SimpleDateFormat("MMMM", Locale.getDefault()).format(PreviousDate)).substring(0, 3));
                        }
                    } else {
                        ATV_DAY_STAMP.setText(PreviousCalender.get(Calendar.YEAR) + " " + (new SimpleDateFormat("MMMM", Locale.getDefault()).format(PreviousDate)).substring(0, 3) + " " + PreviousCalender.get(Calendar.DATE));

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

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        sMyUID = mAuth.getCurrentUser().getUid();
        //Log.d(TAG, "getItemViewType: sender " + mChatList.get(position).getSender());
        if (sMyUID.equals(mChatList.get(position).getSender())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }
}

