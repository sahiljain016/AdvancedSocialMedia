package com.gic.memorableplaces.Home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.DataModels.Photo;
import com.gic.memorableplaces.DataModels.Video;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.Adapters.MainFeedRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SocialFeedFragment extends Fragment {
    private static final String TAG = "SocialFeedFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
//    private LVNews block;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private ArrayList<Object> MediaList;
    private HashMap<String, Object> MediaHashMap;
    private Context mContext;
    private String ProfileUrl, Username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_feed, container, false);
        Log.d(TAG, "onCreateView: SocialFeedFragment");


        mRecyclerView = view.findViewById(R.id.MainFeedRecyclerView);
//        block = view.findViewById(R.id.LV_block);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        MediaHashMap = new HashMap<>();
//        block.startAnim(1000);
        setSelfFollowing();
        GetCurrentUserDetails();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        addContent();

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void addContent() {

        Query query = myRef.child(mContext.getString(R.string.dbname_following))
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MediaHashMap.clear();
                // MediaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    getPhoto(ds, MediaHashMap);
                    getVideo(ds, MediaHashMap);


                }
                android.os.Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    Log.d(TAG, String.format("onDataChange: MediahashMap %s", MediaHashMap));
                    Map<String, Object> sortedMedia = new TreeMap<>(MediaHashMap);
                    MediaList = new ArrayList<>(sortedMedia.values());
                    Collections.reverse(MediaList);
                    Log.d(TAG, String.format("onDataChange: MediaList %s", MediaList));
                    mAdapter = new MainFeedRecyclerViewAdapter(MediaList,ProfileUrl,Username, mContext, getActivity());
                    mRecyclerView.setAdapter(mAdapter);
//                        block.stopAnim();
//                        block.setVisibility(View.GONE);
                }, 1000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void GetCurrentUserDetails() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ProfileUrl = Objects.requireNonNull(snapshot.child(mContext.getString(R.string.field_profile_photo)).getValue()).toString();
                Username = Objects.requireNonNull(snapshot.child(mContext.getString(R.string.field_username)).getValue()).toString();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getPhoto(DataSnapshot ds, final HashMap<String, Object> MediaHashMap) {

        Log.d(TAG, "getPhoto: ds key " + ds.getKey());

        Query query = myRef.child(mContext.getString(R.string.dbname_photos)).orderByChild(mContext.getString(R.string.field_user_id)).equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Photo photo = ds.getValue(Photo.class);
                    Log.d(TAG, "onDataChange: date_added " + Objects.requireNonNull(photo).getDate_added());
                    Log.d(TAG, "onDataChange: photo " + photo);
                    assert photo != null;
                    MediaHashMap.put(photo.getDate_added(), photo);
//                    mAdapter.notifyDataSetChanged();
                    //Log.d(TAG, "onDataChange: MediaHashMap " + MediaHashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getVideo(DataSnapshot ds, final HashMap<String, Object> MediaHashMap) {

        Query query = myRef.child(mContext.getString(R.string.dbname_videos))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Video video = ds.getValue(Video.class);
                    assert video != null;
                    MediaHashMap.put(video.getDate_added(), video);
                    // mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setSelfFollowing() {

        Query query = myRef.child(mContext.getString(R.string.dbname_following)).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .orderByChild(mContext.getString(R.string.field_user_id)).equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    new FirebaseMethods(getActivity()).addNewFollowerAndFollowing(mAuth.getCurrentUser().getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
