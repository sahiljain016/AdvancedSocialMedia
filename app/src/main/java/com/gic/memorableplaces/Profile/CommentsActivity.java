package com.gic.memorableplaces.Profile;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.Adapters.CommentsListAdapter;
import com.gic.memorableplaces.DataModels.Comments;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.HeartAnimation;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {
    private static final String TAG = "CommentsFragment";

    private ImageView mBackArrow;
    private CircleImageView mProfile_photo_current_user;
    private TextView mPostComment;
    private EditText mComment;
    private TextView CommentLikes;
    public ImageView white_like;
    private ImageView red_like;

    public String mediaID, userID, UserNodeID, NodeID, Profile_photo, username, Date_added, LikeID,CurrentUsername,CurrentProfilePhoto;
    private int size;
    public static boolean isCaption = false;
    private boolean CaptionAdded;
    private boolean Photo;
    private Context mContext;
    private Intent intent;
    public static String fontColor, font, Caption;
    private ArrayList<Comments> mCommentsList;
    private ArrayList<String> LikedCommentIDList;

    private HashMap<String, ArrayList<String>> CommentsHashMap;
    private ListView CommentsList;
    private CommentsListAdapter commentsListAdapter;

    //Firebase
    public FirebaseAuth mAuth;
    public DatabaseReference myRef;
    public FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_comments);
        mContext = CommentsActivity.this;
        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);


        mBackArrow = findViewById(R.id.ic_back_arrow);
        mProfile_photo_current_user = findViewById(R.id.UserProfileComments);
        mPostComment = findViewById(R.id.IV_SEND_MESSAGE_CHAT);
        mComment = findViewById(R.id.ET_TYPE_MESSAGE_CHAT);
        CommentsList = findViewById(R.id.CommentsListView);
        mCommentsList = new ArrayList<>();
        LikedCommentIDList = new ArrayList<>();
        CommentsHashMap = new HashMap<>();

        intent = getIntent();

        if (intent  != null) {
            mediaID = intent.getStringExtra(getString(R.string.field_mediaID));
            userID = intent.getStringExtra(getString(R.string.field_user_id));
            UserNodeID =intent.getStringExtra("UserNodeID");
            NodeID = intent.getStringExtra("NodeID");
            Profile_photo = intent.getStringExtra(getString(R.string.field_profile_photo));
            username = intent.getStringExtra(getString(R.string.field_username));
            Caption = intent.getStringExtra(getString(R.string.field_caption));
            Date_added = intent.getStringExtra(getString(R.string.field_date_added));
            fontColor =  intent.getStringExtra(getString(R.string.field_color_caption));
            font = intent.getStringExtra(getString(R.string.field_font));
            Photo = intent.getBooleanExtra("isPhoto",true);
            CurrentUsername =intent.getStringExtra("CurrentUsername");
            CurrentProfilePhoto =intent.getStringExtra("CurrentUserProfilePhoto");

        }

        isCaption = Caption != null && !Caption.isEmpty();
         Log.d(TAG, "onCreateView:Photo " + Photo);
        Log.d(TAG, "onCreateView: CurrentUsername " + CurrentUsername);

        SetUpComments();
        UniversalImageLoader.setImage(CurrentProfilePhoto, mProfile_photo_current_user, null, "");

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();//  requireNonNull(getFragmentManager()).beginTransaction().remove(CommentsActivity.this).commit();
            }
        });


        CommentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                white_like = CommentsList.getChildAt(position).findViewById(R.id.comment_like_image_view);
                red_like = CommentsList.getChildAt(position).findViewById(R.id.CommentLike);
                CommentLikes = CommentsList.getChildAt(position).findViewById(R.id.comment_likes);
                setOnClickLikes(mCommentsList.get(position).getCommentID(), position);
                // Log.d(TAG, "onItemClick: position " + position);
                // Log.d(TAG, "onItemClick: CommentID " + mCommentsList.get(position).getCommentID());
                if (Photo) {
                    SetUpLike(getString(R.string.dbname_user_photos), mCommentsList.get(position).getCommentID(), position);
                } else {
                    SetUpLike(getString(R.string.dbname_user_videos), mCommentsList.get(position).getCommentID(), position);
                }
            }
        });

        Log.d(TAG, String.format("onCreateView: mCommentsList %s", mCommentsList));
        commentsListAdapter = new CommentsListAdapter(mContext, R.layout.layout_comment
                , mCommentsList, isCaption);
        CommentsList.setAdapter(commentsListAdapter);

        DeleteComments();
        Handler handler = new Handler((Looper.getMainLooper()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCreateView: size " + size);
                for (int i = 0; i < size; i++) {
                    Log.d(TAG, "onCreateView: for loop " + i);
                    if (Photo) {
                        SetUpLike(getString(R.string.dbname_user_photos), mCommentsList.get(i).getCommentID(), i);
                    } else {
                        SetUpLike(getString(R.string.dbname_user_videos), mCommentsList.get(i).getCommentID(), i);
                    }
                }
            }
        }, 700);
    }



    private void SetUpComments() {
        mPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mComment.getText().toString().equals("")) {
                    DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                    final String Comment = mComment.getText().toString();
                    if (Photo) {

                        offsetRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                long offset = snapshot.getValue(Long.class);
                                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(estimatedServerTimeMs);
                                String TimeStamp = formatter.format(calendar.getTime());
                                mCommentsList.add(mFirebaseMethods.addNewComment(Comment, CurrentUsername,
                                        CurrentProfilePhoto, getString(R.string.dbname_photos),
                                        getString(R.string.dbname_user_photos), mediaID,userID,TimeStamp));
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                System.err.println("Listener was cancelled");
                            }
                        });

                    } else {
                        offsetRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                long offset = snapshot.getValue(Long.class);
                                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(estimatedServerTimeMs);
                                String TimeStamp = formatter.format(calendar.getTime());
                                mCommentsList.add(mFirebaseMethods.addNewComment(Comment, CurrentUsername,
                                        CurrentProfilePhoto, getString(R.string.dbname_videos),
                                        getString(R.string.dbname_user_videos), mediaID,userID,TimeStamp));
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                System.err.println("Listener was cancelled");
                            }
                        });

                    }

                    mComment.setText("");
                    CloseKeyboard();
                } else {
                    mPostComment.setEnabled(false);
                    mPostComment.setTextColor(Color.parseColor("#7EC4DF"));
                }

            }
        });

        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPostComment.setEnabled(true);
                mPostComment.setTextColor(Color.parseColor("#FF04BAFD"));
                mComment.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (Photo) {
            getAllComments(getString(R.string.dbname_user_photos));
        } else {
            getAllComments(getString(R.string.dbname_user_videos));
        }
    }

    private void CloseKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            im.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    private void getAllComments(String UserNode) {

        Query query = myRef.child(UserNode)
                .child(userID)
                .child(mediaID)
                .child(getString(R.string.field_comments));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (Caption != null && !Caption.isEmpty() && !CaptionAdded) {
                    Comments firstComment = new Comments();
                    firstComment.setComment(Caption);
                    firstComment.setUser_id(userID);
                    firstComment.setProfile_photo(Profile_photo);
                    firstComment.setUsername(username);
                    firstComment.setDate_posted(Date_added);

                    mCommentsList.add(0, firstComment);
                    isCaption = true;
                    CaptionAdded = true;

                    size = 1;
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Comments comments = dataSnapshot.getValue(Comments.class);
                    requireNonNull(comments).getComment();
                    //comments.getLikes();
                    comments.getDate_posted();
                    comments.getProfile_photo();
                    comments.getUser_id();
                    comments.getUsername();
                    comments.getCommentID();
                    //Log.d(TAG, "onDataChange: boolean " + !mCommentsList.contains(comments));
                    //Log.d(TAG, "onDataChange: comments " + comments);
                    if (!mCommentsList.toString().contains(comments.toString())) {
                        // Log.d(TAG, "onDataChange: mCommentsList " + mCommentsList);
                        size = size + 1;
                        mCommentsList.add(comments);
                    }
                }
                commentsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void DeleteComments() {
        CommentsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                if (!isCaption) {
                    if (mCommentsList.get(position).getUser_id().equals(requireNonNull(mAuth.getCurrentUser()).getUid())) {

                        new AlertDialog.Builder(mContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Deleting Comment!")
                                .setMessage("Do you want this comment to be erased?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (Photo)
                                            mFirebaseMethods.RemoveComment(getString(R.string.dbname_photos), getString(R.string.dbname_user_photos)
                                                    , userID, mCommentsList.get(position).getCommentID(), mediaID);
                                        else
                                            mFirebaseMethods.RemoveComment(getString(R.string.dbname_videos),getString(R.string.dbname_user_videos)
                                                    , userID, mCommentsList.get(position).getCommentID(), mediaID);

                                        mCommentsList.remove(position);
                                        commentsListAdapter.notifyDataSetChanged();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                } else {
                    if (position != 0) {
                        if (mCommentsList.get(position).getUser_id().equals(requireNonNull(mAuth.getCurrentUser()).getUid())) {

                            new AlertDialog.Builder(mContext)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Deleting Comment!")
                                    .setMessage("Do you want this comment to be erased?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (Photo)
                                                mFirebaseMethods.RemoveComment(getString(R.string.dbname_photos), getString(R.string.dbname_user_photos)
                                                        , userID, mCommentsList.get(position).getCommentID(), mediaID);
                                            else
                                                mFirebaseMethods.RemoveComment(getString(R.string.dbname_videos), getString(R.string.dbname_user_videos)
                                                        , userID, mCommentsList.get(position).getCommentID(), mediaID);

                                            mCommentsList.remove(position);
                                            commentsListAdapter.notifyDataSetChanged();
                                        }

                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    }
                }

                return true;
            }
        });
    }

    public void setOnClickLikes(final String CommentID, final int position) {
        white_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCommentLike(CommentID, position);
                CommentLikes.setVisibility(View.VISIBLE);

            }
        });

        red_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCommentLike(CommentID, position);
                CommentLikes.setVisibility(View.VISIBLE);
            }
        });
    }

    public void toggleCommentLike(String CommentID, int position) {

        HeartAnimation heartAnimation = new HeartAnimation();
        // Log.d(TAG, String.format("toggleCommentLike: start %s", CommentsHashMap));
        if (Photo) {


            if (CommentsHashMap.isEmpty() && !red_like.isShown()) {
                // Log.d(TAG, "toggleLikeadd: CommentID " + CommentID);
                white_like.setVisibility(View.INVISIBLE);
                red_like.setVisibility(View.VISIBLE);
                // heartAnimation.toggleLike(white_like, red_like);
                mFirebaseMethods.addCommentLike(getString(R.string.dbname_photos), userID
                        , getString(R.string.dbname_user_photos), mediaID, CommentID);
                LikedCommentIDList.add(CommentID);
                CommentsHashMap.put(requireNonNull(mAuth.getCurrentUser()).getUid(), LikedCommentIDList);
            } else {
                if (!red_like.isShown() && !requireNonNull(CommentsHashMap.get(mAuth.getCurrentUser().getUid())).contains(CommentID)) {
                    // Log.d(TAG, "toggleLikeadd: CommentID " + CommentID);
                    white_like.setVisibility(View.INVISIBLE);
                    red_like.setVisibility(View.VISIBLE);
                    //  heartAnimation.toggleLike(white_like, red_like);
                    mFirebaseMethods.addCommentLike(getString(R.string.dbname_photos), userID
                            , getString(R.string.dbname_user_photos), mediaID, CommentID);
                    LikedCommentIDList.add(CommentID);
                    CommentsHashMap.put(requireNonNull(mAuth.getCurrentUser()).getUid(), LikedCommentIDList);
                } else {
                    // Log.d(TAG, "toggleLikeremove: CommentID " + CommentID);
                    white_like.setVisibility(View.VISIBLE);
                    red_like.setVisibility(View.INVISIBLE);
                    //   heartAnimation.toggleLike(white_like, red_like);
                    Log.d(TAG, "toggleCommentLike: userID " + userID);
                    mFirebaseMethods.removeCommentLike(getString(R.string.dbname_photos), userID,
                            getString(R.string.dbname_user_photos), mediaID, CommentID, LikeID);
                    LikedCommentIDList.remove(CommentID);

                    SetUpLike(getString(R.string.dbname_user_photos), mCommentsList.get(position).getCommentID(), position);

                    if (!CommentsHashMap.isEmpty() && requireNonNull(CommentsHashMap.get(mAuth.getCurrentUser().getUid())).size() == 0) {
                        CommentsHashMap.remove(mAuth.getCurrentUser().getUid());
                    }
                }


            }

            // Log.d(TAG, String.format("toggleCommentLike: end %s", CommentsHashMap));

        } else {
            if (CommentsHashMap.isEmpty() && !red_like.isShown()) {
                // Log.d(TAG, "toggleLikeadd: CommentID " + CommentID);
                white_like.setVisibility(View.INVISIBLE);
                red_like.setVisibility(View.VISIBLE);
                // heartAnimation.toggleLike(white_like, red_like);
                mFirebaseMethods.addCommentLike(getString(R.string.dbname_videos), userID
                        , getString(R.string.dbname_user_videos), mediaID, CommentID);
                LikedCommentIDList.add(CommentID);
                CommentsHashMap.put(requireNonNull(mAuth.getCurrentUser()).getUid(), LikedCommentIDList);
            } else {
                if (!red_like.isShown() && !requireNonNull(CommentsHashMap.get(mAuth.getCurrentUser().getUid())).contains(CommentID)) {
                    // Log.d(TAG, "toggleLikeadd: CommentID " + CommentID);
                    white_like.setVisibility(View.INVISIBLE);
                    red_like.setVisibility(View.VISIBLE);
                    //  heartAnimation.toggleLike(white_like, red_like);
                    mFirebaseMethods.addCommentLike(getString(R.string.dbname_videos), userID
                            , getString(R.string.dbname_user_videos), mediaID, CommentID);
                    LikedCommentIDList.add(CommentID);
                    CommentsHashMap.put(requireNonNull(mAuth.getCurrentUser()).getUid(), LikedCommentIDList);
                } else {
                    // Log.d(TAG, "toggleLikeremove: CommentID " + CommentID);
                    white_like.setVisibility(View.VISIBLE);
                    red_like.setVisibility(View.INVISIBLE);
                    //   heartAnimation.toggleLike(white_like, red_like);

                    mFirebaseMethods.removeCommentLike(getString(R.string.dbname_videos), userID,
                    getString(R.string.dbname_user_videos), mediaID, CommentID, LikeID);
                    LikedCommentIDList.remove(CommentID);

                    SetUpLike(getString(R.string.dbname_user_videos), mCommentsList.get(position).getCommentID(), position);

                    if (!CommentsHashMap.isEmpty() && requireNonNull(CommentsHashMap.get(mAuth.getCurrentUser().getUid())).size() == 0) {
                        CommentsHashMap.remove(mAuth.getCurrentUser().getUid());
                    }
                }


            }

        }
    }

    private void SetUpLike(final String UserNode, final String CommentID, int position) {
        final ImageView White_Like = CommentsList.getChildAt(position).findViewById(R.id.comment_like_image_view);
        final ImageView Red_Like = CommentsList.getChildAt(position).findViewById(R.id.CommentLike);
        final TextView LikesComment = CommentsList.getChildAt(position).findViewById(R.id.comment_likes);
        if (CommentID != null) {
            Query query = myRef.child(UserNode)
                    .child(userID)
                    .child(mediaID)
                    .child(getString(R.string.field_comments))
                    .child(CommentID)
                    .child(getString(R.string.field_likes));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                            // Log.d(TAG, String.format("onDataChange: snapshot %s", snapshot.getChildren().toString()));
                            //Log.d(TAG, String.format("onDataChange: single snapshot %s", singleSnapshot.getChildren().toString()));
                            int NumberOfLikes;
                            if (snapshot.getChildrenCount() > 0) {
                                NumberOfLikes = (int) snapshot.getChildrenCount();
                            } else {
                                NumberOfLikes = 0;
                            }
                            // Log.d(TAG, "onDataChange: NumberOfLikes " + NumberOfLikes);
                            //Log.d(TAG, "onDataChange: childrencount " + snapshot.getChildrenCount());

                            //Log.d(TAG, "getView: NOOFLikes for loop" + NumberOfLikes);
                            LikesComment.setText(String.valueOf(NumberOfLikes + " likes"));


                            //user liked
                            LikeID = singleSnapshot.getKey();
                            if (requireNonNull(singleSnapshot.child(getString(R.string.field_user_id)).getValue())
                                    .equals(requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                Log.d(TAG, "onDataChange: LikeID " + singleSnapshot.getKey());

                                 Red_Like.setVisibility(View.VISIBLE);
                                White_Like.setVisibility(View.INVISIBLE);

                            }

                            //user not like

                        }
                    } else {
                        LikesComment.setText(String.valueOf(0 + " likes"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    public static int setCaptionColor(String ColorFont) {

        if (ColorFont != null && !ColorFont.isEmpty()) {

            return Integer.parseInt(ColorFont);
        } else {

            return Color.WHITE;

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
