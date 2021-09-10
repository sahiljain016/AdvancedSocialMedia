package com.gic.memorableplaces.Messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.CustomLibs.EndToEnd.EndToEndEncrypt;
import com.gic.memorableplaces.DataModels.Chat;
import com.gic.memorableplaces.DataModels.MessagePreview;
import com.gic.memorableplaces.Dialogs.CustomDialog;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.ChatDatabase;
import com.gic.memorableplaces.interfaces.ChatDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;
import top.defaults.colorpicker.ColorPickerView;

public class ChatFragment extends Fragment implements ChatRecyclerViewAdapter.OnChatClicked {
    private static final String TAG = "ChatFragment";

    private ChatRecyclerViewAdapter RA_CHATS;

    private EndToEndEncrypt MyE2E, OtherE2E;
    private ChatDatabase CD_CHATS;
    private ChatDao chatDao;
    private Handler handler;
    private ExecutorService databaseWriteExecutor;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private ChatRecyclerViewAdapter.OnChatClicked onChatClicked;
    private ValueEventListener VEL_Load_More_Messages, VEL_Remove_Unsent_Message, VEL_Pending_Unseen_Messages,
            VEL_Get_Current_Chats, VEL_Insert_First_Chat;
    private ChildEventListener CEL_Get_New_Messages;
    private Query Q_Load_More_Messages, Q_Get_New_Messages, Q_Remove_Unsent_Message, Q_Pending_Unseen_Messages,
            Q_Get_Current_Chats, Q_Insert_First_Chat;

    private String sUserID;
    private String sMyUID;
    private String sChatUID;
    private String sProfilePhoto;
    private String sBubbleColor;
    private String sMessage;
    private String sOtherPublicKey;
    private String sMyPublicKey;
    private String sFirstMessageID;
    private long lLastMessageEpoch, lFirstMessageEpoch;
    private boolean isPinned = false, isGroup, isGradient, isFirst = true, isLoadMoreMessages = true,
            isUnsend = false, isMyUnsend = false;
    private int count = 1;

    private Context mContext;
    private ArrayList<Chat> mChatList;
    private ArrayList<String> mGroupReceiversList;
    private HashMap<String, String> mReceiversList;
    private HashMap<String, String> mUsernameList;

    private ImageView IV_BACK, IV_CHAT_OPTIONS, IV_SETTINGS, IV_VIDEO_CALL, IV_CALL, IV_ANCHOR,
            IV_AUDIO, IV_GALLERY, IV_SEND_MESSAGE, IV_HIGHLIGHTED_CHAT, IV_BUBBLE_COLOR;
    private AnimatedRecyclerView RV_CHATS;
    private CircleImageView CIV_PP, CIV_GROUP_PP_1, CIV_GROUP_PP_2;
    private AutofitTextView ATV_FULL_NAME, ATV_STATUS, ATV_UNSEND, ATV_CANCEL_OPTIONS;
    //    private ImageButton IB_COLOR_PICKER_CHAT;
    private EditText ET_TYPE_MESSAGE_CHAT;
    private View V_DIM;
    private MotionLayout ML_MAIN, ML_TYPE_BOX, ML_OPTIONS;
    private ConstraintLayout CL_CHAT_OPTIONS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Log.d(TAG, "onCreateView: ChatFragment");

        RV_CHATS = view.findViewById(R.id.RV_CHAT);
        CIV_PP = view.findViewById(R.id.IV_PP_CHAT);
        CIV_GROUP_PP_1 = view.findViewById(R.id.IV_GROUP_PP_1);
        CIV_GROUP_PP_2 = view.findViewById(R.id.IV_GROUP_PP_2);
        ATV_FULL_NAME = view.findViewById(R.id.ATV_FULL_NAME_CHAT);
        ATV_STATUS = view.findViewById(R.id.ATV_STATUS_CHAT);
        IV_SEND_MESSAGE = view.findViewById(R.id.IV_SEND_MESSAGE_CHAT);
        ET_TYPE_MESSAGE_CHAT = view.findViewById(R.id.ET_TYPE_MESSAGE_CHAT);
        IV_HIGHLIGHTED_CHAT = view.findViewById(R.id.IV_HIGHLIGHTED_CHAT);
        V_DIM = view.findViewById(R.id.V_DIM_BACKGROUND_CHAT);
        IV_CHAT_OPTIONS = view.findViewById(R.id.IV_CHAT_OPTIONS);
//        IB_COLOR_PICKER_CHAT = view.findViewById(R.id.IB_COLOR_PICKER_CHAT);
        IV_AUDIO = view.findViewById(R.id.IV_AUDIO_CHAT);
        IV_BACK = view.findViewById(R.id.IV_BACK_CHAT);
        IV_GALLERY = view.findViewById(R.id.IV_GALLERY_CHAT);
        IV_BUBBLE_COLOR = view.findViewById(R.id.IV_BUBBLE_COLOR);
        ML_MAIN = view.findViewById(R.id.ML_CHAT_FRAGMENT);
        ML_OPTIONS = view.findViewById(R.id.ML_CHAT_BOTTOM);
        ATV_UNSEND = view.findViewById(R.id.ATV_UNSEND_CHAT);
        ATV_CANCEL_OPTIONS = view.findViewById(R.id.ATV_CLOSE_CHAT_OPTIONS);
        ML_TYPE_BOX = view.findViewById(R.id.ML_TYPE_BOX_CHAT_BOTTOM);
        CL_CHAT_OPTIONS = view.findViewById(R.id.CL_CHAT_OPTIONS);

