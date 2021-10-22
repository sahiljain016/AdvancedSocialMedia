package com.gic.memorableplaces.utils;

import static java.util.Objects.requireNonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gic.memorableplaces.CustomLibs.LoadingViewLib.LVGhost;
import com.gic.memorableplaces.CustomLibs.TransitionButton.TransitionButton;
import com.gic.memorableplaces.DataModels.AllUserSettings;
import com.gic.memorableplaces.DataModels.Chat;
import com.gic.memorableplaces.DataModels.Comments;
import com.gic.memorableplaces.DataModels.MessagePreview;
import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.DataModels.UserCommonDetails;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.Dialogs.CustomDialog;
import com.gic.memorableplaces.Home.HomeActivity;
import com.gic.memorableplaces.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.virgilsecurity.common.callback.OnResultListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.grantland.widget.AutofitHelper;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    private int IMAGE_QUALITY = 80;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private String userID;
    private long PostCount, FollowerCount, FollowingCount;
    private CustomDialog CustomDialog;
    private TextView uploadProgressTV;
    private Context mContext;

    /**
     * @param context
     */
    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mContext = context;
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Method to send a group message
     *
     * @param GroupID     Unique ID of the Group
     * @param BubbleColor Bubble color set by User
     * @param message     text of the message sent to the group
     * @param ReceiverIDs Array List of Unique UserIDs of receivers
     * @param isGradient
     */

    public void SendGroupMessage(final String GroupID, final String BubbleColor, final String message,
                                 final ArrayList<String> ReceiverIDs, final boolean isGradient) {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(estimatedServerTimeMs);
                String TimeStamp = formatter.format(calendar.getTime());
                Chat chat = new Chat();
                chat.setBubbleColor(BubbleColor);
                chat.setString_date(TimeStamp);
                chat.setMessage(message);
                chat.setGradient(isGradient);
                chat.setSender(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                String NewMessageID = myRef.push().getKey();

                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chats))
                        .child(Objects.requireNonNull(NewMessageID))
                        .setValue(chat);

                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chat_details))
                        .child("latest_message_ID")
                        .setValue(NewMessageID);
                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chat_details))
                        .child("date_messaged")
                        .setValue(TimeStamp);
                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chat_details))
                        .child("latest_message")
                        .setValue(message);
                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chats))
                        .child(NewMessageID)
                        .child("isSeen")
                        .setValue("true");
                myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(GroupID)
                        .child(mContext.getString(R.string.field_chat_details))
                        .child("isSeen")
                        .setValue("true");

                Log.d(TAG, String.format("SendGroupMessage: Receiver's List %s", ReceiverIDs));
                for (int i = 0; i < ReceiverIDs.size(); i++) {
                    ReceiverIDs.remove(mAuth.getCurrentUser().getUid());
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chats))
                            .child(Objects.requireNonNull(NewMessageID))
                            .setValue(chat);
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chat_details))
                            .child("date_messaged")
                            .setValue(TimeStamp);
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chat_details))
                            .child("latest_message")
                            .setValue(message);
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chat_details))
                            .child("latest_message_ID")
                            .setValue(NewMessageID);
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chats))
                            .child(NewMessageID)
                            .child("isSeen")
                            .setValue("false");
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(ReceiverIDs.get(i))
                            .child(GroupID)
                            .child(mContext.getString(R.string.field_chat_details))
                            .child("isSeen")
                            .setValue("false");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }

    /**
     * @param chat
     * @param messagePreview
     */
    public void SendMessage(final Chat chat, final MessagePreview messagePreview) {

//        Map<String, Object> chat_map = new HashMap<>();
//
////        myRef.child(mContext.getString(R.string.dbname_chats))
////                .child(ChatUID)
////                .child(messagePreview.getLatest_message_id())
////                .setValue(chat);
//        chat_map.put(messagePreview.getLates  t_message_id(), chat);
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ChatUID)
//                .child(messagePreview.getLatest_message_id())
//                .child(mContext.getString(R.string.field_date_messaged))
//                .setValue(ServerValue.TIMESTAMP);




        /*myRef.child(mContext.getString(R.string.dbname_chats))
                .child(userID)
                .child(chat.getReceivers())
                .child(messagePreview.getLatest_message_id())
                .setValue(chat);

        myRef.child(mContext.getString(R.string.dbname_chats))
                .child(chat.getReceivers())
                .child(userID)
                .child(messagePreview.getLatest_message_id())
                .setValue(chat);*/

        Query query = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                .child(chat.getReceivers())
                .child(messagePreview.getChat_uid())
                .child(mContext.getString(R.string.field_pending_unseen_messages));


        final boolean[] isFirst = {true};
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> chat_meta_data = new HashMap<>();
                if (isFirst[0]) {
                    Log.d(TAG, "onDataChange: first message snapshot; " + snapshot);
                    if (snapshot.exists()) {
                        messagePreview.setPending_unseen_messages((snapshot.getValue(Long.class) + 1));
                    } else {
                        messagePreview.setPending_unseen_messages(1);
                    }
                    myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                            .child(chat.getReceivers())
                            .child(messagePreview.getChat_uid())
                            .setValue(messagePreview);

                    messagePreview.setPending_unseen_messages(0);
                    messagePreview.setOther_user_uid(chat.getReceivers());
                    myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                            .child(userID)
                            .child(messagePreview.getChat_uid())
                            .setValue(messagePreview);


                }
                isFirst[0] = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Chat chat = new Chat();
