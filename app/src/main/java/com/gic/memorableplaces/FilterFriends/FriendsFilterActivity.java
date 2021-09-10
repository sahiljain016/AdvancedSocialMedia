package com.gic.memorableplaces.FilterFriends;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.FindFriendsRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.CardStack.ItemTouchHelper;
import com.gic.memorableplaces.CustomLibs.CardStack.OnItemSwiped;
import com.gic.memorableplaces.CustomLibs.CardStack.SwipeableLayoutManager;
import com.gic.memorableplaces.CustomLibs.CardStack.SwipeableTouchHelperCallback;
import com.gic.memorableplaces.CustomLibs.HTextView.RainbowTextView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

import static com.gic.memorableplaces.FilterFriends.FilterFragment.InflateFilterAdapter;
import static com.gic.memorableplaces.FilterFriends.FilterFragment.alsFilterName;

public class FriendsFilterActivity extends AppCompatActivity implements FindFriendsRecyclerViewAdapter.OnMoreFilterClickListener {
    private static final String TAG = "FriendsFilterActivity";
    public static RecyclerView rResults;
    @SuppressLint("StaticFieldLeak")
    public static FindFriendsRecyclerViewAdapter mResultAdapter;
    public static ZoomRecyclerLayout linearLayoutManager;
    @SuppressLint("StaticFieldLeak")
    public static ConstraintLayout CL_FILTERS;
    public static ImageView NoDetailImageView, IV_BG,IV_NOTIFICATIONS;
    public static ImageView No_Result_cross, IV_FAKE;
    public static AutofitTextView NoDetailTV, ATV_TITLE;
    public static RainbowTextView HEADING_FIND_FRIENDS;
    private CardView CV_BACK, CV_FILTERS, CV_NOTIF;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static Activity mActivity;
    public static FindFriendsRecyclerViewAdapter.OnMoreFilterClickListener onMoreFilterClickListener;
    private AlertDialog MoreFilterDialog;
    private AlertDialog.Builder builder;
    public static SpotlightConfig spotlightConfig;

    private static DatabaseReference myRef;
    private static FirebaseAuth mAuth;
    public static String FirstField = "", CurrentFilterType = "none";
    public static boolean isLimitUsed = false, isUltraFilterOn = false;
    public static int NumberOfFilters = 0;

    public static ArrayList<String> alsUserUIDList;
    private static ArrayList<String> alsImagesList;
    private static ArrayList<String> alsUsernameList;
    private static ArrayList<String> alsFullNamesList;
    private static ArrayList<String> alsAgeList;
    private static ArrayList<String> alsDescriptionList;
    private static ArrayList<String> alsFollowersList;
    private static ArrayList<String> alsFollowingList;
    private static ArrayList<String> alsCardBGList;
    public static ArrayList<String> alsFieldsFiltered;
    public static ArrayList<String> alsCurrentFields;

    private static LinkedHashMap<String, ArrayList<String>> hmImagesHashMap;
    public static HashMap<String, Boolean> hmSelectedFiltersHashMap;
    public static HashMap<String, ArrayList<String>> hmSelectedValues;
    public static HashMap<String, ArrayList<String>> hmMyValues;
    public static HashMap<String, HashMap<String, ArrayList<String>>> hmFinal;
    public static LinkedHashMap<String, LinkedHashMap<String, String>> hmDetailsFinal;

    public static boolean isAgeFiltered;
    public static boolean isCourseFiltered;
    public static boolean isZodiacFiltered;
    public static boolean isHobbiesFiltered;
    public static boolean isGenderFiltered;
    public static boolean isCollegeYearFiltered;
    public static boolean isSocietyFiltered;
    public static boolean isGamesFiltered;
    public static boolean isMovieFiltered;
    public static boolean isMusicFiltered;
    public static boolean isBooksFiltered;
    public static boolean isPronounsFiltered;

    public static ArrayList<String> alsFixedUIDList;
    public static ArrayList<String> UsersUIDList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Log.d(TAG, "onCreateView: news");
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mContext = FriendsFilterActivity.this;
        mActivity = FriendsFilterActivity.this;
        alsUserUIDList = new ArrayList<>();
        alsImagesList = new ArrayList<>();
        alsUsernameList = new ArrayList<>();
        alsFollowersList = new ArrayList<>();
        alsFollowingList = new ArrayList<>();
        alsCurrentFields = new ArrayList<>();
        hmImagesHashMap = new LinkedHashMap<>();
        onMoreFilterClickListener = this;

        builder = new AlertDialog.Builder(this);
        hmSelectedFiltersHashMap = new HashMap<>();
        hmFinal = new HashMap<>();
        hmDetailsFinal = new LinkedHashMap<>();
        hmMyValues = new HashMap<>();
        hmSelectedValues = new HashMap<>();
        alsCardBGList = new ArrayList<>();
        alsAgeList = new ArrayList<>();
        alsFullNamesList = new ArrayList<>();
        alsDescriptionList = new ArrayList<>();
        alsFieldsFiltered = new ArrayList<>();
        alsFixedUIDList = new ArrayList<>();


