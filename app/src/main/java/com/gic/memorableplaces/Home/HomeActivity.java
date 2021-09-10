package com.gic.memorableplaces.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.FilterFriends.FriendsFilterActivity;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.Messages.NewMessageActivity;
import com.gic.memorableplaces.Profile.ProfileFragment;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.Share.ShareActivity;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "Home Activity";
    //    private static final int Activity_num = 0;
    private int MENU_POS_SAVE;
    // private int IMAGE_CONFIG = 0;
    private final Context mContext = HomeActivity.this;
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Bottom Navigation
    BottomNavigationView bottomNavigationView;
    public static CoordinatorLayout CL_BOTTOM_NAV;
    //TabFlashyAnimator flashyAnimator;
    private ImageView IV_BLUR;
    //private List<FluidBottomNavigationItem> NavigationList;

    private FragmentTransaction transaction;

    //Lists
    private ArrayList<Integer> ColoursList;
    private ArrayList<Integer> DrawableIconList;
    private ArrayList<String> TitleStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

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
        IV_BLUR = findViewById(R.id.IV_BLUR);

//        IV_BLUR.post(() -> Blurry.with(mContext).capture(findViewById(R.id.Frame_layout_main)).into(IV_BLUR));


        // NavigationList.add(new FluidBottomNavigationItem(
//                "Home Page",
//                ContextCompat.getDrawable(this, R.drawable.ic_social_feed)));
//
////        NavigationList.add(new FluidBottomNavigationItem(
////                "Search Users",
////                ContextCompat.getDrawable(this, R.drawable.ic_search)));
////        NavigationList.add(new FluidBottomNavigationItem(
////                "My Chats",
////                ContextCompat.getDrawable(this, R.drawable.ic_menu_chat)));
//        NavigationList.add(new FluidBottomNavigationItem(
//                "Share Posts",
//                ContextCompat.getDrawable(this, R.drawable.ic_share_posts)));
//        NavigationList.add(new FluidBottomNavigationItem(
//                "Student Dashboard",
//                ContextCompat.getDrawable(this, R.drawable.ic_student_dashbaord)));
//        NavigationList.add(new FluidBottomNavigationItem(
//                "Friends Filter",
//                ContextCompat.getDrawable(this, R.drawable.ic_find_friends)));
//        NavigationList.add(new FluidBottomNavigationItem(
//                "My Profile",
//                ContextCompat.getDrawable(this, R.drawable.ic_default_user)));
//bottomNavigation.ad
//        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_social_feed));
//        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_student_dashbaord));
//        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_share_posts));
//        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_find_friends));
//        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_default_user));
        Log.d(TAG, "onCreate: HomeAct");

//        ColoursList.add(Color.parseColor("#D81B60"));
//        ColoursList.add(Color.parseColor("#F4511E"));
//        ColoursList.add(Color.parseColor("#1E88E5"));
//        ColoursList.add(Color.parseColor("#64E535"));
//        ColoursList.add(Color.parseColor("#FDD835"));
//        ColoursList.add(Color.parseColor("#03DAC5"));
//        ColoursList.add(Color.parseColor("#8E24AA"));


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
        bottomNavigationView = findViewById(R.id.BottomNavigationMenu);
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
        //Fragment Classes & Others
        SocialFeedFragment fragmentH = new SocialFeedFragment();
        FragmentControl(fragmentH, getString(R.string.social_feed_fragment));

//        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.Frame_layout_main, fragment);
//        transaction.addToBackStack(getString(R.string.home_fragment));
//        transaction.commit();
        Log.d(TAG, String.format("onCreate: ColoursList %s", ColoursList));
        setupFirebaseAuth();
        //MenuHighlight();
        //enableNavigation(bottomNavigationView);
        //TextOutsideCircleButton.Builder builder = null;
        // BottomBoomMenu.setRippleEffect(true);
        // BottomBoomMenu.setBackgroundEffect(true);
        // BottomBoomMenu.setShadowColor(Color.WHITE);
        //  for (int i = 0; i < BottomBoomMenu.getPiecePlaceEnum().pieceNumber(); i++) {
//            builder = new TextOutsideCircleButton.Builder()
//                    .pieceColor(ColoursList.get(i))
//                    .rippleEffect(true)
//                    .shadowColor(ColoursList.get(i))
//                    .textSize(14)
//                    .shadowCornerRadius(Util.dp2px(16))
//                    .normalColor(ColoursList.get(i))
//                    .normalImageRes(DrawableIconList.get(i))
//                    .normalText(TitleStringList.get(i))
//                    .listener(new OnBMClickListener() {
//                       // @Override
//                        //public void onBoomButtonClick(int index) {
//                            Log.d(TAG, "onBoomButtonClick: index " + index);


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
