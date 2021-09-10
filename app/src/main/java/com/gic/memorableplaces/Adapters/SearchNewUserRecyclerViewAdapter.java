package com.gic.memorableplaces.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.gic.memorableplaces.DataModels.UserCommonDetails;
import com.gic.memorableplaces.Messages.ChatFragment;
import com.gic.memorableplaces.Messages.NewMessageActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;


public class SearchNewUserRecyclerViewAdapter extends RecyclerView.Adapter<SearchNewUserRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "SearchNewUserRecyclerViewAdapter";

    //Variables
    private ArrayList<UserCommonDetails> aluSearchedList;
    private ArrayList<String> alsGroupMembers;
    private Context mContext;
    private Activity mActivity;
    private OnNewChatUserClicked MyOnNewChatUserClicked;
    private String SelectedUserID, sMyPublicKey;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public AutofitTextView ATV_USERNAME_LEFT, ATV_USERNAME_RIGHT;
        public TextView TV_FULL_NAME_RIGHT, TV_FULL_NAME_LEFT;
        public CircleImageView CIV_PP_LEFT, CIV_PP_RIGHT;
        public ImageView IV_SELECT_USER_LEFT, IV_SELECT_USER_RIGHT;
        public ConstraintLayout CL_LEFT, CL_RIGHT;
        public CardView CV_LEFT, CV_RIGHT;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ATV_USERNAME_LEFT = itemView.findViewById(R.id.ATV_USERNAME_NUC_LEFT);
            ATV_USERNAME_RIGHT = itemView.findViewById(R.id.ATV_USERNAME_NUC_RIGHT);
            TV_FULL_NAME_RIGHT = itemView.findViewById(R.id.TV_FULL_NAME_NUC_RIGHT);
            TV_FULL_NAME_LEFT = itemView.findViewById(R.id.TV_FULL_NAME_NUC_LEFT);
            CIV_PP_LEFT = itemView.findViewById(R.id.CIV__PP_NUC_LEFT);
            CIV_PP_RIGHT = itemView.findViewById(R.id.CIV__PP_NUC_RIGHT);
            IV_SELECT_USER_LEFT = itemView.findViewById(R.id.IV_SELECT_USER_NUC_LEFT);
            IV_SELECT_USER_RIGHT = itemView.findViewById(R.id.IV_SELECT_USER_NUC_RIGHT);
            CV_LEFT = itemView.findViewById(R.id.CV_LEFT_NUC);
            CV_RIGHT = itemView.findViewById(R.id.CV_RIGHT_NUC);
            CL_LEFT = itemView.findViewById(R.id.CL_NUC_LEFT);
            CL_RIGHT = itemView.findViewById(R.id.CL_NUC_RIGHT);
            //itemView.setOnClickListener(this);

        }