        builder = new AlertDialog.Builder(mContext);
        MoreFilterDialog = builder.create();

//        ImageButton backButton = findViewById(R.id.BackButton);
        rResults = findViewById(R.id.RV_FF_RESULT);
        No_Result_cross = findViewById(R.id.No_Result_cross);
        CV_BACK = findViewById(R.id.CV_BACK);
        CL_FILTERS = findViewById(R.id.CL_FILTERS);
        CV_FILTERS = findViewById(R.id.CV_FILTERS);
        IV_NOTIFICATIONS = findViewById(R.id.IV_NOTIFICATIONS);
        CV_NOTIF = findViewById(R.id.CV_NOTIF);
        ATV_TITLE = findViewById(R.id.ATV_TITLE);



//        FIND_FILTERS = findViewById(R.id.FIND_FILTERS);
        NoDetailTV = findViewById(R.id.no_user_found_text);
        NoDetailImageView = findViewById(R.id.missing_iv);
//        Tutorial = findViewById(R.id.TutorialButton);
//        MyCard = findViewById(R.id.My_Card);
//        AIB_LIKES_MESSAGES = findViewById(R.id.AIB_LIKES_MESSAGES);
        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        ATV_TITLE.setTypeface(title, Typeface.NORMAL);

        Log.d(TAG, "onCreate: ENTERING MAIN PAGE FILTERS");

        linearLayoutManager = new ZoomRecyclerLayout(FriendsFilterActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rResults.setLayoutManager(linearLayoutManager);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("Card", false)) {
            SetSpotlightConfig("#eb273f", "#ffffff", "#eb273f");
            //InitiateSequence();
        } else {
            GetRandomUserUIDs();
        }
        CV_BACK.setFocusableInTouchMode(false);
        CV_BACK.setOnClickListener(v -> {

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        });


//        MyCard.setOnClickListener(v -> {
//            Fragment fragment = new TestSample();
//            Bundle bundle = new Bundle();
//            bundle.putString("fragment_name", mContext.getString(R.string.friends_filter_fragment));
//            String FragmentName = mContext.getString(R.string.Card_information_fragment);
//            fragment.setArguments(bundle);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
//            transaction.addToBackStack(FragmentName);
//            transaction.commit();
//
//
//        });

        CV_NOTIF.setOnClickListener(v -> {
            Fragment fragment = new LikesAndMessagesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("fragment_name", mContext.getString(R.string.friends_filter_fragment));
            String FragmentName = mContext.getString(R.string.likes_messages_filter_fragment);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
            transaction.addToBackStack(FragmentName);
            transaction.commit();
        });


//
//        Tutorial.setOnClickListener(v -> {
//            PreferencesManager mPreferencesManager = new PreferencesManager(FriendsFilterActivity.this);
//            mPreferencesManager.resetAll();
//            new Handler(Looper.getMainLooper()).postDelayed(this::InitiateSequence, 400);
//        });

        CV_FILTERS.setOnClickListener(v -> {
            NoDetailTV.setVisibility(View.GONE);
            NoDetailImageView.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: IV NOTIF BUTTON");
            Fragment fragment = new FilterFragment();
            String FragmentName = mContext.getString(R.string.filter_1_fragment);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
            transaction.addToBackStack(FragmentName);
            transaction.commit();
        });


        GetMyDetails();

    }

    public static void SetSpotlightConfig(String HeadingTvColor, String SubHeadingTvColor, String LineAndArcColor) {

        spotlightConfig = new SpotlightConfig();

        spotlightConfig.setIntroAnimationDuration(400);
        spotlightConfig.setRevealAnimationEnabled(true);
        spotlightConfig.setPerformClick(true);
        spotlightConfig.setFadingTextDuration(400);
        spotlightConfig.setHeadingTvColor(Color.parseColor(HeadingTvColor));
        spotlightConfig.setHeadingTvSize(32);
        spotlightConfig.setSubHeadingTvColor(Color.parseColor(SubHeadingTvColor));
        spotlightConfig.setSubHeadingTvSize(16);
        spotlightConfig.setMaskColor(Color.parseColor("#dc000000"));
        spotlightConfig.setLineAnimationDuration(400);
        spotlightConfig.setLineAndArcColor(Color.parseColor(LineAndArcColor));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);


    }