//        chat.setBubbleColor(BubbleColor);
//        chat.setDate_messaged(TimeStamp);
//        chat.setIsSeen(isSeen);
//        chat.setMessage(message);
//        chat.setGradient(isGradient);
//        chat.setReceivers(ReceiverUserID);
//        chat.setSender(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

//        chat.setIsSeen("true");
//        chat_map.put(("/" + userID
//                + "/" + ReceiverUserID
//                + "/" + mContext.getString(R.string.field_chats))
//                + "/" + MessageID
//                + "/", chat);
//
//        chat.setIsSeen("false");
//        chat_map.put(("/" + ReceiverUserID
//                + "/" + userID
//                + "/" + mContext.getString(R.string.field_chats))
//                + "/" + MessageID
//                + "/", chat);
//


//         myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_date_messaged))
//                .setValue(TimeStamp);
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_message))
//                .setValue(message);
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(mAuth.getCurrentUser().getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_message_id))
//                .setValue(MessageID);
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_date_messaged))
//                .setValue(TimeStamp);
//
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_sender_id))
//                .setValue(userID);
//
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(mAuth.getCurrentUser().getUid())
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_sender_id))
//                .setValue(userID);
//
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_message))
//                .setValue(message);
//
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(mAuth.getCurrentUser().getUid())
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_latest_message_id))
//                .setValue(MessageID);
//
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(mAuth.getCurrentUser().getUid())
//                .child(mContext.getString(R.string.field_chat_details))
//                .child(mContext.getString(R.string.field_is_seen))
//                .setValue("false");
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(ReceiverUserID)
//                .child(mAuth.getCurrentUser().getUid())
//                .child(mContext.getString(R.string.field_chats))
//                .child(MessageID)
//                .child("isSeen")
//                .setValue("false");
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(mAuth.getCurrentUser().getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chat_details))
//                .child("isSeen")
//                .setValue("true");
//        myRef.child(mContext.getString(R.string.dbname_chats))
//                .child(mAuth.getCurrentUser().getUid())
//                .child(ReceiverUserID)
//                .child(mContext.getString(R.string.field_chats))
//                .child(MessageID)
//                .child("isSeen")
//                .setValue("true");
    }


    public void CheckIfFollowing(String TargetUID, Runnable Exists, Runnable DoesNotExists, Runnable MainRunnable) {
        Query query = myRef.child(mContext.getString(R.string.dbname_following))
                .child(userID)
                .child(TargetUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (Exists != null)
                        Exists.run();
                } else {
                    if (DoesNotExists != null)
                        DoesNotExists.run();
                }
                MainRunnable.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Adding Following and follower
     *
     * @param UID UserID of user who has been followed
     */
    public void addNewFollowerAndFollowing(String UID) {

        myRef.child(mContext.getString(R.string.dbname_followers))
                .child(UID)
                .child(userID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(userID);

        myRef.child(mContext.getString(R.string.dbname_following))
                .child(userID)
                .child(UID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(UID);
    }

    /**
     * @param UID
     * @param username
     * @param MyUsername
     * @param MyPPLink
     * @param filters_matched
     * @param ProfilePhotoLink
     */
    public void addFollowOrMessageToFilterList(String UID, String username, String MyUsername, String MyPPLink,
                                               String filters_matched, String ProfilePhotoLink, String AnimationType,
                                               String MessageOrNotification, String Title) {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(estimatedServerTimeMs);
                String TimeStamp = formatter.format(calendar.getTime());

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .child(mContext.getString(R.string.field_date_added))
                        .setValue(TimeStamp);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .child(mContext.getString(R.string.field_date_added))
                        .setValue(TimeStamp);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .child(mContext.getString(R.string.field_username))
                        .setValue(username);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .child(Title)
                        .setValue(filters_matched);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .child(mContext.getString(R.string.field_profile_photo))
                        .setValue(ProfilePhotoLink);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .child("isSeen")
                        .setValue(false);
                if (MessageOrNotification.equals(mContext.getString(R.string.field_message_list_for_notifications))) {
                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(MessageOrNotification)
                            .child(userID)
                            .child(mContext.getString(R.string.field_sent))
                            .child(UID)
                            .child(mContext.getString(R.string.field_animation_type_filter_message))
                            .setValue(AnimationType);

                    myRef.child(mContext.getString(R.string.dbname_filter_data))
                            .child(MessageOrNotification)
                            .child(UID)
                            .child(mContext.getString(R.string.field_received))
                            .child(userID)
                            .child(mContext.getString(R.string.field_animation_type_filter_message))
                            .setValue(AnimationType);
                }
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .child(Title)
                        .setValue(filters_matched);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .child("isSeen")
                        .setValue(false);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .child(mContext.getString(R.string.field_username))
                        .setValue(MyUsername);

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification)
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .child(mContext.getString(R.string.field_profile_photo))
                        .setValue(MyPPLink);

                Query query = myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(MessageOrNotification);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        long sent_count = 0, received_count = 0;
                        if (snapshot.child(userID).child(mContext.getString(R.string.field_sent_count)).exists()) {
                            sent_count = (long) snapshot.child(userID).child(mContext.getString(R.string.field_sent_count)).getValue();
                        }
                        if (snapshot.child(UID).child(mContext.getString(R.string.field_received_count)).exists()) {
                            received_count = (long) snapshot.child(UID).child(mContext.getString(R.string.field_received_count)).getValue();
                        }
                        sent_count++;
                        received_count++;

                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(MessageOrNotification)
                                .child(userID)
                                .child(mContext.getString(R.string.field_sent_count))
                                .setValue(sent_count);

                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(MessageOrNotification)
                                .child(UID)
                                .child(mContext.getString(R.string.field_received_count))
                                .setValue(received_count);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


    }


    public void DeleteFollowFromFilterList(String UID) {


        Query query = myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(mContext.getString(R.string.field_follow_list_for_notifications));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child(userID).child(mContext.getString(R.string.field_sent)).child(UID).child("isSeen").getValue().toString().equals("false")) {
                    long sent_count = (long) snapshot.child(userID).child(mContext.getString(R.string.field_sent_count)).getValue();
                    if (sent_count > 0) {
                        sent_count--;
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_follow_list_for_notifications))
                                .child(userID)
                                .child(mContext.getString(R.string.field_sent_count))
                                .setValue(sent_count);
                    }
                }
                if (snapshot.child(UID).child(mContext.getString(R.string.field_received)).child(userID).child("isSeen").getValue().toString().equals("false")) {
                    long received_count = (long) snapshot.child(UID).child(mContext.getString(R.string.field_received_count)).getValue();
                    if (received_count > 0) {
                        received_count--;
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_follow_list_for_notifications))
                                .child(UID)
                                .child(mContext.getString(R.string.field_received_count))
                                .setValue(received_count);
                    }
                }
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(mContext.getString(R.string.field_follow_list_for_notifications))
                        .child(userID)
                        .child(mContext.getString(R.string.field_sent))
                        .child(UID)
                        .removeValue();

                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(mContext.getString(R.string.field_follow_list_for_notifications))
                        .child(UID)
                        .child(mContext.getString(R.string.field_received))
                        .child(userID)
                        .removeValue();
                //   Log.d(TAG, "onDataChange: exists sent " + snapshot.child(UID).child(mContext.getString(R.string.field_sent)).exists());
                // Log.d(TAG, "onDataChange: exists received " + snapshot.child(UID).child(mContext.getString(R.string.field_received)).exists());


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {


            Query query1 = myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(mContext.getString(R.string.field_follow_list_for_notifications));

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: exists sent " + snapshot.child(UID).child(mContext.getString(R.string.field_sent)).exists());
                    Log.d(TAG, "onDataChange: exists received " + snapshot.child(UID).child(mContext.getString(R.string.field_received)).exists());
                    if (!snapshot.child(userID).child(mContext.getString(R.string.field_received)).exists()
                            && !snapshot.child(userID).child(mContext.getString(R.string.field_sent)).exists()) {
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_follow_list_for_notifications))
                                .child(userID)
                                .removeValue();
                    }
                    if (!snapshot.child(UID).child(mContext.getString(R.string.field_received)).exists()
                            && !snapshot.child(UID).child(mContext.getString(R.string.field_sent)).exists()) {
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_follow_list_for_notifications))
                                .child(UID)
                                .removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }, 2000);

    }

    /**
     * Removing Following and follower
     *
     * @param UID UserID of user who has been unfollowed
     */
    public void removeFollowerAndFollowing(String UID) {

        myRef.child(mContext.getString(R.string.dbname_followers))
                .child(UID)
                .child(userID)
                .removeValue();

        myRef.child(mContext.getString(R.string.dbname_following))
                .child(userID)
                .child(UID)
                .removeValue();

    }

    public void SetOnlineStatus(boolean isOnline) {
        if (userID != null) {
            if (isOnline) {
                myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(userID)
                        .child(mContext.getString(R.string.field_is_online))
                        .setValue(true);
            } else {
                myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(userID)
                        .child(mContext.getString(R.string.field_is_online))
                        .setValue(false);
            }

        }

    }
 /*
    ----------------------------------------------------------Comments METHODS-----------------------------------------------------
     */

    /**
     * Adding new comment to database & returning comments object
     *
     * @param comment       Text of the comment made
     * @param username      Username of user who made the comment
     * @param profile_photo profile_photo url of user who made the comment
     * @param Node          global Photos/Videos Node
     * @param UserNode      Specific user's Photos/Videos Node
     * @param mediaID       Photo/Video Unique ID
     * @param UserID        Commented By UserID
     * @return returning the comment object
     */
    public Comments addNewComment(String comment, String username, String profile_photo, String Node,
                                  String UserNode, String mediaID, String UserID, String TimeStamp) {
        Log.d(TAG, "addNewComment: comment " + comment);

        String CommentID = myRef.push().getKey();

        Comments comments = new Comments();
        comments.setDate_posted(TimeStamp);
        comments.setUsername(username);
        comments.setProfile_photo(profile_photo);
        comments.setUser_id(userID);
        comments.setComment(comment);
        comments.setCommentID(CommentID);

        myRef.child(Node)
                .child(mediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(Objects.requireNonNull(CommentID))
                .setValue(comments);
        myRef.child(UserNode)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(Objects.requireNonNull(CommentID))
                .setValue(comments);

        return comments;
    }

    /**
     * Method to remove comment from database
     *
     * @param Node      global Photos/Videos Node
     * @param UserNode  Specific user's Photos/Videos Node
     * @param UserID    Commented By UserID
     * @param CommentID Comment Unique ID
     * @param mediaID   Photo/Video Unique ID
     */

    public void RemoveComment(String Node, String UserNode, String UserID, String CommentID, String mediaID) {

        myRef.child(Node)
                .child(mediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .removeValue();

        myRef.child(UserNode)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .removeValue();
    }

    /**
     * Method to add A new Like To a Comment
     *
     * @param Node      global Photos/Videos Node
     * @param userID    Liked By UserID
     * @param UserNode  Specific user's Photos/Videos Node
     * @param MediaID   Photo/Video Unique ID
     * @param CommentID Comment Unique ID
     */
    public void addCommentLike(String Node, String userID, String UserNode, String MediaID, String CommentID) {
        String NewLikeID = myRef.push().getKey();
        assert NewLikeID != null;
        myRef.child(Node)
                .child(MediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .child(mContext.getString(R.string.field_likes))
                .child(NewLikeID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(mAuth.getCurrentUser().getUid());

        myRef.child(UserNode)
                .child(userID)
                .child(MediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .child(mContext.getString(R.string.field_likes))
                .child(NewLikeID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(mAuth.getCurrentUser().getUid());
    }

    /**
     * Method to remove like from a comment in the firebase database
     *
     * @param Node      global Photos/Videos Node
     * @param userID    Liked By UserID
     * @param UserNode  Specific user's Photos/Videos Node
     * @param MediaID   Photo/Video Unique ID
     * @param CommentID Comment Unique ID
     * @param LikesID   Like Unique ID
     */

    public void removeCommentLike(String Node, String userID, String UserNode, String MediaID, String CommentID, String LikesID) {

        myRef.child(Node)
                .child(MediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .child(mContext.getString(R.string.field_likes))
                .child(LikesID)
                .removeValue();

        myRef.child(UserNode)
                .child(userID)
                .child(MediaID)
                .child(mContext.getString(R.string.field_comments))
                .child(CommentID)
                .child(mContext.getString(R.string.field_likes))
                .child(LikesID)
                .removeValue();
    }
    /*
    ----------------------------------------------------------Likes METHODS-----------------------------------------------------
     */

    /**
     * Adding new like to database into both nodes photos & user_photos
     *
     * @param nodeMedia
     * @param nodeUserMedia
     * @param mediaID
     * @param UserID
     */
    public void addNewLike(String nodeMedia, String nodeUserMedia, String mediaID, String UserID, String ProfileUrl, String Username) {
        String newLikeID = myRef.push().getKey();
//        Likes likes = new Likes();
//        likes.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        assert newLikeID != null;
        myRef.child(nodeMedia)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(userID);

        myRef.child(nodeUserMedia)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_user_id))
                .setValue(userID);
        myRef.child(nodeUserMedia)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue(ProfileUrl);
        myRef.child(nodeMedia)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue(ProfileUrl);
        myRef.child(nodeUserMedia)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_username))
                .setValue(Username);
        myRef.child(nodeMedia)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .child(mContext.getString(R.string.field_username))
                .setValue(Username);

    }


    public void removeLike(String nodeMedia, String nodeUserMedia, String mediaID, String LikesID, String UserID) {

        myRef.child(nodeMedia)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(LikesID)
                .removeValue();

        myRef.child(nodeUserMedia)
                .child(UserID)
                .child(mediaID)
                .child(mContext.getString(R.string.field_likes))
                .child(LikesID)
                .removeValue();

    }
/*
-----------------------------------------STORAGE METHODS STARTED------------------------------------------------------------
 */

    /**
     * method to get the number of images posted by the user.
     *
     * @param dataSnapshot
     * @return
     */
    public int getImageCount(DataSnapshot dataSnapshot) {
        int Icount = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .getChildren()) {
            Icount++;
        }
        return Icount;
    }

    /**
     * method to get tje count of videos posted by the user
     *
     * @param dataSnapshot
     * @return
     */
    public int getVideoCount(DataSnapshot dataSnapshot) {
        int Vcount = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_videos))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .getChildren()) {
            Vcount++;
        }
        return Vcount;
    }

    /**
     * uploading new photo to storage & database
     *
     * @param photoType
     * @param caption
     * @param scaleType
     * @param font
     * @param fontColor
     * @param count
     * @param imageUrl
     */
    public void UploadNewPhoto(String photoType, final String caption, final String scaleType, final String font,
                               final String fontColor, int count, String imageUrl, final Activity activity) {
        Log.d(TAG, "UploadNewPhoto: uploading new photo");

        final String FIREBASE_IMAGE_STORAGE = "photos/users/";
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FileCompressor fileCompressor = new FileCompressor(mContext);
        final StorageReference storageReference;
        CustomDialog = new CustomDialog(mContext, R.layout.dialog_upload_progress);
        CustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CustomDialog.show();
        uploadProgressTV = (CustomDialog).findViewById(R.id.UploadProgressTV);
        TextView dialogTitle = (CustomDialog).findViewById(R.id.dialogTitle);

        AutofitHelper.create(uploadProgressTV);
        //Upload new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {

            storageReference = mStorageRef.child(FIREBASE_IMAGE_STORAGE + user_id + "/photo" + (count + 1));
            UploadTask uploadTask = null;
            uploadTask = storageReference.putFile(Uri.fromFile(fileCompressor.compressImage(imageUrl, IMAGE_QUALITY)));

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Uri downloaduri = task.getResult();
                    addPhotoToDatabase(caption, font, scaleType, fontColor, Objects.requireNonNull(downloaduri).toString());
                    uploadProgressTV.setText("Photo Uploaded Successfully!");
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        CustomDialog.dismiss();
                        activity.finish();
                        mContext.startActivity(new Intent(mContext, HomeActivity.class));
                    }, 1000);
                } else {
                    uploadProgressTV.setText("Photo Upload Failed. :(");
                    CustomDialog.dismiss();
                }
            });

            uploadTask.addOnProgressListener(snapshot -> {
                int uploadProgress = (int) ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount());
                uploadProgressTV.setText(String.format("Upload Progress: %d%%", uploadProgress));
                RoundedProgressBar roundedHorizontalProgressBar =
                        (CustomDialog).findViewById(R.id.UploadProgressBar);
                roundedHorizontalProgressBar.setProgressPercentage(uploadProgress, true);
            });


        }
        //Upload new profile photo
        else if (photoType.equals(mContext.getString(R.string.new_profile_photo))) {
            Log.d(TAG, "UploadNewPhoto: uploading new profile photo");

            storageReference = mStorageRef.child(FIREBASE_IMAGE_STORAGE + user_id + "/profile_photo");
            UploadTask uploadTask = null;
            uploadTask = storageReference.putFile(Uri.fromFile(fileCompressor.compressImage(imageUrl, IMAGE_QUALITY)));
            dialogTitle.setText("Updating Profile Photo!");

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Uri downloaduri = task.getResult();
                    setUpProfilePhoto(Objects.requireNonNull(downloaduri).toString());
                    uploadProgressTV.setText("Profile Photo Changed!");
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CustomDialog.dismiss();
                            activity.finish();
                        }
                    }, 1000);
                } else {
                    uploadProgressTV.setText("Profile Photo Change Failed. :(");
                    CustomDialog.dismiss();
                }
            });

            uploadTask.addOnProgressListener(snapshot -> {
                int uploadProgress = (int) ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount());
                uploadProgressTV.setText(String.format("Upload Progress: %d%%", uploadProgress));
                RoundedProgressBar roundedHorizontalProgressBar =
                        (CustomDialog).findViewById(R.id.UploadProgressBar);
                roundedHorizontalProgressBar.setProgressPercentage(uploadProgress, true);
            });
        }
    }

    /**
     * adding new details of new photo to database
     *
     * @param caption
     * @param font
     * @param scaleType
     * @param fontColor
     * @param imgUrl
     */
    public void addPhotoToDatabase(final String caption, final String font, final String scaleType, final String fontColor, final String imgUrl) {
        Log.d(TAG, "addPhotoToDatabase: sending Data to firebase");
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
                String mPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
                Log.d(TAG, "addPhotoToDatabase: caption " + caption);
                String tags = "";
                if (caption.contains("SXC#")) {
                    tags = StringManipulation.getTags(caption);
                }
                Photo photo = new Photo();
                photo.setCaption(caption);
                photo.setTags(tags);
                photo.setDate_added(TimeStamp);
                photo.setFont(font);
                photo.setScaleType(scaleType);
                photo.setFont_color(fontColor);
                photo.setImageUrl(imgUrl);
                photo.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                photo.setPhoto_id(mPhotoKey);

                // insert data into firebase
                myRef.child(mContext.getString(R.string.dbname_user_photos))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(Objects.requireNonNull(mPhotoKey)).setValue(photo);
                myRef.child(mContext.getString(R.string.dbname_photos)).child(mPhotoKey).setValue(photo);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


    }

    private void setUpProfilePhoto(String url) {
        Log.d(TAG, "setUpProfilePhoto: profile photo " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue(url);
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue(url);
        myRef.child(mContext.getString(R.string.dbname_user_common_details))
                .child(userID)
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue(url);
    }

    /**
     * Get date added of post
     *
     * @return
     */
    public String getDateAdded() {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault());
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
//        DateFormat df = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss",Locale.ENGLISH);
//        //df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//
//        return sdf.format(new Date());
        final String[] TimeStamp = {""};
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(estimatedServerTimeMs);
                TimeStamp[0] = formatter.format(calendar.getTime());
                Log.d(TAG, String.format("onDataChange: time Stamp %s", (Object[]) TimeStamp));
//                System.out.println(formatter.format(calendar.getTime()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        Log.d(TAG, "getDateAdded: TimeStamp return" + TimeStamp[0]);
        return Arrays.toString(TimeStamp);
    }

    /**
     * storing & uploading new video to database & storage
     *
     * @param caption
     * @param font
     * @param fontColor
     * @param count
     * @param videoUrl
     */
    public void UploadNewVideo(final String caption, final String font, final String fontColor, int count, final String videoUrl
            , final Activity activity) {

        final String FIREBASE_VIDEO_STORAGE = "videos/users/";
        final StorageReference storageReference;
        final String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        storageReference = mStorageRef.child(FIREBASE_VIDEO_STORAGE + user_id + "/video" + (count + 1));
        CustomDialog = new CustomDialog(mContext, R.layout.dialog_upload_progress);
        CustomDialog.show();
        uploadProgressTV = (CustomDialog).findViewById(R.id.UploadProgressTV);
        UploadTask uploadTask = null;
        uploadTask = storageReference.putFile(Uri.fromFile(new File(videoUrl)));
        AutofitHelper.create(uploadProgressTV);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addVideoToDatabase(caption, font, fontColor, Objects.requireNonNull(downloadUri).toString());

                    uploadProgressTV.setText("Video Upload Success!");

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CustomDialog.dismiss();
                            activity.finish();
                            mContext.startActivity(new Intent(mContext, HomeActivity.class));
                        }
                    }, 1000);

                } else {
                    uploadProgressTV.setText("Video Upload failed. :(");
                    CustomDialog.dismiss();
                }


            }
        });

        uploadTask.addOnProgressListener(snapshot -> {
            int uploadProgress = (int) ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount());
            AutofitHelper.create(uploadProgressTV);
            uploadProgressTV.setText(String.format("Upload Progress: %d%%", uploadProgress));
            RoundedProgressBar roundedHorizontalProgressBar =
                    (CustomDialog).findViewById(R.id.UploadProgressBar);
            roundedHorizontalProgressBar.setProgressPercentage(uploadProgress, true);

        });
    }

    /**
     * Adding data related to new video to database
     *
     * @param caption
     * @param font
     * @param fontColor
     * @param videoUrl
     */
    public void addVideoToDatabase(final String caption, final String font, final String fontColor, final String videoUrl) {
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
                Log.d(TAG, "addPhotoToDatabase: sending Data to firebase");
                String tags = "";
                String mVideoKey = myRef.child(mContext.getString(R.string.dbname_videos)).push().getKey();
                if (caption.contains("SXC#")) {
                    tags = StringManipulation.getTags(caption);
                }
                Video video = new Video();
                video.setCaption(caption);
                video.setTags(tags);
                video.setDate_added(TimeStamp);
                video.setFont(font);
                video.setFont_color(fontColor);
                video.setVideoUrl(videoUrl);
                video.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                video.setVideo_id(mVideoKey);

                // insert data into firebase
                myRef.child(mContext.getString(R.string.dbname_user_videos))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mVideoKey).setValue(video);
                myRef.child(mContext.getString(R.string.dbname_videos)).child(mVideoKey).setValue(video);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


    }

    public void removePhotoFromDatabase(String Node, String UserNode, String UserID, String MediaID, String mediaURL) {
        myRef.child(Node)
                .child(MediaID)
                .removeValue();

        myRef.child(UserNode)
                .child(UserID)
                .child(MediaID)
                .removeValue();


        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = mFirebaseStorage.getReferenceFromUrl(mediaURL);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(mContext, "Post Deleted Successfully!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, "Post Delete Failed! :(", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void removeProfilePhoto(String PPUrl) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue("");

        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_profile_photo))
                .setValue("N/A");

        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = mFirebaseStorage.getReferenceFromUrl(PPUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(mContext, "Profile Picture Deleted Successfully!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, "Profile Picture Delete Failed! :(", Toast.LENGTH_SHORT).show();

            }
        });
    }


