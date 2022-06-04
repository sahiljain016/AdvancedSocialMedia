package com.gic.memorableplaces.Home;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.FilterFriends.FriendsFilterActivity;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.Messages.NewMessageActivity;
import com.gic.memorableplaces.Profile.ProfileFragment;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.FilterDetailsDatabase;
import com.gic.memorableplaces.Share.ShareActivity;
import com.gic.memorableplaces.SignUp.QuestionsFragment;
import com.gic.memorableplaces.interfaces.FilterDetailsDao;
import com.gic.memorableplaces.interfaces.FilterPrivacyDao;
import com.gic.memorableplaces.interfaces.MatchFilterDetailsDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import nl.joery.animatedbottombar.AnimatedBottomBar;
import pl.droidsonroids.gif.GifImageView;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "Home Activity";
    private int MENU_POS_SAVE;
    private final Context mContext = HomeActivity.this;
    //FIREBASE
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FilterDetails fdMyDetails;
    private MatchFilterDetails mfdMatchDetails, mfdFilledDetails;
    private FilterPrivacyDetails fpdDetails;

    //Bottom Navigation
    BottomNavigationView bottomNavigationView;
    private ImageFilterButton IFB_USER, IFB_QUESTION, IFB_FRIENDS, IFB_MESSAGES;
    private View V_LOADING_COVER;
    private GifImageView GIV_LOADING;
    private TextView TV_LOADING;
    public static CoordinatorLayout CL_BOTTOM_NAV;
    //TabFlashyAnimator flashyAnimator;
    private ImageView IV_BLUR;
    private FragmentTransaction transaction;
    //Lists
    private ArrayList<Integer> ColoursList;
    private ArrayList<Integer> DrawableIconList;
    private ArrayList<String> TitleStringList;
    private String current = "", sUID;
    private Handler handler;
    boolean MyDetails = true, PrivacyDetails = true, MatchDetails = true;

    private ExecutorService databaseWriteExecutor;
    private FilterDetailsDatabase FD_DETAILS;
    private FilterDetailsDao FD_Dao;
    private MatchFilterDetailsDao MFD_Dao;
    private FilterPrivacyDao FP_Dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        sUID = mAuth.getCurrentUser().getUid();

        handler = new Handler(Looper.getMainLooper());
        ColoursList = new ArrayList<>();
        DrawableIconList = new ArrayList<>();
        TitleStringList = new ArrayList<>();
        //NavigationList = new ArrayList<>();

        // flashyAnimator.


//        mAuth.signOut();
//        Query query = myRef.child(mContext.getString(R.string.dbname_users))
//                .child(mAuth.getCurrentUser().getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                assert user != null;
//                if (!user.getIs_email_verified().equals("true")) {
//                    Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Log.d(TAG, "onDataChange: navigating to sign up ");
//                    if (!user.getPage_number().equals("completed")) {
//                        Log.d(TAG, "onDataChange: navigating to sign up");
//                        Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
//                        intent.putExtra("status", "not_done");
//                        intent.putExtra(mContext.getString(R.string.field_username), user.getUsername());
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        setContentView(R.layout.activity_home);
        //private BoomMenuButton BottomBoomMenu;
        //Menu menu;
        //MenuItem menuItem;
        AnimatedBottomBar bottomNavigation = findViewById(R.id.BottomNav);
        IFB_FRIENDS = findViewById(R.id.IFB_FRIENDS_MBN);
        IFB_MESSAGES = findViewById(R.id.IFB_MESSAGES_MBN);
        IFB_USER = findViewById(R.id.IFB_USER_MBN);
        IFB_QUESTION = findViewById(R.id.IFB_HOME_MBN);
        V_LOADING_COVER = findViewById(R.id.V_LOADING_COVER_HOME);
        GIV_LOADING = findViewById(R.id.GIV_LOADING_HOME);
        TV_LOADING = findViewById(R.id.TV_LOADING_HOME);
        IV_BLUR = findViewById(R.id.IV_BLUR);
        MotionLayout ML = findViewById(R.id.CL_MBN);
        View V_INDICATOR = findViewById(R.id.V_BOTTOM_INDICATOR_MBN);


        Log.d(TAG, "onCreate: HomeAct");


        DrawableIconList.add(R.drawable.ic_social_feed);
        DrawableIconList.add(R.drawable.ic_search);
        DrawableIconList.add(R.drawable.ic_menu_chat);
        DrawableIconList.add(R.drawable.ic_share_posts);
        DrawableIconList.add(R.drawable.ic_student_dashbaord);
        DrawableIconList.add(R.drawable.ic_find_friends);
        DrawableIconList.add(R.drawable.ic_default_user);

        TitleStringList.add("Home Page");
        TitleStringList.add("Search Users");
        TitleStringList.add("My Chats");
        TitleStringList.add("Share Posts");
        TitleStringList.add("Student Dashboard");
        TitleStringList.add("Friends Filter");
        TitleStringList.add("My Profile");
        //bottomNavigationView = findViewById(R.id.BottomNavigationMenu);
        //BottomBoomMenu = findViewById(R.id.BottomBoomMenu);
        //CL_BOTTOM_NAV = findViewById(R.id.CL_BOTTOM_NAV);
