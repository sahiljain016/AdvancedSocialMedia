package com.gic.memorableplaces.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.gic.memorableplaces.DataModels.AllUserSettings;
import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.SignUp.GamesFragment;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.FollowButtonAnimation;
import com.gic.memorableplaces.Adapters.GridViewAdapter;
import com.gic.memorableplaces.utils.UniversalImageLoader;
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

public class ViewProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    public View view;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private Video video;
    private Photo photo;
    private User user;
    String UserID;
    private boolean IsMyProfile;
    private Intent intent;
    private UserAccountSettings settings;

    //Grid View related
    private ArrayList<String> mediaUrls;
    private ArrayList<Serializable> dataList;
    private HashMap<String, String> MediaHashMap;
    private HashMap<String, Serializable> MediaDataHashMap;
    private int gridWidth;

    //Navigation View Drawer
    private NavigationView navigationView;
    private View parentView;
    private Menu menu;
    private MenuItem menuItem;

    //Widgets
    private TextView mPosts, mFollowers, mFollowing, mDescription, mWebsite, mUsername, mDisplayName;
    private Button EditMyProfile, Follow, UnFollow, ping;
    private ImageButton BackArrow;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Skeleton skeleton1;
    private RelativeLayout relativeLayout;
    private GridViewAdapter gridViewAdapter;

    //private static final int defaultImage = R.drawable.default_user_profile;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_view_profile);
        Log.d(TAG, "onCreateView: started");
        mContext = ViewProfileActivity.this;
        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));

        //User Information Widgets
        mPosts = findViewById(R.id.Posts);
        mFollowers = findViewById(R.id.followers);
        mFollowing = findViewById(R.id.following);
        mDescription = findViewById(R.id.profile_description);
        mWebsite = findViewById(R.id.profile_webiste_link);
        mUsername = findViewById(R.id.username_profile);
        mDisplayName = findViewById(R.id.profile_name);
        mProfilePhoto = findViewById(R.id.profile_photo);
        EditMyProfile = findViewById(R.id.EditProfileButton);
        BackArrow = findViewById(R.id.Back_arrow);
        Follow = findViewById(R.id.FollowButton);
        UnFollow = findViewById(R.id.UnFollowButton);
        ping = findViewById(R.id.PingButton);


        //General Widgets
        gridView = findViewById(R.id.grid_view);
        AutofitHelper.create(mUsername);
