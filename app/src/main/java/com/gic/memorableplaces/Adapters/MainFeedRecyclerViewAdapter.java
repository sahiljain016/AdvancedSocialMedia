package com.gic.memorableplaces.Adapters;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.CustomLibs.shinebuttonlib.ShineButton;
import com.gic.memorableplaces.Profile.CommentsActivity;
import com.gic.memorableplaces.Profile.ViewProfileActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GestureDetector;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.HeartAnimation;
import com.gic.memorableplaces.utils.MiscTools;
import com.gic.memorableplaces.utils.Platform;
import com.gic.memorableplaces.utils.StringManipulation;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

public class MainFeedRecyclerViewAdapter extends RecyclerView.Adapter<MainFeedRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "MainFeedRecyclerViewAda";

    //Variables
    private ArrayList<Object> mMediaList;
    private Context mContext;
    private String TimePassed, LikeID, LikesString, sProfileUrl, sUsername;
    private boolean isPhoto, checked = false, misLikedByUser = false;
    private StringBuilder mStringBuilder;
    private Intent intent;
    private Activity mActivity;
    public SimpleExoPlayer exoPlayer;
    private Typeface face;
    ArrayList<String> vidPos = new ArrayList<>();
    ;
    //Classes
    private MiscTools miscTools = new MiscTools();
    private HeartAnimation heartAnimation = new HeartAnimation();
    private Photo photo;
    private Video video;
    private GestureDetector gestureDetector;
    //Firebase
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseMethods mFirebaseMethods;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public RoundedImageView profilePhoto;
        public StyledPlayerView simpleExoPlayerView;
        public ImageView PhotoPostImageView;
        public ImageView Like_outline;
        //public ShadowView shadowLayout;
        public CardView cardView;
        public AutofitTextView usernameTV, mCaption, date_added, likedBy, view_comments;
        public ImageButton audioVP, post_settings, CommentButton, mPlayButton;
        public RoundedImageView RPP1, RPP2, RPP3;
        public RelativeLayout Menu_POst_RL;
        public ConstraintLayout CL_POST_DETAILS;
        public ImageButton SaveButton;
        public ShineButton LikeButton;

        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.profile_photo);
            usernameTV = itemView.findViewById(R.id.username_VP);
            mCaption = itemView.findViewById(R.id.post_caption);
            date_added = itemView.findViewById(R.id.date_posted);
            audioVP = itemView.findViewById(R.id.audio_vp);

            PhotoPostImageView = itemView.findViewById(R.id.view_post_imageView);
            simpleExoPlayerView = itemView.findViewById(R.id.Exo_player_view_post);
            post_settings = itemView.findViewById(R.id.post_settings);
            Menu_POst_RL = itemView.findViewById(R.id.Menu_Posts_RL);
            //shadowLayout = itemView.findViewById(R.id.SL_POST);
            CommentButton = itemView.findViewById(R.id.Comment);
            cardView = itemView.findViewById(R.id.CardView);
            LikeButton = itemView.findViewById(R.id.Like);
            Like_outline = itemView.findViewById(R.id.Like_outline);
            SaveButton = itemView.findViewById(R.id.Save);
            likedBy = itemView.findViewById(R.id.liked_number);
            CL_POST_DETAILS = itemView.findViewById(R.id.LL_post_details);
            view_comments = itemView.findViewById(R.id.view_comments);
            RPP3 = itemView.findViewById(R.id.LikePP3);
            RPP2 = itemView.findViewById(R.id.LikePP2);
            RPP1 = itemView.findViewById(R.id.LikePP1);
            mPlayButton = itemView.findViewById(R.id.Play_button);

        }
    }

    public MainFeedRecyclerViewAdapter(ArrayList<Object> MediaArrayList, String ProfileUrl, String Username, Context context, Activity activity) {
        mMediaList = MediaArrayList;
        mContext = context;
        sProfileUrl = ProfileUrl;
        sUsername = Username;
        mActivity = activity;
        mFirebaseMethods = new FirebaseMethods(mContext);
        intent = new Intent(mContext, CommentsActivity.class);
//        gestureDetector = new android.view.GestureDetector(mContext, new ViewPostActivity.GestureListener());
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main_feed_recycler_view, parent, false);
        return new MainFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        Object object = mMediaList.get(position);
        if (object.getClass() == Photo.class) {
            isPhoto = true;
            photo = (Photo) object;
            //  Log.d(TAG, String.format("onBindViewHolder: photo %s", photo));
            Log.d(TAG, "onBindViewHolder: photo getDate added " + photo.getDate_added());
            setProfileInfo(((Photo) object).getUser_id(), holder, true, photo, null);
            CommentsOnClick(holder, true, photo, video);
            getNumberOfComments(mContext.getString(R.string.dbname_user_photos), photo.getUser_id(), photo.getPhoto_id(), holder.view_comments);
            DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            final String PhotoDateAdded = photo.getDate_added();

            offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(estimatedServerTimeMs);
                    String TimeStamp = formatter.format(calendar.getTime());
                    // Log.d(TAG, "onDataChange: TimeStamp " + TimeStamp);
                    Log.d(TAG, "onDataChange: date_added " + PhotoDateAdded);
                    TimePassed = miscTools.SetUpDateWidget(PhotoDateAdded, false, TimeStamp);
                    Log.d(TAG, "SetUpViewPostData: TimePassed " + TimePassed);
                    holder.date_added.setText(TimePassed);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });

        } else {
            isPhoto = false;
            video = (Video) object;
            DisplayMetrics displayMetrics = new DisplayMetrics();

            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            Log.d(TAG, "onBindViewHolder: width: " +displayMetrics.widthPixels);
            //LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(displayMetrics.widthPixels - (80),displayMetrics.widthPixels);
            //layoutParams.setMargins(20,20,20,20);
//            holder.cardView.setLayoutParams(layoutParams);


            holder.PhotoPostImageView.setVisibility(View.GONE);
            holder.simpleExoPlayerView.setVisibility(View.VISIBLE);
            holder.audioVP.setVisibility(View.VISIBLE);
            holder.mPlayButton.setVisibility(View.GONE);
            playVideo(video.getVideoUrl(), holder.simpleExoPlayerView, holder.mPlayButton);
            holder.mPlayButton.setOnClickListener(v -> {
                holder.PhotoPostImageView.setVisibility(View.GONE);
                holder.simpleExoPlayerView.setVisibility(View.VISIBLE);
                holder.audioVP.setVisibility(View.VISIBLE);
                playVideo(video.getVideoUrl(), holder.simpleExoPlayerView, holder.mPlayButton);
                holder.mPlayButton.setVisibility(View.GONE);
            });
            DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            offsetRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(estimatedServerTimeMs);
                    String TimeStamp = formatter.format(calendar.getTime());
                    Log.d(TAG, "onDataChange: TimeStamp " + TimeStamp);
                    Log.d(TAG, "onDataChange: date_added " + video.getDate_added());
                    TimePassed = miscTools.SetUpDateWidget(video.getDate_added(), false, TimeStamp);
                    Log.d(TAG, "SetUpViewPostData: TimePassed " + TimePassed);
                    holder.date_added.setText(TimePassed);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });
            vidPos.add(String.valueOf(position));
            SetUpAudio(holder.audioVP, holder.simpleExoPlayerView);
            setProfileInfo(((Video) object).getUser_id(), holder, false, null, video);
            CommentsOnClick(holder, false, photo, video);
            getNumberOfComments(mContext.getString(R.string.dbname_user_videos), video.getUser_id(), video.getVideo_id(), holder.view_comments);
        }

        holder.usernameTV.setOnClickListener(v -> ViewProfile(isPhoto, photo, video));
        holder.profilePhoto.setOnClickListener(v -> ViewProfile(isPhoto, photo, video));
        getCurrentUserUsername();
        SetLikesListener(holder, object);
        SetUpViewPostData(holder, position);
