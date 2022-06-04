package com.gic.memorableplaces.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.CustomLibs.CardStack.SwipeableLayoutManager;
import com.gic.memorableplaces.DataModels.FFUserDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.FilterFriends.DataDisplayFragment;
import com.gic.memorableplaces.FilterFriends.FriendsFilterActivity;
import com.gic.memorableplaces.FilterFriends.UserDetailsFragment;
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
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class FindFriendsRecyclerViewAdapter extends RecyclerView.Adapter<FindFriendsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";

    //Variables

    private final LinkedHashMap<String, User> hmDetailsFinal;
    private final HashMap<String, HashMap<String, ArrayList<String>>> hmFinal;
    private final ArrayList<String> mUIDList;
    private final OnMoreFilterClickListener MyOnMoreFilterClickListener;

    private final boolean isRandom;
    private final String sUsername, sProfileLink, sMyName;
    private final Context mContext;
    private final Activity mActivity;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private MatchFilterDetails mfd;

    private View vDialog;
    private AlertDialog MessageDialog;
    private LayoutInflater inflaterDialog;
    private AlertDialog.Builder builder;

    private SwipeableLayoutManager SLM;
    UserImageFFRecyclerViewAdapter mUserImageAdapter;
    RecyclerView.LayoutManager mFilterLayoutManager;

    ArrayList<String> alsFiltersList = new ArrayList<>();
    ArrayList<String> alsFilterType1 = new ArrayList<>();
    ArrayList<String> alsFilterType2 = new ArrayList<>();


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnMoreFilterClickListener mOnMoreFilterClickListener;
        public TextView ATV_DESP, ATV_FULLNAME, ATV_GENDER/*, tDescription*/;

        /*public ShineButton SB_FOLLOW;*/
//        public RecyclerView mRecyclerView;
//        public BlurLayout BL_DETAILS;
        /*public AutofitTextView MoreFilters, tvFollowers, tvFollowing;*/
        public ImageView IV_CARD, IV_FOLLOW, IV_LIKE, IV_HI;
        /* public ImageButton IV_PLUS;
         public ShadowLayout SL_FOLLOW;
         public LinearLayout LL;*/