//        // RELATED TO UNIVERSAL IMAGE LOADER FIND METHOD BELOW
//        initImageLoader();
//        //Bottom navigation view
//        setUpBottomNav();
//        // View pager
//        SetUpViewPager();
        // FIREBASE AUTHENTICATION
        //INITIALIZING FRAGMENTS CLASSSES

        FD_DETAILS = FilterDetailsDatabase.getDatabase(mContext);
        FD_Dao = FD_DETAILS.filterDetailsDao();
        MFD_Dao = FD_DETAILS.matchFilterDetailsDao();
        FP_Dao = FD_DETAILS.filterPrivacyDao();
        databaseWriteExecutor = FD_DETAILS.databaseWriteExecutor;
        fdMyDetails = new FilterDetails();
        mfdFilledDetails = new MatchFilterDetails();
        mfdMatchDetails = new MatchFilterDetails();
        fpdDetails = new FilterPrivacyDetails();

        databaseWriteExecutor.execute(() -> {
            Log.d(TAG, "onCreateView: GetAllFilterDetails: " + FD_Dao.GetAllFilterDetails().size());
            Log.d(TAG, "onCreateView: GetAllMatchFilterDetails: " + MFD_Dao.GetAllMatchFilterDetails().size());
            Log.d(TAG, "onCreateView: GetAllPrivacyDetails: " + FP_Dao.GetAllPrivacyDetails().size());
            if (FD_Dao.GetAllFilterDetails().size() == 0) {
                MyDetails = false;
            }
            if (MFD_Dao.GetAllMatchFilterDetails().size() == 0) {
                MatchDetails = false;
            }
            if (FP_Dao.GetAllPrivacyDetails().size() == 0) {
                PrivacyDetails = false;
            }

            if (!MyDetails) {
                GetMyDetailsFromFirebase();
                SwitchLoadingViews();
            } else if (!MatchDetails) {
                GetMatchDetailsFromFirebase();
                SwitchLoadingViews();
            } else if (!PrivacyDetails) {
                GetPrivacyDetailsFromFirebase();
                SwitchLoadingViews();
            }


        });

        //Fragment Classes & Others
        SocialFeedFragment fragmentH = new SocialFeedFragment();
        FragmentControl(fragmentH, getString(R.string.social_feed_fragment));

//        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.Frame_layout_main, fragment);
//        transaction.addToBackStack(getString(R.string.home_fragment));
//        transaction.commit();
        Log.d(TAG, String.format("onCreate: ColoursList %s", ColoursList));
        setupFirebaseAuth();

        current = "home";

        IFB_QUESTION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: current: " + current);
                if (current.equals("friends")) {
                    ML.setTransition(R.id.TRANS_HOME_TO_FRIENDS);
                    ML.transitionToStart();
                    current = "home";
                    V_INDICATOR.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
                }
            }
        });
        IFB_USER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuestionsFragment fragment = new QuestionsFragment();
                FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.Frame_layout_main, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.get_started_fragment));
                Transaction.commit();