        onChatClicked = this;
        mChatList = new ArrayList<>();
        mReceiversList = new HashMap<>();
        mUsernameList = new HashMap<>();
        mGroupReceiversList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        sMyUID = mAuth.getCurrentUser().getUid();
        mFirebaseMethods = new FirebaseMethods(mContext);

        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        ATV_FULL_NAME.setTypeface(title, Typeface.BOLD);

        if (getArguments() != null) {
            sUserID = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_user_id)));
            sProfilePhoto = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_profile_photo)));
            if (getArguments().containsKey(mContext.getString(R.string.field_other_public_key))) {
                sOtherPublicKey = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_other_public_key)));
            }
            if (getArguments().containsKey(mContext.getString(R.string.field_my_public_key))) {
                sMyPublicKey = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_my_public_key)));
            }
            ATV_FULL_NAME.setText(String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username))));
            isGroup = getArguments().getBoolean("isGroup");
            isPinned = getArguments().getBoolean(mContext.getString(R.string.field_is_message_pinned));
            sChatUID = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_chat_uid)));

        }

        if (!TextUtils.isEmpty(sProfilePhoto))
            GlideImageLoader.loadImageWithOutTransition(mContext, sProfilePhoto, CIV_PP);

        MyE2E = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), sMyPublicKey, mContext);
        OtherE2E = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), sOtherPublicKey, mContext);

        CD_CHATS = ChatDatabase.getDatabase(mContext);
        chatDao = CD_CHATS.chatDao();
        databaseWriteExecutor = CD_CHATS.databaseWriteExecutor;

        handler = new Handler(Looper.getMainLooper());

        // long size = new File(mContext.getDatabasePath("chat_database").getAbsolutePath()).length();

        GetChats();
        SendMessage();
        RemoveUnsentMessage();

        if (isPinned) {
            CL_CHAT_OPTIONS.setBackgroundResource(R.drawable.rounded_rect_tl_tr_gradient_pink_orange);
            IV_CALL.setBackgroundResource(R.drawable.ic_call_pinned);
            IV_VIDEO_CALL.setBackgroundResource(R.drawable.ic_video_call_pinned);
            IV_SEND_MESSAGE.setBackgroundResource(R.drawable.ic_send_message_chat);
        }

        // Log.d(TAG, "onCreateView: UserID " + sUserID);

        IV_BUBBLE_COLOR.setOnClickListener(v -> {
            CustomDialog customDialog = new CustomDialog(mContext, R.layout.layout_color_picker);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.show();

            ColorPickerView colorPickerView = (customDialog).findViewById(R.id.CPV_COLOR_WHEEL);
            AppCompatButton MakeGradient = (customDialog).findViewById(R.id.ACB_MAKE_GRADIENT);

            colorPickerView.setEnabledAlpha(true);
            colorPickerView.setEnabledBrightness(true);
            colorPickerView.subscribe((color, fromUser, shouldPropagate) -> {
                Log.d(TAG, "onColor: color  " + color);
                sBubbleColor = String.valueOf(color);
            });

            MakeGradient.setOnClickListener(v1 -> isGradient = MakeGradient.getTag().equals("Unselected"));

        });

        IV_BACK.setOnClickListener(v -> {
            getParentFragmentManager().popBackStackImmediate(mContext.getString(R.string.chat_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        IV_CHAT_OPTIONS.setOnClickListener(v -> {
            if (ML_OPTIONS.getProgress() == 0.0)
                ML_OPTIONS.transitionToEnd();
            else
                ML_OPTIONS.transitionToStart();
        });

//        mBackToSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mGradient.setVisibility(View.VISIBLE);
//                mAudioToText.setVisibility(View.VISIBLE);
//                mAdminAlert.setVisibility(View.VISIBLE);
//                mSendMessage.setVisibility(View.GONE);
//                mBackToSettings.setVisibility(View.GONE);
//
//                ConstraintSet constraintSet = new ConstraintSet();
//                constraintSet.clone(constraintLayout);
//                constraintSet.connect(R.id.ET_TYPE_MESSAGE_CHAT, ConstraintSet.END, R.id.Gradient, ConstraintSet.START, 0);
//                constraintSet.applyTo(constraintLayout);
//            }
//        });
//        mAudioToText.setOnClickListener(v -> {
//            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_type));
//            intent.putExtra("android.speech.extra.DICTATION_MODE", true);
//            try {
//                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//
//            } catch (ActivityNotFoundException a) {
//                Toast.makeText(mContext, "Unable to Hear", Toast.LENGTH_SHORT).show();
//            }
//        });

//        mGradient.setOnClickListener(v -> {
//            final Dialog Dialog = new Dialog(requireActivity());
//            requireNonNull(Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//            final TextView GradientApplied = new TextView(getActivity());
//            GradientApplied.setTextColor(Color.WHITE);
//            GradientApplied.setBackgroundColor(Color.parseColor("#66000000"));
//            if (!isGradient) {
//                isGradient = true;
//                GradientApplied.setText("Gradient Chat Color Applied!");
//            } else {
//                isGradient = false;
//                GradientApplied.setText("Gradient Chat Color Removed!");
//            }
//            Dialog.setContentView(GradientApplied);
//            Dialog.show();
//
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Dialog.dismiss();
//                }
//            }, 2500);
//
//
//        });

        ET_TYPE_MESSAGE_CHAT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    //Log.d(TAG, "afterTextChanged: called s: " + s);
                    ML_TYPE_BOX.transitionToEnd();
                } else {
                    ML_TYPE_BOX.transitionToStart();
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RV_CHATS.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);
        RV_CHATS.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
        SetMessageSeen("false", false);
        if (VEL_Load_More_Messages != null)
            Q_Load_More_Messages.removeEventListener(VEL_Load_More_Messages);
        if (VEL_Get_Current_Chats != null)
            Q_Get_Current_Chats.removeEventListener(VEL_Get_Current_Chats);
        if (VEL_Insert_First_Chat != null)
            Q_Insert_First_Chat.removeEventListener(VEL_Insert_First_Chat);
        if (VEL_Remove_Unsent_Message != null)
            Q_Remove_Unsent_Message.removeEventListener(VEL_Remove_Unsent_Message);
        if (VEL_Pending_Unseen_Messages != null)
            Q_Pending_Unseen_Messages.removeEventListener(VEL_Pending_Unseen_Messages);
        if (CEL_Get_New_Messages != null)
            Q_Get_New_Messages.removeEventListener(CEL_Get_New_Messages);
    }

    /**
     * Methods Jump
     * {@link #DecryptChat(Chat, long, String)}
     * {@link #EncryptAndStore()}
     * {@link #GetNewMessage()}
     * {@link #SendMessage()}
     * {@link #GetChats()}
     * {@link #InsertMyChat(Chat)} (Chat)}
     * {@link #LoadMoreMessages()}
     * {@link #RemoveUnsentMessage()}
     * {@link #EncryptUnsendMessage(String, int)} ()}
     */





    /*
    --------------------------------------------------------- ENCRYPTION/DECRYPTION METHODS--------------------------------------------
     */

    /**
     * Method to decrypt Chats, set time into string from epoch milliseconds and updating recycelr view according to @param LoadType
     *
     * @param chat          chat to be added to the list/recycler view
     * @param ChildrenCount total number of messages to be added
     * @param LoadType      Which method is calling this method
     */
    private void DecryptChat(Chat chat, long ChildrenCount, String LoadType) {

        //  Log.d(TAG, "onDataChange: chat.getEpoch_gmt_milliseconds: " + chat.getEpoch_gmt_milliseconds_long());

        chat.setString_date(MiscTools.GetTimeStamp(chat.getEpoch()));
        chat.setEpoch(chat.getEpoch());

        //Log.d(TAG, "DecryptChat: chat string_date:" + chat.getString_date());

        try {
            if (chat.getSender().equals(sMyUID))
                chat.setMessage(MyE2E.authDecrypt(chat.getMessage()));
            else
                chat.setMessage(OtherE2E.authDecrypt(chat.getMessage()));
        } catch (com.virgilsecurity.sdk.crypto.exceptions.DecryptionException e) {
            e.printStackTrace();
        }

        switch (LoadType) {
            case "first_update": {
                mChatList.add(chat);
                databaseWriteExecutor.execute(() -> {
                    chat.setChat_uid(sChatUID);
                    chatDao.InsertNewChat(chat);
                });
                lLastMessageEpoch = chat.getEpoch();
                RA_CHATS = new ChatRecyclerViewAdapter(getActivity(), mChatList, sProfilePhoto, sUserID, isGroup, isPinned,
                        mUsernameList, onChatClicked);
                RV_CHATS.setItemAnimator(new DefaultItemAnimator());
                RV_CHATS.setAdapter(RA_CHATS);
                RA_CHATS.notifyItemInserted(0);
                RV_CHATS.scheduleLayoutAnimation();
                if (isFirst) {
                    GetNewMessage();
                    sFirstMessageID = chat.getChat_id();
                    lFirstMessageEpoch = chat.getEpoch();
                    LoadMoreMessages();
                }
                isFirst = false;
                //SetMessageSeen("true",true);

            }
            break;
            case "get_messages": {
                mChatList.add(chat);
                databaseWriteExecutor.execute(() -> chatDao.InsertNewChat(chat));
                if (count == ChildrenCount) {
                    //  Log.d(TAG, "DecryptChat: count: " + count);
                    //  Log.d(TAG, String.format("DecryptChat: mChatList before sorting: %s", mChatList));
                    // mChatList.sort(Comparator.comparing(Chat::getEpoch_gmt_milliseconds_long));

                    lLastMessageEpoch = chat.getEpoch();

                    //   Log.d(TAG, String.format("DecryptChat: mChatList after sorting: %s", mChatList));
                    RA_CHATS = new ChatRecyclerViewAdapter(getActivity(), mChatList, sProfilePhoto, sUserID, isGroup, isPinned, mUsernameList, onChatClicked);
                    RV_CHATS.setItemAnimator(new DefaultItemAnimator());
                    RV_CHATS.setAdapter(RA_CHATS);
                    // RA_CHATS.notifyItemRangeInserted(0, (int) ChildrenCount);
                    RV_CHATS.scheduleLayoutAnimation();
                    if (isFirst) {
                        GetNewMessage();
                        LoadMoreMessages();
                        SetMessageSeen("true", true);
                    }
                }
                count++;
            }
            break;
            case "update": {
                //  Log.d(TAG, "onSuccess: unsend inserted pos: " + (mChatList.size() - 1));
//                if (mChatList.isEmpty()) {
//                    myRef.child(mContext.getString(R.string.dbname_chats_removed))
//                            .child(sChatUID)
//                            .child(mContext.getString(R.string.field_chat_removed))
//                            .setValue(mContext.getString(R.string.not_available));
//                }
                mChatList.add(chat);
                databaseWriteExecutor.execute(() -> {
                    chat.setChat_uid(sChatUID);
                    chatDao.InsertNewChat(chat);
                });

                //   Log.d(TAG, "onSuccess: unsend chat list: " + mChatList);
                RA_CHATS.notifyItemInserted((mChatList.size() - 1));
                RV_CHATS.smoothScrollToPosition((mChatList.size() - 1));
                RA_CHATS.notifyItemChanged((mChatList.size() - 2));
            }
            break;
            case "load_more": {
                //  Log.d(TAG, "onSuccess: unsend inserted pos: " + (mChatList.size() - 1));
                mChatList.add((count - 1), chat);
                databaseWriteExecutor.execute(() -> chatDao.InsertNewChat(chat));
                //  Log.d(TAG, "onSuccess: unsend chat list: " + mChatList);
                RA_CHATS.notifyItemInserted(0);
                RA_CHATS.notifyItemChanged(1);
                count++;
            }
            break;
        }

    }

    /**
     * Method to remove unsent message from recycler view, alsChatList and update chat_removed, chat_meta_data nodes accordingly
     * It is being called by 2 methods -
     * {@link #DecryptChat(Chat, long, String)}  it is being initiated after the get messages is completed.}
     * {@link #SendMessage()} (Chat, long, String) it is being initiated after first ever message has been sent.}
     * Initiated through {@link #onItemClick(int, ConstraintLayout)}
     */
    private void RemoveUnsentMessage() {

        Q_Remove_Unsent_Message = myRef.child(mContext.getString(R.string.dbname_chats_removed))
                .child(sChatUID);

        if (VEL_Remove_Unsent_Message != null) {
            Log.d(TAG, "RemoveUnsentMessage: is attached");
            Q_Remove_Unsent_Message.removeEventListener(VEL_Remove_Unsent_Message);
        }
        VEL_Remove_Unsent_Message = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, String.format("onChildChanged: unsend snapshot: %s", snapshot));
                if (snapshot.getValue() != null) {
                    try {
                        int pos;
                        try {
                            pos = Math.toIntExact(snapshot.child(mContext.getString(R.string.field_chat_removed)).getValue(Long.class));
                        } catch (NullPointerException nullPointerException) {
                            Log.d(TAG, "onChildChanged: called exception");
                            return;
                        }
//                        String messageID = snapshot.getValue(Chat.class).getMessageID();
//                        for (pos = 0; pos < mChatList.size(); pos++) {
//                            if (messageID.equals(mChatList.get(pos).getMessageID())) {
//                                break;
//                            }


                        // Log.d(TAG, "onChildAdded: unsend pos: " + pos);

                        if (pos == (mChatList.size() - 1)) {


                            if (mChatList.size() > 1) {
                                if (isMyUnsend)
                                    EncryptUnsendMessage(mChatList.get(pos - 1).getMessage(), (mChatList.size() - 1));

                            } else {
                                databaseWriteExecutor.execute(() -> {
                                    Log.d(TAG, "onDataChange: unsend mChatList pos: " + mChatList.get(pos).toString());
                                    chatDao.DeleteSingleEntry(sChatUID, mChatList.get(pos).getChat_id());
                                    Log.d(TAG, "onDataChange: all chats: " + chatDao.GetAllChats(sChatUID).toString());
                                    handler.post(() -> {
                                        mChatList.remove(pos);
                                        RA_CHATS.notifyItemRemoved(pos);
                                        myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                                                .child(sMyUID)
                                                .child(sChatUID)
                                                .removeValue();
                                        myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                                                .child(sUserID)
                                                .child(sChatUID)
                                                .removeValue();

                                        myRef.child(mContext.getString(R.string.dbname_chats_removed))
                                                .child(sChatUID).removeValue();

                                        isUnsend = true;
                                    });

                                });

                            }
                        } else {
                            databaseWriteExecutor.execute(() -> {
                                Log.d(TAG, "onDataChange: unsend mChatList pos: " + mChatList.get(pos).toString());
                                chatDao.DeleteSingleEntry(sChatUID, mChatList.get(pos).getChat_id());
                                Log.d(TAG, "onDataChange: all chats: " + chatDao.GetAllChats(sChatUID).toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatList.remove(pos);
                                        RA_CHATS.notifyItemRemoved(pos);
                                        // Log.d(TAG, "onChildChanged: pos - 1: " + (pos - 1));
                                        // Log.d(TAG, "onChildChanged: pos + 1: " + (pos + 1));

                                        RA_CHATS.notifyItemChanged(pos - 1);
                                        RA_CHATS.notifyItemChanged(pos);
                                    }
                                });
                            });

                            //isUnsend = false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Q_Remove_Unsent_Message.addValueEventListener(VEL_Remove_Unsent_Message);

    }

    /**
     * Encrypt message to be sent using listener called by the {@link #SendMessage() SendMessage} method
     */

    private void EncryptAndStore() {
        final String encryptedText = OtherE2E.authEncrypt(sMessage);
        String sMessageID = myRef.push().getKey();


        Chat chat = new Chat(encryptedText, sMyUID, isGradient, sUserID, sMessageID,
                "false", sBubbleColor, ServerValue.TIMESTAMP);

        MessagePreview messagePreview = new MessagePreview(
                encryptedText,
                sMessageID,
                sMyUID,
                sMyPublicKey,
                sMyUID,
                "false",
                "false",
                sChatUID,
                ServerValue.TIMESTAMP,
                0
        );


        mFirebaseMethods.SendMessage(chat, messagePreview);

        myRef.child(mContext.getString(R.string.dbname_chats))
                .child(sChatUID)
                .child(messagePreview.getLatest_message_id())
                .setValue(chat).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                if (mChatList.isEmpty()) {
                    if (isFirst)
                        InsertMyChat(chat);

                  /*  myRef.child(mContext.getString(R.string.dbname_chats_removed))
                            .child(sChatUID)
                            .child(mContext.getString(R.string.field_chat_removed))
                            .setValue(mContext.getString(R.string.not_available)).addOnCompleteListener(task1 -> {
                        if (task1.isComplete()) {
                            Log.d(TAG, "onComplete:is First: " + isFirst);
                            //RemoveUnsentMessage();


                        }
                    });*/
                }

            }
        });


    }


    /**
     * Method to encrypt unsend messages again only if the latest message has been unsent as we need to update the
     * "CHAT META DATA" node in firebase as message before the unsent message has now become the latest message
     * It is called by the {@link #RemoveUnsentMessage()}
     *
     * @param Message this gives the message that has been unsent
     * @param pos     this is the position from which the unsent message is being removed (most probably, size of alsChatList)
     */
    private void EncryptUnsendMessage(String Message, int pos) {

        Q_Pending_Unseen_Messages = myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                .child(sUserID)
                .child(sChatUID)
                .child(mContext.getString(R.string.field_pending_unseen_messages));

        VEL_Pending_Unseen_Messages = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //EncryptUnsendMessage(mChatList.get((mChatList.size() - 2)).getMessage(), snapshot, finalPos);
                Log.d(TAG, "onDataChange: first message snapshot; " + snapshot);

                final String encryptedText = OtherE2E.authEncrypt(Message);
                MessagePreview messagePreview = new MessagePreview(
                        encryptedText,
                        mChatList.get(pos - 1).getChat_id(),
                        sMyUID,
                        sMyPublicKey,
                        sMyUID,
                        "false",
                        "false",
                        sChatUID,
                        0
                );
                if (snapshot.exists()) {
                    messagePreview.setPending_unseen_messages((snapshot.getValue(Long.class) + 1));
                } else {
                    messagePreview.setPending_unseen_messages(1);
                }
                myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(sUserID)
                        .child(sChatUID)
                        .setValue(messagePreview);

                myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(sUserID)
                        .child(sChatUID)
                        .child(mContext.getString(R.string.field_latest_date_messaged))
                        .setValue(mChatList.get(pos - 1).getEpoch());

                messagePreview.setPending_unseen_messages(0);
                messagePreview.setOther_user_uid(sUserID);
                myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(sMyUID)
                        .child(messagePreview.getChat_uid())
                        .setValue(messagePreview);

                myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                        .child(sMyUID)
                        .child(sChatUID)
                        .child(mContext.getString(R.string.field_latest_date_messaged))
                        .setValue(mChatList.get(pos - 1).getEpoch());

                databaseWriteExecutor.execute(() -> {
                    Log.d(TAG, "onDataChange: unsend mChatList pos: " + mChatList.get(pos).toString());
                    chatDao.DeleteSingleEntry(sChatUID, mChatList.get(pos).getChat_id());
                    Log.d(TAG, "onDataChange: all chats: " + chatDao.GetAllChats(sChatUID).toString());
                    handler.post(() -> {
                        mChatList.remove(pos);
                        RA_CHATS.notifyItemRemoved(pos);
                        // Log.d(TAG, "onSuccess: pos+1 " + (pos + 1) + " pos - 1" + (pos - 1));
                        RA_CHATS.notifyItemChanged((pos - 1));

                        myRef.child(mContext.getString(R.string.dbname_chats_removed))
                                .child(sChatUID)
                                .child(mContext.getString(R.string.field_chat_removed)).removeValue();


                        isMyUnsend = false;
                    });
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        Q_Pending_Unseen_Messages.addListenerForSingleValueEvent(VEL_Pending_Unseen_Messages);
        //  RESULT_OTHER_ENCRYPT.addCallback(onEncryptUnsendMessageListener);
    }
    /*
       ----------------------------------------------------- Firebase Methods to retrieve/unsend chats ----------------------------------
        */


    /**
     * Get 20 latest chats to filll up recycler view uses {@link #DecryptChat(Chat, long, String)}
     */
    private void GetChats() {
        // Log.d(TAG, "GetChats: sChatUID; " + sChatUID);
        databaseWriteExecutor.execute(() -> {
            Log.d(TAG, "GetChats: executor 20 messages: " + chatDao.Get20LatestChats(sChatUID).toString());
            for (Chat ch : chatDao.Get20LatestChats(sChatUID)) {
                ch.setString_date(MiscTools.GetTimeStamp(ch.getEpoch()));
                mChatList.add(0, ch);
                Log.d(TAG, "GetChats: executor mChatList message: " + ch.getMessage() + ", epoch: " + ch.getEpoch());
            }

            if (!mChatList.isEmpty()) {
                Log.d(TAG, "GetChats: last chate epoch: " + mChatList.get(mChatList.size() - 1).getEpoch());

                lLastMessageEpoch = mChatList.get(mChatList.size() - 1).getEpoch();
                lFirstMessageEpoch = mChatList.get(0).getEpoch();
                sFirstMessageID = mChatList.get(0).getChat_id();

                handler.post(() -> {
                    RA_CHATS = new ChatRecyclerViewAdapter(getActivity(), mChatList, sProfilePhoto, sUserID, isGroup, isPinned, mUsernameList, onChatClicked);
                    RV_CHATS.setItemAnimator(new DefaultItemAnimator());
                    RV_CHATS.setAdapter(RA_CHATS);
                    // RA_CHATS.notifyItemRangeInserted(0, (int) ChildrenCount);
                    RV_CHATS.scheduleLayoutAnimation();
                    if (isFirst) {
                        GetNewMessage();
                        LoadMoreMessages();
                        SetMessageSeen("true", true);
                    }
                });
                //   Log.d(TAG, String.format("DecryptChat: mChatList after sorting: %s", mChatList));

            } else {
                Q_Get_Current_Chats = myRef.child(mContext.getString(R.string.dbname_chats))
                        .child(sChatUID)
                        .orderByChild(mContext.getString(R.string.field_date_messaged)).limitToLast(20);

                VEL_Get_Current_Chats = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, String.format("onDataChange: get chats snapshot: %s", snapshot));
                        mChatList.clear();
                        count = 1;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getValue() != null) {

                                Chat chat = new Chat(dataSnapshot.child("message").getValue().toString(),
                                        dataSnapshot.child("sender").getValue().toString(),
                                        dataSnapshot.child("gradient").getValue(Boolean.class),
                                        dataSnapshot.child("receivers").getValue().toString(),
                                        dataSnapshot.child("chat_id").getValue().toString(),
                                        dataSnapshot.child("isSeen").getValue().toString(),
                                        dataSnapshot.child("bubbleColor").getValue().toString());
                                chat.setEpoch(dataSnapshot.child(mContext.getString(R.string.field_date_messaged)).getValue(Long.class));
                                if (count == 1) {
                                    lFirstMessageEpoch = dataSnapshot.child(mContext.getString(R.string.field_date_messaged)).getValue(Long.class);
                                    sFirstMessageID = chat.getChat_id();
                                }
                                DecryptChat(chat, snapshot.getChildrenCount(), "get_messages");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                Q_Get_Current_Chats.addListenerForSingleValueEvent(VEL_Get_Current_Chats);
            }
        });
    }

    /**
     * Message to load 10 more messages from tge time stamp of the last message date and id loaded int the chat
     * It gets the above mentioed parameter from the {@link #GetChats()} method}
     */
    private void LoadMoreMessages() {

        RV_CHATS.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    //scrolled to TOP
                    //Log.d(TAG, "onScrolled: Top Reached!" + sLastMessageDate);
//                    if (lLastMessageEpoch != 0) {
                    isLoadMoreMessages = true;
                    Log.d(TAG, "onScrolled: sFirstMessageID: " + sFirstMessageID);
                    databaseWriteExecutor.execute(() -> {
                        count = 1;
                        Log.d(TAG, "onScrolled: Load 10 More Chats: " + chatDao.Load10MoreChats(sChatUID, lFirstMessageEpoch));
                        for (Chat ch : chatDao.Load10MoreChats(sChatUID, lFirstMessageEpoch)) {
                            ch.setString_date(MiscTools.GetTimeStamp(ch.getEpoch()));
                            Log.d(TAG, "onScrolled: ch: " + ch.toString());
                            mChatList.add(0, ch);
                            count++;
                        }
                        if (count > 1) {
                            handler.post(() -> {
                                RA_CHATS.notifyItemRangeInserted(0, count - 1);
                                Log.d(TAG, "onScrolled: count: " + mChatList.get(count - 1).toString());
                                RA_CHATS.notifyItemChanged(count - 1);
                            });
                            lFirstMessageEpoch = mChatList.get(0).getEpoch();
                        }
                        Log.d(TAG, "onScrolled: lFirstMessageEpoch load: " + lFirstMessageEpoch);

                        if (count == 1 && !TextUtils.isEmpty(sFirstMessageID)) {
                            Q_Load_More_Messages = myRef.child(mContext.getString(R.string.dbname_chats))
                                    .child(sChatUID)
                                    .orderByChild(mContext.getString(R.string.field_date_messaged))
                                    .endBefore(lFirstMessageEpoch, sFirstMessageID)
                                    .limitToLast(10);

                            VEL_Load_More_Messages = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Log.d(TAG, "onDataChange: load more Snapshot: " + snapshot);
                                    sFirstMessageID = "";
                                    if (isLoadMoreMessages && snapshot.getValue() != null) {
                                        Log.d(TAG, "onDataChange: load more calling: ");
                                        count = 1;
                                        //alsMoreMessagesList.clear();
                                        // ArrayList<String> als = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.getValue() != null) {

                                                Chat chat = new Chat(
                                                        dataSnapshot.child("message").getValue().toString(),
                                                        dataSnapshot.child("sender").getValue().toString(),
                                                        dataSnapshot.child("gradient").getValue(Boolean.class),
                                                        dataSnapshot.child("receivers").getValue().toString(),
                                                        dataSnapshot.child("chat_id").getValue().toString(),
                                                        dataSnapshot.child("isSeen").getValue().toString(),
                                                        dataSnapshot.child("bubbleColor").getValue().toString());

                                                chat.setEpoch(dataSnapshot.child(mContext.getString(R.string.field_date_messaged)).getValue(Long.class));
                                                if (count == 1) {
                                                    lFirstMessageEpoch = dataSnapshot.child(mContext.getString(R.string.field_date_messaged)).getValue(Long.class);
                                                    sFirstMessageID = chat.getChat_id();
                                                }
                                                DecryptChat(chat, snapshot.getChildrenCount(), "load_more");
                                                //als.add(chat.getMessage());
                                            }
                                        }
                                        // Log.d(TAG, "onDataChange: temp load more: " + als);
                                    }

                                    isLoadMoreMessages = false;


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            Q_Load_More_Messages.addListenerForSingleValueEvent(VEL_Load_More_Messages);
                        }
                    });

                }

            }
        });
    }


    /**
     * Method to add Child Listener to a specific node which gets all chats new chats added after the latest chat time stamp
     * using the {@link #DecryptChat(Chat, long, String)}
     */
    private void GetNewMessage() {

        Log.d(TAG, "GetNewMessage: lLastMesssageEpoch: " + lLastMessageEpoch);
        Q_Get_New_Messages = myRef.child(mContext.getString(R.string.dbname_chats))
                .child(sChatUID)
                .orderByChild(mContext.getString(R.string.field_date_messaged))
                .startAfter(lLastMessageEpoch);

        if (CEL_Get_New_Messages != null)
            Q_Get_New_Messages.removeEventListener(CEL_Get_New_Messages);

        CEL_Get_New_Messages = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: GetNewMessage snapshot: " + snapshot);

                if (snapshot.getValue() != null) {
                    Chat chat = new Chat(snapshot.child("message").getValue().toString(),
                            snapshot.child("sender").getValue().toString(),
                            snapshot.child("gradient").getValue(Boolean.class),
                            snapshot.child("receivers").getValue().toString(),
                            snapshot.child("chat_id").getValue().toString(),
                            snapshot.child("isSeen").getValue().toString(),
                            null);
                    if (snapshot.hasChild("bubbleColor")) {
                        chat.setBubbleColor(snapshot.child("bubbleColor").getValue().toString());
                    }
                    long epoch = snapshot.child(mContext.getString(R.string.field_date_messaged)).getValue(Long.class);
                    myRef.child(mContext.getString(R.string.dbname_chats))
                            .child(sChatUID)
                            .child(chat.getChat_id())
                            .child(mContext.getString(R.string.field_date_messaged))
                            .setValue(epoch);
                    chat.setEpoch(epoch);
                    DecryptChat(chat,
                            0,
                            "update");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
        };

        Q_Get_New_Messages.addChildEventListener(CEL_Get_New_Messages);
    }




    /*
   ---------------------------------------------------Set Once Methods-------------------------------------------------------
    */


    /**
     * Setting on click listener for Send Typing Message by calling {@link #EncryptAndStore()}
     * to encrypt and send message to firebase
     */
    private void SendMessage() {
        IV_SEND_MESSAGE.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(ET_TYPE_MESSAGE_CHAT.getText().toString())) {
                sMessage = ET_TYPE_MESSAGE_CHAT.getText().toString();
                if (!isGroup) {
//                    if (isFirst) {
//                        thread1 = new Thread(new Runnable() {
//                            @Override
//                            public void run() {

                    EncryptAndStore();


//                            }
//                        });
//                        thread1.start();
//                    } else {
//                        thread1.run();
//                    }


                } else {
                    mFirebaseMethods.SendGroupMessage(sUserID, sBubbleColor,
                            sMessage, mGroupReceiversList, isGradient);
                }
            }
            ET_TYPE_MESSAGE_CHAT.setText("");
            isGradient = false;
            sBubbleColor = null;


        });
    }

    /**
     * Method to update the first ever chat added to chat room and it uses the {@link #DecryptChat(Chat, long, String) method}
     * This method is also used by the {@link #SendMessage() method}
     *
     * @param chat this parameter gives the first ever chat object created by the user
     */
    private void InsertMyChat(Chat chat) {

        /// Log.d(TAG, "InsertFirstChat: sChatUID: " + sChatUID);
        if (chat != null) {
            Q_Insert_First_Chat = myRef.child(mContext.getString(R.string.dbname_chats))
                    .child(sChatUID)
                    .child(chat.getChat_id())
                    .child(mContext.getString(R.string.field_date_messaged));
            VEL_Insert_First_Chat = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: insert snapshot: " + snapshot);
                    if (snapshot.getValue() != null) {
                        Long epoch = snapshot.getValue(Long.class);
                        myRef.child(mContext.getString(R.string.dbname_chats))
                                .child(sChatUID)
                                .child(chat.getChat_id())
                                .child(mContext.getString(R.string.field_date_messaged))
                                .setValue(epoch);
                        chat.setEpoch(epoch);
                        DecryptChat(chat, 0, "first_update");


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            Q_Insert_First_Chat.addListenerForSingleValueEvent(VEL_Insert_First_Chat);
        }
    }

    /**
     * Setting message seen status everytime user opens chat room or data is changed in the node given below -
     */
    private void SetMessageSeen(String Status, boolean isOpen) {

        myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                .child(sMyUID)
                .child(sChatUID)
                .child("isSeen")
                .setValue(Status);
        if (isOpen) {
            myRef.child(mContext.getString(R.string.dbname_chat_meta_data))
                    .child(sMyUID)
                    .child(sChatUID)
                    .child(mContext.getString(R.string.field_pending_unseen_messages))
                    .setValue(0);
        }

    }


    /**
     * Called when chat message is pressed for long time displays chat options menu and implements method to unsend chat.
     * {@link #RemoveUnsentMessage()}
     * {@link #EncryptUnsendMessage(String, int)}
     *
     * @param position it is the position of the unsent message on the recycler view
     * @param CL_CHAT  CL View of message to get the bitmap for big display
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onItemClick(int position, ConstraintLayout CL_CHAT) {
        //Log.d(TAG, "onItemClick: mChatList pos: " + mChatList.get(position));
//        int width = CL_CHAT.getWidth();
//        int height = CL_CHAT.getHeight();
//
//        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
//        isUnsend = true;
        ML_MAIN.transitionToEnd();
        CL_CHAT.setDrawingCacheEnabled(true);

        V_DIM.setClickable(true);
        V_DIM.setFocusable(true);
        //Cause the view to re-layout

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(CL_CHAT.getDrawingCache());
        Drawable drawable = new BitmapDrawable(getResources(), b);
        V_DIM.setVisibility(View.VISIBLE);
        IV_HIGHLIGHTED_CHAT.setVisibility(View.VISIBLE);
        IV_HIGHLIGHTED_CHAT.setImageDrawable(drawable);

        ATV_UNSEND.setOnClickListener(v -> {
            ML_MAIN.transitionToStart();
            CL_CHAT.setDrawingCacheEnabled(false);
            V_DIM.setClickable(false);
            V_DIM.setFocusable(false);
            isMyUnsend = true;
            myRef.child(mContext.getString(R.string.dbname_chats))
                    .child(sChatUID)
                    .child(mChatList.get(position).getChat_id())
                    .removeValue();

            myRef.child(mContext.getString(R.string.dbname_chats_removed))
                    .child(sChatUID)
                    .child(mContext.getString(R.string.field_chat_removed))
                    .setValue(position);

            //RemoveUnsentMessage();
        });
        ATV_CANCEL_OPTIONS.setOnClickListener(v -> {
            ML_MAIN.transitionToStart();
            CL_CHAT.setDrawingCacheEnabled(false);

            V_DIM.setClickable(false);
            V_DIM.setFocusable(false);
        });

    }
}