//        public CircleImageView CIV_PP;
//        public AnimatedRecyclerView ARV_FILTER_NAMES, ARV_MORE_PHOTOS;
        public CardView CV_MAIN_FF;
        public ImageView IV_MAIN, IV_OPTIONS, IV_DETAIL_ARROW;
        public MotionLayout ML_FF;
        public View V_BLACK_GRADIENT;

        public MainFeedViewHolder(@NonNull View itemView, OnMoreFilterClickListener OnMoreFilterClickListener) {
            super(itemView);
            mOnMoreFilterClickListener = OnMoreFilterClickListener;
//            mRecyclerView = itemView.findViewById(R.id.RV_USER_IMAGES);
            ATV_FULLNAME = itemView.findViewById(R.id.ATV_FULLNAME);
            ATV_GENDER = itemView.findViewById(R.id.ATV_GENDER);
//            BL_DETAILS = itemView.findViewById(R.id.BL_DETAILS);
            ATV_DESP = itemView.findViewById(R.id.ATV_DESP_FFR);
            IV_OPTIONS = itemView.findViewById(R.id.IV_OPEN_OPTIONS);
            ML_FF = itemView.findViewById(R.id.ML_FF);
            IV_DETAIL_ARROW = itemView.findViewById(R.id.IV_TOP_SLIDER);
            V_BLACK_GRADIENT = itemView.findViewById(R.id.V_BLACK_GRADIENT);
            CV_MAIN_FF = itemView.findViewById(R.id.CV_MAIN_FF);
            // ARV_FILTER_NAMES = itemView.findViewById(R.id.ARV_FITLERS);
            //ARV_MORE_PHOTOS = itemView.findViewById(R.id.ARV_MORE_PHOTOS);


            IV_MAIN = itemView.findViewById(R.id.IV_MAIN);
//            CIV_PP = itemView.findViewById(R.id.CIV_PP);
//            IV_COLLEGE_YEAR_BADGE = itemView.findViewById(R.id.IV_COLLEGE_YEAR);
//            SB_FOLLOW = itemView.findViewById(R.id.SB_FOLLOW);
//            MoreFilters = itemView.findViewById(R.id.Show_More_Values);
//            tDescription = itemView.findViewById(R.id.description_ff);
//            tvFollowers = itemView.findViewById(R.id.TV_FOLLOWERS);
//            tvFollowing = itemView.findViewById(R.id.TV_FOLLOWING);
            IV_CARD = itemView.findViewById(R.id.IV_CARD_DETAILS);
            IV_FOLLOW = itemView.findViewById(R.id.IV_FOLLOW_FF);
            IV_LIKE = itemView.findViewById(R.id.IV_LIKE_FF);
            IV_HI = itemView.findViewById(R.id.IV_SAY_HI_FF);
//            IV_PLUS = itemView.findViewById(R.id.IV_PLUS);
//            SL_FOLLOW = itemView.findViewById(R.id.SL_Follow);
//            LL = itemView.findViewById(R.id.LinearLayout_Options);
            itemView.setFocusable(false);
            //MoreFilters.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: position: " + getAbsoluteAdapterPosition());
            mOnMoreFilterClickListener.onItemClick(getAbsoluteAdapterPosition());
        }


    }

    public interface OnMoreFilterClickListener {
        void onItemClick(int position);
    }

    public FindFriendsRecyclerViewAdapter(ArrayList<String> UIDList, MatchFilterDetails mfd,
                                          LinkedHashMap<String, User> DetailsFinal,
                                          HashMap<String, HashMap<String, ArrayList<String>>> Final, Activity activity,
                                          Context context, SwipeableLayoutManager swipeableLayoutManager, String ProfileLink, String Username, String name, boolean bIsRandom, OnMoreFilterClickListener onMoreFilterClickListener) {

        mUIDList = UIDList;
        hmFinal = Final;
        hmDetailsFinal = DetailsFinal;
        isRandom = bIsRandom;
        sProfileLink = ProfileLink;
        SLM = swipeableLayoutManager;
        sUsername = Username;
        this.mfd = mfd;
        sMyName = name;
        mActivity = activity;
        mContext = context;
        MyOnMoreFilterClickListener = onMoreFilterClickListener;
        builder = new AlertDialog.Builder(mContext);
        MessageDialog = builder.create();
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        alsFiltersList.add(mContext.getString(R.string.field_age));
        alsFiltersList.add(mContext.getString(R.string.field_books));
        alsFiltersList.add(mContext.getString(R.string.field_class_representative));
        alsFiltersList.add(mContext.getString(R.string.field_college_year));
        alsFiltersList.add(mContext.getString(R.string.field_games));
        alsFiltersList.add(mContext.getString(R.string.field_gender));
        alsFiltersList.add(mContext.getString(R.string.field_hobbies));
        alsFiltersList.add(mContext.getString(R.string.field_movie));
        alsFiltersList.add(mContext.getString(R.string.field_music));
        alsFiltersList.add(mContext.getString(R.string.field_society));
        alsFiltersList.add(mContext.getString(R.string.field_other_posts));
        alsFiltersList.add(mContext.getString(R.string.field_pronouns));
        alsFiltersList.add(mContext.getString(R.string.field_zodiac_sign));

        alsFilterType1.add(mContext.getString(R.string.field_age));
        alsFilterType1.add(mContext.getString(R.string.field_zodiac_sign));
        alsFilterType1.add(mContext.getString(R.string.field_gender));
        alsFilterType1.add(mContext.getString(R.string.field_pronouns));
        alsFilterType1.add(mContext.getString(R.string.field_class_representative));
        alsFilterType1.add(mContext.getString(R.string.field_college_year));

        alsFilterType2.add(mContext.getString(R.string.field_books));
        alsFilterType2.add(mContext.getString(R.string.field_games));
        alsFilterType2.add(mContext.getString(R.string.field_movie));
        alsFilterType2.add(mContext.getString(R.string.field_music));
        alsFilterType2.add(mContext.getString(R.string.field_society));
        alsFilterType2.add(mContext.getString(R.string.field_other_posts));


    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_find_friends_results_shrinked_2, parent, false);
        return new MainFeedViewHolder(v, MyOnMoreFilterClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.setIsRecyclable(false);
        int pos = holder.getBindingAdapterPosition();
        //mImagesList.clear();
        User user;
        if (hmDetailsFinal.get(mUIDList.get(pos)) != null) {
            user = hmDetailsFinal.get(mUIDList.get(pos));
        } else {
            user = new User();
        }

        GlideImageLoader.loadImageWithOutTransition(mContext, user.getProfile_photo(), holder.IV_MAIN);
        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/convergence.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        holder.ATV_DESP.setTypeface(cap, Typeface.NORMAL);
        holder.ATV_FULLNAME.setTypeface(title, Typeface.NORMAL);
        holder.ATV_GENDER.setTypeface(title, Typeface.NORMAL);
        //holder.MoreFilters.setFocusable(false);
        //InitiateOptionsSequence(holder, true);
        Log.d(TAG, String.format("onBindViewHolder: mUIDList: %s", mUIDList));
        Log.d(TAG, String.format("onBindViewHolder: user: %s", user));
//        if (!hmFinal.isEmpty()) {
//            Log.d(TAG, "onBindViewHolder: position " + position);
//            Log.d(TAG, "onBindViewHolder: UID: " + mUIDList.get(position));
//            // Log.d(TAG, "onBindViewHolder: HashMap for UID: " + hmFinal.get(mUIDList.get(position)));
//            //Log.d(TAG, "onBindViewHolder: HashMap size: " + hmFinal.get(mUIDList.get(position)).size());
//            if (hmFinal.get(mUIDList.get(position)).size() == 1) {
//                if (!hmFinal.get(mUIDList.get(position)).containsKey(mContext.getString(R.string.field_age))) {
//                    //   holder.MoreFilters.setVisibility(View.VISIBLE);
//                    //  holder.MoreFilters.setText("+" + (hmFinal.get(mUIDList.get(position)).size() - 0) + " Filters");
//                }
//            } else {
//                if (!hmFinal.get(mUIDList.get(position)).containsKey(mContext.getString(R.string.field_age))) {
//                    //  holder.MoreFilters.setVisibility(View.VISIBLE);
//                    //  holder.MoreFilters.setText("+" + (hmFinal.get(mUIDList.get(position)).size() - 0) + " Filters");
//                } else {
//                    //holder.MoreFilters.setVisibility(View.VISIBLE);
//                    // holder.MoreFilters.setText("+" + (hmFinal.get(mUIDList.get(position)).size() - 1) + " Filters");
//                }
//            }
//        }

        //CheckIfFollower(position, holder);


        /*holder.IV_PLUS.setOnClickListener(v -> {

            Log.d(TAG, "onClick: sl card");
            if (holder.IV_CARD.getVisibility() != View.VISIBLE) {

                holder.LL.setBackgroundResource(R.drawable.rounded_top_white_background);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> holder.IV_CARD.setVisibility(View.VISIBLE), 500);
                handler.postDelayed(() -> {
                    holder.IV_FOLLOW.setVisibility(View.VISIBLE);
                    holder.SB_FOLLOW.setVisibility(View.VISIBLE);
                }, 1000);
                handler.postDelayed(() -> holder.IV_LIKE.setVisibility(View.VISIBLE), 1500);
                handler.postDelayed(() -> {
                    holder.IV_HI.setVisibility(View.VISIBLE);
                    InitiateOptionsSequence(holder, false);
                }, 2000);
            } else {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    holder.LL.setBackgroundColor(Color.TRANSPARENT);
//                            holder.IV_PLUS.setImageResource(R.drawable.ic_plus_sign);
//                            holder.IV_PLUS.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    holder.IV_CARD.setVisibility(View.GONE);
                }, 2000);
                handler.postDelayed(() -> {
                    holder.IV_FOLLOW.setVisibility(View.GONE);
                    holder.SB_FOLLOW.setVisibility(View.GONE);
                }, 1500);
                handler.postDelayed(() -> holder.IV_LIKE.setVisibility(View.GONE), 1000);
                handler.postDelayed(() -> {
                            holder.IV_HI.setVisibility(View.GONE);

                        }
                        , 500);
            }
        });*/

//        holder.IV_MAIN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (holder.ML_FF_OUTER.getProgress() == 0.0)
//                    holder.ML_FF_OUTER.transitionToEnd();
//
//                Handler handler = new Handler(Looper.getMainLooper());
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        holder.ML_FF_OUTER.transitionToStart();
//                    }
//                }, 3500);
//
//            }
//        });
        holder.CV_MAIN_FF.setOnClickListener(v -> {


            Bundle bundle = new Bundle();
            FFUserDetails ffUserDetails = new FFUserDetails();

            ffUserDetails.setAlsImagesList(user.getPhotos_list());
            ffUserDetails.setTargetDisplayName(user.getDisplay_name()
                    + ", "
                    + hmDetailsFinal.get(user.getAge()));
            ffUserDetails.setDesp(user.getAuto_desp());
            ffUserDetails.setTargetUID(mUIDList.get(pos));
            ffUserDetails.setTargetUsername(user.getUsername());
            ffUserDetails.setMyUsername(sUsername);
            ffUserDetails.setMyProfilePic(sProfileLink);
            if (!hmFinal.isEmpty()) {
                ffUserDetails.setFiltersMatched(hmFinal.get(mUIDList.get(position)).keySet().toString());

            } else {
                ffUserDetails.setFiltersMatched(mContext.getString(R.string.field_random_match));
            }
            ffUserDetails.setMyName(sMyName);


            Query query = myRef.child(mContext.getString(R.string.dbname_user_card));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Random random = new Random();

                    alsFiltersList.clear();
                    int Score = 0;
                    for (DataSnapshot Detail : snapshot.child(mUIDList.get(position)).child(mContext.getString(R.string.field_is_private)).getChildren()) {
                        if (Detail.getValue().toString().equals(mContext.getString(R.string.field_public))) {
                            alsFiltersList.add(Detail.getKey());

                        }
                    }


                    if (alsFiltersList.size() > 0) {
                        ArrayList<String> MyList = new ArrayList<>(), TargetList = new ArrayList<>();
                        ArrayList<Integer> IconList = new ArrayList<>();
                        List<String> SelectedFilterList = new ArrayList<>(6);
                        int NewNumber = 0, OldNumber = 0;

                        for (int i = 0; i < Math.min(alsFiltersList.size(), 6); i++) {

                            if (i == 0)
                                OldNumber = random.nextInt(alsFiltersList.size());
                            else {
                                NewNumber = random.nextInt(alsFiltersList.size());
                                while (NewNumber == OldNumber) {
                                    NewNumber = random.nextInt(alsFiltersList.size());
                                }
                                OldNumber = NewNumber;
                            }


                            SelectedFilterList.add(alsFiltersList.get(OldNumber));
                        }

                        Log.d(TAG, String.format("onDataChange: SelectedFilterList: %s", SelectedFilterList));
                        for (String Values : alsFiltersList) {
                            DataSnapshot TargetsSnapshot = snapshot.child(mUIDList.get(position)).child(mContext.getString(R.string.field)).child(Values);
                            DataSnapshot MySnapshot = snapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field)).child(Values);
                            if (alsFilterType1.contains(Values)) {

                                if (SelectedFilterList.contains(Values)) {
                                    if (!TargetsSnapshot.getValue().toString().equals("N/A")) {
                                        TargetList.add(TargetsSnapshot.getValue().toString());
                                        IconList.add(MiscTools.GetFilterIcon(Values, mContext));
                                        MyList.add(MySnapshot.getValue().toString());
                                    }
                                }
                                Log.d(TAG, "onDataChange: TValue 1: " + TargetsSnapshot.getValue().toString());
                                Log.d(TAG, "onDataChange: MValue 1: " + MySnapshot.getValue().toString());
                                if (TargetsSnapshot.getValue().toString().equals(MySnapshot.getValue().toString())) {
                                    Score += 8;
                                }
                            } else if (alsFilterType2.contains(Values)) {
                                Log.d(TAG, String.format("onDataChange: TValue 1: %s", TargetsSnapshot.getValue().toString()));
                                Log.d(TAG, String.format("onDataChange: MValue 1: %s", MySnapshot.getValue().toString()));
                                if (!TargetsSnapshot.getValue().toString().equals("N/A") && !MySnapshot.getValue().toString().equals("N/A)")) {
                                    long ChildrenCount = Math.min(TargetsSnapshot.getChildrenCount(), MySnapshot.getChildrenCount());

                                    for (DataSnapshot FilterChildren : TargetsSnapshot.getChildren()) {

                                        String value = FilterChildren.child(MiscTools.GetKeyForFilter(Values, mContext)).getValue().toString();
                                        for (DataSnapshot MyFilterChildren : MySnapshot.getChildren()) {
                                            if (value.equals(MyFilterChildren.child(MiscTools.GetKeyForFilter(Values, mContext)).getValue().toString())) {
                                                Score += 8 / ChildrenCount;
                                            }
                                            if (SelectedFilterList.contains(Values)) {
                                                if (!value.equals("N/A")) {
                                                    TargetList.add(value);
                                                    IconList.add(MiscTools.GetFilterIcon(Values, mContext));
                                                    MyList.add(MyFilterChildren.child(MiscTools.GetKeyForFilter(Values, mContext)).getValue().toString());
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    Score += 8;
                                }
                            } else if (Values.equals(mContext.getString(R.string.field_hobbies))) {
                                if (!TargetsSnapshot.getValue().toString().equals("N/A")) {
                                    long ChildrenCount = Math.min(TargetsSnapshot.getChildrenCount(), MySnapshot.getChildrenCount());
                                    for (DataSnapshot Hobbies : TargetsSnapshot.getChildren()) {
                                        String hobby = Hobbies.getValue().toString();
                                        for (DataSnapshot MyHobbies : MySnapshot.getChildren()) {
                                            if (hobby.equals(MyHobbies.getValue().toString())) {
                                                Score += 8 / ChildrenCount;
                                            }
                                            if (SelectedFilterList.contains(Values)) {
                                                if (!hobby.equals("N/A")) {
                                                    TargetList.add(hobby);
                                                    IconList.add(MiscTools.GetFilterIcon(Values, mContext));
                                                    MyList.add(MyHobbies.getValue().toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "onDataChange: Score: " + Score);
                        //Log.d(TAG, "onDataChange: Score Percentage: " + (int)(((double) Score / 104) * 100));


                        ffUserDetails.setMatchPercentage((int) (((double) Score / 104) * 100));
                        ffUserDetails.setAliIconList(IconList);
                        ffUserDetails.setAlsMyFilterList(MyList);
                        ffUserDetails.setAlsTargetFilterList(TargetList);
                        Log.d(TAG, String.format("onDataChange: TargetList: %s", TargetList));

                    }
                    Log.d(TAG, String.format("onDataChange: alsFiltersList: %s", alsFiltersList));
                    if (alsFiltersList.size() > 6)
                        for (int i = 0; i < (alsFiltersList.size() - 6); i++)
                            alsFiltersList.remove(random.nextInt(alsFiltersList.size()));

                    bundle.putSerializable(mContext.getString(R.string.ff_user_details), ffUserDetails);
                    UserDetailsFragment fragment = new UserDetailsFragment();
                    String FragmentName = "UserDetailsFragment";
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = ((FriendsFilterActivity) mActivity).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.addToBackStack(FragmentName);
                    transaction.commit();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        });

        holder.IV_CARD.setOnClickListener(v -> {
            Fragment fragment = new DataDisplayFragment();
            String FragmentName = "Data_Display_Fragment";
            Bundle bundle = new Bundle();
            Log.d(TAG, "onClick: UID TO TRANSFER: " + mUIDList.get(position));
            bundle.putString(mContext.getString(R.string.field_user_id), mUIDList.get(position));
            bundle.putString(mContext.getString(R.string.field_auto_desp), user.getAuto_desp());
            bundle.putSerializable(mContext.getString(R.string.field_filter), mfd);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = ((FriendsFilterActivity) mActivity).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
            transaction.addToBackStack(FragmentName);
            transaction.commit();
        });

        holder.IV_HI.setOnClickListener(v -> {
            inflaterDialog = mActivity.getLayoutInflater();
            vDialog = inflaterDialog.inflate(R.layout.dialog_age_filter_search, null);
            builder.setView(vDialog);
            MessageDialog = builder.create();
            MessageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            MessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MessageDialog.show();

            AnimatedRecyclerView rResults = vDialog.findViewById(R.id.ARV_MESSAGES);
            GifImageView MessageGif = vDialog.findViewById(R.id.Message_sent_gif);
            ArrayList<String> Messages = new ArrayList<>();
            Messages.add("Hello this is sahil jain, how are you doing? yum yum yum!");
            DespAndQARecyclerViewAdapter mResultAdapter = new DespAndQARecyclerViewAdapter(Messages, null,mContext, true,
                    mUIDList.get(position), sProfileLink, user.getProfile_photo(), sUsername,
                    user.getUsername(), rResults,
                    MessageDialog, MessageGif);
            rResults.setItemAnimator(new DefaultItemAnimator());
            rResults.setAdapter(mResultAdapter);
            mResultAdapter.notifyDataSetChanged();
            rResults.scheduleLayoutAnimation();

        });

//        holder.V_SWIPE.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        SLM.setVerticalScrollingEnabled(false);
//                        SLM.setHorizontalScrollingEnabled(false);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        SLM.setVerticalScrollingEnabled(false);
//                        SLM.setHorizontalScrollingEnabled(false);
//                        if (holder.ML_FF_OUTER.getProgress() == 0.0)
//                            holder.ML_FF_OUTER.transitionToEnd();
//                        else
//                            holder.ML_FF_OUTER.transitionToStart();
//                        break;
//
//                }
//                return true;
//            }
//        });
        holder.IV_OPTIONS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: OPTIONS IV REACHED");

                holder.IV_FOLLOW.setClickable(true);
                holder.IV_LIKE.setClickable(true);
                holder.IV_CARD.setClickable(true);
                holder.IV_HI.setClickable(true);


                if (holder.ML_FF.getProgress() == 0.0)
                    holder.ML_FF.transitionToEnd();
                else {
                    holder.ML_FF.transitionToStart();
                    holder.IV_FOLLOW.setClickable(false);
                    holder.IV_LIKE.setClickable(false);
                    holder.IV_CARD.setClickable(false);
                    holder.IV_HI.setClickable(false);
                }


                Query query = myRef.child(mContext.getString(R.string.dbname_following))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mUIDList.get(position));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.IV_FOLLOW.setTag("Followed");
                            holder.IV_FOLLOW.setImageResource(R.drawable.ic_followed_bubble);

                        } else {
                            holder.IV_FOLLOW.setTag("Not Followed");
                            holder.IV_FOLLOW.setImageResource(R.drawable.ic_follow_bubble);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


/**
 * Do not change View.OnClickListener to Lambda (For Ballon Tip View)
 */
        holder.IV_FOLLOW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);
                if (holder.IV_FOLLOW.getTag().toString().equals("Not Followed")) {
                    holder.IV_FOLLOW.setTag("Followed");
                    holder.IV_FOLLOW.setImageResource(R.drawable.ic_followed_bubble);


                    mFirebaseMethods.addNewFollowerAndFollowing(mUIDList.get(position));
                    // Log.d(TAG, "onBindViewHolder: username: " + hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_username)));

                    if (!hmFinal.isEmpty()) {
                        mFirebaseMethods.addFollowOrMessageToFilterList(mUIDList.get(position),
                                user.getUsername()
                                , hmFinal.get(mUIDList.get(position)).keySet().toString(), sUsername, sProfileLink
                                , user.getProfile_photo(), "",
                                mContext.getString(R.string.field_follow_list_for_notifications), mContext.getString(R.string.field_filters_matched));
                    } else {
                        mFirebaseMethods.addFollowOrMessageToFilterList(mUIDList.get(position),
                                user.getUsername()
                                , mContext.getString(R.string.field_random_match), sUsername, sProfileLink
                                , user.getProfile_photo(), "",
                                mContext.getString(R.string.field_follow_list_for_notifications), mContext.getString(R.string.field_filters_matched));

                    }

                    message = "You Followed " + user.getDisplay_name() + "!";
                } else {
                    holder.IV_FOLLOW.setTag("Not Followed");
                    holder.IV_FOLLOW.setImageResource(R.drawable.ic_follow_bubble);
                    message = "You UnFollowed " + user.getDisplay_name() + "!";
                    mFirebaseMethods.removeFollowerAndFollowing(mUIDList.get(position));
                    mFirebaseMethods.DeleteFollowFromFilterList(mUIDList.get(position));
                }

                MiscTools.InflateBalloonTooltip(mContext, message, 0, v);


            }
        });


        if (hmDetailsFinal.size() > position) {
            //Log.d(TAG, String.format("onBindViewHolder: mDescriptionList : %s Current Description: %s Current Position: %d", mDescriptionList, mDescriptionList.get(position), position));
            /*holder.tDescription.setText(Html.fromHtml(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_card_bio))
                    , Html.FROM_HTML_MODE_LEGACY));*/

            // Log.d(TAG, String.format("onBindViewHolder: mUserNameList : %s Current user name: %s Current Position: %d", mUserNameList, mUserNameList.get(position), position));
//            holder.tUsername.setText(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_username)));


            //Log.d(TAG, String.format("onBindViewHolder: mUserNameList : %s Current user name: %s Current Position: %d", mUserNameList, mUserNameList.get(position), position));
            /*holder.tvFollowers.setText(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_followers)));
             */

            //  Log.d(TAG, String.format("onBindViewHolder: mUserNameList : %s Current user name: %s Current Position: %d", mUserNameList, mUserNameList.get(position), position));
/*
            holder.tvFollowing.setText(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_following)));
*/

            //  Log.d(TAG, String.format("onBindViewHolder: mFullNameList : %s Current full name: %s Current Position: %d", mFullNameList, mFullNameList.get(position), position));
            //  Log.d(TAG, String.format("onBindViewHolder: mAgeList : %s Current age: %s Current Position: %d", mAgeList, mAgeList.get(position), position));

            holder.ATV_FULLNAME.setText(String.format("%s, %s", user.getDisplay_name(),
                    user.getAge()));

            holder.ATV_DESP.setText(Html.fromHtml(user.getAuto_desp(), Html.FROM_HTML_MODE_LEGACY));
            holder.ATV_GENDER.setText(Html.fromHtml(user.getGender(), Html.FROM_HTML_MODE_LEGACY));
//            if (!hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_card_bg_color)).equals("N/A")) {
//                //   Log.d(TAG, String.format("onBindViewHolder: mCardBGList : %s Current card bg: %s Current Position: %d", mCardBGList, mCardBGList.get(position), position));
//
//                int[] colors = {manipulateColor(Integer.parseInt(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_card_bg_color))), 1.6f),
//                        Integer.parseInt(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_card_bg_color)))
//                        , manipulateColor(Integer.parseInt(hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_card_bg_color))), 0.6f)};
//                //Log.d(TAG, "onBindViewHolder: darker color " +  manipulateColor(Integer.parseInt(chat.getBubbleColor()),0.8f));
//                GradientDrawable gd = new GradientDrawable(
//                        GradientDrawable.Orientation.BOTTOM_TOP, colors);
//
//                gd.setCornerRadius(5);
//
//                holder.mRecyclerView.setBackground(gd);
//            }

            //holder.IV_COLLEGE_YEAR_BADGE.setImageResource(R.drawable.ic_badge);

            // GlideImageLoader.loadImageWithOutTransition(mContext, mImagesHashMap.get(mUIDList.get(position)).get(0), holder.IV_BG);
            //Blurry.with(mContext).capture(holder.IV_BACKGROUND).into(holder.IV_BACKGROUND);
//            Log.d(TAG, String.format("onBindViewHolder: mImagesHashMap : %s Current image hm: %s Current Position: %d", mImagesHashMap, mImagesHashMap.get(mUIDList.get(position)), position));
//
//            mFilterLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            holder.ARV_FILTER_NAMES.setLayoutManager(mFilterLayoutManager);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
//            holder.ARV_MORE_PHOTOS.setLayoutManager(linearLayoutManager);
//
//            List<String> FitlerList = new ArrayList<>();
//            ArrayList<String> FilterName = new ArrayList<>();
//            FitlerList.add("19");
//            FilterName.add(mContext.getString(R.string.field_age));
//            FitlerList.add("Scorpion");
//            FilterName.add(mContext.getString(R.string.field_zodiac_sign));
//            FitlerList.add("Male");
//            FilterName.add(mContext.getString(R.string.field_gender));
//            FitlerList.add("Coding");
//            FilterName.add(mContext.getString(R.string.field_hobbies));
//
//
//            Log.d(TAG, "onBindViewHolder: mImagesHashMap: " + mImagesHashMap.get(mUIDList.get(position)));
//            mUserImageAdapter = new UserImageFFRecyclerViewAdapter(mImagesHashMap.get(mUIDList.get(position)),null,holder.RIV_MAIN,holder.IV_BG,true, mContext);
//            holder.ARV_MORE_PHOTOS.setItemAnimator(new DefaultItemAnimator());
//            mUserImageAdapter.notifyDataSetChanged();
//            holder.ARV_MORE_PHOTOS.scheduleLayoutAnimation();
//            holder.ARV_MORE_PHOTOS.setAdapter(mUserImageAdapter);
////
//            UserImageFFRecyclerViewAdapter UserImageAdapter = new UserImageFFRecyclerViewAdapter(FitlerList,FilterName,holder.RIV_MAIN,holder.IV_BG,false, mContext);
//
//            holder.ARV_FILTER_NAMES.setItemAnimator(new DefaultItemAnimator());
//            UserImageAdapter.notifyDataSetChanged();
//            holder.ARV_FILTER_NAMES.scheduleLayoutAnimation();
//            holder.ARV_FILTER_NAMES.setAdapter(UserImageAdapter);
//            holder.BL_DETAILS.invalidate();
//            holder.BL_DETAILS.startBlur();
//            holder.BL_DETAILS.setFPS(0);


        }
    }

    public void removeItemFromTop() {
        hmDetailsFinal.remove(mUIDList.get(0));
        mUIDList.remove(0);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mUIDList.size();
    }

    String getItem(int id) {
        return mUIDList.get(id);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void GetFilters(String TargetUserUID, Bundle bundle) {


    }

    //    private void InitiateOptionsSequence(MainFeedViewHolder holder, boolean isSingle) {
//
//        if (isSingle) {
//            FriendsFilterActivity.SetSpotlightConfig("#FDD835", "#ffffff", "#FDD835");
//            SpotlightSequence.getInstance(mActivity, FriendsFilterActivity.spotlightConfig)
//                    .addSpotlight(holder.IV_PLUS, "Options Button", "This button displays various options\nto interact with users.", "OptionsButton")
//                    .startSequence();
//        } else {
//            FriendsFilterActivity.SetSpotlightConfig("#1E88E5", "#ffffff", "#1E88E5");
//            SpotlightSequence.getInstance(mActivity, FriendsFilterActivity.spotlightConfig)
//                    .addSpotlight(holder.IV_CARD, "Card Button", "This button allows you to view\nthe user's card details.", "UserCardButton")
//                    .addSpotlight(holder.IV_FOLLOW, "Follow Button", "This buttons acts as a\nshortcut to follow the user.", "FollowButton")
//                    .addSpotlight(holder.IV_LIKE, "Like Button", "This buttons shows that you found\nthe user to be a good match.", "LikeButton")
//                    .addSpotlight(holder.IV_HI, "Message Button", "This buttons allows you to send a\nquick message to the user.", "HelloButton")
//                    .startSequence();
//        }
//    }
//
//    private int manipulateColor(int color, float factor) {
//        int a = Color.alpha(color);
//        int r = Math.round(Color.red(color) * factor);
//        int g = Math.round(Color.green(color) * factor);
//        int b = Math.round(Color.blue(color) * factor);
//        return Color.argb(a,
//                Math.min(r, 255),
//                Math.min(g, 255),
//                Math.min(b, 255));
//    }
//
    private void CheckIfFollower(int position, final MainFeedViewHolder holder) {


//
//
//    }

    }
}

