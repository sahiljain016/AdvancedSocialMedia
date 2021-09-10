package com.gic.memorableplaces.FilterFriends;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.CardDetailsRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.DespAndQARecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

;

public class DataDisplayFragment extends Fragment {
    private static final String TAG = "DataDisplayFragment";
    private Context mContext;

    private ImageView ivShowMatchingValues;
    private TextView tvTitle;
    private RecyclerView rDescription;
    private AnimatedRecyclerView rDetails;

    private CardDetailsRecyclerViewAdapter raDetailAdapter;
    private DespAndQARecyclerViewAdapter raDescriptionAdapter;
    private ZoomRecyclerLayout lmDescription;

    private String UserUID;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private HashMap<String, ArrayList<String>> hmAllDetails;
    private ArrayList<String> alsDespLists;
    // private CardView cvCollegeCard,cvClassGroup,cvCRCard,cvMap,cvQuestion,cvBooks,cvSocieties,cvWebsite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);
        Log.d(TAG, "onCreateView: AgeFilterFragment");

        mContext = getActivity();
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        ivShowMatchingValues = view.findViewById(R.id.CD_Show_matching_values);
        tvTitle = view.findViewById(R.id.CD_Heading);
        rDescription = view.findViewById(R.id.CD_RecyclerView_DESP_QA);
        rDetails = view.findViewById(R.id.CD_RecylerView_Details);

        hmAllDetails = new HashMap<>();
        alsDespLists = new ArrayList<>();

        if (getArguments() != null) {
            UserUID = getArguments().getString(mContext.getString(R.string.field_user_id));
        }

        GetDescription();
        GetCardDetails();