//    private void InitiateSequence() {
//
//        SpotlightListener SecondSL = spotlightViewId -> GetRandomUserUIDs();
//        SpotlightListener firstSL = spotlightViewId -> CreateSpotlightView(SecondSL, FIND_FILTERS, "Find Filters Button", "This button will take you to all the filters\nthat you can apply as per your desire.", "FilterFriends");
//
//        CreateSpotlightView(firstSL, MyCard, "My Card Button", "This button allows you to change\nyour filter details.", "Card");
//
//    }

    private void CreateSpotlightView(SpotlightListener spotlightListener, View view, String Title, String SubTitle, String Usage) {
        new SpotlightView.Builder(this).setConfiguration(spotlightConfig)
                .headingTvText(Title)
                .subHeadingTvText(SubTitle)
                .target(view)
                .usageId(Usage)
                .setListener(spotlightListener)
                .show();
    }

    private void GetMyDetails() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> ValueList = null;
                //    Log.d(TAG, String.format("onDataChange: hmAllDetails: %s", hmAllDetails));

                for (DataSnapshot DetailBuilder : snapshot.child(mContext.getString(R.string.field)).getChildren()) {
                    String field = DetailBuilder.getKey();
                    assert field != null;
                    if (field.equals(mContext.getString(R.string.field_age)) || field.equals(mContext.getString(R.string.field_gender))
                            || field.equals(mContext.getString(R.string.field_birth_date)) || field.equals(mContext.getString(R.string.field_class_representative))
                            || field.equals(mContext.getString(R.string.field_college_year)) || field.equals(mContext.getString(R.string.field_pronouns)) ||
                            field.equals(mContext.getString(R.string.zodiac_sign))) {

                        ValueList = new ArrayList<>();
                        ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                        hmMyValues.put(field, ValueList);

                    } else if (field.equals(mContext.getString(R.string.field_games)) || field.equals(mContext.getString(R.string.field_books))
                            || field.equals(mContext.getString(R.string.field_movie)) || field.equals(mContext.getString(R.string.field_music))
                            || field.equals(mContext.getString(R.string.field_other_posts)) || field.equals(mContext.getString(R.string.field_society))) {
                        if (Objects.requireNonNull(DetailBuilder.getValue()).toString().equals("N/A") || DetailBuilder.getValue().toString().equals("false")) {

                            ValueList = new ArrayList<>();
                            ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                            hmMyValues.put(field, ValueList);

                        } else {

                            ValueList = new ArrayList<>();

                            for (DataSnapshot Children : DetailBuilder.getChildren()) {
                                for (DataSnapshot Details : Children.getChildren()) {
                                    if (Objects.requireNonNull(Details.getKey()).contains("name")) {
                                        Objects.requireNonNull(ValueList).add(Objects.requireNonNull(Details.getValue()).toString());
                                    }
                                }
                            }
                            hmMyValues.put(field, ValueList);
                        }

                    } else if (field.equals(mContext.getString(R.string.field_hobbies)) || field.equals(mContext.getString(R.string.Top5Platform))) {
                        if (DetailBuilder.getValue().toString().equals("N/A") || DetailBuilder.getValue().toString().equals("false")) {
                            ValueList = new ArrayList<>();
                            ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                            hmMyValues.put(field, ValueList);

                        } else {
                            ValueList = new ArrayList<>();

                            for (DataSnapshot Details : DetailBuilder.getChildren()) {

                                ValueList.add(Objects.requireNonNull(Details.getValue()).toString());
                            }
                            hmMyValues.put(field, ValueList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private static void CLearAllList() {
        hmImagesHashMap.clear();
        hmDetailsFinal.clear();
        alsImagesList.clear();
        alsUsernameList.clear();
        alsFullNamesList.clear();
        alsAgeList.clear();
        alsDescriptionList.clear();
        alsCardBGList.clear();
    }
//    private v

    /*public static void GetRandomUserUIDs() {
        Query query = myRef.child(mContext.getString(R.string.dbname_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alsUserUIDList.clear();
                for (DataSnapshot UIDSnapshot : snapshot.getChildren()) {
                    if (alsUserUIDList.size() > 21) {
                        break;
                    }
                    if(!UIDSnapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
                    alsUserUIDList.add(UIDSnapshot.getKey());
                }

                Collections.shuffle(alsUserUIDList);

                int MaxResults = alsUserUIDList.size();
                Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings));
                final int finalMaxResults = MaxResults;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot SettingsSnapshot) {
                        if (SettingsSnapshot.exists()) {

                            for (int i = 0; i < finalMaxResults; i++) {
                                for (DataSnapshot DetailsSnapshot : SettingsSnapshot.getChildren()) {
                                    if (DetailsSnapshot.getKey().equals(alsUserUIDList.get(i))) {
                                        for (DataSnapshot UserDetails : DetailsSnapshot.getChildren()) {
                                            // Log.d(TAG, "onDataChange: DetailsSnapshot key " + UserDetails.getKey());
//
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_display_name))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsFullNamesList.add(UserDetails.getValue().toString());
                                                else
                                                    alsFullNamesList.add("N/A");
                                            }
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_username))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsUsernameList.add(UserDetails.getValue().toString());
                                                else
                                                    alsUsernameList.add("N/A");
                                            }
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_followers))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsFollowersList.add(UserDetails.getValue().toString());
                                                else
                                                    alsFollowersList.add("N/A");
                                            }
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_following))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsFollowingList.add(UserDetails.getValue().toString());
                                                else
                                                    alsFollowingList.add("N/A");
                                            }
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_card_bg_color))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString())) {
                                                    alsCardBGList.add(UserDetails.getValue().toString());
                                                } else
                                                    alsCardBGList.add("N/A");

                                            }
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_age))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsAgeList.add(UserDetails.getValue().toString());
                                                else
                                                    alsAgeList.add("N/A");
                                            }
                                            Log.d(TAG, "onDataChange: AgeList " + alsAgeList);
                                            if (UserDetails.getKey().equals(mContext.getString(R.string.field_card_bio))) {
                                                if (!TextUtils.isEmpty(UserDetails.getValue().toString()))
                                                    alsDescriptionList.add(UserDetails.getValue().toString());
                                                else
                                                    alsDescriptionList.add("N/A");
                                                Log.d(TAG, "run: DescriptionSnapshot " + UserDetails.getValue().toString());
                                            }
                                        }
                                    }
                                }
                            }
                            // Log.d(TAG, String.format("onDataChange:  mImagesHashMap get UID %d%s", 0, mImagesHashMap.get(UserUIDList.get(0))));
                            Query query = myRef.child(mContext.getString(R.string.dbname_user_photos));

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (int i = 0; i < finalMaxResults; i++) {
                                            String ProfilePictureLink = "";
                                            alsImagesList.clear();
//                                            Log.d(TAG, String.format("onDataChange:  mImagesHashMap get UID %d%s", i, mImagesHashMap.get(UserUIDList.get(i))));
//                                            ArrayList<String> TempImagesList =  mImagesHashMap.get(UserUIDList.get(i));
//                                            Log.d(TAG, String.format("onDataChange:  TempImagesList %s", TempImagesList));
                                            for (DataSnapshot DetailsSnapshot : snapshot.getChildren()) {
                                               // Log.d(TAG, String.format("onDataChange: UserUIDList %s", alsUserUIDList));
                                                if (DetailsSnapshot.getKey().equals(alsUserUIDList.get(i))) {
                                                    for (DataSnapshot ImageChildren : DetailsSnapshot.getChildren()) {
                                                       // Log.d(TAG, "onDataChange: ImageChildren " + ImageChildren.getValue().toString());
                                                       // Log.d(TAG, "onDataChange: ImageChildren key" + ImageChildren.getKey());
                                                        if (ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                                            ProfilePictureLink = ImageChildren.getValue().toString();
                                                        } else {
                                                            ProfilePictureLink = "N/A";
                                                        }
                                                        if (!ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                                            for (DataSnapshot ImageDetails : ImageChildren.getChildren()) {
                                                                if (ImageDetails.getKey().equals("imageUrl")) {
                                                                    if (!TextUtils.isEmpty(Objects.requireNonNull(ImageDetails.getValue()).toString())) {
                                                                        alsImagesList.add(ImageDetails.getValue().toString());
                                                                    } else {
                                                                        alsImagesList.add("N/A");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Collections.shuffle(alsImagesList);
                                            int MAX_RESULTS = 0;
                                            if (alsImagesList.size() > 6) {
                                                MAX_RESULTS = 6;
                                            } else {
                                                MAX_RESULTS = alsImagesList.size();
                                            }
                                            ArrayList<String> ImagesList = new ArrayList<>();

                                            for (int j = 0; j < MAX_RESULTS; j++) {
                                                ImagesList.add(alsImagesList.get(j));
                                            }
                                            alsImagesList.clear();
                                            ImagesList.add(0, ProfilePictureLink);
                                            hmImagesHashMap.put(alsUserUIDList.get(i), ImagesList);
                                        }


//                                        Log.d(TAG, String.format("onDataChange: ImagesList %s", ImagesList));
//                                        Log.d(TAG, String.format("onDataChange: imagesList size %d", ImagesList.size()));


                                    } else {
                                        alsImagesList.add("N/A");
                                    }
                                   // Log.d(TAG, String.format("onDataChange: Random UserUIDList  %s", alsUserUIDList));
                                    mResultAdapter = new FindFriendsRecyclerViewAdapter(alsUserUIDList, hmImagesHashMap,
                                            alsAgeList, alsUsernameList, hmFinal, alsDescriptionList, alsFollowersList, alsFollowingList, alsFullNamesList,
                                            alsImagesList, alsCardBGList, mActivity, mContext, true, onMoreFilterClickListener);
                                    rResults.setAdapter(mResultAdapter);
                                    mResultAdapter.notifyDataSetChanged();
                                    rResults.setNestedScrollingEnabled(false);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Log.d(TAG, "onDataChange: Settings snapshot doesn't exist.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // GetUserDetails(UserUIDList, MaxResults);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
    public static void GetRandomUserUIDs() {


        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                if (Snapshot.exists()) {
//                    alsUserUIDList.clear();
//                    for (DataSnapshot UIDSnapshot : Snapshot.getChildren()) {
//                        if (alsUserUIDList.size() > 21) {
//                            break;
//                        }
//                        if (!UIDSnapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
//                            alsUserUIDList.add(UIDSnapshot.getKey());
//                    }
//                    Collections.shuffle(alsUserUIDList);
//                    int MaxResults = alsUserUIDList.size();
                    String MyUsername = Snapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_username)).getValue().toString();
                    String MyName = Snapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_display_name)).getValue().toString();
                    String MyProfilePictureLink = Snapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_profile_photo)).getValue().toString();

                    int count = 0;
                    for (DataSnapshot UIDSnapshot : Snapshot.getChildren()) {
                        if (!UIDSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            count++;
                            if (count > 21) {
                                break;
                            }

                            String UID = UIDSnapshot.getKey();
                            LinkedHashMap<String, String> hmTempDetails = new LinkedHashMap<>();
                            hmTempDetails.put(mContext.getString(R.string.field_display_name), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_username), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_followers), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_following), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_card_bg_color), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_age), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_card_bio), "N/A");
                            for (DataSnapshot DetailsSnapshot : UIDSnapshot.getChildren()) {
                                // Log.d(TAG, "onDataChange: DetailsSnapshot key " + UserDetails.getKey());
                                String Field = "", Value = "";
                                if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_display_name))) {
                                    Field = mContext.getString(R.string.field_display_name);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_username))) {
                                    Field = mContext.getString(R.string.field_username);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_followers))) {
                                    Field = mContext.getString(R.string.field_followers);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_following))) {
                                    Field = mContext.getString(R.string.field_following);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_card_bg_color))) {
                                    Field = mContext.getString(R.string.field_card_bg_color);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString())) {
                                        Value = DetailsSnapshot.getValue().toString();
                                    } else
                                        Value = "N/A";

                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_age))) {
                                    Field = mContext.getString(R.string.field_age);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_card_bio))) {
                                    Field = mContext.getString(R.string.field_card_bio);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                    Log.d(TAG, "run: DescriptionSnapshot " + DetailsSnapshot.getValue().toString());
                                }


                                hmTempDetails.replace(Field, Value);

                            }


                            hmDetailsFinal.put(UID, hmTempDetails);

                        }
                    }
                    Log.d(TAG, String.format("onDataChange: hmDetailsFinal: %s", hmDetailsFinal));
                    ArrayList<String> KeySetUIDS = new ArrayList<>(hmDetailsFinal.keySet());

                    // Log.d(TAG, String.format("onDataChange:  mImagesHashMap get UID %d%s", 0, mImagesHashMap.get(UserUIDList.get(0))));
                    Query query = myRef.child(mContext.getString(R.string.dbname_user_photos));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (int i = 0; i < KeySetUIDS.size(); i++) {
                                    String ProfilePictureLink = "";
                                    alsImagesList.clear();
//                                            Log.d(TAG, String.format("onDataChange:  mImagesHashMap get UID %d%s", i, mImagesHashMap.get(UserUIDList.get(i))));
//                                            ArrayList<String> TempImagesList =  mImagesHashMap.get(UserUIDList.get(i));
//                                            Log.d(TAG, String.format("onDataChange:  TempImagesList %s", TempImagesList));
                                    // Log.d(TAG, String.format("onDataChange: UserUIDList %s", alsUserUIDList));
                                    for (DataSnapshot ImageChildren : snapshot.child(KeySetUIDS.get(i)).getChildren()) {
                                        // Log.d(TAG, "onDataChange: ImageChildren " + ImageChildren.getValue().toString());
                                        // Log.d(TAG, "onDataChange: ImageChildren key" + ImageChildren.getKey());

                                        if (ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                            if (!TextUtils.isEmpty(ImageChildren.getValue().toString()))
                                                ProfilePictureLink = ImageChildren.getValue().toString();
                                            else
                                                ProfilePictureLink = "N/A";

                                        } else if (!ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                            for (DataSnapshot ImageDetails : ImageChildren.getChildren()) {
                                                if (ImageDetails.getKey().equals("imageUrl")) {
                                                    if (!TextUtils.isEmpty(Objects.requireNonNull(ImageDetails.getValue()).toString())) {
                                                        alsImagesList.add(ImageDetails.getValue().toString());
                                                    } else {
                                                        alsImagesList.add("N/A");
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Collections.shuffle(alsImagesList);
                                    int MAX_RESULTS = 0;
                                    if (alsImagesList.size() > 6) {
                                        MAX_RESULTS = 6;
                                    } else {
                                        MAX_RESULTS = alsImagesList.size();
                                    }
                                    ArrayList<String> ImagesList = new ArrayList<>();

                                    for (int j = 0; j < MAX_RESULTS; j++) {
                                        ImagesList.add(alsImagesList.get(j));
                                    }
                                    alsImagesList.clear();
                                    ImagesList.add(0, ProfilePictureLink);
                                    hmImagesHashMap.put(KeySetUIDS.get(i), ImagesList);
                                }


//                                        Log.d(TAG, String.format("onDataChange: ImagesList %s", ImagesList));
//                                        Log.d(TAG, String.format("onDataChange: imagesList size %d", ImagesList.size()));


                            } else {
                                alsImagesList.add("N/A");
                            }

                            // Log.d(TAG, String.format("onDataChange: Random UserUIDList  %s", alsUserUIDList));


                            SwipeableTouchHelperCallback swipeableTouchHelperCallback = new SwipeableTouchHelperCallback(new OnItemSwiped() {
                                @Override
                                public void onItemSwiped() {
                                    mResultAdapter.removeItemFromTop();
                                }

                                @Override
                                public void onItemSwipedLeft() {
                                    Log.d(TAG, "onItemSwipedLeft: LEFT SWIPED");
                                }

                                @Override
                                public void onItemSwipedRight() {
                                    Log.d(TAG, "onItemSwipedRight: RIGHT SWIPED");
                                }
                            }) {
                                @Override
                                public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
                                    return ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                                }
                            };

                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeableTouchHelperCallback);
                            itemTouchHelper.attachToRecyclerView(rResults);

                            SwipeableLayoutManager swipeableLayoutManager = new SwipeableLayoutManager() {
                                @Override
                                public boolean canScrollVertically() {
                                    return false;
                                }
                            }.setAngle(10)
                                    .setAnimationDuratuion(450)
                                    .setMaxShowCount(2)
                                    .setScaleGap(0.1f)
                                    .setTransYGap(0);
                            mResultAdapter = new FindFriendsRecyclerViewAdapter(KeySetUIDS, hmImagesHashMap, hmDetailsFinal,
                                    hmFinal, mActivity, mContext, swipeableLayoutManager, MyProfilePictureLink, MyUsername, MyName,
                                    true, onMoreFilterClickListener);

                            rResults.setLayoutManager(swipeableLayoutManager);
                            rResults.setAdapter(mResultAdapter);
                            mResultAdapter.notifyDataSetChanged();
                            rResults.setNestedScrollingEnabled(true);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Log.d(TAG, "onDataChange: Settings snapshot doesn't exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // GetUserDetails(UserUIDList, MaxResults);

    }


    public static void GetFilteredUID() {
        CLearAllList();

        //FIGURE OUT HOW TO TAKE DATA FROM hmFINAL
        UsersUIDList = new ArrayList<>(hmFinal.keySet());

        Log.d(TAG, String.format("GetFilteredUID: on executed UsersUIDList %s", UsersUIDList));
        if (!UsersUIDList.isEmpty()) {

            Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot SettingsSnapshot) {
                    if (SettingsSnapshot.exists()) {
                        InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);
                        String MyUsername = SettingsSnapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_username)).getValue().toString();
                        String MyProfilePictureLink = SettingsSnapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_profile_photo)).getValue().toString();
                        String MyName = SettingsSnapshot.child(mAuth.getCurrentUser().getUid()).child(mContext.getString(R.string.field_display_name)).getValue().toString();


                        for (int i = 0; i < UsersUIDList.size(); i++) {
                            String UID = UsersUIDList.get(i);
                            LinkedHashMap<String, String> hmTempDetails = new LinkedHashMap<>();
                            hmTempDetails.put(mContext.getString(R.string.field_display_name), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_username), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_followers), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_following), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_card_bg_color), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_age), "N/A");
                            hmTempDetails.put(mContext.getString(R.string.field_card_bio), "N/A");

                            for (DataSnapshot DetailsSnapshot : SettingsSnapshot.child(UsersUIDList.get(i)).getChildren()) {
                                String Value = "", Field = "";
                                // Log.d(TAG, "onDataChange: DetailsSnapshot key " + DetailsSnapshot.getKey());
                                if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_display_name))) {
                                    Field = mContext.getString(R.string.field_display_name);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_username))) {
                                    Field = mContext.getString(R.string.field_username);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_followers))) {
                                    Field = mContext.getString(R.string.field_followers);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_following))) {
                                    Field = mContext.getString(R.string.field_following);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_card_bg_color))) {
                                    Field = mContext.getString(R.string.field_card_bg_color);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString())) {
                                        Value = DetailsSnapshot.getValue().toString();
                                    } else
                                        Value = "N/A";

                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_age))) {
                                    Field = mContext.getString(R.string.field_age);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                } else if (DetailsSnapshot.getKey().equals(mContext.getString(R.string.field_card_bio))) {
                                    Field = mContext.getString(R.string.field_card_bio);
                                    if (!TextUtils.isEmpty(DetailsSnapshot.getValue().toString()))
                                        Value = DetailsSnapshot.getValue().toString();
                                    else
                                        Value = "N/A";
                                    //Log.d(TAG, "run: DescriptionSnapshot " + UserDetails.getValue().toString());
                                }
                                hmTempDetails.replace(Field, Value);
                            }
                            hmDetailsFinal.put(UID, hmTempDetails);
                        }
                        Query query = myRef.child(mContext.getString(R.string.dbname_user_photos));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (int i = 0; i < UsersUIDList.size(); i++) {
                                        String ProfilePictureLink = "";
                                        alsImagesList.clear();
                                        //Log.d(TAG, String.format("onDataChange: UserUIDList %s", UsersUIDList));
                                        for (DataSnapshot ImageChildren : snapshot.child(UsersUIDList.get(i)).getChildren()) {
                                            //   Log.d(TAG, "onDataChange: ImageChildren " + ImageChildren.getValue().toString());
                                            // Log.d(TAG, "onDataChange: ImageChildren key" + ImageChildren.getKey());
                                            if (ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                                if (!TextUtils.isEmpty(ImageChildren.getValue().toString()))
                                                    ProfilePictureLink = ImageChildren.getValue().toString();
                                                else
                                                    ProfilePictureLink = "N/A";

                                            } else if (!ImageChildren.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                                                for (DataSnapshot ImageDetails : ImageChildren.getChildren()) {
                                                    if (ImageDetails.getKey().equals("imageUrl")) {
                                                        if (!TextUtils.isEmpty(Objects.requireNonNull(ImageDetails.getValue()).toString())) {
                                                            alsImagesList.add(ImageDetails.getValue().toString());
                                                        } else {
                                                            alsImagesList.add("N/A");
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Collections.shuffle(alsImagesList);
                                        int MAX_RESULTS = 0;
                                        if (alsImagesList.size() > 6) {
                                            MAX_RESULTS = 6;
                                        } else {
                                            MAX_RESULTS = alsImagesList.size();
                                        }
                                        ArrayList<String> ImagesList = new ArrayList<>();

                                        for (int j = 0; j < MAX_RESULTS; j++) {
                                            ImagesList.add(alsImagesList.get(j));
                                        }
                                        alsImagesList.clear();
                                        ImagesList.add(0, ProfilePictureLink);
                                        hmImagesHashMap.put(UsersUIDList.get(i), ImagesList);
                                    }
                                } else {
                                    alsImagesList.add("N/A");
                                }
                                Log.d(TAG, String.format("onDataChange: ShuffledUIDList %s", UsersUIDList));
                                Log.d(TAG, String.format("onDataChange: UsernameList %s", alsUsernameList));

                                mResultAdapter = new FindFriendsRecyclerViewAdapter(UsersUIDList, hmImagesHashMap, hmDetailsFinal,
                                        hmFinal, mActivity, mContext, null, MyProfilePictureLink, MyUsername,MyName,
                                        true, onMoreFilterClickListener);
                                rResults.setAdapter(mResultAdapter);
                                mResultAdapter.notifyDataSetChanged();
                                //rResults.setNestedScrollingEnabled(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Log.d(TAG, "onDataChange: Settings snapshot doesn't exist.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);
            UsersUIDList.clear();
            mResultAdapter.notifyDataSetChanged();
            rResults.setVisibility(View.GONE);
            NoDetailTV.setVisibility(View.VISIBLE);
            NoDetailImageView.setVisibility(View.VISIBLE);
            No_Result_cross.setVisibility(View.VISIBLE);
            No_Result_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoDetailTV.setVisibility(View.GONE);
                    NoDetailImageView.setVisibility(View.GONE);
                    No_Result_cross.setVisibility(View.GONE);
                    rResults.setVisibility(View.VISIBLE);
                    GetRandomUserUIDs();
                }
            });

        }

    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, String.format("onItemClick: UsersUIDList & Position: %s %d", UsersUIDList, position));
        //   String UID = mResultAdapter.
        //MoreFiltersButton.setFocusable(false);
        if (!hmFinal.isEmpty()) {

            if (Objects.requireNonNull(hmFinal.get(UsersUIDList.get(position))).size() == 1) {
                if (!Objects.requireNonNull(hmFinal.get(UsersUIDList.get(position))).containsKey(mContext.getString(R.string.field_age))) {
                    ConfigureValues(position);
                }
            } else {
                ConfigureValues(position);
            }
        }


    }

    private void ConfigureValues(int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.removeAllViews();
        params.gravity = Gravity.CENTER;
        params.setMargins(0, 0, 0, 20);
        linearLayout.setPadding(50, 50, 50, 50);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView Notice = new TextView(mContext);
        Notice.setLayoutParams(params);
        Notice.setPadding(10, 10, 10, 10);
        Notice.setTextColor(Color.WHITE);
        Notice.setTextSize(20);
        Notice.setBackgroundColor(Color.BLACK);
        Notice.setText("Filters Applied");
        linearLayout.addView(Notice);

        ArrayList<String> TempFieldsList = new ArrayList<>(hmFinal.get(UsersUIDList.get(position)).keySet());
        TempFieldsList.remove(mContext.getString(R.string.field_age));
        params.setMargins(0, 0, 0, 10);

        Log.d(TAG, "ConfigureValues: UID Position: " + UsersUIDList.get(position) + ":" + position);
        Log.d(TAG, String.format("onBindViewHolder: size: %d", hmFinal.get(UsersUIDList.get(position)).size()));
        //for (int i = 0; i < hmFinal.get(mUIDList.get(position)).size(); i++) {
        for (String Field : TempFieldsList) {
            Log.d(TAG, "onBindViewHolder: ERROR VALUE: " + hmFinal.get(UsersUIDList.get(position)).get(Field));


            TextView NewField = new TextView(mContext);
            NewField.setLayoutParams(params);
            NewField.setBackgroundResource(R.drawable.rounded_white_background);
            ;
            NewField.setPadding(20, 10, 10, 10);
            NewField.setTextColor(Color.BLACK);
            NewField.setTextSize(20);

            if (Objects.requireNonNull(Objects.requireNonNull(hmFinal.get(UsersUIDList.get(position))).get(Field)).size() > 1) {
                StringBuilder mStringBuilder = new StringBuilder();
                int i = 1;

                for (String Value : Objects.requireNonNull(hmFinal.get(UsersUIDList.get(position)).get(Field))) {
                    if (i++ != hmFinal.get(UsersUIDList.get(position)).get(Field).size())
                        mStringBuilder.append(Value).append(", ");
                    else
                        mStringBuilder.append(Value);
                }
                Log.d(TAG, "GetHeading: Heading: " + GetHeading(Field));
                NewField.setText(Html.fromHtml("<B>" + GetHeading(Field) + "</B>" + mStringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                Log.d(TAG, "GetHeading: Heading: " + GetHeading(Field));
                NewField.setText(Html.fromHtml("<B>" + GetHeading(Field) + "</B>" + hmFinal.get(UsersUIDList.get(position)).get(Field).get(0), Html.FROM_HTML_MODE_LEGACY));
            }
//            NewField.setBackgroundColor(Color.WHITE);
            linearLayout.addView(NewField);
        }

        builder.setView(linearLayout);

        MoreFilterDialog = builder.create();
        MoreFilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MoreFilterDialog.show();
    }

    private String GetHeading(String Fields) {

        Log.d(TAG, "GetHeading: Fields: " + Fields);
        String Heading = null;

        if (Fields.equals(mContext.getString(R.string.field_age))) {
            Heading = "Age: ";

        }
        if (Fields.equals(mContext.getString(R.string.field_course))) {
            Heading = "Course: ";

        } else if (Fields.equals(mContext.getString(R.string.field_books))) {
            Heading = "Books: ";

        } else if (Fields.equals(mContext.getString(R.string.field_class_representative))) {
            Heading = "Is Class Representative: ";

        } else if (Fields.equals(mContext.getString(R.string.field_college_year))) {
            Heading = "College Year: ";

        } else if (Fields.equals(mContext.getString(R.string.field_games))) {
            Heading = "Games: ";

        } else if (Fields.equals(mContext.getString(R.string.field_gender))) {
            Heading = "Gender ";

        } else if (Fields.equals(mContext.getString(R.string.field_hobbies))) {
            Heading = "Hobbies: ";

        } else if (Fields.equals(mContext.getString(R.string.field_movie))) {
            Heading = "Movies: ";

        } else if (Fields.equals(mContext.getString(R.string.field_music))) {
            Heading = "Music: ";

        } else if (Fields.equals(mContext.getString(R.string.field_society))) {
            Heading = "Societies: ";

        } else if (Fields.equals(mContext.getString(R.string.field_other_posts))) {
            Heading = "Other Posts: ";

        } else if (Fields.equals(mContext.getString(R.string.field_pronouns))) {
            Heading = "Pronouns Proffered: ";

        } else if (Fields.equals(mContext.getString(R.string.Top5Platform))) {
            Heading = "Top 5 Gaming Platforms: ";

        } else if (Fields.equals(mContext.getString(R.string.field_zodiac_sign))) {
            Heading = "Zodiac Sign: ";

        }
        Log.d(TAG, "GetHeading: Heading: " + Heading);
        return Heading;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(true);
    }
}


//if (CheckIfTableExists("Filters"))
//            FiltersDatabase.execSQL("DELETE FROM filters"); //delete all rows in a table db. close();
//
//        FiltersDatabase.execSQL("Create table IF NOT EXISTS filters (" + mContext.getString(R.string.field_age) + " VARCHAR UNIQUE, " + mContext.getString(R.string.field_zodiac_sign) + " VARCHAR  UNIQUE, " + mContext.getString(R.string.field_hobbies) + " VARCHAR  UNIQUE)");

//    public static void InsertUIDIntoDatabase(ArrayList<String> FinalUIDList, ArrayList<String> FieldFilteredList, String FieldName) {
//        if (FieldFilteredList.size() > 0) {
//            DeleteOldUIDFromDB(FieldName, FieldFilteredList);
//        }
//        alsFinalListOfUsers.clear();
//        Log.d(TAG, String.format("InsertUIDIntoDatabase: Fixed UID List before: %s", alsFixedUIDList));
//
//        ArrayList<String> TempUID = new ArrayList<>();
//        boolean TempBool = false;
//
//        if (alsFixedUIDList.size() > 0) {
//            for (String FilteredUID : FinalUIDList) {
//                if (!alsFixedUIDList.contains(FilteredUID)) {
//                    TempUID.add(FilteredUID);
//                }
//            }
//            for (String WasteUID : TempUID) {
//                FinalUIDList.remove(WasteUID);
//            }
//        }
//
//        if (FinalUIDList.isEmpty()) {
//            TempBool = true;
//            GetFilteredUID();
//            NoDetailTV.setVisibility(View.VISIBLE);
//            NoDetailImageView.setVisibility(View.VISIBLE);
//        }
//
//        FinalUIDList.addAll(TempUID);
//        Log.d(TAG, "InsertUIDIntoDatabase: Final UID List after: " + FinalUIDList);
//
//
//        Log.d(TAG, "InsertUIDIntoDatabase: Fixed List : " + alsFixedUIDList);
//        //  Log.d(TAG, "InsertUIDIntoDatabase: Field Name: " + FieldName);
//        for (String UID : FinalUIDList) {
////            if (!CheckIfRecordExistsInDB(FieldName, UID)) {
//            try {
//                Log.d(TAG, "InsertUIDIntoDatabase: UID & Field Name: " + UID + " & " + FieldName);
//                FiltersDatabase.execSQL("INSERT INTO filters (" + FieldName + ") VALUES ('" + UID + "')");
//                FieldFilteredList.add(UID);
//
//            } catch (Exception e) {
//                Log.d(TAG, "InsertUIDIntoDatabase: e " + e.getMessage());
//            }
//            // }
//        }
//        if (!alsFieldsFiltered.contains(FieldName)) {
//            alsFieldsFiltered.add(FieldName);
//        }
//        Log.d(TAG, String.format("InsertUIDIntoDatabase: TempBool: %s", TempBool));
//        if (!TempBool)
//            FinalUIDListBuilder();
//
//    }
//
//    public static void FinalUIDListBuilder() {
//
//
//        for (String Field : alsFieldsFiltered) {
//            // Log.d(TAG, "FinalUIDListBuilder: Field " + Field);
//            Cursor c = FiltersDatabase.rawQuery("SELECT " + Field + " FROM filters", null);
//
//            AddUIDFromDB(Field, c, alsFinalListOfUsers, false);
//        }
//
//        Log.d(TAG, String.format("InsertUIDIntoDatabase: Final List of UID%s", alsFinalListOfUsers));
//
//
//        GetFilteredUID();
//
//    }
//
//    public static void DeleteOldUIDFromDB(String Field, ArrayList<String> FieldUIDList) {
//        boolean isLoopBroken = false;
//        if (FieldUIDList.size() > 0) {
//            Cursor c = FiltersDatabase.rawQuery("SELECT " + Field + " FROM filters", null);
//
//            AddUIDFromDB(Field, c, FieldUIDList, true);
//            Log.d(TAG, String.format("DeleteOldUIDFromDB: fields filtered: %s", alsFieldsFiltered));
//            for (String UID : FieldUIDList) {
//                FiltersDatabase.execSQL("DELETE FROM filters Where " + Field + " = \'" + UID + "\'");
//                if (alsFieldsFiltered.size() > 0) {
//                    for (String FieldName : alsFieldsFiltered) {
//                        c = FiltersDatabase.rawQuery("SELECT " + FieldName + " FROM filters", null);
//                        int fieldNumber = c.getColumnIndex(Field);
//
//                        while (c.moveToNext()) {
//                            if (UID.equals(c.getString(fieldNumber))) {
//                                isLoopBroken = true;
//                                break;
//                            }
//                        }
//                        //  Log.d(TAG, "DeleteOldUIDFromDB: isLoopBroken: " + is);
//                        if (!isLoopBroken) {
//                            alsFixedUIDList.remove(UID);
//                        }
//                    }
//                } else {
//                    alsFixedUIDList.remove(UID);
//                }
//            }
//
//
//        }
//    }
//
//    public static boolean CheckIfTableExists(String TableName) {
//
//        Cursor c = FiltersDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=\'" + TableName + "\'", null);
//
//        return c.getCount() > 0;
//    }
//
//    public static void AddUIDFromDB(String Field, Cursor c, ArrayList<String> fListUID, boolean isNUll) {
//        //Log.d(TAG, "InsertUIDIntoDatabase: Field Being Filtered: " + Field);
//        int FieldNumber = c.getColumnIndex(Field);
//        // Log.d(TAG, "AddUIDFromDB: move to first: " + c.);
//        //c.moveToFirst();
//        while (c.moveToNext()) {
//            //Log.d(TAG, "InsertUIDIntoDatabase: INDIVIDUAL UID IN LOOP " + c.getString(FieldNumber));
//            if (!fListUID.contains(c.getString(FieldNumber))) {
//                if (isNUll) {
//                    fListUID.add(c.getString(FieldNumber));
//                } else {
//                    if (!TextUtils.isEmpty(c.getString(FieldNumber))) {
//                        //  Log.d(TAG, "InsertUIDIntoDatabase: INDIVIDUAL UID IN LOOP " + c.getString(FieldNumber));
//                        fListUID.add(c.getString(FieldNumber));
//                        if (!alsFixedUIDList.contains(c.getString(FieldNumber))) {
//                            alsFixedUIDList.add(c.getString(FieldNumber));
//                        }
//
//                    }
//                }
//
//            }
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy: filters Activity destroyed.");
//        FiltersDatabase.execSQL("DELETE FROM filters");
//        FiltersDatabase.close();
//    }