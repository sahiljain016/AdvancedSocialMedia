package com.gic.memorableplaces.Messages;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.faltenreich.skeletonlayout.Skeleton;
import com.gic.memorableplaces.Adapters.SearchNewUserRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.DataModels.UserCommonDetails;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class SearchUserNewChatFragment extends Fragment implements SearchNewUserRecyclerViewAdapter.OnNewChatUserClicked {
    private static final String TAG = "SearchFragment";
    private Context mContext;
    private String sFullName, sUsername, sPP, sMyPublicKey;
    private long delay = 1500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private String queryUsername;

    private SearchNewUserRecyclerViewAdapter searchUserListAdapter;
    private AnimatedRecyclerView rResults;
    private ArrayList<UserCommonDetails> alusSearchedList;
    private SearchNewUserRecyclerViewAdapter.OnNewChatUserClicked onNewChatUserClicked;
    // private LVGhost progressGhost;
    private EditText SearchBar;
    private AutofitTextView ATV_FORM_CHAT;
    private ImageView IV_BACK_ARROW;
    //private ImageView mBackArrow;
    private Skeleton skeleton;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> alsGroupMembersList;
//    private AnimatedTabLayout animatedTabLayout;
//    private ViewPager viewPager;

    private int size;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_new_chat, container, false);
        Log.d(TAG, "onCreateView: SearchFragment");

        mContext = requireActivity();
        rResults = view.findViewById(R.id.searchList);
        alusSearchedList = new ArrayList<>();
        alsGroupMembersList = new ArrayList<>();
        IV_BACK_ARROW = view.findViewById(R.id.IV_BACK_NEW_USER_CHAT);
        //CancelSearch = view.findViewById(R.id.cancel_search);
        SearchBar = view.findViewById(R.id.ET_NEW_USERNAME_CHAT);
        //mBackArrow = view.findViewById(R.id.ic_back_arrow);
        onNewChatUserClicked = this;
        ATV_FORM_CHAT = view.findViewById(R.id.ATV_FORM_CHAT);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        IV_BACK_ARROW.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate(mContext.getString(R.string.new_chat_user_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE));

        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_public_key));

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    sMyPublicKey = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
        query.removeEventListener(valueEventListener);


        Handler handler = new Handler();

        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    // ............
                    // ............

                    Log.d(TAG, "run: called queryUsername: " + queryUsername);
                    String keyword = queryUsername;
                    SearchForMatch(keyword);
                }
            }
        };

        SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    queryUsername = s.toString().toLowerCase(Locale.ROOT).trim();
                    handler.postDelayed(input_finish_checker, delay);
                }

            }
        });

        //ListViewOnClick();


        ATV_FORM_CHAT.setOnClickListener(v -> {
            Log.d(TAG, String.format("onClick: mGroupMembers %s", alsGroupMembersList));
            if (alsGroupMembersList.size() == 1) {
                if (alsGroupMembersList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Log.d(TAG, "onClick: Condition 1 " + alsGroupMembersList);
                    Toast.makeText(getActivity(), "You can't make a group with yourself. :)", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: Condition 2 " + alsGroupMembersList);
                    TransactionToChatFragment(false, alsGroupMembersList.get(0));
                }
            } else if (alsGroupMembersList.size() == 2) {
                if (alsGroupMembersList.contains(mAuth.getCurrentUser().getUid())) {
                    alsGroupMembersList.remove(mAuth.getCurrentUser().getUid());
                    Log.d(TAG, "onClick: Condition 3 " + alsGroupMembersList);
                    TransactionToChatFragment(false, alsGroupMembersList.get(0));
                } else {
                    Log.d(TAG, "onClick: ");
                    alsGroupMembersList.add(mAuth.getCurrentUser().getUid());
                    Log.d(TAG, "onClick: Condition 4 " + alsGroupMembersList);
                    String GroupID = myRef.push().getKey();
                    AddGroupMembers(GroupID);
                    TransactionToChatFragment(true, GroupID);

                }
            } else if (alsGroupMembersList.size() > 2) {
                Log.d(TAG, "onClick: Condition 5 " + alsGroupMembersList);
                if (!alsGroupMembersList.contains(requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    alsGroupMembersList.add(mAuth.getCurrentUser().getUid());
                }
                String GroupID = myRef.push().getKey();
                AddGroupMembers(GroupID);
                TransactionToChatFragment(true, GroupID);

            }
            alsGroupMembersList.clear();


        });
        return view;
    }


    private void TransactionToChatFragment(Boolean isGroup, String UserID) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGroup", isGroup);
        bundle.putString(mContext.getString(R.string.field_username), sUsername);
        bundle.putString(mContext.getString(R.string.field_profile_photo), sPP);
        bundle.putBoolean(mContext.getString(R.string.field_is_message_pinned), false);
        bundle.putString(mContext.getString(R.string.field_previous_message_id), mContext.getString(R.string.not_available));
        bundle.putString(mContext.getString(R.string.field_user_id), UserID);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);
        FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Transaction.replace(R.id.FL_messages, fragment);
        Transaction.addToBackStack(getActivity().getString(R.string.chat_fragment));
        getParentFragmentManager().popBackStackImmediate(mContext.getString(R.string.new_chat_user_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Transaction.commit();


    }

    private void AddGroupMembers(final String GroupID) {
        for (int i = 0; i < alsGroupMembersList.size(); i++) {
            final String UserID = alsGroupMembersList.get(i);
            for (int j = 0; j < alsGroupMembersList.size(); j++) {
                myRef.child(getActivity().getString(R.string.dbname_chat_meta_data))
                        .child(UserID)
                        .child(GroupID)
                        .child("members")
                        .child(requireNonNull(myRef.push().getKey()))
                        .child("user_id")
                        .setValue(alsGroupMembersList.get(j));
            }
            myRef.child(getActivity().getString(R.string.dbname_chat_meta_data))
                    .child(UserID)
                    .child(GroupID)
                    .child("group_name")
                    .setValue("My Chat Group!");
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
                    myRef.child(getActivity().getString(R.string.dbname_chat_meta_data))
                            .child(UserID)
                            .child(GroupID)
                            .child("date_messaged")
                            .setValue(TimeStamp);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });

        }
    }