        return view;
    }

    private void GetDescription() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(UserUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Children : snapshot.getChildren()) {
                    if (Objects.equals(Children.getKey(), mContext.getString(R.string.field_card_bio))) {
                        if (!TextUtils.isEmpty(Children.getValue().toString())) {
                            alsDespLists.add(Children.getValue().toString());
                        } else {
                            alsDespLists.add("N/A");
                        }
                        rDescription.setHasFixedSize(true);
                        lmDescription = new ZoomRecyclerLayout(mContext);
                        lmDescription.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rDescription.setLayoutManager(lmDescription);
                        raDescriptionAdapter = new DespAndQARecyclerViewAdapter(alsDespLists, mContext, false, "","",
                                "","","",null,null,null);
                        rDescription.setAdapter(raDescriptionAdapter);
                    }
                    if (Children.getKey().equals(mContext.getString(R.string.field_display_name))) {
                        if (Objects.requireNonNull(Children.getValue()).toString().contains(" "))
                            tvTitle.setText(Children.getValue().toString().substring(0, Children.getValue().toString().indexOf(" ")) + "'s Card");
                        else
                            tvTitle.setText(Children.getValue().toString() + "'s Card");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetCardDetails() {
        Log.d(TAG, "GetCardDetails: entered :" + UserUID);
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(UserUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> ValueList = null;
                for (DataSnapshot PrivacyBuilder : snapshot.child(mContext.getString(R.string.field_is_private)).getChildren()) {
                    if (Objects.requireNonNull(PrivacyBuilder.getValue()).toString().equals(mContext.getString(R.string.field_private))) {
                        ValueList = new ArrayList<>();
                        ValueList.add(mContext.getString(R.string.field_private));
                        hmAllDetails.put(PrivacyBuilder.getKey(), ValueList);
                    }
                }

                //    Log.d(TAG, String.format("onDataChange: hmAllDetails: %s", hmAllDetails));

                for (DataSnapshot DetailBuilder : snapshot.child(mContext.getString(R.string.field)).getChildren()) {
                    String field = DetailBuilder.getKey();
                    if (field.equals(mContext.getString(R.string.field_age)) || field.equals(mContext.getString(R.string.field_gender))
                            || field.equals(mContext.getString(R.string.field_birth_date)) || field.equals(mContext.getString(R.string.field_class_representative))
                            || field.equals(mContext.getString(R.string.field_college_year)) || field.equals(mContext.getString(R.string.field_pronouns)) ||
                            field.equals(mContext.getString(R.string.zodiac_sign))) {
                        if (hmAllDetails.containsKey(field)) {
                            Objects.requireNonNull(hmAllDetails.get(field)).add(DetailBuilder.getValue().toString());
                        } else {
                            ValueList = new ArrayList<>();
                            ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                            hmAllDetails.put(field, ValueList);
                        }
                    } else if (field.equals(mContext.getString(R.string.field_games)) || field.equals(mContext.getString(R.string.field_books))
                            || field.equals(mContext.getString(R.string.field_movie)) || field.equals(mContext.getString(R.string.field_music))
                            || field.equals(mContext.getString(R.string.field_other_posts)) || field.equals(mContext.getString(R.string.field_society))) {
                        if (DetailBuilder.getValue().toString().equals("N/A") || DetailBuilder.getValue().toString().equals("false")) {
                            if (hmAllDetails.containsKey(field)) {
                                Objects.requireNonNull(hmAllDetails.get(field)).add(DetailBuilder.getValue().toString());
                            } else {
                                ValueList = new ArrayList<>();
                                ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                                hmAllDetails.put(field, ValueList);
                            }
                        } else {
                            if (!hmAllDetails.containsKey(field)) {
                                ValueList = new ArrayList<>();
                            }
                            for (DataSnapshot Children : DetailBuilder.getChildren()) {
                                for (DataSnapshot Details : Children.getChildren()) {
                                    if (Objects.requireNonNull(Details.getKey()).contains("name")) {
                                        if (hmAllDetails.containsKey(field)) {
                                            Objects.requireNonNull(hmAllDetails.get(field)).add(Objects.requireNonNull(Details.getValue()).toString());
                                        } else {
                                            Objects.requireNonNull(ValueList).add(Objects.requireNonNull(Details.getValue()).toString());

                                        }


                                    }
                                }
                            }
                            if (!hmAllDetails.containsKey(field))
                                hmAllDetails.put(field, ValueList);
                        }

                    } else if (field.equals(mContext.getString(R.string.field_hobbies)) || field.equals(mContext.getString(R.string.Top5Platform))) {
                        if (DetailBuilder.getValue().toString().equals("N/A") || DetailBuilder.getValue().toString().equals("false")) {
                            if (hmAllDetails.containsKey(field)) {
                                Objects.requireNonNull(hmAllDetails.get(field)).add(DetailBuilder.getValue().toString());
                            } else {
                                ValueList = new ArrayList<>();
                                ValueList.add(Objects.requireNonNull(DetailBuilder.getValue()).toString());
                                hmAllDetails.put(field, ValueList);
                            }
                        } else {
                            if (!hmAllDetails.containsKey(field))
                                ValueList = new ArrayList<>();

                            for (DataSnapshot Details : DetailBuilder.getChildren()) {


                                if (hmAllDetails.containsKey(field)) {
                                    Objects.requireNonNull(hmAllDetails.get(field)).add(Details.getValue().toString());
                                } else {

                                    assert ValueList != null;
                                    ValueList.add(Objects.requireNonNull(Details.getValue()).toString());

                                }
                            }
                            hmAllDetails.put(field, ValueList);
                        }
                    }
                }
                Log.d(TAG, String.format("onDataChange: hmAllDetails: %s", hmAllDetails));
                ArrayList<String> alsLeftKeySet = new ArrayList<>(hmAllDetails.keySet());
                ArrayList<String> alsRightKeySet = new ArrayList<>();
                alsLeftKeySet.remove(mContext.getString(R.string.field_birth_date));
                alsLeftKeySet.remove(mContext.getString(R.string.Top5Platform));
                alsLeftKeySet.remove(mContext.getString(R.string.field_other_posts));
                alsLeftKeySet.remove(mContext.getString(R.string.field_pronouns));

                int count = 0;
                for (String Value : alsLeftKeySet) {
                    if (count < 5) {
                        alsRightKeySet.add(Value);
                        count++;
                    }

                }

                alsLeftKeySet.removeAll(alsRightKeySet);

                Log.d(TAG, String.format("onDataChange: alsKeySet: %s", alsLeftKeySet));

                raDetailAdapter = new CardDetailsRecyclerViewAdapter(hmAllDetails, alsLeftKeySet, alsRightKeySet, mContext);
                rDetails.setItemAnimator(new DefaultItemAnimator());
                rDetails.setAdapter(raDetailAdapter);
                raDetailAdapter.notifyDataSetChanged();
                rDetails.scheduleLayoutAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
