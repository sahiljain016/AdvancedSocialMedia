package com.gic.memorableplaces.Profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.CustomLibs.shinebuttonlib.ShineButton;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.HeartAnimation;
import com.gic.memorableplaces.utils.MiscTools;
import com.gic.memorableplaces.utils.StringManipulation;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

import static java.util.Objects.requireNonNull;

public class ViewPostActivity extends AppCompatActivity {
    //CONSTANTS
    private boolean checked = false;
    private static final String TAG = "ViewPostActivity";
    private boolean misLikedByUser = false;
    //Classes & Custom objects
    private HeartAnimation heartAnimation;
    private GestureDetector gestureDetector;
    private Photo photo;
    private Video video;
    private MiscTools miscTools;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;

    //VARIABLES
    private boolean isPhoto;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener1;
    public static Context mContext;
    public static Typeface face;
    private Serializable serializable;
    private String userID, LikesString, LikeID, username, TimePassed, profilePhotoUrl, CurrentUsername;
    private StringBuilder mStringBuilder;
    private Bundle bundle;
    private Intent intent;
    //WIDGETS
    private RoundedImageView profilePhoto;
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private ImageView PhotoPostImageView;
    public static ImageView Like_outline;
    private AutofitTextView mCaption, date_added, usernameTV, likedBy, view_comments;
    private ImageButton audioVP, post_settings, CommentButton;
    private ConstraintLayout MediaContainerRL;
    private ShineButton SaveButton;
    public static ShineButton LikeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        mContext = ViewPostActivity.this;

        intent = new Intent(ViewPostActivity.this, CommentsActivity.class);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        try {
            mFirebaseDatabase.setPersistenceEnabled(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);

        //getCurrentUserUsername();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
        //General INITIALIZATIONS
        //Getting intent data
        Intent intent = getIntent();
        //Initialising all widgets

        setUpViewPostWidgets(intent);
        SetLikesListener();
        serializable = intent.getSerializableExtra(getString(R.string.Media_Data));
        //Methods
        SetUpViewPostData();
        GoBack();
        if (isPhoto)
            getNumberOfComments(getString(R.string.dbname_user_photos), photo.getUser_id(), photo.getPhoto_id());
        else
            getNumberOfComments(getString(R.string.dbname_user_videos), video.getUser_id(), video.getVideo_id());
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfile();
            }
        });
        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfile();
            }
        });