//                Fragment fragment;
//                String fragmentName;
//                fragment = new ProfileFragment();
//                fragmentName = getString(R.string.profile_fragment);
//                transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.Frame_layout_main, Objects.requireNonNull(fragment));
//                transaction.addToBackStack(fragmentName);
//                transaction.commit();
            }
        });
        IFB_MESSAGES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, NewMessageActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        IFB_FRIENDS.setOnClickListener(v -> {
            Log.d(TAG, "onClick: current: " + current);
            if (current.equals("home")) {
                ML.setTransition(R.id.TRANS_HOME_TO_FRIENDS);
                ML.transitionToEnd();
                current = "friends";
                V_INDICATOR.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
            }
            Intent ffIntent = new Intent(HomeActivity.this, FriendsFilterActivity.class);
            startActivity(ffIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


        bottomNavigation.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

                Fragment fragment = null;
                String fragmentName = null;
                Log.d(TAG, "onTabSelected: position: I =  " + i + ", i1 = " + i1);
                Log.d(TAG, "onTabSelected: tab name: tab = " + tab + ", tab1 = " + tab1);
                if (i1 == 0) {

                    //FragmentControl(fragmentH, getString(R.string.home_fragment));
                    fragment = new SocialFeedFragment();
                    fragmentName = getString(R.string.social_feed_fragment);
                    // MENU_POS_SAVE = 0;
                    //IMAGE_CONFIG = R.drawable.default_user_profile;
//                        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.Frame_layout_main, fragment);
//                        transaction.addToBackStack(getString(R.string.home_fragment));
//                        transaction.commit();
                } else if (i1 == 1) {


//                    fragment = new SearchFragment();
//                    fragmentName = getString(R.string.search_fragment);

                    Intent intent = new Intent(HomeActivity.this, NewMessageActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else if (i1 == 2) {

//                    Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Intent shareIntent = new Intent(HomeActivity.this, ShareActivity.class);
                    startActivity(shareIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (i1 == 3) {
//                    Intent shareIntent = new Intent(HomeActivity.this, ShareActivity.class);
//                    startActivity(shareIntent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Intent ffIntent = new Intent(HomeActivity.this, FriendsFilterActivity.class);
                    startActivity(ffIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else if (i1 == 4) {
                    //  IMAGE_CONFIG = R.drawable.ic_default_image_share;


//                    fragment = new CardInformationFragment();
//                    fragmentName = getString(R.string.Card_information_fragment);
                    fragment = new ProfileFragment();
                    fragmentName = getString(R.string.profile_fragment);
                }
                if (fragment != null) {
                    transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.Frame_layout_main, Objects.requireNonNull(fragment));
                    transaction.addToBackStack(fragmentName);
                    transaction.commit();
                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {
                Log.d(TAG, "onTabReselected: reselected pos: " + i);
            }
        });

        // }
        // });
        // BottomBoomMenu.addBuilder(builder);
        // }

    }

    private void SwitchLoadingViews() {

        handler.post(() -> {
            if (GIV_LOADING.getTag().equals("visible")) {
                GIV_LOADING.setVisibility(View.GONE);
                TV_LOADING.setVisibility(View.GONE);
                TV_LOADING.setVisibility(View.GONE);
                GIV_LOADING.setTag("gone");
            } else {
                GIV_LOADING.setVisibility(View.VISIBLE);
                TV_LOADING.setVisibility(View.VISIBLE);
                TV_LOADING.setVisibility(View.VISIBLE);
                GIV_LOADING.setTag("visible");
            }
        });
    }

    ;

    private void GetMyDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field));

        ValueEventListener VEL_FILTER_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    fdMyDetails.setType(mContext.getString(R.string.field_my_details));
                    mfdFilledDetails.setType(mContext.getString(R.string.field_visited_status));
                    mfdFilledDetails.SetAllDefault("none");
                    fdMyDetails.SetAllDefault(mContext.getString(R.string.not_available));

                    Log.d(TAG, String.format("onDataChange: My snapshot: %s", snapshot));
                    if (snapshot.hasChild(mContext.getString(R.string.field_age))) {
                        fdMyDetails.setAge((Long) snapshot.child(mContext.getString(R.string.field_age)).getValue());
                        if (fdMyDetails.getAge() == 0L) {
                            mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_red));
                        } else {

                            mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_green));
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_birth_date))) {
                        fdMyDetails.setBirthdate(String.valueOf(snapshot.child(mContext.getString(R.string.field_birth_date)).getValue()));
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_books))) {
                        ArrayList<String> books = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_books)).getChildren()) {
                            books.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setBooks(books);
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (books.size() == 1 && books.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_books(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_college_year))) {
                        fdMyDetails.setCollege_year(String.valueOf(snapshot.child(mContext.getString(R.string.field_college_year)).getValue()));
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (fdMyDetails.getCollege_year().equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_college_year(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_gender))) {
                        fdMyDetails.setGender(String.valueOf(snapshot.child(mContext.getString(R.string.field_gender)).getValue()));
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (fdMyDetails.getGender().equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_gender(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_general_details))) {
                        ArrayList<String> general_details = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_general_details)).getChildren()) {
                            general_details.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setGeneral_details(general_details);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (general_details.size() == 1 && general_details.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_general_details(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_pronouns))) {
                        fdMyDetails.setPronouns(String.valueOf(snapshot.child(mContext.getString(R.string.field_pronouns)).getValue()));
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_hobbies))) {
                        ArrayList<String> hobbies = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_hobbies)).getChildren()) {
                            hobbies.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setHobbies(hobbies);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (hobbies.size() == 1 && hobbies.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_hobbies(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_movie))) {
                        ArrayList<String> movie = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_movie)).getChildren()) {
                            movie.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMovies(movie);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (movie.size() == 1 && movie.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_movie(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_music))) {
                        ArrayList<String> music = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_music)).getChildren()) {
                            music.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMusic(music);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (music.size() == 1 && music.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_music(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_society_in_college))) {
                        ArrayList<String> soceties = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_society_in_college)).getChildren()) {
                            soceties.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setSociety_in_college(soceties);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (soceties.size() == 1 && soceties.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_society_in_college(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_titles_posts))) {
                        ArrayList<String> titles = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_titles_posts)).getChildren()) {
                            titles.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setTitles_posts(titles);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (titles.size() == 1 && titles.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_titles_posts(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_video_games))) {
                        ArrayList<String> video_games = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_video_games)).getChildren()) {
                            video_games.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setVideo_games(video_games);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (video_games.size() == 1 && video_games.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_video_games(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_my_loc))) {

                        fdMyDetails.setMy_loc(String.valueOf(snapshot.child(mContext.getString(R.string.field_my_loc)).getValue()));

                        String tickType = "";
                        if (fdMyDetails.getMy_loc().equals(mContext.getString(R.string.not_available))) {
                            tickType = mContext.getString(R.string.field_red);
                        } else {
                            tickType = mContext.getString(R.string.field_green);
                        }
                        mfdFilledDetails.setMatch_loc(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_my_dd))) {
                        ArrayList<String> my_dd = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_my_dd)).getChildren()) {
                            my_dd.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMy_dd(my_dd);

                        String tickType = "";
                        if (my_dd.size() == 1 && my_dd.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType = mContext.getString(R.string.field_red);
                        } else {
                            tickType = mContext.getString(R.string.field_green);
                        }
                        mfdFilledDetails.setMatch_loc(tickType);
                    }

                    Log.d(TAG, String.format("onDataChange: fdMyDetails: %s", fdMyDetails));

                    databaseWriteExecutor.execute(() -> {
                        FD_Dao.InsertNewDetail(fdMyDetails);
                        if (!MatchDetails) {
                            GetMatchDetailsFromFirebase();
                        } else {
                            SwitchLoadingViews();
                        }
                    });
                } else {
                    SwitchLoadingViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_FILTER_DETAILS);
    }

    private void GetMatchDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_match_details));

        ValueEventListener VEL_MATCH_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mfdMatchDetails.setType(mContext.getString(R.string.field_match_details));
                    mfdMatchDetails.SetAllDefault(mContext.getString(R.string.not_available));

                    if (snapshot.hasChild(mContext.getString(R.string.field_match_age_range))) {
                        mfdMatchDetails.setMatch_age_range(String.valueOf(snapshot.child(mContext.getString(R.string.field_match_age_range)).getValue()));
                        if (mfdFilledDetails.getMatch_age_range().equals(mContext.getString(R.string.field_green))) {
                            if (mfdFilledDetails.getMatch_age_range().equals(mContext.getString(R.string.not_available))) {
                                mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_red));
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_general_details))) {
                        ArrayList<String> general_details = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_general_details)).getChildren()) {
                            general_details.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_general_details(general_details);

                        if (mfdFilledDetails.getMatch_general_details().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (general_details.size() == 1 && general_details.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_general_details(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_books))) {
                        ArrayList<String> books = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_books)).getChildren()) {
                            books.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_books(books);

                        if (mfdFilledDetails.getMatch_books().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (books.size() == 1 && books.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_books(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_college_year))) {
                        ArrayList<String> college_year = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_college_year)).getChildren()) {
                            college_year.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_college_year(college_year);

                        if (mfdFilledDetails.getMatch_college_year().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (college_year.size() == 1 && college_year.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_college_year(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_hobbies))) {
                        ArrayList<String> hobbies = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_hobbies)).getChildren()) {
                            hobbies.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_hobbies(hobbies);

                        if (mfdFilledDetails.getMatch_hobbies().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (hobbies.size() == 1 && hobbies.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_hobbies(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_gender))) {
                        ArrayList<String> gender = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_gender)).getChildren()) {
                            gender.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_gender(gender);

                        if (mfdFilledDetails.getMatch_gender().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (gender.size() == 1 && gender.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_gender(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_movie))) {
                        ArrayList<String> movie = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_movie)).getChildren()) {
                            movie.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_movie(movie);

                        if (mfdFilledDetails.getMatch_movie().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (movie.size() == 1 && movie.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_movie(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_music))) {
                        ArrayList<String> music = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_music)).getChildren()) {
                            music.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_music(music);

                        if (mfdFilledDetails.getMatch_music().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (music.size() == 1 && music.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_music(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_society_in_college))) {
                        ArrayList<String> society = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_society_in_college)).getChildren()) {
                            society.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_society_in_college(society);

                        if (mfdFilledDetails.getMatch_society_in_college().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (society.size() == 1 && society.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_society_in_college(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_video_games))) {
                        ArrayList<String> video_games = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_video_games)).getChildren()) {
                            video_games.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_video_games(video_games);

                        if (mfdFilledDetails.getMatch_video_games().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (video_games.size() == 1 && video_games.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_video_games(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_dd))) {
                        ArrayList<String> match_dd = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_dd)).getChildren()) {
                            match_dd.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_dd(match_dd);

                        if (mfdFilledDetails.getMatch_loc().equals(mContext.getString(R.string.field_green))) {
                            String tickType = "";
                            if (match_dd.size() == 1 && match_dd.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType = mContext.getString(R.string.field_red);
                                mfdFilledDetails.setMatch_loc(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_loc))) {
                        mfdMatchDetails.setMatch_loc(String.valueOf(snapshot.child(mContext.getString(R.string.field_match_loc)).getValue()));
                        if (mfdFilledDetails.getMatch_loc().equals(mContext.getString(R.string.field_green))) {
                            String tickType = "";
                            if (mfdMatchDetails.getMatch_loc().equals(mContext.getString(R.string.not_available))) {
                                tickType = mContext.getString(R.string.field_red);
                                mfdFilledDetails.setMatch_loc(tickType);
                            }
                        }
                    }

                    databaseWriteExecutor.execute(() -> {
                        MFD_Dao.InsertNewDetail(mfdMatchDetails);
                        MFD_Dao.InsertNewDetail(mfdFilledDetails);
                        if (!PrivacyDetails) {
                            GetPrivacyDetailsFromFirebase();
                        } else {
                            SwitchLoadingViews();
                        }
                    });
                } else {
                    SwitchLoadingViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_MATCH_DETAILS);
    }

    private void GetPrivacyDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_is_private));

        ValueEventListener VEL_PRIVACY_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    fpdDetails = snapshot.getValue(FilterPrivacyDetails.class);
                    databaseWriteExecutor.execute(() -> {
                        FP_Dao.InsertPrivacyDetails(fpdDetails);

                        SwitchLoadingViews();

                    });
                } else {
                    SwitchLoadingViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_PRIVACY_DETAILS);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
    public void setUpBottomNav() {
        // Log.d(TAG, "Bottom nav view clicked");

//        HomeFrag fragment = new HomeFrag();
//        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.Frame_layout_main, fragment);
//        transaction.addToBackStack(getString(R.string.profile_fragment));
//        transaction.commit();
        //BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigationMenu);

        //NavigationViewHelper.enableNavigation(HomeActivity.this, bottomNavigationView);
       /* Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(Activity_num);
        menuItem.setChecked(true);*/
    }

    //    public void MenuHighlight(){