//    private void SetUpTabLayout(){
//         adapter.AddFragment(new AllPostsFragment()); //index 1
////        viewPager.setAdapter(adapter);
////
////        animatedTabLayout.setupViewPager(viewPager);
//// SectionViewPagerAdapter adapter = new SectionViewPagerAdapter(getChildFragmentManager());
//        adapter.AddFragment(new AllQuestionsFragment()); //index 0
//
//    }


    private void CloseKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void SearchForMatch(String Keyword) {
//        searchUserListAdapter.notifyDataSetChanged();

        if (Keyword.length() > 0) {
//            progressGhost.setVisibility(View.VISIBLE);
//            progressGhost.startAnim(1000);


            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef.child(requireActivity().getString(R.string.dbname_user_common_details))
                    .orderByChild(mContext.getString(R.string.field_username))
                    .startAt(Keyword).endAt(Keyword + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    alusSearchedList.clear();

//                    searchUserListAdapter.notifyDataSetChanged();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getValue() != null) {
                            UserCommonDetails ucd = dataSnapshot.getValue(UserCommonDetails.class);
                            if (ucd != null) {
                                ucd.setUid(dataSnapshot.getKey());
                            }
                            alusSearchedList.add(ucd);
                        }


                        //size = size + 1;
                    }

                    Log.d(TAG, "onDataChange: aluSearchedList size " + alusSearchedList.size());

                    searchUserListAdapter = new SearchNewUserRecyclerViewAdapter(alusSearchedList, alsGroupMembersList, sMyPublicKey,
                            mContext, requireActivity(), onNewChatUserClicked);
                    rResults.setItemAnimator(new DefaultItemAnimator());
                    rResults.setAdapter(searchUserListAdapter);
                    searchUserListAdapter.notifyItemRangeInserted(0, alusSearchedList.size());
                    rResults.scheduleLayoutAnimation();
//                    progressGhost.stopAnim();
//                    progressGhost.setVisibility(View.INVISIBLE);
//                    CancelSearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    @Override
    public void onSelected(int position, boolean isAdded, String SelectedUserID, String Username, String FullName, String PP) {
        Log.d(TAG, "onSelected: SelectedUserId: " + SelectedUserID);
        Log.d(TAG, "onSelected: position: " + position);
        if (isAdded) {
            alsGroupMembersList.add(SelectedUserID);
            sFullName = FullName;
            sUsername = Username;
            sPP = PP;
        } else
            alsGroupMembersList.remove(SelectedUserID);


    }
}
