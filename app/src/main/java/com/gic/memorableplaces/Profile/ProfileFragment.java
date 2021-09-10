package com.gic.memorableplaces.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.gic.memorableplaces.Adapters.GridViewAdapter;
import com.gic.memorableplaces.DataModels.AllUserSettings;
import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.Home.HomeActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitHelper;

public class ProfileFragment extends Fragment {


    private static final String TAG = "ProfileFragment";
    private static final int Activity_num = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    public View view;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private Video video;
    private Photo photo;
    private User user;
    private UserAccountSettings settings;

    //Grid View related
    private ArrayList<String> mediaUrls;
    private ArrayList<Serializable> dataList;
    private HashMap<String, String> MediaHashMap;
    private HashMap<String, Serializable> MediaDataHashMap;
    private int gridWidth;

    //Navigation View Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private View parentView;
    private Menu menu;
    private MenuItem menuItem;
    private FragmentTransaction Transaction;

    //Widgets
    private TextView mPosts, mFollowers, mFollowing, mDescription, mWebsite, mUsername, mDisplayName, nvDisplayName, nvEmail;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Skeleton skeleton1;
    private RelativeLayout relativeLayout;
    private GridViewAdapter gridViewAdapter;
    private Button editProfileButton, Follow, UnFollow, Ping;
    //private static final int defaultImage = R.drawable.default_user_profile;
    private Context mContext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "onCreateView: started");
        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(getActivity(), R.drawable.default_user_profile));

        //User Information Widgets
        mPosts = view.findViewById(R.id.Posts);
        mFollowers = view.findViewById(R.id.followers);
        mFollowing = view.findViewById(R.id.following);
        mDescription = view.findViewById(R.id.profile_description);
        mWebsite = view.findViewById(R.id.profile_webiste_link);
        mUsername = view.findViewById(R.id.username_profile);
        mDisplayName = view.findViewById(R.id.profile_name);
        mProfilePhoto = view.findViewById(R.id.profile_photo);

        //General Widgets
        gridView = view.findViewById(R.id.grid_view);
        bottomNavigationView = view.findViewById(R.id.BottomNavigationMenu);
        toolbar = view.findViewById(R.id.profileToolbar);
        AutofitHelper.create(mUsername);
        navigationView = view.findViewById(R.id.NavigationViewLeft);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        parentView = navigationView.getHeaderView(0);
        nvDisplayName = parentView.findViewById(R.id.header_full_name);
        nvEmail = parentView.findViewById(R.id.header_email);
        editProfileButton = view.findViewById(R.id.EditProfileButton);
        Follow = view.findViewById(R.id.FollowButton);
        UnFollow = view.findViewById(R.id.UnFollowButton);
        Ping = view.findViewById(R.id.PingButton);
        Follow.setVisibility(View.GONE);
        UnFollow.setVisibility(View.GONE);
        Ping.setVisibility(View.GONE);
        editProfileButton.setVisibility(View.VISIBLE);
        // skeleton =  view.findViewById(R.id.Skeleton_layout);
        relativeLayout = view.findViewById(R.id.skeleton_rl);
        menu = navigationView.getMenu();

        //Arrays Lists HashMaps etc
        MediaHashMap = new HashMap<>();
        MediaDataHashMap = new HashMap<>();
        dataList = new ArrayList<>();
        gridWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
        //Firebase
        mFirebaseMethods = new FirebaseMethods(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();
        mContext = getActivity();

        skeleton1 = SkeletonLayoutUtils.createSkeleton(relativeLayout);
        skeleton1.setShowShimmer(true);
        skeleton1.setShimmerDurationInMillis(1000);
        skeleton1.showSkeleton();
        //skeleton.showSkeleton();
        //Methods
        setUpProfileToolbar();
        //setUpBottomNav();
        //setupFirebaseAuth();
        setUpFirebaseRetriever();
        EditProfileButton();

        navigationView.setCheckedItem(0);

        return view;
    }

    /**
     * responsible for settings all the values from to the widgets
     *
     * @param allUserSettings
     */
    private void setProfileWidgets(AllUserSettings allUserSettings) {

        settings = allUserSettings.getUserAccountSettings();

        user = allUserSettings.getUser();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        nvDisplayName.setText(settings.getDisplay_name());
        nvEmail.setText(user.getEmail());
        menu = navigationView.getMenu();
        menuItem = menu.findItem(R.id.profile_item);
        menuItem.setTitle(settings.getUsername());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mFirebaseMethods.SetPostCount(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), mPosts);
        mFirebaseMethods.setFollowersCount(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), mFollowers);
        mFirebaseMethods.setFollowingCount(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), mFollowing);
        //mFollowing.setText(String.valueOf(settings.getFollowing()));
        // mFollowers.setText(String.valueOf(settings.getFriendly_xavierites()));
        //mPosts.setText(String.valueOf(settings.getPosts()));
        skeleton1.showOriginal();
        //mProgressBar.setVisibility(View.GONE);

    }

    /**
     * RESPONSIBLE FOR PROFILE TOP TOOLBAR
     */
    private void setUpProfileToolbar() {

        ((HomeActivity) requireActivity()).setSupportActionBar(toolbar);

        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_profile_menu);


        menuItem = menu.findItem(R.id.profile_item);
        menuItem.setChecked(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);

                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                navigationView.setEnabled(false);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String fragmentName = null;
                switch (item.getItemId()) {
                    case R.id.edit_profile_item:
                        //Log.d(TAG, "onNavigationItemSelected: edit profile");
                        fragment = new EditProfileFragment();
                        fragmentName = getString(R.string.edit_profile_fragment);
                        menuItem = menu.findItem(R.id.edit_profile_item);
                        //  navigationView.getMenu().getItem(0).setChecked(true);

                        break;
                    case R.id.profile_item:
                        toggle.setDrawerIndicatorEnabled(true);
                        menuItem = menu.findItem(R.id.profile_item);
                        //   navigationView.getMenu().getItem(0).setChecked(true); //2nd Menu in navigation drawer first item
                        break;
                    case R.id.report_a_bug_item:
                        fragment = new ReportBugFragment();
                        fragmentName = getString(R.string.report_bug_fragment);
                        menuItem = menu.findItem(R.id.report_a_bug_item);
                        //  navigationView.getMenu().getItem(1).setChecked(true);
                        // setDrawerLayoutEnable(false);
                        break;
                    case R.id.log_out_item:
                        fragment = new LogOutFragment();
                        fragmentName = getString(R.string.log_out_fragment);
                        menuItem = menu.findItem(R.id.log_out_item);
                        // navigationView.getMenu().getItem(1).setChecked(true);//2nd Menu in navigation drawer second item
                        // toggle.setDrawerIndicatorEnabled(false);
                        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,navigationView);
                        break;
                }
                MenuItem menuItemP = menu.findItem(R.id.profile_item);
                menuItemP.setChecked(false);
                menuItem.setChecked(true);
                if (fragment != null) {
                    Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Transaction.replace(R.id.frame_layout_profile1, Objects.requireNonNull(fragment));
                    Transaction.addToBackStack(fragmentName);
                    Transaction.commit();
                }
                drawerLayout.closeDrawer(Gravity.RIGHT);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MenuItem menuItem1 = menu.findItem(R.id.profile_item);
                        MenuItem menuItem2 = menu.findItem(R.id.log_out_item);
                        MenuItem menuItem3 = menu.findItem(R.id.edit_profile_item);
                        MenuItem menuItem4 = menu.findItem(R.id.report_a_bug_item);
                        menuItem1.setChecked(true);
                        menuItem2.setChecked(false);
                        menuItem3.setChecked(false);
                        menuItem4.setChecked(false);
                    }
                }, 2500);
                return true;

            }
        });
    }

