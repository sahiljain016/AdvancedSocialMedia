package com.gic.memorableplaces.Messages;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.DataModels.MessagePreview;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.common.collect.Iterables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnResultListener;
import com.virgilsecurity.common.model.Result;
import com.virgilsecurity.sdk.cards.Card;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.functions.Function0;
import me.grantland.widget.AutofitTextView;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseMethods mFirebaseMethods;
    private EThree eThreeSender;
    private Result<Card> result, result2;

    private RecyclerView RV_PINNED, RV_RECENT;
    private ImageButton IB_NEW_CHAT;
    private ImageView mBackArrow, IV_SEARCH_M, IV_STAR_PINNED, IV_STAR_RECENT;
    private EditText ET_SEARCH_NEW_USERS;
    private AutofitTextView ATV_PINNED_TITLE, ATV_RECENT_TITLE;
    private MotionLayout ML;
    private MessagesRecyclerViewAdapter RVA_PINNED, RVA_RECENT;
    //    private LVCircularZoom circularZoom;
    private RecyclerView.LayoutManager LM_PINNED, LM_RECENT;

    private ArrayList<MessagePreview> almpPinnedChats, almpRecentChats;
    private boolean isPaused = true, isChildListenerFirstCall = true, isChildAdded = false;
    private long count = 0, CountChildEvent = 0;

    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messages);


        mContext = MessageActivity.this;

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        almpPinnedChats = new ArrayList<>();
        almpRecentChats = new ArrayList<>();


        // mBackArrow = findViewById(R.id.ic_back_arrow);