//        Log.d(TAG, "MenuHighlight: started");
//
//        if(fragmentH.isVisible()){
//            bottomNavigationView.getMenu();
//            menu = bottomNavigationView.getMenu();
//            menuItem = menu.getItem(0);
//            menuItem.setChecked(true);
//        }
//        else if(fragmentL.isVisible()){
//            menu = bottomNavigationView.getMenu();
//            menuItem = menu.getItem(1);
//            menuItem.setChecked(true);
//        }
//        else if(fragmentS.isVisible()){
//            menu = bottomNavigationView.getMenu();
//            menuItem = menu.getItem(3);
//            menuItem.setChecked(true);
//        }
//        else if(fragmentT.isVisible()){
//            menu = bottomNavigationView.getMenu();
//            menuItem = menu.getItem(4);
//            menuItem.setChecked(true);
//        }
//    }
//    public void enableNavigation(BottomNavigationView bottomNavigationView) {
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment = null;
//                String fragmentName = null;
//                switch (item.getItemId()) {
//                    case R.id.home:
//
//                        //FragmentControl(fragmentH, getString(R.string.home_fragment));
//                        fragment = new HomeFrag();
//                        fragmentName = getString(R.string.home_fragment);
//                        MENU_POS_SAVE = 0;
//                        //IMAGE_CONFIG = R.drawable.default_user_profile;
////                        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
////                        transaction.replace(R.id.Frame_layout_main, fragment);
////                        transaction.addToBackStack(getString(R.string.home_fragment));
////                        transaction.commit();
//                        break;
//                    case R.id.search:
//
//                        //FragmentControl(fragmentS, getString(R.string.search_fragment));
//                        fragment = new SearchFragment();
//                        fragmentName = getString(R.string.search_fragment);
//                        MENU_POS_SAVE = 1;
//                        // IMAGE_CONFIG = R.drawable.default_user_profile;
////                        FragmentTransaction transactions = HomeActivity.this.getSupportFragmentManager().beginTransaction();
////                        transactions.replace(R.id.Frame_layout_main, fragments);
////                        transactions.addToBackStack(getString(R.string.search_fragment));
////                        transactions.commit();
//                        break;
//                    case R.id.add:
//                        Intent shareIntent = new Intent(HomeActivity.this, ShareActivity.class);
//                        startActivity(shareIntent);
//                        //  IMAGE_CONFIG = R.drawable.ic_default_image_share;
//                        break;
//                    case R.id.heart:
//
//                        //FragmentControl(fragmentL, getString(R.string.like_frgament));
//                        fragment = new CardInformationFragment();
//                        fragmentName = getString(R.string.Card_information_fragment);
//                        MENU_POS_SAVE = 3;
//                        //  IMAGE_CONFIG = R.drawable.default_user_profile;
////                        FragmentTransaction transactionL = HomeActivity.this.getSupportFragmentManager().beginTransaction();
////                        transactionL.replace(R.id.Frame_layout_main, fragmentL);
////                        transactionL.addToBackStack(getString(R.string.like_frgament));
////                        transactionL.commit();
//                        break;
//                    case R.id.timeline:
//
//                        //FragmentControl(fragmentT, getString(R.string.profile_fragment));
//                        fragment = new ProfileFragment();
//                        fragmentName = getString(R.string.profile_fragment);
//                        MENU_POS_SAVE = 4;
//                        // IMAGE_CONFIG = R.drawable.default_user_profile;
//
////                        FragmentTransaction transactionT = HomeActivity.this.getSupportFragmentManager().beginTransaction();
////                        transactionT.replace(R.id.Frame_layout_main, fragmentT);
////                        transactionT.addToBackStack(getString(R.string.like_frgament));
////                        transactionT.commit();
//                        break;
//                }
//                if (fragment != null) {
//                    transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.Frame_layout_main, Objects.requireNonNull(fragment));
//                    transaction.addToBackStack(fragmentName);
//                    transaction.commit();
//                }
////                if(IMAGE_CONFIG != 0){
////                    Log.d(TAG, "onNavigationItemSelected: IMAGECONFIG " + IMAGE_CONFIG);
////
////                }
//                return true;
//            }
//        });
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void FragmentControl(Fragment fragment, String FragmentName) {
        transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Frame_layout_main, fragment);
        transaction.addToBackStack(FragmentName);
        transaction.commit();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume: done");