//        mCaption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewProfile();
//            }
//        });
        CommentsOnClick();
        likedBy.setVisibility(View.VISIBLE);
        PostSettings();
    }

    /*
    Initialising all widgets in the activity
       */
    private void setUpViewPostWidgets(Intent intent) {
        profilePhoto = findViewById(R.id.profile_photo);
        usernameTV = findViewById(R.id.username_VP);
        mCaption = findViewById(R.id.post_caption);
        date_added = findViewById(R.id.date_posted);
        audioVP = findViewById(R.id.audio_vp);
        MediaContainerRL = findViewById(R.id.RelLayout_Post);
        PhotoPostImageView = findViewById(R.id.view_post_imageView);
        simpleExoPlayerView = findViewById(R.id.Exo_player_view_post);
        post_settings = findViewById(R.id.post_settings);
        CommentButton = findViewById(R.id.Comment);
        LikeButton = findViewById(R.id.Like);
        Like_outline = findViewById(R.id.Like_outline);
        SaveButton = findViewById(R.id.Save);
        likedBy = findViewById(R.id.liked_number);
        view_comments = findViewById(R.id.view_comments);

        heartAnimation = new HeartAnimation();
        gestureDetector = new GestureDetector(mContext, new GestureListener());
        miscTools = new MiscTools();

        username = intent.getStringExtra(getString(R.string.field_username));
        profilePhotoUrl = intent.getStringExtra(getString(R.string.field_profile_photo));

        if (intent.getBooleanExtra(getString(R.string.IsViewProfile), true)) {
            post_settings.setVisibility(View.GONE);
        } else {
            post_settings.setVisibility(View.VISIBLE);
        }

        UniversalImageLoader.setImage(profilePhotoUrl, profilePhoto, null, "");
        //GlideImageLoader.loadImageWithTransition(ViewPostActivity.this, profilePhotoUrl, profilePhoto, null, null);
        usernameTV.setText(username);

    }

    private void ViewProfile() {
        Intent intent = new Intent(mContext, ViewProfileActivity.class);

        if (isPhoto) {
            if (photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                intent.putExtra("IsMyProfile", true);
            } else {

                intent.putExtra("IsMyProfile", false);
            }
            intent.putExtra(requireNonNull(mContext).getString(R.string.field_username), photo.getUser_id());
        } else {
            if (video.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                intent.putExtra("IsMyProfile", true);
            } else {

                intent.putExtra("IsMyProfile", false);
            }
            intent.putExtra(requireNonNull(mContext).getString(R.string.field_username), video.getUser_id());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void getCurrentUserUsername() {


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(getString(R.string.dbname_user_account_settings))) {
                        intent.putExtra("CurrentUsername", Objects.requireNonNull(ds.child(mAuth.getCurrentUser().getUid())
                                .getValue(UserAccountSettings.class))
                                .getUsername());
                        intent.putExtra("CurrentUserProfilePhoto", Objects.requireNonNull(ds.child(mAuth.getCurrentUser().getUid())
                                .getValue(UserAccountSettings.class))
                                .getProfile_photo());
                        Log.d(TAG, "onDataChange: Current USerName " + CurrentUsername);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d(TAG, "onDataChange: Current USerName  return " + CurrentUsername);
    }

    private void PostSettings() {
        post_settings.setOnClickListener(v -> {

            final AlertDialog builder = new AlertDialog.Builder(ViewPostActivity.this).create();
            final Button deleteButton = new Button(ViewPostActivity.this);
            deleteButton.setTextColor(Color.BLUE);
            deleteButton.setText("Delete Post");
            deleteButton.setBackgroundColor(Color.WHITE);
            builder.setView(deleteButton);
            builder.show();
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isPhoto)
                        mFirebaseMethods.removePhotoFromDatabase(getString(R.string.dbname_photos), getString(R.string.dbname_user_photos)
                                , photo.getUser_id(), photo.getPhoto_id(), photo.getImageUrl());
                    else
                        mFirebaseMethods.removePhotoFromDatabase(getString(R.string.dbname_videos), getString(R.string.dbname_user_videos)
                                , video.getUser_id(), video.getVideo_id(), video.getVideoUrl());

                    builder.dismiss();

                    finish();
                }
            });

        });
    }

    private void CommentsOnClick() {
        bundle = new Bundle();
        getCurrentUserUsername();
        if (isPhoto) {
            intent.putExtra(getString(R.string.field_mediaID), photo.getPhoto_id());
            intent.putExtra(getString(R.string.field_user_id), photo.getUser_id());
            intent.putExtra("UserNodeID", getString(R.string.dbname_user_photos));
            intent.putExtra("NodeID", getString(R.string.dbname_photos));
            intent.putExtra(getString(R.string.field_caption), photo.getCaption());
            intent.putExtra(getString(R.string.field_date_added), photo.getDate_added());
            intent.putExtra(getString(R.string.field_font), photo.getFont());
            intent.putExtra("isPhoto", true);
            Log.d(TAG, "CommentsOnClick: FontColor " + photo.getFont_color());
            if (photo.getFont_color() != null) {
                Log.d(TAG, "CommentsOnClick: Font_color " + photo.getFont_color());
                intent.putExtra(getString(R.string.field_color_caption), photo.getFont_color());
            }
        } else {
            intent.putExtra(getString(R.string.field_mediaID), video.getVideo_id());
            intent.putExtra(getString(R.string.field_user_id), video.getUser_id());
            intent.putExtra("UserNodeID", getString(R.string.dbname_user_videos));
            intent.putExtra("NodeID", getString(R.string.dbname_videos));
            intent.putExtra(getString(R.string.field_caption), video.getCaption());
            intent.putExtra(getString(R.string.field_date_added), video.getDate_added());
            intent.putExtra(getString(R.string.field_font), video.getFont());
            intent.putExtra("isPhoto", false);
            if (video.getFont_color() != null) {
                intent.putExtra(getString(R.string.field_color_caption), video.getFont_color());
            }
        }
        //Log.d(TAG, "CommentsOnClick: CurrentUsername " + CurrentUsername);
        //bundle.putString("CurrentUsername",CurrentUsername);

        intent.putExtra(getString(R.string.field_username), username);
        intent.putExtra(getString(R.string.field_profile_photo), profilePhotoUrl);
        CommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransactionComments();
            }
        });

        view_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransactionComments();
            }
        });
    }

    private void FragmentTransactionComments() {

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        Fragment fragment = new CommentsFragment();
//        fragment.setArguments(bundle);
//        String fragmentName = getString(R.string.fragment_comments);
//        FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
//        Transaction.replace(R.id.FrameLayoutComments, Objects.requireNonNull(fragment));
//        Transaction.addToBackStack(fragmentName);
//        Transaction.commit();
    }

    private void getNumberOfComments(String UserNode, String userID, String mediaID) {

        Query query = myRef.child(UserNode)
                .child(userID)
                .child(mediaID)
                .child(getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: snapshot children count " + snapshot.getChildrenCount());
                long NumberOfComments = 0;

                if (snapshot.exists()) {
                    NumberOfComments = snapshot.getChildrenCount();
                }

                if (NumberOfComments == 0) {
                    view_comments.setVisibility(View.GONE);
                } else {
                    view_comments.setText(Html.fromHtml("View all Comments " + "<font color = '#FF0033'> (" + NumberOfComments + ") </font>"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    private void SetLikesListener() {

        Like_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
                likedBy.setVisibility(View.VISIBLE);

            }
        });

        LikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
                likedBy.setVisibility(View.VISIBLE);
            }
        });

        MediaContainerRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void toggleLike() {


        if (isPhoto) {

            if (!misLikedByUser) {
                Like_outline.setVisibility(View.VISIBLE);
                LikeButton.setVisibility(View.INVISIBLE);
                heartAnimation.toggleLike(Like_outline, LikeButton);
                mFirebaseMethods.addNewLike(getString(R.string.dbname_photos), getString(R.string.dbname_user_photos), photo.getPhoto_id(), photo.getUser_id(),profilePhotoUrl,username);
            } else {
                Like_outline.setVisibility(View.INVISIBLE);
                LikeButton.setVisibility(View.VISIBLE);
                heartAnimation.toggleLike(Like_outline, LikeButton);
                misLikedByUser = false;
                mFirebaseMethods.removeLike(getString(R.string.dbname_photos), getString(R.string.dbname_user_photos), photo.getPhoto_id(), LikeID, photo.getUser_id());
            }

        } else {
            if (!misLikedByUser) {
                Like_outline.setVisibility(View.VISIBLE);
                LikeButton.setVisibility(View.INVISIBLE);
                heartAnimation.toggleLike(Like_outline, LikeButton);
                mFirebaseMethods.addNewLike(getString(R.string.dbname_videos), getString(R.string.dbname_user_videos), video.getVideo_id(), video.getUser_id(),profilePhotoUrl,username);
            } else {
                Like_outline.setVisibility(View.INVISIBLE);
                LikeButton.setVisibility(View.VISIBLE);
                heartAnimation.toggleLike(Like_outline, LikeButton);
                misLikedByUser = false;
                mFirebaseMethods.removeLike(getString(R.string.dbname_videos), getString(R.string.dbname_user_videos), video.getVideo_id(), LikeID, video.getUser_id());
            }

        }

    }

    private void getUserLikes(final DataSnapshot singleSnapshot) {
        Log.d(TAG, String.format("onDataChange: single snapshot value%s", singleSnapshot.getValue()));
        mStringBuilder = new StringBuilder();
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d(TAG, String.format("onDataChange: snapshot children %s", snapshot.getChildren()));
                // Log.d(TAG, String.format("onDataChange: snapshot value%s", snapshot.getValue()));
                Log.d(TAG, String.format("onDataChange: snapshot children count%s", snapshot.getChildrenCount()));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange:  dataSnapshot getUserLikes found like "
                            + Objects.requireNonNull(dataSnapshot.getValue(User.class)).getUser_id());
                    mStringBuilder.append(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getUsername());
                    mStringBuilder.append(",");
                }
                //  Log.d(TAG, String.format("onDataChange: LikedByUsernames %s", mStringBuilder.toString()));
                String[] splitUsers = mStringBuilder.toString().split(",");
                int length = splitUsers.length;
                if (length == 1) {
                    LikesString = "Liked by "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[0]
                            + "</font>"
                            + "</B>";
                } else if (length == 2) {
                    LikesString = "Liked by "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[0]
                            + "</font>"
                            + "</B>"
                            + " and "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[1]
                            + "</font>"
                            + "</B>";
                } else if (length == 3) {
                    LikesString = "Liked by "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[0]
                            + "</font>"
                            + "</B>"
                            + ", "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[1]
                            + "</font>"
                            + "</B>"
                            + " and "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[2]
                            + "</font>"
                            + "</B>";
                } else if (length > 3) {
                    //Log.d(TAG, "onDataChange: singleSnapshot.getChildrenCount() " + snapshot.getChildrenCount());
                    LikesString = "Liked by "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[0]
                            + "</font>"
                            + "</B>"
                            + ", "
                            + "<B>"
                            + "<font color=\"black\">"
                            + splitUsers[1]
                            + "</font>"
                            + "</B>"
                            + " and "
                            + "<B>"
                            + "<font color=\"black\">"
                            + (length - 2)
                            + " others"
                            + "</font>"
                            + "</B>";
                }
                likedBy.setText(Html.fromHtml(LikesString, Html.FROM_HTML_MODE_LEGACY));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpLikes(String node, String mediaID, String UserID) {
        Query query = myRef.child(node)
                .child(UserID)
                .child(mediaID)
                .child(getString(R.string.field_likes));
        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                    // Log.d(TAG, "onDataChange: single snapshot " + singleSnapshot.getChildrenCount());
                    getUserLikes(singleSnapshot);
                    //user liked
                    if (Objects.requireNonNull(singleSnapshot.child(getString(R.string.field_user_id)).getValue())
                            .equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                        LikeID = singleSnapshot.getKey();
                        LikeButton.setVisibility(View.VISIBLE);
                        Like_outline.setVisibility(View.INVISIBLE);
                        misLikedByUser = true;
                    }
                    //user not like
                }
                if (!snapshot.exists()) {
                    LikesString = "";
                    misLikedByUser = false;
                    LikeButton.setVisibility(View.INVISIBLE);
                    Like_outline.setVisibility(View.VISIBLE);
                    likedBy.setVisibility(View.GONE);
                    likedBy.setText(LikesString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /*
    Settings data related to post in the activity widgets
     */
    private void SetUpViewPostData() {
        if (Objects.requireNonNull(serializable).getClass() == Photo.class) {
            //Log.d(TAG, String.format("onCreate: PHOTO %s", serializable));
            isPhoto = true;
            photo = (Photo) serializable;
            //getTimeStampDifference(photo, null);
            // Log.d(TAG, String.format("onCreate: photo %s", photo));
            displayImage(photo.getImageUrl(), photo.getScaleType());
            setFontType(photo.getFont());
            mCaption.setTypeface(face);
            String Caption = StringManipulation.findEmptyLines(photo.getCaption());
            if (photo.getFont_color() != null && !photo.getFont_color().isEmpty()) {
                mCaption.setTextColor(Integer.parseInt(photo.getFont_color()));
            }
            if (Caption.equals("")) {
                mCaption.setVisibility(View.GONE);
            } else {
                mCaption.setText(Html.fromHtml(
                        "<font color = '#000000' face = 'arial'>"
                                + "<B>"
                                +"<big>"
                                + username
                                + "</B>"
                                +"</big>"
                                + "</font>"
                                + " "
                                + Caption, Html.FROM_HTML_MODE_LEGACY));
            }
            DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            offsetRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(estimatedServerTimeMs);
                    String TimeStamp = formatter.format(calendar.getTime());
                    TimePassed = miscTools.SetUpDateWidget(photo.getDate_added(), false, TimeStamp);
                    date_added.setText(TimePassed);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });

            Log.d(TAG, "SetUpViewPostData: TimePassed " + TimePassed);

            // SetUpDateWidget(photo, null);
            setUpLikes(getString(R.string.dbname_user_photos), photo.getPhoto_id(), photo.getUser_id());
        } else {
            isPhoto = false;
            video = (Video) serializable;
            //getTimeStampDifference(null, video);
            // Log.d(TAG, String.format("onCreate: photo %s", photo));
            playVideo(video.getVideoUrl());
            setFontType(video.getFont());
            mCaption.setTypeface(face);
            String Caption = StringManipulation.findEmptyLines(video.getCaption());
            if (video.getFont_color() != null && !video.getFont_color().isEmpty()) {
                mCaption.setTextColor(Integer.parseInt(video.getFont_color()));
            }
            if (Caption.equals("")) {
                mCaption.setVisibility(View.GONE);
            } else {
                mCaption.setText(Html.fromHtml(
                        "<font color = '#000000' face = 'arial'>"
                                + "<B>"
                                +"<big>"
                                + username
                                + "</B>"
                                +"</big>"
                                + "</font>"
                                + " "
                                + Caption, Html.FROM_HTML_MODE_LEGACY));
            }
            DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            offsetRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(estimatedServerTimeMs);
                    String TimeStamp = formatter.format(calendar.getTime());
                    TimePassed = miscTools.SetUpDateWidget(video.getDate_added(), false, TimeStamp);
                    Log.d(TAG, "SetUpViewPostData: TimePassed " + TimePassed);
                    date_added.setText(TimePassed);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });

            // SetUpDateWidget(null, video);
            SetUpAudio();
            setUpLikes(getString(R.string.dbname_user_videos), video.getVideo_id(), video.getUser_id());

        }

    }

    /*
    settings audio on relative layout click
     */
    private void SetUpAudio() {
        OnClickVolume();
        HandlerForAudio();
        simpleExoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: simpleExoPlayerView");
                audioVP.setVisibility(View.VISIBLE);
                OnClickVolume();
                HandlerForAudio();
            }
        });
    }

    /*
    setting audio on audio button click
     */
    private void OnClickVolume() {
        audioVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checked) {
                    audioVP.setImageResource(R.drawable.ic_volume_off);
                    exoPlayer.setVolume(0);
                    checked = true;
                } else {
                    audioVP.setImageResource(R.drawable.ic_volume_up);
                    exoPlayer.setVolume(100);
                    checked = false;
                }
            }
        });

    }

    /*
    Handler to hide audio button
     */
    private void HandlerForAudio() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioVP.setVisibility(View.GONE);
            }
        }, 4000);
    }

    /**
     * displaying the image that the user clicked on
     *
     * @param path
     * @param scaleType
     */
    private void displayImage(String path, String scaleType) {

        simpleExoPlayerView.setVisibility(View.GONE);
        PhotoPostImageView.setVisibility(View.VISIBLE);

        if (scaleType.equals(getString(R.string.scale_type_center_crop))) {

            PhotoPostImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (scaleType.equals(getString(R.string.scale_type_center_inside))) {

            PhotoPostImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        GlideImageLoader.loadImageWithTransition(mContext, path, PhotoPostImageView, null, null);
    }

    /**
     * settings exo player & simple exo player view
     *
     * @param path
     */
    private void playVideo(String path) {

//        PhotoPostImageView.setVisibility(View.GONE);
//        simpleExoPlayerView.setVisibility(View.VISIBLE);
//        audioVP.setVisibility(View.VISIBLE);
//
//        simpleExoPlayerView.setUseController(false);
//        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.SYSTEM_UI_FLAG_FULLSCREEN);
//
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//
//        Uri videoUri = Uri.parse(path);
//        Log.d(TAG, "playVideo: path " + path);
//        Log.d(TAG, "playVideo: uri " + videoUri);
//        final Handler mainHandler = new Handler();
//        String userAgent = Util.getUserAgent(mContext, mContext.getString(R.string.app_name));
//        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        ProgressiveMediaSource mediaSource =
//                new ProgressiveMediaSource.Factory(
//                        defaultHttpDataSourceFactory, extractorsFactory)
//                        .createMediaSource(videoUri);
//
//        simpleExoPlayerView.setPlayer(exoPlayer);
//        exoPlayer.prepare(mediaSource);
//        exoPlayer.setPlayWhenReady(true);
//        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
//        exoPlayer.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                if (playbackState == Player.STATE_BUFFERING) {
//                    mProgressBar.setVisibility(View.VISIBLE);
//                } else {
//                    mProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });

    }

    /*
    Destroying exoplayer on activity destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
        myRef.removeEventListener(valueEventListener);
        // myRef.removeEventListener(valueEventListener1);
    }

    /*
    going back to the profile fragment
     */
    private void GoBack() {
        ImageView backArrow = findViewById(R.id.back_arrow_view_post);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * settings the font received from firebase to the caption widget
     *
     * @param font
     */
    public static Typeface setFontType(String font) {

        switch (font) {
            case "Arial":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Arial.ttf");
                break;
            case "akaDora":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/akaDora.ttf");
                break;
            case "Eutemia":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Eutemia.ttf");
                break;
            case "GREENPIL":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/GREENPIL.ttf");
                break;
            case "Grinched":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Grinched.ttf");
                break;
            case "Helvetica":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Helvetica.ttf");
                break;
            case "Libertango":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Libertango.ttf");
                break;
            case "MetalMacabre":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/MetalMacabre.ttf");
                break;
            case "ParryHotter":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/ParryHotter.ttf");
                break;
            case "TheGodfather":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/TheGodfather_v2.ttf");
                break;
            case "waltograph":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/waltograph42.ttf");
                break;
        }
        return face;
    }


    class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener {
        HeartAnimation heartAnimation = new HeartAnimation();

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            likedBy.setVisibility(View.VISIBLE);
            toggleLike();
            return true;
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