//        navigationView = findViewById(R.id.NavigationViewLeft);
//        parentView = navigationView.getHeaderView(0);
//        nvDisplayName = parentView.findViewById(R.id.header_full_name);
//        nvEmail = parentView.findViewById(R.id.header_email);
        relativeLayout = findViewById(R.id.skeleton_rl);
        //menu = navigationView.getMenu();
        //Arrays Lists HashMaps etc
        MediaHashMap = new HashMap<>();
        MediaDataHashMap = new HashMap<>();
        dataList = new ArrayList<>();
        gridWidth = getResources().getDisplayMetrics().widthPixels;
        //Firebase
        mFirebaseMethods = new FirebaseMethods(mContext);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        skeleton1 = SkeletonLayoutUtils.createSkeleton(relativeLayout);
        skeleton1.setShowShimmer(true);
        skeleton1.setShimmerDurationInMillis(1000);
        skeleton1.showSkeleton();

        BackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        intent = getIntent();
        UserID = intent.getStringExtra(getString(R.string.field_username));
        IsMyProfile = intent.getBooleanExtra("IsMyProfile", false);
        Log.d(TAG, "onCreate: IsMyProfile " + IsMyProfile);
        SetUpEditProfileOrFollowButton();
        Log.d(TAG, "onCreate: UserId " + UserID);

        if (!IsMyProfile)
            SetUpFollowOrUnFollow();

        setUpFirebaseRetriever();
        //navigationView.setCheckedItem(0);
    }

    private void SetUpEditProfileOrFollowButton() {

        if (IsMyProfile) {
            Log.d(TAG, "SetUpEditProfileOrFollowButton: ");
            EditMyProfile.setVisibility(View.VISIBLE);
            Follow.setVisibility(View.GONE);
            UnFollow.setVisibility(View.GONE);
            ping.setVisibility(View.GONE);
            EditMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "KHULAAAAA EDIT PRFILE");
//                    EditProfileFragment fragment = new EditProfileFragment();
//                    FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
//                    Transaction.replace(R.id.frame_layout_profile1, fragment);
//                    Transaction.addToBackStack(getString(R.string.edit_profile_fragment));
//                    Transaction.commit();

                    GamesFragment fragment = new GamesFragment();
                    FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
                    Transaction.replace(R.id.frame_layout_profile1, fragment);
                    Transaction.addToBackStack(mContext.getString(R.string.games_fragment));
                    Transaction.commit();

                }
            });
        } else {
            EditMyProfile.setVisibility(View.GONE);
            Follow.setVisibility(View.VISIBLE);
            UnFollow.setVisibility(View.INVISIBLE);
            ping.setVisibility(View.VISIBLE);
            final FollowButtonAnimation followButtonAnimation = new FollowButtonAnimation();

            Follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followButtonAnimation.animateFollow(Follow, UnFollow);
                    mFirebaseMethods.addNewFollowerAndFollowing(UserID);
                    Follow.setVisibility(View.INVISIBLE);
                    UnFollow.setVisibility(View.VISIBLE);
                }
            });

            UnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followButtonAnimation.animateFollow(UnFollow, Follow);
                    mFirebaseMethods.removeFollowerAndFollowing(UserID);
                    Follow.setVisibility(View.VISIBLE);
                    UnFollow.setVisibility(View.INVISIBLE);
                }
            });

        }
    }

    private void SetUpFollowOrUnFollow() {
        Query query = myRef.child(getString(R.string.dbname_following))
                .child(mAuth.getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(UserID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Follow.setVisibility(View.INVISIBLE);
                    UnFollow.setVisibility(View.VISIBLE);
                } else {
                    Follow.setVisibility(View.VISIBLE);
                    UnFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getCard_bio());
        mFirebaseMethods.SetPostCount(UserID, mPosts);
        mFirebaseMethods.setFollowersCount(UserID, mFollowers);
        mFirebaseMethods.setFollowingCount(UserID, mFollowing);
        // mFollowing.setText(String.valueOf(settings.getFollowing()));
        // mFollowers.setText(String.valueOf(settings.getFriendly_xavierites()));
        //mPosts.setText(String.valueOf(settings.getPosts()));
        skeleton1.showOriginal();

    }

    private void setUpGridViewFiles(final ArrayList<String> mediaUrls, final ArrayList<Serializable> dataList) {

        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        //Log.d(TAG, String.format("setUpGridViewFiles: mediaUrls %s", mediaUrls));

        gridViewAdapter = new GridViewAdapter(mContext, R.layout.layout_gridview_images, "", mediaUrls);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra(Objects.requireNonNull(mContext).getString(R.string.Media_Data), dataList.get(position));
                intent.putExtra(mContext.getString(R.string.field_username), settings.getUsername());
                intent.putExtra(mContext.getString(R.string.field_profile_photo), settings.getProfile_photo());
                intent.putExtra(getString(R.string.IsViewProfile), true);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    /*
   ------------------------------------ Firebase ---------------------------------------------
    */
    private void GetGridDataFromFirebase(String node, final boolean isVideo) {

        Query query = myRef.child(node).child(UserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    if (isVideo) {
                        String videoUrl = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getVideoUrl();
                        String date_added = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getDate_added();
                        video = singleSnapshot.getValue(Video.class);
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

    private void setUpFirebaseRetriever() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot, UserID));
                GetGridDataFromFirebase("user_photos", false);
                GetGridDataFromFirebase("user_videos", true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