//        if (MENU_POS_SAVE == 0) {
//            Log.d(TAG, "onResume: fragmentH");
//            bottomNavigationView.getMenu().getItem(0).setChecked(true);
//        } else if (MENU_POS_SAVE == 1) {
//            Log.d(TAG, "onResume: fragmentS");
//            bottomNavigationView.getMenu().getItem(1).setChecked(true);
//        } else if (MENU_POS_SAVE == 3) {
//            Log.d(TAG, "onResume: fragmentL ");
//            bottomNavigationView.getMenu().getItem(3).setChecked(true);
//        } else if (MENU_POS_SAVE == 4) {
//            Log.d(TAG, "onResume:  fragmentT");
//            bottomNavigationView.getMenu().getItem(4).setChecked(true);
//        }
//    }

    //
//    //RESPONSIBLE FOR ADDING THE THREE FRAGMENTS TO THE LIST BY ADAPTER
//    private void SetUpViewPager() {
//        Log.d(TAG, "ONCREATE STARTED");
//        SectionViewPagerAdapter adapter = new SectionViewPagerAdapter(getSupportFragmentManager());
//        adapter.AddFragment(new NewsWallFragment()); //index 0
//        adapter.AddFragment(new HomeFragment()); //index 1
//        adapter.AddFragment(new ClassroomBSCITFragment()); //index 2
//        ViewPager viewPager =  findViewById(R.id.container);
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout =  findViewById(R.id.tabs);
//
//        tabLayout.setupWithViewPager(viewPager);
//        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("News Wall");
//        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Social Feed");
//        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("BSC IT ONLY");
//
//    }
//
//    private void initImageLoader() {
//
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
//        ImageLoader.getInstance().init(universalImageLoader.getConfig());
//    }
//
//
     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(mContext, LogInActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
