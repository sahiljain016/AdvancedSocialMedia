package com.gic.memorableplaces.Search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.Adapters.SearchUserListAdapter;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.CustomLibs.LoadingViewLib.LVGhost;
import com.gic.memorableplaces.Profile.ViewProfileActivity;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private SearchUserListAdapter searchUserListAdapter;
    private ListView UserListView;
    private ArrayList<String> userSearchList,userIDSearchList;
    private LVGhost progressGhost;
    private EditText SearchBar;
    private GridView searchGridView;
    private TextView CancelSearch;

//    private AnimatedTabLayout animatedTabLayout;
//    private ViewPager viewPager;

    private int size;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d(TAG, "onCreateView: SearchFragment");

        UserListView = view.findViewById(R.id.searchList);
        userSearchList = new ArrayList<>();
        userIDSearchList = new ArrayList<>();
        CancelSearch = view.findViewById(R.id.cancel_search);
        SearchBar = view.findViewById(R.id.search);
        searchGridView = view.findViewById(R.id.grid_view_search);
//        animatedTabLayout = view.findViewById(R.id.TabLayout);
//        viewPager = view.findViewById(R.id.ViewContainer);
        progressGhost = view.findViewById(R.id.progress_ghost);

        Random random = new Random();
        int rand = random.nextInt(6);
        switch (rand){
            case 0:
                progressGhost.setViewColor(Color.BLACK);
                progressGhost.setHandColor(Color.WHITE);
                break;
            case 1:
                progressGhost.setViewColor(Color.WHITE);
                progressGhost.setHandColor(Color.BLACK);
                break;
            case 2:
                progressGhost.setViewColor(Color.BLUE);
                progressGhost.setHandColor(Color.WHITE);
                break;
            case 3:
                progressGhost.setViewColor(Color.RED);
                progressGhost.setHandColor(Color.BLACK);
                break;
            case 4:
                progressGhost.setViewColor(Color.GREEN);
                progressGhost.setHandColor(Color.WHITE);
                break;
            case 5:
                progressGhost.setViewColor(Color.MAGENTA);
                progressGhost.setHandColor(Color.BLACK);
        }

        progressGhost.setVisibility(View.INVISIBLE);
        CancelSearch.setVisibility(View.INVISIBLE);
        searchUserListAdapter = new SearchUserListAdapter(requireActivity(), R.layout.layout_search_list, userSearchList);
        UserListView.setAdapter(searchUserListAdapter);

       // SetUpTabLayout();

        SearchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (SearchBar.hasFocus()) {
                    CancelSearch.setVisibility(View.VISIBLE);
                    searchGridView.setVisibility(View.INVISIBLE);
                    UserListView.setVisibility(View.VISIBLE);
                    progressGhost.setVisibility(View.INVISIBLE);
                } else {
                    CancelSearch.setVisibility(View.INVISIBLE);
                    searchGridView.setVisibility(View.VISIBLE);
                    UserListView.setVisibility(View.INVISIBLE);
                    progressGhost.setVisibility(View.VISIBLE);
                }

            }
        });

        SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CancelSearch.setVisibility(View.INVISIBLE);
                progressGhost.setVisibility(View.VISIBLE);

                progressGhost.startAnim(1000);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim().toLowerCase();
                SearchForMatch(keyword);

            }
        });

        ListViewOnClick();

        CancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBar.clearFocus();
                Log.d(TAG, "onClick: CancelSearch");
                CancelSearch.setVisibility(View.INVISIBLE);
                UserListView.setVisibility(View.INVISIBLE);
                searchGridView.setVisibility(View.VISIBLE);
                progressGhost.stopAnim();
                progressGhost.setVisibility(View.INVISIBLE);
                CloseKeyboard();
                SearchBar.setText("");
                userSearchList.clear();
            }
        });

        return view;
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

    private void ListViewOnClick(){
    UserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.d(TAG, "onItemClick: position  " + position);
            Log.d(TAG, "onItemClick: userIDSearchList.get(position) " + userIDSearchList.get(position));
            Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
            if(userIDSearchList.get(position).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                intent.putExtra("IsMyProfile",true);
            }else {

                intent.putExtra("IsMyProfile",false);
            }
            intent.putExtra(requireActivity().getString(R.string.field_username), userIDSearchList.get(position));
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    });

    }

    private void CloseKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void SearchForMatch(String Keyword) {
        userSearchList.clear();
        userIDSearchList.clear();
        searchUserListAdapter.notifyDataSetChanged();

        if (Keyword.length() > 0) {
            progressGhost.setVisibility(View.VISIBLE);
            progressGhost.startAnim(1000);


            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef.child(requireActivity().getString(R.string.dbname_users))
                    .orderByChild(getActivity().getString(R.string.field_username))
                    .startAt(Keyword).endAt(Keyword + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userSearchList.clear();
                    userIDSearchList.clear();
                    searchUserListAdapter.notifyDataSetChanged();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userSearchList.add(dataSnapshot.getValue(User.class).getUsername());
                        userIDSearchList.add(dataSnapshot.getValue(User.class).getUser_id());
                        size= size+1;
                        searchUserListAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: size first " + size);
                    }
                    progressGhost.stopAnim();
                    progressGhost.setVisibility(View.INVISIBLE);
                    CancelSearch.setVisibility(View.VISIBLE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