//    /**
//     * RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
//     */
//    public void setUpBottomNav() {
//        Log.d(TAG, "Bottom nav view Profile clicked");
//        NavigationViewHelper.enableNavigation(getActivity(), bottomNavigationView);
//        Menu menu = bottomNavigationView.getMenu();
//        MenuItem menuItem = menu.getItem(Activity_num);
//        menuItem.setChecked(true);
//    }


    /**
     * RESPONSIBLE FOR SHOWING EDIT PROFILE FRAGMENT FROM PROFILE Page
     */
    private void EditProfileButton() {

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "KHULAAAAA EDIT PRFILE");
                EditProfileFragment fragment = new EditProfileFragment();
                Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.frame_layout_profile1, fragment);
                Transaction.addToBackStack(getString(R.string.edit_profile_fragment));
                Transaction.commit();


            }
        });
    }

    private void setUpGridViewFiles(final ArrayList<String> mediaUrls, final ArrayList<Serializable> dataList) {

        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        //Log.d(TAG, String.format("setUpGridViewFiles: mediaUrls %s", mediaUrls));


        gridViewAdapter = new GridViewAdapter(view.getContext(), R.layout.layout_gridview_images, "", mediaUrls);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ViewPostActivity.class);
            intent.putExtra(requireActivity().getString(R.string.Media_Data), dataList.get(position));
            intent.putExtra(getActivity().getString(R.string.field_username), settings.getUsername());
            intent.putExtra(getActivity().getString(R.string.field_profile_photo), settings.getProfile_photo());
            intent.putExtra(getString(R.string.IsViewProfile), false);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