//        holder.Menu_Options_CV.post(() -> {
//            //  Dali.create(mContext).liveBlur(holder.PhotoPostImageView, holder.Menu_Options_CV).blurRadius(20).assemble(true);
//            holder.Menu_Options_CV.startBlur();
//
//        });


    }

    @Override
    public int getItemCount() {
        return mMediaList.size();
    }

    private void setProfileInfo(final String userId, MainFeedViewHolder holder, final boolean IsPhoto, final Photo photo, final Video video) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByKey().equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getProfile_photo(), holder.profilePhoto);
                    holder.usernameTV.setText(Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername());

                    intent.putExtra(mContext.getString(R.string.field_username), requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername());
                    intent.putExtra(mContext.getString(R.string.field_profile_photo), requireNonNull(ds.getValue(UserAccountSettings.class)).getProfile_photo());
                    String Caption;
                    if (IsPhoto) {
                        //Log.d(TAG, "onDataChange: photo caption: " + photo.getCaption());
                        Caption = StringManipulation.findEmptyLines(photo.getCaption());
                        //Log.d(TAG, "onDataChange: Caption " + Caption);
                        //Log.d(TAG, "onDataChange: username " + Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername());

                    } else {
                        Caption = StringManipulation.findEmptyLines(video.getCaption());
                        Log.d(TAG, "onDataChange: username video " + Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername());
                    }
                    if (TextUtils.isEmpty(Caption)) {
                        holder.mCaption.setVisibility(View.GONE);
                    } else {
                        holder.mCaption.setText(Html.fromHtml(
                                "<font color = '#000000' face = 'arial'>"
                                        + "<B>"
                                        + "<big>"
                                        + Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername()
                                        + "</big>"
                                        + "</B>"
                                        + "</font>"
                                        + " "
                                        + Caption, Html.FROM_HTML_MODE_LEGACY));
                    }
                    Log.d(TAG, "onDataChange: Caption: " + holder.mCaption.getText().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /*
    Settings data related to post in the activity widgets
     */
    private void SetUpViewPostData(MainFeedViewHolder holder, int position) {
        if (isPhoto) {
            // Log.d(TAG, String.format("onCreate: photo %s", photo));
            displayImage(photo.getImageUrl(), photo.getScaleType(), holder);
            setFontType(photo.getFont());
            holder.mCaption.setTypeface(face);
            if (photo.getFont_color() != null && !photo.getFont_color().isEmpty()) {
                holder.mCaption.setTextColor(Integer.parseInt(photo.getFont_color()));
            }
//            Query query = myRef.child(mContext.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).child(mContext.getString(R.string.field_likes));
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            setUpLikes(mContext.getString(R.string.dbname_photos), photo.getPhoto_id(), holder);
//                }
//
//                @Override
//                public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                }
//            });

        } else {
            // Log.d(TAG, String.format("onCreate: photo %s", photo));


            setFontType(video.getFont());
            holder.mCaption.setTypeface(face);
            // String Caption = StringManipulation.findEmptyLines(video.getCaption());
            if (video.getFont_color() != null && !video.getFont_color().isEmpty()) {
                holder.mCaption.setTextColor(Integer.parseInt(video.getFont_color()));
            }
//            Query query = myRef.child(mContext.getString(R.string.dbname_videos)).child(video.getVideo_id()).child(mContext.getString(R.string.field_likes));
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            setUpLikes(mContext.getString(R.string.dbname_videos), video.getVideo_id(), holder);
//                }
//
//                @Override
//                public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                }
//            });

        }

    }

    private void getCurrentUserUsername() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                        intent.putExtra("CurrentUsername", Objects.requireNonNull(ds.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .getValue(UserAccountSettings.class))
                                .getUsername());
                        intent.putExtra("CurrentUserProfilePhoto", Objects.requireNonNull(ds.child(mAuth.getCurrentUser().getUid())
                                .getValue(UserAccountSettings.class))
                                .getProfile_photo());
                        // Log.d(TAG, "onDataChange: Current USerName " + CurrentUsername);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //Log.d(TAG, "onDataChange: Current USerName  return " + CurrentUsername);
    }

    private void ViewProfile(boolean isPhoto, Photo photo, Video video) {
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
        mContext.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void CommentsOnClick(MainFeedViewHolder holder, final boolean isPhoto, final Photo photo, final Video video) {
        //Log.d(TAG, "CommentsOnClick: CurrentUsername " + CurrentUsername);

        holder.CommentButton.setOnClickListener(v -> {
            // getCurrentUserUsername();

            if (isPhoto) {
                intent.putExtra(mContext.getString(R.string.field_mediaID), photo.getPhoto_id());
                intent.putExtra(mContext.getString(R.string.field_user_id), photo.getUser_id());
                intent.putExtra("UserNodeID", mContext.getString(R.string.dbname_user_photos));
                intent.putExtra("NodeID", mContext.getString(R.string.dbname_photos));
                intent.putExtra(mContext.getString(R.string.field_caption), photo.getCaption());
                intent.putExtra(mContext.getString(R.string.field_date_added), photo.getDate_added());
                intent.putExtra(mContext.getString(R.string.field_font), photo.getFont());
                intent.putExtra("isPhoto", true);
                Log.d(TAG, "CommentsOnClick: FontColor " + photo.getFont_color());
                if (photo.getFont_color() != null) {
                    Log.d(TAG, "CommentsOnClick: Font_color " + photo.getFont_color());
                    intent.putExtra(mContext.getString(R.string.field_color_caption), photo.getFont_color());
                }
            } else {
                intent.putExtra(mContext.getString(R.string.field_mediaID), video.getVideo_id());
                intent.putExtra(mContext.getString(R.string.field_user_id), video.getUser_id());
                intent.putExtra("UserNodeID", mContext.getString(R.string.dbname_user_videos));
                intent.putExtra("NodeID", mContext.getString(R.string.dbname_videos));
                intent.putExtra(mContext.getString(R.string.field_caption), video.getCaption());
                intent.putExtra(mContext.getString(R.string.field_date_added), video.getDate_added());
                intent.putExtra(mContext.getString(R.string.field_font), video.getFont());
                intent.putExtra("isPhoto", false);
                if (video.getFont_color() != null) {
                    intent.putExtra(mContext.getString(R.string.field_color_caption), video.getFont_color());
                }
            }

            FragmentTransactionComments();

        });

        holder.view_comments.setOnClickListener(v -> {

            if (isPhoto) {
                intent.putExtra(mContext.getString(R.string.field_mediaID), photo.getPhoto_id());
                intent.putExtra(mContext.getString(R.string.field_user_id), photo.getUser_id());
                intent.putExtra("UserNodeID", mContext.getString(R.string.dbname_user_photos));
                intent.putExtra("NodeID", mContext.getString(R.string.dbname_photos));
                intent.putExtra(mContext.getString(R.string.field_caption), photo.getCaption());
                intent.putExtra(mContext.getString(R.string.field_date_added), photo.getDate_added());
                intent.putExtra(mContext.getString(R.string.field_font), photo.getFont());
                intent.putExtra("isPhoto", true);
                Log.d(TAG, "CommentsOnClick: FontColor " + photo.getFont_color());
                if (photo.getFont_color() != null) {
                    Log.d(TAG, "CommentsOnClick: Font_color " + photo.getFont_color());
                    intent.putExtra(mContext.getString(R.string.field_color_caption), photo.getFont_color());
                }
            } else {
                intent.putExtra(mContext.getString(R.string.field_mediaID), video.getVideo_id());
                intent.putExtra(mContext.getString(R.string.field_user_id), video.getUser_id());
                intent.putExtra("UserNodeID", mContext.getString(R.string.dbname_user_videos));
                intent.putExtra("NodeID", mContext.getString(R.string.dbname_videos));
                intent.putExtra(mContext.getString(R.string.field_caption), video.getCaption());
                intent.putExtra(mContext.getString(R.string.field_date_added), video.getDate_added());
                intent.putExtra(mContext.getString(R.string.field_font), video.getFont());
                intent.putExtra("isPhoto", false);
                if (video.getFont_color() != null) {
                    intent.putExtra(mContext.getString(R.string.field_color_caption), video.getFont_color());
                }

            }
            FragmentTransactionComments();
        });
    }

    private void FragmentTransactionComments() {
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        Fragment fragment = new CommentsFragment();
//        fragment.setArguments(bundle);
//        String fragmentName = mContext.getString(R.string.fragment_comments);
//        FragmentTransaction Transaction = ((HomeActivity) mActivity).getSupportFragmentManager().beginTransaction();
//        Transaction.replace(R.id.FrameLayoutCommentsSocialFeed, Objects.requireNonNull(fragment));
//        Transaction.addToBackStack(fragmentName);
//        Transaction.commit();
    }

    private void getNumberOfComments(String UserNode, String userID, String mediaID, final TextView view_comments) {

        Query query = myRef.child(UserNode)
                .child(userID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_comments));

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
    private void SetLikesListener(MainFeedViewHolder holder, final Object object) {

        holder.Like_outline.setOnClickListener(v -> {
            toggleLike(holder, object);
            holder.likedBy.setVisibility(View.VISIBLE);

        });

        holder.LikeButton.setOnClickListener(v -> {
            toggleLike(holder, object);
            holder.likedBy.setVisibility(View.VISIBLE);
        });

//        MediaContainerRL.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        });
    }

    public void toggleLike(MainFeedViewHolder holder, Object object) {

        misLikedByUser = false;
        if (Objects.requireNonNull(object).getClass() == Photo.class) {
            photo = (Photo) object;

            Query query = myRef.child(mContext.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> mUIDList = new ArrayList<>();
                    if (!dataSnapshot.exists()) {
                        LikesString = "";
                        misLikedByUser = false;
                        holder.LikeButton.setVisibility(View.INVISIBLE);
                        holder.Like_outline.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.child(mContext.getString(R.string.field_user_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                misLikedByUser = true;
                                LikeID = ds.getKey();
                                //Log.d("TAG", "LikeID = " + );
                                holder.Like_outline.setVisibility(View.INVISIBLE);
                                holder.LikeButton.setVisibility(View.VISIBLE);
                                heartAnimation.toggleLike(holder.Like_outline, holder.LikeButton);
                                mFirebaseMethods.removeLike(mContext.getString(R.string.dbname_photos), mContext.getString(R.string.dbname_user_photos),
                                        photo.getPhoto_id(), LikeID, photo.getUser_id());

                                setUpLikes(mContext.getString(R.string.dbname_photos), photo.getPhoto_id(), holder);
                            }
                        }
                    }

                    if (!misLikedByUser) {
                        Log.d("TAG", "Datasnapshot doesn't exists");
                        holder.Like_outline.setVisibility(View.VISIBLE);
                        holder.LikeButton.setVisibility(View.INVISIBLE);
                        heartAnimation.toggleLike(holder.Like_outline, holder.LikeButton);
                        mFirebaseMethods.addNewLike(mContext.getString(R.string.dbname_photos), mContext.getString(R.string.dbname_user_photos),
                                photo.getPhoto_id(), photo.getUser_id(), sProfileUrl, sUsername);
                        setUpLikes(mContext.getString(R.string.dbname_photos), photo.getPhoto_id(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            video = (Video) object;

            Query query = myRef.child(mContext.getString(R.string.dbname_videos)).child(video.getVideo_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        misLikedByUser = false;
                        LikesString = "";
                        holder.LikeButton.setVisibility(View.INVISIBLE);
                        holder.Like_outline.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.child(mContext.getString(R.string.field_user_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                misLikedByUser = true;
                                LikeID = ds.getKey();
                                // Log.d("TAG", "LikeID = " + LikedBy);
                                holder.Like_outline.setVisibility(View.INVISIBLE);
                                holder.LikeButton.setVisibility(View.VISIBLE);
                                heartAnimation.toggleLike(holder.Like_outline, holder.LikeButton);
                                mFirebaseMethods.removeLike(mContext.getString(R.string.dbname_videos), mContext.getString(R.string.dbname_user_videos),
                                        video.getVideo_id(), LikeID, video.getUser_id());
                                setUpLikes(mContext.getString(R.string.dbname_videos), video.getVideo_id(), holder);
                            }
                        }
                    }
                    if (!misLikedByUser) {
                        holder.Like_outline.setVisibility(View.VISIBLE);
                        holder.LikeButton.setVisibility(View.INVISIBLE);
                        heartAnimation.toggleLike(holder.Like_outline, holder.LikeButton);
                        mFirebaseMethods.addNewLike(mContext.getString(R.string.dbname_videos), mContext.getString(R.string.dbname_user_videos),
                                video.getVideo_id(), video.getUser_id(), sProfileUrl, sUsername);
                        setUpLikes(mContext.getString(R.string.dbname_videos), video.getVideo_id(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUserLikes(ArrayList<String> ImagePaths, ArrayList<String> Usernames, int LikesCount, MainFeedViewHolder holder) {
        //  Log.d(TAG, String.format("onDataChange: single snapshot value%s", singleSnapshot.getValue()));
        //   Log.d(TAG, String.format("onDataChange: string builder before %s", mStringBuilder));
        mStringBuilder = new StringBuilder();
        //  Log.d(TAG, String.format("onDataChange: string builder after %s", mStringBuilder));
        LikesString = "";
//        Query query = myRef
//                .child(mContext.getString(R.string.dbname_user_account_settings))
//                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(UID);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
        //Log.d(TAG, String.format("onDataChange: snapshot children %s", snapshot.getChildren()));
        // Log.d(TAG, String.format("onDataChange: snapshot value%s", snapshot.getValue()));

        // LikesString = "";
        // UserAccountSettings userAccountSettings = snapshot.getValue(UserAccountSettings.class);
        // Log.d(TAG, String.format("onDataChange: snapshot children count%s", snapshot.getChildrenCount()));
        // for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
        //  Log.d(TAG, "onDataChange: Profile Photo+ " + userAccountSettings.getProfile_photo());
        //  ImagePaths.add(userAccountSettings.getProfile_photo());

        // if (mStringBuilder.indexOf(userAccountSettings.getUsername()) == -1) {
        //     mStringBuilder.append(userAccountSettings.getUsername());
        //    mStringBuilder.append(",");
        // }
        //  Log.d(TAG, String.format("onDataChange: string builder in for loop %s", mStringBuilder));
        // }
        // Log.d(TAG, String.format("onDataChange: string builder %s", mStringBuilder));
        //Log.d(TAG, "onDataChange: ImagePaths " + ImagePaths);
        //String[] splitUsers = mStringBuilder.toString().split(",");
        int length = Usernames.size();
        Log.d(TAG, String.format("onDataChange: Length %s", length));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.CL_POST_DETAILS);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.likedBy.getLayoutParams();
        //holder.RPP1.setVisibility(View.VISIBLE);
        //if(ImagePaths)
        if (length == 1) {
            Log.d(TAG, "onDataChange: ");
            LikesString = "Liked by "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(0)
                    + "</font>"
                    + "</B>";
//            holder.RPP1.setVisibility(View.VISIBLE);
//            holder.RPP2.setVisibility(View.GONE);
//            holder.RPP3.setVisibility(View.GONE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(0), holder.RPP1);
            layoutParams.startToEnd = R.id.LikePP1;
            //constraintSet.connect(R.id.liked_number, ConstraintSet.START, R.id.LikePP1, ConstraintSet.END, 10);
//                    holder.RPP2.setVisibility(View.GONE);
//                    GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(0), holder.RPP3);

        } else if (length == 2) {
            LikesString = "Liked by "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(0)
                    + "</font>"
                    + "</B>"
                    + " and "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(1)
                    + "</font>"
                    + "</B>";
            //
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(0), holder.RPP1);
            //holder.RPP2.setVisibility(View.VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(1), holder.RPP2);
            //holder.RPP3.setVisibility(View.GONE);
            layoutParams.startToEnd = R.id.LikePP2;
            //constraintSet.connect(R.id.liked_number, ConstraintSet.START, R.id.LikePP2, ConstraintSet.END, 10);


        } else if (length == 3) {
            LikesString = "Liked by "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(0)
                    + "</font>"
                    + "</B>"
                    + ", "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(1)
                    + "</font>"
                    + "</B>"
                    + " and "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(2)
                    + "</font>"
                    + "</B>";

            // holder.RPP1.setVisibility(View.VISIBLE);
            // holder.RPP2.setVisibility(View.VISIBLE);
            // holder.RPP3.setVisibility(View.VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(2), holder.RPP1);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(1), holder.RPP2);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(0), holder.RPP3);
            layoutParams.startToEnd = R.id.LikePP3;
            //constraintSet.connect(R.id.liked_number, ConstraintSet.START, R.id.LikePP3, ConstraintSet.END, 10);
        } else if (length > 3) {
            //Log.d(TAG, "onDataChange: singleSnapshot.getChildrenCount() " + snapshot.getChildrenCount());
            LikesString = "Liked by "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(0)
                    + "</font>"
                    + "</B>"
                    + ", "
                    + "<B>"
                    + "<font color=\"black\">"
                    + Usernames.get(1)
                    + "</font>"
                    + "</B>"
                    + " and "
                    + "<B>"
                    + "<font color=\"black\">"
                    + (LikesCount - 2)
                    + " others"
                    + "</font>"
                    + "</B>";
            // holder.RPP1.setVisibility(View.VISIBLE);
            // holder.RPP2.setVisibility(View.VISIBLE);
            //holder.RPP3.setVisibility(View.VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(0), holder.RPP1);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(1), holder.RPP2);
            GlideImageLoader.loadImageWithOutTransition(mContext, ImagePaths.get(2), holder.RPP3);
            layoutParams.startToEnd = R.id.LikePP3;
            // constraintSet.connect(R.id.liked_number, ConstraintSet.START, R.id.LikePP3, ConstraintSet.END, 10);
        }
        holder.likedBy.setLayoutParams(layoutParams);
        Log.d(TAG, "onDataChange:LikesString " + LikesString);
        holder.likedBy.setVisibility(View.VISIBLE);
        holder.likedBy.setText(Html.fromHtml(LikesString, Html.FROM_HTML_MODE_LEGACY));


//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


    private void setUpLikes(String node, String mediaID, MainFeedViewHolder holder) {
//        Query query = myRef.child(node)
//                .child(UserID)
//                .child(mediaID)
//                .child(mContext.getString(R.string.field_likes));
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
        ArrayList<String> ImagePaths = new ArrayList<>();
        ArrayList<String> Usernames = new ArrayList<>();
        // Log.d(TAG, "setUpLikes: Likes Get Children Count: " + Likes.getChildrenCount());
        Query query = myRef.child(node).child(mediaID)
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    holder.Like_outline.setVisibility(View.VISIBLE);
                    holder.LikeButton.setVisibility(View.INVISIBLE);
                    holder.likedBy.setVisibility(View.GONE);
                    holder.likedBy.setText("");
                } else {
                    for (DataSnapshot LikesUID : dataSnapshot.getChildren()) {
                        if (requireNonNull(LikesUID.child(mContext.getString(R.string.field_user_id)).getValue()).toString().equals(mAuth.getCurrentUser().getUid())) {
                            holder.Like_outline.setVisibility(View.INVISIBLE);
                            holder.LikeButton.setVisibility(View.VISIBLE);
                        }
                    }
                    if (dataSnapshot.getChildrenCount() > 3) {
                        int count = 1;
                        for (DataSnapshot LikesUID : dataSnapshot.getChildren()) {

                            if (count == 4) {
                                return;
                            } else {
                                ImagePaths.add(LikesUID.child(mContext.getString(R.string.field_profile_photo)).getValue().toString());
                                Usernames.add(LikesUID.child(mContext.getString(R.string.field_username)).getValue().toString());
                            }
                            count++;
                        }
                    } else {
                        for (DataSnapshot LikesUID : dataSnapshot.getChildren()) {
                            Log.d(TAG, String.format("onDataChange: LikesUID: %s", LikesUID));
                            ImagePaths.add(LikesUID.child(mContext.getString(R.string.field_profile_photo)).getValue().toString());
                            Usernames.add(LikesUID.child(mContext.getString(R.string.field_username)).getValue().toString());

                        }
                    }
                    getUserLikes(ImagePaths, Usernames, (int) dataSnapshot.getChildrenCount(), holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
//        mStringBuilder = new StringBuilder();
//        mStringBuilder.append("Liked by ");
//
//        if (Likes.getChildrenCount() > 3) {
//            holder.LikeButton.setVisibility(View.INVISIBLE);
//            holder.Like_outline.setVisibility(View.VISIBLE);
//            int count = 0;
//            for (DataSnapshot LikesUID : Likes.getChildren()) {
//                if (requireNonNull(LikesUID.child(mContext.getString(R.string.field_user_id)).getValue()).toString().equals(mAuth.getCurrentUser().getUid())) {
//                    holder.Like_outline.setVisibility(View.INVISIBLE);
//                    holder.LikeButton.setVisibility(View.VISIBLE);
//                }
//                if ()
//                    UID
//            }
//        }
//        if (UIDList.isEmpty()) {
//            holder.Like_outline.setVisibility(View.VISIBLE);
//            holder.LikeButton.setVisibility(View.INVISIBLE);
//            holder.likedBy.setVisibility(View.GONE);
//            holder.likedBy.setText("");
//        } else {
//            holder.LikeButton.setVisibility(View.INVISIBLE);
//            holder.Like_outline.setVisibility(View.VISIBLE);
//            for (String UID : UIDList) {
//
//                if (UID.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
//                    holder.Like_outline.setVisibility(View.INVISIBLE);
//                    holder.LikeButton.setVisibility(View.VISIBLE);
//                }
//                getUserLikes(UID, holder);
//
//            }
//
//        }
        //}

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    /**
     * settings the font received from firebase to the caption widget
     *
     * @param font
     */
    private void setFontType(String font) {

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
    }

    /*
        settings audio on relative layout click
         */
    private void SetUpAudio(final ImageButton audioVP, StyledPlayerView simpleExoPlayerView) {
        OnClickVolume(audioVP);
        HandlerForAudio(audioVP);
        simpleExoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: simpleExoPlayerView");
                audioVP.setVisibility(View.VISIBLE);
                OnClickVolume(audioVP);
                HandlerForAudio(audioVP);
            }
        });
    }

    /*
    setting audio on audio button click
     */
    private void OnClickVolume(final ImageButton audioVP) {

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
    private void HandlerForAudio(final ImageButton audioVP) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> audioVP.setVisibility(View.GONE), 4000);
    }

    /**
     * displaying the image that the user clicked on
     *
     * @param path
     * @param scaleType
     */
    private void displayImage(String path, String scaleType, MainFeedViewHolder holder) {

        holder.simpleExoPlayerView.setVisibility(View.GONE);
        holder.PhotoPostImageView.setVisibility(View.VISIBLE);

        if (scaleType.equals(mContext.getString(R.string.scale_type_center_crop))) {

            holder.PhotoPostImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (scaleType.equals(mContext.getString(R.string.scale_type_center_inside))) {

            holder.PhotoPostImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

//        UniversalImageLoader.setImage(path,holder.PhotoPostImageView,null,"http:/");
        GlideImageLoader.loadImageWithOutTransition(mContext, path, holder.PhotoPostImageView);

        if(Platform.greaterThanP()) {
            Glide.with(mContext).asBitmap().load(path).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                    Palette.from(resource).generate(palette -> {
                        assert palette != null;
                        holder.cardView.post(() -> {
                            Log.d(TAG, "onResourceReady: color: " + palette.getDominantColor(0x000000));
                            Log.d(TAG, "onResourceReady: color Vibrant: " + palette.getVibrantColor(0x000000));
                            Log.d(TAG, "onResourceReady: color Vibrant dark: " + palette.getDarkVibrantColor(0x000000));
                            Log.d(TAG, "onResourceReady: color muted : " + palette.getMutedColor(0x000000));
                            holder.cardView.setOutlineAmbientShadowColor(palette.getDominantColor(0x000000));
//                        //holder.shadowLayout.setShadowColor(getTransparentColor(palette.getDominantColor(0x000000)));
//                        //holder.shadowLayout.setShadowStartColor(getTransparentColor(palette.getMutedColor(0x000000)));
//
                        });
                    });
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });
        }
//        holder.PhotoPostImageView.setDrawingCacheEnabled(true);

//        Bitmap bitmap = holder.PhotoPostImageView.getDrawingCache();

//        int ColorCode = bitmap.getPixel(x, 0);
//        Log.d(TAG, "onBindViewHolder: ColorCode: " + ColorCode);
//        holder.shadowLayout.setShadowColor(ColorCode);
    }

    private int getTransparentColor(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // Set alpha based on your logic, here I'm making it 25% of it's initial value.
        alpha *= 0.75;

        return Color.argb(alpha, red, green, blue);
    }

    /**
     * settings exo player & simple exo player view
     *
     * @param path
     * @param simpleExoPlayerView
     */

    private void playVideo(String path, StyledPlayerView simpleExoPlayerView, final ImageButton PlayButton) {
// bandwisthmeter is used for
        // getting default bandwidth
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//
//        // track selector is used to navigate between
//        // video using a default seekbar.
//        TrackSelector trackSelector = new DefaultTrackSelector(new ExoTrackSelection.Factory() {
//            @Override
//            public ExoTrackSelection[] createTrackSelections(ExoTrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter, MediaSource.MediaPeriodId mediaPeriodId, Timeline timeline) {
//                return new ExoTrackSelection[0];
//            }
//        });
//
//        // we are adding our track selector to exoplayer.
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//
//        // we are parsing a video url
//        // and parsing its video uri.
//        Uri videouri = Uri.parse(videoURL);
//
//        // we are creating a variable for datasource factory
//        // and setting its user agent as 'exoplayer_view'
//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
//
//        // we are creating a variable for extractor factory
//        // and setting it to default extractor factory.
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//
//        // we are creating a media source with above variables
//        // and passing our event handler as null,
//        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
//
//        // inside our exoplayer view
//        // we are setting our player
//        exoPlayerView.setPlayer(exoPlayer);
//
//        // we are preparing our exoplayer
//        // with media source.
//        exoPlayer.prepare(mediaSource);
//
//        // we are setting our exoplayer
//        // when it is ready.
//        exoPlayer.setPlayWhenReady(true);

//        simpleExoPlayerView.setUseController(false);
//        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);


        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        exoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

        Uri videoUri = Uri.parse(path);
        Log.d(TAG, "playVideo: path " + path);

        Log.d(TAG, "playVideo: uri " + videoUri);
        String userAgent = Util.getUserAgent(mContext, mContext.getString(R.string.app_name));
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        ProgressiveMediaSource mediaSource =
                new ProgressiveMediaSource.Factory(
                        defaultHttpDataSourceFactory, extractorsFactory)
                        .createMediaSource(videoUri);

        simpleExoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    exoPlayer.stop();
                    //exoPlayer.release();

                    PlayButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }

//    class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener {
//        HeartAnimation heartAnimation = new HeartAnimation();
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//
//            return true;
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            likedBy.setVisibility(View.VISIBLE);
//            toggleLike(Like_outline, LikeButton);
//            return true;
//        }
//    }
}

    /*
    Destroying exoplayer on activity destroy
     */