//            ML = findViewById(R.id.ML_CENTER_MESSAGES);
        ET_SEARCH_NEW_USERS = findViewById(R.id.ET_MESSAGE_NEW_USERS);
        IB_NEW_CHAT = findViewById(R.id.IV_NEW_CHAT);

        ATV_PINNED_TITLE = findViewById(R.id.ATV_TITLE_PINNED_CHATS);
        IV_STAR_PINNED = findViewById(R.id.IV_STAR_PINNED);
        RV_PINNED = findViewById(R.id.RV_PINNED_MESSAGES);

        ATV_RECENT_TITLE = findViewById(R.id.ATV_TITLE_RECENT_CHATS);
        IV_STAR_RECENT = findViewById(R.id.IV_STAR_RECENT);
        RV_RECENT = findViewById(R.id.RV_RECENT_MESSAGES);

        IV_SEARCH_M = findViewById(R.id.IV_SEARCH_M);

        LM_PINNED = new LinearLayoutManager(mContext);
        LM_RECENT = new LinearLayoutManager(mContext);
        RV_PINNED.setLayoutManager(LM_PINNED);
        RV_RECENT.setLayoutManager(LM_RECENT);


        /*Query query1 = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child("public_key");

        final boolean[] isFirst = {true};
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Mysnapshot) {
                Log.d(TAG, "onDataChange: public key snapshot: " + Mysnapshot);
                if (isFirst[0]) {

                    Query query = myRef.child(mContext.getString(R.string.dbname_users))
                            .child("cIsEJlI460d1cHbJKRFDxqM2B1y2")
                            .child("public_key");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            EndToEndEncrypt e = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), Mysnapshot.getValue().toString(), mContext);
                            String Emessage = e.authEncrypt("my name is sahil!", snapshot.getValue().toString());
                            Log.d(TAG, "onDataChange: encrypted message: " + Emessage);
                            try {
                                EndToEndEncrypt e2 = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), snapshot.getValue().toString(), mContext);
                                Log.d(TAG, "onDataChange: decrypted message: " + e2.authDecrypt(Emessage, Mysnapshot.getValue().toString()));
                            } catch (DecryptionException decryptionException) {
                                decryptionException.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    isFirst[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/
//        EThreeParams params = new EThreeParams(mAuth.getCurrentUser().getUid(), getAuthTokenUserOne, mContext);
//        eThreeSender = new EThree(params);
//        result2 = eThreeSender.findUser(mAuth.getCurrentUser().getUid());

        //Log.d(TAG, String.format("onCreate: token: %s", getAuthTokenUserOne));


        //  getCurrentChats();


        Query query = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                .child(mAuth.getCurrentUser().getUid())
                .orderByChild(mContext.getString(R.string.field_latest_date_messaged))
                .limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String TimeStamp = "", UID = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UID = dataSnapshot.getKey();
                    TimeStamp = dataSnapshot.child(mContext.getString(R.string.field_latest_date_messaged)).getValue().toString();
                    break;
                }

                Log.d(TAG, "onDataChange: Timestam: " + TimeStamp);
                Log.d(TAG, "onDataChange: UID: " + UID);

                Query query = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(requireNonNull(mAuth.getCurrentUser()).getUid())
                        .orderByChild(mContext.getString(R.string.field_latest_date_messaged))
                        .startAfter(TimeStamp, UID);

                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        try {
                            Log.d(TAG, String.format("onChildAdded: prev child 1: %s", previousChildName));

                            isChildAdded = true;
                            GatherAndSetMessageDetails(snapshot, 0, true, snapshot.getKey());
                        } catch (Exception e) {
                            Log.d(TAG, "onChildAdded: error: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, String.format("onChildChanged: prev child 1: %s", previousChildName));
                        if (!isChildAdded) {
                            GatherAndSetMessageDetails(snapshot, 0, true, snapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
//
//            Query ChildChangedQuery = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
//                    .child(mAuth.getCurrentUser().getUid())
//                    .orderByChild(mContext.getString(R.string.field_latest_date_messaged));
//
//            ChildChangedQuery.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    Log.d(TAG, "onChildAdded: prev child name 2: " + previousChildName);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    try {
//                     //   Log.d(TAG, String.format("onChildChanged: snapshot: %s", snapshot));
//                        Log.d(TAG, "onChildChanged: prev child 2: " + previousChildName);
//                    } catch (Exception e) {
//                        Log.d(TAG, "onChildChanged: error 2: " + e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });


        //UpdateMessages();


//            ET_SEARCH_NEW_USERS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//
//                        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        rotate.setDuration(3000);
//                        rotate.setInterpolator(new LinearInterpolator());
//
//                        IV_SEARCH_M.setAnimation(rotate);
//
//                        IV_SEARCH_M.animate().alpha(0).setDuration(1000).setStartDelay(1000).setListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//
//                                IV_SEARCH_M.setImageResource(R.drawable.ic_back_ff);
//                                IV_SEARCH_M.animate().alpha(1).setDuration(1000);
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        });
//                        ML.transitionToEnd();
//                    }
//                }
//            });

        IV_SEARCH_M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ML.getProgress() == 1.0)
                    ML.transitionToStart();

            }
        });

        IB_NEW_CHAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUserNewChatFragment fragment = new SearchUserNewChatFragment();
                FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FL_messages, fragment);
                Transaction.addToBackStack(getString(R.string.new_chat_user_fragment));
                Transaction.commit();
            }
        });

//            .setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });


    }

    private final Function0<String> getAuthTokenUserOne = () -> {

        Log.d(TAG, "getAutHtokenUserOne: ");

        final String[] token = {""};
        FirebaseFunctions.getInstance()
                .getHttpsCallable("getVirgilJwt")
                .call()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        token[0] = ((Map<String, String>) task.getResult().getData()).get("token");
                        Log.d(TAG, "onComplete: task result: " + token[0]);
                        isPaused = false;
                    } else {
                        Log.d(TAG, String.format("onComplete: task error: %s", task.getException()));
                    }
                });

        while (true) {
            if (!isPaused) {
                //Log.d(TAG, "invoke: token[0]: " + token[0]);
                return token[0];
            }
        }

    };


    //Log.d(TAG, "UpdateChat: sMessageID: " + sMessageID);


//        query.addListenerForSingleValueEvent( new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Chat chat = snapshot.child(sMessageID).getValue(Chat.class);
////                Log.d(TAG, "onDataChange: chat: " + chat.toString());
//                // Log.d(TAG, "onSuccess: mChatList: " + mChatList);
//                // Log.d(TAG, "onDataChange: chat : " + chat);
//                if (chat != null && !((mChatList.get(mChatList.size() - 1)).getDate_messaged()).equals(chat.getDate_messaged())) {
//                    DecryptMe(chat, false, 0);
//
//                    isPaused = true;
//                    while (true) {
//                        if (!isPaused) {
//                            //Log.d(TAG, "onDataChange: message: " + );
//                            break;
//                        }
//                    }
//
//
//                }
////                }
//
//                mRecyclerView.smoothScrollToPosition(mChatList.size());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        Query_Update_Chat.addValueEventListener(Listener_Update_chat);


    private void DecryptMe(MessagePreview messagePreview, DataSnapshot Details, boolean isUpadate, long ChildrenCount) {


        final OnResultListener<Card> onDecryptedListener = new OnResultListener<Card>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(Card result) {
                try {

                    //Log.d(TAG, "onDataChange: sMessage before: " + chat.getMessage());
                    messagePreview.setLatest_message(eThreeSender.authDecrypt(messagePreview.getLatest_message(), result));
                } catch (Exception e) {
                    messagePreview.setLatest_message("N/A");
                    Log.d(TAG, "onSuccess: error: " + e.getMessage());
                }

//                if (Details.child(mContext.getString(R.string.field_latest_date_messaged)).getValue() != null)
//                    messagePreview.setLatest_epoch_gmt_milliseconds_long(Details.child(mContext.getString(R.string.field_epoch_gmt_milliseconds)).getValue(Long.class));
//                else
//                    messagePreview.setLatest_epoch_gmt_milliseconds_long(0);

//                if (Details.child(mContext.getString(R.string.field_previous_message_id)).getValue() != null)
//                    messagePreview.setPrevious_message_id(Details.child(mContext.getString(R.string.field_previous_message_id)).getValue().toString());
//                else
//                    messagePreview.setPrevious_message_id("N/A");

                if (Details.child(mContext.getString(R.string.field_latest_message_id)).getValue() != null)
                    messagePreview.setLatest_message_id(Details.child(mContext.getString(R.string.field_latest_message_id)).getValue().toString());
                else
                    messagePreview.setLatest_message_id("N/A");

                if (Details.child(mContext.getString(R.string.field_pending_unseen_messages)).getValue() != null) {

                    messagePreview.setPending_unseen_messages((long) Details.child(mContext.getString(R.string.field_pending_unseen_messages)).getValue());
                } else {
                    messagePreview.setPending_unseen_messages(0);
                }

                runOnUiThread(() -> {
                    if (Details.child(mContext.getString(R.string.field_is_message_pinned)).getValue() != null) {
                        if ((boolean) Details.child(mContext.getString(R.string.field_is_message_pinned)).getValue()) {
                            if (isUpadate) {
                                almpPinnedChats.add(0, messagePreview);
                                RVA_PINNED.notifyItemInserted(0);
                            } else
                                almpPinnedChats.add(messagePreview);
                        } else {
                            if (isUpadate) {
                                int index = ContainsMessageID(almpRecentChats, messagePreview.getOther_user_uid());
                                if (index != -1) {
                                    almpRecentChats.remove(index);
                                    RVA_RECENT.notifyItemRemoved(index);
                                }
                                almpRecentChats.add(0, messagePreview);
                                RVA_RECENT.notifyItemInserted(0);
                            } else
                                almpRecentChats.add(messagePreview);
                        }
                    } else {
                        if (isUpadate) {
                            if (!almpRecentChats.isEmpty()) {
                                int index = ContainsMessageID(almpRecentChats, messagePreview.getOther_user_uid());
                                Log.d(TAG, "onSuccess: index: " + index);
                                if (index != -1) {
                                    almpRecentChats.remove(index);
                                    RVA_RECENT.notifyItemRemoved(index);
                                }
                                almpRecentChats.add(0, messagePreview);
                                RVA_RECENT.notifyItemInserted(0);

                                isChildAdded = false;
                            } else {
                                almpRecentChats.add(messagePreview);
                                RVA_RECENT = new MessagesRecyclerViewAdapter(almpRecentChats,false, mContext, MessageActivity.this);
                                RV_RECENT.setAdapter(RVA_RECENT);
                                RVA_RECENT.notifyDataSetChanged();
                            }
                        } else {
                            almpRecentChats.add(messagePreview);
                        }
                    }

                    Log.d(TAG, String.format("onSuccess: almpPinnedChats: %s", almpPinnedChats));
                    Log.d(TAG, String.format("onSuccess: almpRecentChats: %s", almpRecentChats));

                    //Log.d(TAG, "onSuccess: count: " + count);
                    //Log.d(TAG, "onSuccess: childrenCount: " + ChildrenCount);
//                if (runnable != null)
//                    runnable.run();
                    if (!isUpadate) {
                        if (count == ChildrenCount) {
                            if (!almpPinnedChats.isEmpty() && !almpRecentChats.isEmpty()) {
                                // Log.d(TAG, "onSuccess: call1");
                                RVA_PINNED = new MessagesRecyclerViewAdapter(almpPinnedChats,true, mContext, MessageActivity.this);
                                RVA_RECENT = new MessagesRecyclerViewAdapter(almpRecentChats,false, mContext, MessageActivity.this);
                                RV_PINNED.setAdapter(RVA_PINNED);
                                RV_RECENT.setAdapter(RVA_RECENT);
                                RVA_RECENT.notifyDataSetChanged();
                                RVA_PINNED.notifyDataSetChanged();
                            } else if (almpPinnedChats.isEmpty() && !almpRecentChats.isEmpty()) {
                                // Log.d(TAG, String.format("onSuccess: call2 + %s", almpRecentChats));
                                RVA_RECENT = new MessagesRecyclerViewAdapter(almpRecentChats,false, mContext, MessageActivity.this);
                                RV_RECENT.setAdapter(RVA_RECENT);
                                RVA_RECENT.notifyDataSetChanged();


                            } else if (!almpPinnedChats.isEmpty()) {
                                // Log.d(TAG, "onSuccess: call3");
                                RVA_PINNED = new MessagesRecyclerViewAdapter(almpPinnedChats,true, mContext, MessageActivity.this);
                                RV_PINNED.setAdapter(RVA_PINNED);
                                RVA_PINNED.notifyDataSetChanged();
                            }
                        }
                        count++;
                    }
                });

                //Log.d(TAG, "Decrypted Text: \n" + chat.getMessage());
//                if (!isLoadMore) {
//                    mChatList.add(chat);
//                    if (!isFirst) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.notifyItemInserted((mChatList.size() - 1));
//                            }
//                        });
//                    }
//                } else {
//                    //Log.d(TAG, "onSuccess: count: " + count);
//                    mChatList.add((count - 1), chat);
//                }
            }

            @Override
            public void onError(final Throwable throwable) {
                throwable.printStackTrace();
            }
        };


        if (messagePreview.getLatest_sender_id() != null) {
            result = eThreeSender.findUser(messagePreview.getLatest_sender_id());
            result.addCallback(onDecryptedListener);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int ContainsMessageID(ArrayList<MessagePreview> almp, String UID) {
        int count = -1;
        count = Iterables.indexOf(almp, messagePreview -> (Objects.equals(messagePreview.getOther_user_uid(), UID)));

        return count;
    }

    private void getCurrentChats() {
        Query qCurrentChats = myRef.child(getString(R.string.dbname_chat_meta_data))
                .child(requireNonNull(mAuth.getCurrentUser()).getUid())
                .orderByChild(mContext.getString(R.string.field_latest_date_messaged)).limitToLast(10);

        ValueEventListener velCurrentChats = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  UserIDHashMap.clear();
                almpPinnedChats.clear();
                almpRecentChats.clear();

                count = 1;
                //Log.d(TAG, String.format("onDataChange: snaphot: %s", snapshot));


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d(TAG, String.format("onDataChange: datasnapshot get current chats: %s", dataSnapshot));
                    GatherAndSetMessageDetails(dataSnapshot, snapshot.getChildrenCount(), false, dataSnapshot.getKey());
                }

                // Log.d(TAG, String.format("onDataChange: almpPinnedChats: %s", almpPinnedChats));
                // Log.d(TAG, String.format("onDataChange: almpRecentChats: %s", almpRecentChats));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        qCurrentChats.addListenerForSingleValueEvent(velCurrentChats);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            FragmentManager fm = getSupportFragmentManager();

            if (fm != null) {
                int backStackCount = fm.getBackStackEntryCount();
                if (backStackCount == 0) {
                    // Log.d(TAG, "onBackStackChanged: fragment destroyed");
                    // qCurrentChats.addValueEventListener(velCurrentChats);
                } else {
                    /// Log.d(TAG, "onBackStackChanged: fragment called");

                    // qCurrentChats.removeEventListener(velCurrentChats);
                }
            }
        });

    }

    private void GatherAndSetMessageDetails(DataSnapshot dataSnapshot, long countChildren, boolean isUpdate, String UpdateUID) {

        MessagePreview messagePreview = new MessagePreview();
        messagePreview.setOther_user_uid(UpdateUID);

        ///Log.d(TAG, String.format("GatherAndSetMessageDetails: other user id: %s", messagePreview.getOtherUserUID()));


        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(messagePreview.getOther_user_uid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot UAS) {
                if (UAS.child(mContext.getString(R.string.field_is_online)).getValue() != null)
                    messagePreview.setOnline(UAS.child(mContext.getString(R.string.field_is_online)).getValue(boolean.class));
                else
                    messagePreview.setOnline(false);

                if (UAS.child(mContext.getString(R.string.field_profile_photo)).getValue() != null) {
                    // Log.d(TAG, "onDataChange: pp other: " + snapshot.child(mContext.getString(R.string.field_profile_photo)).getValue().toString());
                    messagePreview.setOther_pp_url(UAS.child(mContext.getString(R.string.field_profile_photo)).getValue().toString());
                } else
                    messagePreview.setOther_pp_url(" ");

                if (UAS.child(mContext.getString(R.string.field_username)).getValue() != null) {
                    messagePreview.setOther_display_name(UAS.child(mContext.getString(R.string.field_username)).getValue().toString());
                    //Log.d(TAG, "onDataChange: username:   " + snapshot.child(mContext.getString(R.string.field_username)).getValue().toString());
                } else
                    messagePreview.setOther_display_name("N/A");

                // Log.d(TAG, String.format("onDataChange: latest sender UID: %s", dataSnapshot.child(mContext.getString(R.string.field_chat_details)).child(mContext.getString(R.string.field_latest_sender_id)).getValue()));
                if (dataSnapshot.child(mContext.getString(R.string.field_latest_message)).getValue() != null)
                    messagePreview.setLatest_message(dataSnapshot.child(mContext.getString(R.string.field_latest_message)).getValue().toString());
                else {
                    messagePreview.setLatest_message("N/A");
                }

                if (dataSnapshot.child(mContext.getString(R.string.field_latest_sender_id)).getValue() != null)
                    messagePreview.setLatest_sender_id(dataSnapshot.child(mContext.getString(R.string.field_latest_sender_id)).getValue().toString());
                else
                    messagePreview.setLatest_message("N/A");


                DecryptMe(messagePreview, dataSnapshot, isUpdate, countChildren);
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