//    public boolean CheckIfUsernameAlreadyExists(String username, DataSnapshot dataSnapshot){
//        Log.d(TAG, "Checking to see if "+ username + " already exists in the database!");
//
//        User user = new User();
//
//        for(DataSnapshot ds:dataSnapshot.child(userID).getChildren()){
//Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND & MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//        }
    /*
    --------------------------------------------------STORAGE METHODS FINISHED/ REGISTERING DETAILS STARTED--------------------------
     */

    /**
     * Registering details of new user to firebase
     *
     * @param email
     * @param password
     * @param progressButton
     */
    public void registerNewUser(String email, String password, final String username, final Activity activity,
                                final TransitionButton progressButton, final OnResultListener<String> onResultListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        progressButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, null);
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        Toast.makeText(mContext, R.string.auth_success_singup,
                                Toast.LENGTH_LONG).show();


                        // SENDING VERIFICATION EMAIL
                        sendVerificationEmail(activity, username, onResultListener);
//                        final Handler handler = new Handler(Looper.getMainLooper());
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressButton.setBackgroundResource(R.drawable.custom_button_login);
//                                progressButton.revertAnimation();
//                            }
//                        }, 2000);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        progressButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        Toast.makeText(mContext, task.getException().toString().substring(task.getException().toString().indexOf(":") + 1),
                                Toast.LENGTH_LONG).show();