/*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void GetGridDataFromFirebase(String node, final boolean isVideo) {

        Query query = myRef.child(node).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                    if (isVideo) {
                        String videoUrl = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getVideoUrl();
                        String date_added = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getDate_added();
                        video = singleSnapshot.getValue(Video.class);
                        Log.d(TAG, "onDataChange: date_added " + date_added);
                        if (!MediaHashMap.containsValue(videoUrl) && date_added != null && !date_added.isEmpty()) {
                            MediaHashMap.put(date_added, videoUrl);
                        }
                        if (!MediaDataHashMap.containsValue(video) && date_added != null && !date_added.isEmpty()) {
                            MediaDataHashMap.put(date_added, video);
                        }

                    } else {
                        if (!singleSnapshot.getKey().equals(mContext.getString(R.string.field_profile_photo))) {
                            String photoUrl = Objects.requireNonNull(singleSnapshot.getValue(Photo.class)).getImageUrl();
                            String date_added = Objects.requireNonNull(singleSnapshot.getValue(Photo.class)).getDate_added();
                            Log.d(TAG, "onDataChange: date_added " + date_added);
                            photo = singleSnapshot.getValue(Photo.class);
                            if (!MediaHashMap.containsValue(photoUrl) && date_added != null && !date_added.isEmpty()) {
                                MediaHashMap.put(date_added, photoUrl);
                            }
                            if (!MediaDataHashMap.containsValue(photo) && date_added != null && !date_added.isEmpty()) {
                                MediaDataHashMap.put(date_added, photo);
                            }

                        }

                    }
                }
                // Log.d(TAG, String.format("onDataChange: video %s", dataList));
                Map<String, String> sortedMedia = new TreeMap<>(MediaHashMap);
                Map<String, Serializable> sortedData = new TreeMap<>(MediaDataHashMap);
                mediaUrls = new ArrayList<>(sortedMedia.values());
                dataList = new ArrayList<>(sortedData.values());
                Collections.reverse(mediaUrls);
                Collections.reverse(dataList);
                setUpGridViewFiles(mediaUrls, dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: failed");
            }
        });

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gridViewAdapter.notifyDataSetChanged();
//            }
//        },5000);
//    }

    private void setUpFirebaseRetriever() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mAuth.getCurrentUser() != null) {
                    setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot, mAuth.getCurrentUser().getUid()));
                    GetGridDataFromFirebase("user_photos", false);
                    GetGridDataFromFirebase("user_videos", true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