//        @Override
//        public void onClick(View v) {
//            mOnNewChatUserClicked.onItemClick(getAdapterPosition(), , SelectedUserID);
//        }
    }

    public interface OnNewChatUserClicked {
        void onSelected(int position, boolean isAdded, String SelectedUserID, String username, String fullname, String pp);
    }

    public SearchNewUserRecyclerViewAdapter(ArrayList<UserCommonDetails> uasList, ArrayList<String> GroupMembersList
            , String MyPublicKey, Context context, Activity activity, OnNewChatUserClicked onNewChatUserClicked) {
        aluSearchedList = uasList;
        mContext = context;
        mActivity = activity;
        sMyPublicKey = MyPublicKey;
        alsGroupMembers = GroupMembersList;
        MyOnNewChatUserClicked = onNewChatUserClicked;
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_new_chat_list, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {

        Skeleton skeletonLayout = SkeletonLayoutUtils.createSkeleton(holder.CL_LEFT);


        skeletonLayout.setShowShimmer(true);
        skeletonLayout.setShimmerDurationInMillis(1000);

        skeletonLayout.showSkeleton();
        int LeftPos = holder.getBindingAdapterPosition();
        UserCommonDetails LeftDetails = aluSearchedList.get(LeftPos);
        holder.ATV_USERNAME_LEFT.setText(LeftDetails.getUsername());
        Log.d(TAG, "onBindViewHolder: userCommonDetails: " + LeftDetails.toString());
        if (alsGroupMembers.contains(LeftDetails.getUid())) {
            holder.IV_SELECT_USER_LEFT.setImageResource(R.drawable.ic_correct);
            holder.IV_SELECT_USER_LEFT.setBackgroundResource(R.drawable.gradient_blue_green);
        } else {
            holder.IV_SELECT_USER_LEFT.setImageResource(0);
            holder.IV_SELECT_USER_LEFT.setBackgroundResource(R.drawable.rounded_corners_checkbox_unchecked);
        }

        holder.CV_LEFT.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isGroup", false);
            bundle.putString(mContext.getString(R.string.field_username), LeftDetails.getDisplay_name());
            bundle.putString(mContext.getString(R.string.field_profile_photo), LeftDetails.getProfile_photo());
            bundle.putBoolean(mContext.getString(R.string.field_is_message_pinned), false);
            bundle.putString(mContext.getString(R.string.field_chat_uid), myRef.push().getKey());
            bundle.putString(mContext.getString(R.string.field_my_public_key), sMyPublicKey);
            bundle.putString(mContext.getString(R.string.field_user_id), LeftDetails.getUid());

            GetUserPublicKey(bundle, LeftDetails.getUid());
        });

        holder.IV_SELECT_USER_LEFT.setOnClickListener(v -> {
            if (holder.IV_SELECT_USER_LEFT.getTag().equals("unselected")) {
                if (!alsGroupMembers.contains(LeftDetails.getUid())) {
                    alsGroupMembers.add(LeftDetails.getUid());
                    SelectedUserID = LeftDetails.getUid();
                    holder.IV_SELECT_USER_LEFT.setImageResource(R.drawable.ic_correct);
                    holder.IV_SELECT_USER_LEFT.setBackgroundResource(R.drawable.gradient_blue_green);
                    MyOnNewChatUserClicked.onSelected(LeftPos, true, SelectedUserID, LeftDetails.getUsername(),
                            LeftDetails.getDisplay_name(), LeftDetails.getProfile_photo());
                    holder.IV_SELECT_USER_LEFT.setTag("selected");
                }
            } else {
                if (alsGroupMembers.contains(LeftDetails.getUid())) {
                    alsGroupMembers.remove(LeftDetails.getUid());
                    SelectedUserID = LeftDetails.getUid();
                    holder.IV_SELECT_USER_LEFT.setImageResource(0);
                    holder.IV_SELECT_USER_LEFT.setBackgroundResource(R.drawable.rounded_corners_checkbox_unchecked);
                    MyOnNewChatUserClicked.onSelected(LeftPos, false, SelectedUserID, LeftDetails.getUsername(),
                            LeftDetails.getDisplay_name(), LeftDetails.getProfile_photo());
                    holder.IV_SELECT_USER_LEFT.setTag("unselected");
                }
            }
        });

        GlideImageLoader.loadImageWithOutTransition(mContext, LeftDetails.getProfile_photo(), holder.CIV_PP_LEFT);
        holder.TV_FULL_NAME_LEFT.setText(LeftDetails.getDisplay_name());


        skeletonLayout.showOriginal();

        Log.d(TAG, "onBindViewHolder: Item Count: " + getItemCount());
        Log.d(TAG, "onBindViewHolder: holder pos: " + holder.getBindingAdapterPosition());
        if ((holder.getBindingAdapterPosition() + 1) == getItemCount()) {
            if ((aluSearchedList.size() % 2) == 0) {
                InitRightView(holder);
            } else {
                holder.CV_RIGHT.setVisibility(View.GONE);
            }
        } else {
            InitRightView(holder);
        }


    }

    private void GetUserPublicKey(Bundle bundle, String UID) {
        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(UID)
                .child(mContext.getString(R.string.field_public_key));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null)
                    bundle.putString(mContext.getString(R.string.field_other_public_key), snapshot.getValue().toString());

                ChatFragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                FragmentTransaction Transaction = ((NewMessageActivity) mActivity).getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FL_messages, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.chat_fragment));
                ((NewMessageActivity) mActivity).getSupportFragmentManager().popBackStackImmediate(mContext.getString(R.string.new_chat_user_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Transaction.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitRightView(MainFeedViewHolder holder) {
        holder.CV_RIGHT.setVisibility(View.VISIBLE);
        int RightPos = (holder.getBindingAdapterPosition() + 1);
        Skeleton skeletonLayout1 = SkeletonLayoutUtils.createSkeleton(holder.CL_RIGHT);
        skeletonLayout1.setShowShimmer(true);
        skeletonLayout1.setShimmerDurationInMillis(1000);
        skeletonLayout1.showSkeleton();

        UserCommonDetails RightDetails = aluSearchedList.get(RightPos);

        holder.ATV_USERNAME_RIGHT.setText(RightDetails.getUsername());

        if (alsGroupMembers.contains(RightDetails.getUid())) {
            holder.IV_SELECT_USER_RIGHT.setImageResource(R.drawable.ic_correct);
            holder.IV_SELECT_USER_RIGHT.setBackgroundResource(R.drawable.gradient_blue_green);
        } else {
            holder.IV_SELECT_USER_RIGHT.setImageResource(0);
            holder.IV_SELECT_USER_RIGHT.setBackgroundResource(R.drawable.rounded_corners_checkbox_unchecked);
        }

        holder.CV_RIGHT.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isGroup", false);
            bundle.putString(mContext.getString(R.string.field_username), RightDetails.getDisplay_name());
            bundle.putString(mContext.getString(R.string.field_profile_photo), RightDetails.getProfile_photo());
            bundle.putBoolean(mContext.getString(R.string.field_is_message_pinned), false);
            bundle.putString(mContext.getString(R.string.field_my_public_key), sMyPublicKey);
            bundle.putString(mContext.getString(R.string.field_user_id), RightDetails.getUid());
            bundle.putString(mContext.getString(R.string.field_chat_uid), myRef.push().getKey());
            GetUserPublicKey(bundle, RightDetails.getUid());


        });


        holder.IV_SELECT_USER_RIGHT.setOnClickListener(v -> {
            Log.d(TAG, "onClick: tag right: " + holder.IV_SELECT_USER_RIGHT.getTag());
            if (holder.IV_SELECT_USER_RIGHT.getTag().equals("unselected")) {
                if (!alsGroupMembers.contains(RightDetails.getUid())) {
                    alsGroupMembers.add(RightDetails.getUid());
                    SelectedUserID = RightDetails.getUid();
                    holder.IV_SELECT_USER_RIGHT.setImageResource(R.drawable.ic_correct);
                    holder.IV_SELECT_USER_RIGHT.setBackgroundResource(R.drawable.gradient_blue_green);
                    MyOnNewChatUserClicked.onSelected(holder.getAbsoluteAdapterPosition(), true, SelectedUserID,
                            RightDetails.getUsername(), RightDetails.getDisplay_name(), RightDetails.getProfile_photo());
                    holder.IV_SELECT_USER_RIGHT.setTag("selected");
                }
            } else {
                if (alsGroupMembers.contains(RightDetails.getUid())) {
                    alsGroupMembers.remove(RightDetails.getUid());
                    SelectedUserID = RightDetails.getUid();
                    holder.IV_SELECT_USER_RIGHT.setImageResource(0);
                    holder.IV_SELECT_USER_RIGHT.setBackgroundResource(R.drawable.rounded_corners_checkbox_unchecked);
                    MyOnNewChatUserClicked.onSelected(holder.getAbsoluteAdapterPosition(), false, SelectedUserID,
                            RightDetails.getUsername(), RightDetails.getDisplay_name(), RightDetails.getProfile_photo());
                    holder.IV_SELECT_USER_RIGHT.setTag("unselected");
                }
            }
        });

        GlideImageLoader.loadImageWithOutTransition(mContext, RightDetails.getProfile_photo(), holder.CIV_PP_RIGHT);
        holder.TV_FULL_NAME_RIGHT.setText(RightDetails.getDisplay_name());
        skeletonLayout1.showOriginal();
    }

    @Override
    public int getItemCount() {
        int count = aluSearchedList.size();
        Log.d(TAG, "getItemCount: count: " + count);
        if ((count % 2) == 0) {
            Log.d(TAG, "getItemCount: count = count/2: " + (count / 2));
            count = count / 2;
        } else {
            Log.d(TAG, "getItemCount: count = count/2: " + ((count / 2) + 1));
            count = ((count / 2) + 1);
        }

        return count;
    }


}