//                        final Handler handler = new Handler(Looper.getMainLooper());
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressButton.setBackgroundResource(R.drawable.custom_button_login);
//                                progressButton.revertAnimation();
//                            }
//                        }, 2000);

                    }

                    // ...
                });


    }

    public void sendVerificationEmail(final Activity activity, final String Username, final OnResultListener<String> onResultListener) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_confirm_email);
        final LVGhost ghost = dialog.findViewById(R.id.ghost_verify);
        final TextView Title = dialog.findViewById(R.id.title_missing_details);
        final TextView Desp = dialog.findViewById(R.id.desp_missing_items);
        final Button Positive = dialog.findViewById(R.id.positive);
        final Button Negative = dialog.findViewById(R.id.negative);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ghost.startAnim(1500);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: LOG 1");
                                Title.setText("Verification Email Sent!");
                                Desp.setText("Please check your email for a confirmation link.\nThis is to verify that the email belongs to you. :)");
                                Positive.setText("Confirmed. Let's Go!");
                                Negative.setText("I'll Do It Later...");
                                dialog.show();
                                ghost.setOnClickListener((View.OnClickListener) v -> requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {

                                                Toast.makeText(mContext, "Refreshed!", Toast.LENGTH_SHORT).show();
                                            }

                                        }));
                                Positive.setOnClickListener(v -> {
                                    // FirebaseAuth.getInstance().getCurrentUser().reload();
                                    if (requireNonNull(user).isEmailVerified()) {
                                        Log.d(TAG, "onComplete: LOG 2");
                                        Toast.makeText(mContext, "Great Work! Let's Proceed.",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                        user.getIdToken(true).addOnCompleteListener(task12 -> {
                                            if (task12.isSuccessful()) {
                                                Log.i(TAG, "ID Token received", task12.getException());
                                                onResultListener.onSuccess(task12.getResult().getToken());
                                            } else {
                                                Log.e(TAG, "ID Token not received", task12.getException());
                                                onResultListener.onError(task12.getException());
                                            }
                                        });

//                                        Bundle bundle = new Bundle();
//                                        Log.d(TAG, "onClick: username firebase methods " + Username);
//                                        bundle.putString(mContext.getString(R.string.field_username), Username);
//                                        PrepareCardFragment fragment = new PrepareCardFragment();
//                                        fragment.setArguments(bundle);
//                                        FragmentTransaction Transaction = ((SignUpActivity) activity).getSupportFragmentManager().beginTransaction();
//                                        Transaction.replace(R.id.FrameLayoutCard, fragment);
//                                        Transaction.addToBackStack(mContext.getString(R.string.prepare_card_fragment));
//                                        Transaction.commit();

                                    } else {
                                        Log.d(TAG, "onComplete: LOG 3");
                                        Toast.makeText(mContext, "If you have already verified your email. Please click the ghost to refresh the page.",
                                                Toast.LENGTH_LONG).show();

                                    }
                                });
                                Negative.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d(TAG, "onComplete: LOG 4");
                                        Toast.makeText(mContext, "Trying doing it right now. Please!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Log.d(TAG, "onComplete: LOG 5");
                                Title.setText("Verification Email Failed!");
                                Desp.setText("Please try again. This is important.");
                                Positive.setText("Try Again..");
                                Negative.setText("I'll Do It Later...");
                                Positive.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendVerificationEmail(activity, Username, onResultListener);
                                    }
                                });
                                Negative.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(mContext, "Trying doing it right now. Please!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.show();
                                Toast.makeText(mContext, "Could'nt send Verification Email. Try again",
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    });
        }

    }

    /**
     * Add details to users node in database
     * Add details to user_account_settings node in database
     *
     * @param display_name
     * @param college_uid
     * @param phone_number
     * @param email
     * @param username
     * @param gender
     * @param course
     * @param website
     * @param description
     * @param profile_photo
     */
    public void AddNewUser(String display_name, long college_uid, long phone_number, String email, String username,
                           String gender, String course, String website, String description, String profile_photo) {

        User user = new User(college_uid, course, email, gender, phone_number, userID, "false", "1", StringManipulation.comdenseUsername(username));

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_is_email_verified))
                .setValue("false");

        UserAccountSettings userAccountSettings = new UserAccountSettings();
        userAccountSettings.setCard_bio(description);
        userAccountSettings.setDisplay_name(display_name);
        userAccountSettings.setFollowing(0);
        userAccountSettings.setFriendly_xavierites(0);
        userAccountSettings.setPosts(0);
        userAccountSettings.setProfile_photo(profile_photo);
        userAccountSettings.setUsername(StringManipulation.comdenseUsername(username));
        userAccountSettings.setWebsite(website);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(userAccountSettings);

        UserCommonDetails userCommonDetails = new UserCommonDetails();
        userCommonDetails.setDisplay_name(display_name);
        userCommonDetails.setUsername(username);

        myRef.child(mContext.getString(R.string.dbname_user_common_details))
                .child(userID)
                .setValue(userCommonDetails);

    }

    public void setFollowingCount(final String UID, final TextView textView) {
        Query query = myRef.child(mContext.getString(R.string.dbname_following))
                .child(UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowingCount = snapshot.getChildrenCount();
                textView.setText(String.valueOf(FollowingCount));
                myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(UID)
                        .child(mContext.getString(R.string.field_following))
                        .setValue(FollowingCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setFollowersCount(final String UID, final TextView textView) {
        Query query = myRef.child(mContext.getString(R.string.dbname_followers))
                .child(UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowerCount = snapshot.getChildrenCount();
                textView.setText(String.valueOf(FollowerCount));
                myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(UID)
                        .child(mContext.getString(R.string.field_followers))
                        .setValue(FollowerCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void SetPostCount(final String UID, final TextView textView) {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long photoCount = 0;

                PostCount = 0;
                photoCount = snapshot.getChildrenCount();
                Log.d(TAG, "onDataChange: PostCont photos " + PostCount);
                Query query = myRef.child(mContext.getString(R.string.dbname_user_videos))
                        .child(UID);

                final long finalPhotoCount = photoCount;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                        long VideoCount = 0;
                        VideoCount = snapshot1.getChildrenCount();
                        android.os.Handler handler = new Handler(Looper.getMainLooper());
                        final long finalVideoCount = VideoCount;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                PostCount = (finalPhotoCount + finalVideoCount) - 1;
                                Log.d(TAG, "onDataChange: PostCont videos " + snapshot1.getChildrenCount());
                                Log.d(TAG, "onDataChange: PostCont total " + PostCount);
                                textView.setText(String.valueOf(PostCount));
                            }
                        }, 1000);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(UID)
                .child(mContext.getString(R.string.field_posts))
                .setValue(PostCount);
    }

    /**
     * Retrieves user account settings from firebase of the user currently logged in
     * Firebase: user_account_settings node
     *
     * @param dataSnapshot
     * @return
     */
    public AllUserSettings getUserSettings(DataSnapshot dataSnapshot, String UserID) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase database.");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            // For user_account_settings Node
            if (Objects.requireNonNull(ds.getKey()).equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: data snapshot" + ds);

                try {
                    settings.setCard_bio(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getCard_bio()
                    );
                    settings.setDisplay_name(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getDisplay_name()
                    );
                    settings.setFollowing(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getFollowing()
                    );
                    settings.setFriendly_xavierites(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getFriendly_xavierites()
                    );
                    settings.setPosts(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getPosts()
                    );
                    settings.setProfile_photo(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getProfile_photo()
                    );
                    settings.setUsername(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getUsername()
                    );
                    settings.setWebsite(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(UserAccountSettings.class))
                                    .getWebsite()
                    );
                    Log.d(TAG, "getUserAccountSettings: user_account_settings information retrieved Success!" + settings.toString());
                } catch (NullPointerException e) {
                    Log.d(TAG, "getUserAccountSettings: NullPointerException " + e.getMessage());
                }
            }
            // For user_account_settings Node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                try {
                    user.setUsername(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getUsername()
                    );
                    user.setCollege_uid(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getCollege_uid()
                    );
                    user.setCourse(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getCourse()
                    );
                    user.setEmail(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getEmail()
                    );
                    user.setGender(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getGender()
                    );
                    user.setPhone_number(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getPhone_number()
                    );
                    user.setUser_id(
                            Objects.requireNonNull(ds.child(UserID)
                                    .getValue(User.class))
                                    .getUser_id()
                    );
                    Log.d(TAG, "getUserAccountSettings: users information retrieved Success!" + user.toString());
                } catch (NullPointerException e) {
                    Log.d(TAG, "getUserAccountSettings: NullPointerException " + e.getMessage());
                }
            }
        }
        return new AllUserSettings(user, settings);
    }

    public void UpdateUserAccountSettings(String Display_name, String website, String description, String course, String gender) {
        if (Display_name != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(Display_name);
        }
        if (website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }
        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }
        if (course != null) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_course))
                    .setValue(course);
        }
        if (gender != null) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_gender))
                    .setValue(gender);
        }

    }

    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to : " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * updating the user's email to the database
     *
     * @param email
     */
    public void updateEmail(String email) {
        Log.d(TAG, "updateEmail: updating email to : " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    /**
     * updating the user's phone number to the database
     *
     * @param phoneNumber
     */
    public void updatePhoneNumber(long phoneNumber) {
        Log.d(TAG, "updateEmail: updating email to : " + phoneNumber);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_phoneNumber))
                .setValue(phoneNumber);
    }

    /**
     * updating the user's phone number to the database
     *
     * @param collegeUID
     */
    public void updateCollegeUID(long collegeUID) {
        Log.d(TAG, "updateEmail: updating email to : " + collegeUID);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_collegeUID))
                .setValue(collegeUID);
    }

    /**
     * --------------------------------------------------------GENERAL METHODS (Upload to firebase)--------------------------------------
     */

    public void Set5ChildrenValue(String Field1, String Field2, String Field3, String Field4, Object Value) {
        myRef.child(Field1)
                .child(Field2)
                .child(Field3)
                .child(Field4)
                .setValue(Value);
    }
}

