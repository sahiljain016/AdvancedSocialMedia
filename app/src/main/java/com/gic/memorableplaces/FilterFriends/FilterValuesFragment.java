package com.gic.memorableplaces.FilterFriends;

import static com.gic.memorableplaces.FilterFriends.FilterFragment.Pentagon;
import static com.gic.memorableplaces.FilterFriends.FilterFragment.alsFinalUIDToFilter;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.FirstField;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.NumberOfFilters;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsCurrentFields;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsFieldsFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsFixedUIDList;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmFinal;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmSelectedValues;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isAgeFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isBooksFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isCollegeYearFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isCourseFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isGamesFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isGenderFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isHobbiesFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isLimitUsed;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isMovieFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isMusicFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isPronounsFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isSocietyFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isUltraFilterOn;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isZodiacFiltered;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.gic.memorableplaces.Adapters.FilterResultRecyclerViewAdapter;
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

import me.grantland.widget.AutofitTextView;

public class FilterValuesFragment extends Fragment implements FilterResultRecyclerViewAdapter.OnValueClickListener {
    private static final String TAG = "FilterValuesFragment";

    private long last_text_edit = 0;
    float x1 = 0, x2;
    float MIN_DISTANCE = 150;

    private Context mContext;
    private String userUID;
    private AnimatedRecyclerView rResults;
    private FilterResultRecyclerViewAdapter mResultAdapter;

    private FilterResultRecyclerViewAdapter.OnValueClickListener onValueClickListener;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private AppCompatButton reset;
    private View vDialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflaterDialog;
    private AlertDialog Alertdialog;

    private EditText SearchBar;
    private EditText MinValue;
    private EditText MaxValue;
    private ArrayList<String> alsValuesList;
    private ArrayList<String> alsSearchedValueList;
    public static ArrayList<String> alsSelectedValuesList;


    private HashMap<String, ArrayList<String>> hmValues;

    private int max, min, first = 0, CurrentFieldNO;
    private String CurrentField, CurrentFilterName, CurrentFilterNameField, FilterName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_age_filter, container, false);
        Log.d(TAG, "onCreateView: AgeFilterFragment");

        mContext = getActivity();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userUID = mAuth.getCurrentUser().getUid();
        onValueClickListener = this;
        alsValuesList = new ArrayList<>();
        alsSearchedValueList = new ArrayList<>();

        alsSelectedValuesList = new ArrayList<>();
        hmValues = new HashMap<>();

        rResults = view.findViewById(R.id.Results_Filter_1);


        builder = new AlertDialog.Builder(getActivity());
        inflaterDialog = requireActivity().getLayoutInflater();
        vDialog = inflaterDialog.inflate(R.layout.dialog_filter_search, null);
        MinValue = vDialog.findViewById(R.id.ET_LEFT_VALUE);
        MaxValue = vDialog.findViewById(R.id.ET_RIGHT_VALUE);
        reset = vDialog.findViewById(R.id.ACB_SEARCH_VALUE);
        ImageView close = vDialog.findViewById(R.id.IV_CLOSE);
        SearchBar = vDialog.findViewById(R.id.ET_Search_Bar_Filters);
        builder.setView(vDialog);
        Alertdialog = builder.create();
        Alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        close.setOnClickListener(v -> Alertdialog.dismiss());
        if (getArguments() != null) {
            FilterName = getArguments().getString(mContext.getString(R.string.field_filter));


            if (!FilterName.equals(mContext.getString(R.string.field_age))) {

                reset.setVisibility(View.GONE);
                if (FilterName.equals(mContext.getString(R.string.field_course))) {
                    isCourseFiltered = true;
                    CurrentField = mContext.getString(R.string.field_course);
                    CurrentFilterName = mContext.getString(R.string.course);
                    CurrentFieldNO = 0;

                } else if (FilterName.equals(mContext.getString(R.string.field_college_year))) {
                    isCollegeYearFiltered = true;
                    CurrentField = mContext.getString(R.string.field_college_year);

                    CurrentFilterName = mContext.getString(R.string.college_year);

                    CurrentFieldNO = 1;
                } else if (FilterName.equals(mContext.getString(R.string.field_gender))) {
                    isGenderFiltered = true;
                    CurrentField = mContext.getString(R.string.field_gender);
                    CurrentFilterName = mContext.getString(R.string.gender);

                    CurrentFieldNO = 2;
                } else if (FilterName.equals(mContext.getString(R.string.field_zodiac_sign))) {
                    isZodiacFiltered = true;
                    CurrentField = mContext.getString(R.string.field_zodiac_sign);
                    CurrentFilterName = mContext.getString(R.string.zodiac_sign);
                    CurrentFieldNO = 4;
                } else if (FilterName.equals(mContext.getString(R.string.field_hobbies))) {
                    isHobbiesFiltered = true;
                    CurrentField = mContext.getString(R.string.field_hobbies);
                    CurrentFilterName = mContext.getString(R.string.hobbies);

                    CurrentFieldNO = 5;

                } else if (FilterName.equals(mContext.getString(R.string.field_games))) {
                    isGamesFiltered = true;
                    CurrentField = mContext.getString(R.string.field_games);
                    CurrentFilterName = mContext.getString(R.string.games);
                    CurrentFilterNameField = mContext.getString(R.string.field_game_name);
                    CurrentFieldNO = 6;
                } else if (FilterName.equals(mContext.getString(R.string.field_music))) {
                    isMusicFiltered = true;
                    CurrentField = mContext.getString(R.string.field_music);
                    CurrentFilterName = mContext.getString(R.string.music);
                    CurrentFilterNameField = mContext.getString(R.string.field_track_name);
                    CurrentFieldNO = 7;
                } else if (FilterName.equals(mContext.getString(R.string.field_movie))) {
                    isMovieFiltered = true;
                    CurrentField = mContext.getString(R.string.field_movie);
                    CurrentFilterName = mContext.getString(R.string.movie);
                    CurrentFilterNameField = mContext.getString(R.string.field_movie_name);
                    CurrentFieldNO = 8;
                } else if (FilterName.equals(mContext.getString(R.string.field_books))) {
                    isBooksFiltered = true;
                    CurrentField = mContext.getString(R.string.field_books);
                    CurrentFilterName = mContext.getString(R.string.books);
                    CurrentFilterNameField = mContext.getString(R.string.field_book_name);
                    CurrentFieldNO = 9;
                } else if (FilterName.equals(mContext.getString(R.string.field_society))) {
                    isSocietyFiltered = true;
                    CurrentField = mContext.getString(R.string.field_society);
                    CurrentFilterName = mContext.getString(R.string.societies);

                    CurrentFieldNO = 10;
                } else if (FilterName.equals(mContext.getString(R.string.field_pronouns))) {
                    isPronounsFiltered = true;
                    CurrentField = mContext.getString(R.string.field_pronouns);
                    CurrentFilterName = mContext.getString(R.string.pronouns);

                    CurrentFieldNO = 11;
                }
                Log.d(TAG, "onCreateView: Number of Filters " + NumberOfFilters);
                if (isUltraFilterOn) {
                    if (TextUtils.isEmpty(FirstField)) {
                        GetSingleDetail(FilterName, null);
                    } else {
                        if (FirstField.equals(CurrentField))
                            GetSingleDetail(FilterName, null);
                        else {
                            if (!hmFinal.isEmpty()) {
                                GetDetailsForUltraFilter(null);
                            } else {
                                GetSingleDetail(FilterName, null);
                            }
                        }
                    }
                } else {
                    GetSingleDetail(FilterName, null);
                }
            } else {
                isAgeFiltered = true;
                SearchBar.setInputType(InputType.TYPE_CLASS_NUMBER);
                CurrentFilterName = mContext.getString(R.string.age);
                CurrentFieldNO = 3;
                CurrentField = mContext.getString(R.string.field_age);
                Log.d(TAG, String.format("onCreateView: hmSelectedValues: %s", hmSelectedValues));
                if (hmSelectedValues.containsKey(CurrentField) && isLimitUsed) {
                    min = Integer.parseInt(hmSelectedValues.get(CurrentField).get(0));
                    max = Integer.parseInt(hmSelectedValues.get(CurrentField).get(0));
                    for (String ageValue : Objects.requireNonNull(hmSelectedValues.get(CurrentField))) {

                        if (Integer.parseInt(ageValue) < min) {
                            min = Integer.parseInt(ageValue);
                        }
                        if (Integer.parseInt(ageValue) > max) {
                            max = Integer.parseInt(ageValue);
                        }
                    }

                }
                SetAgeFilter();
            }
            // GetSingleDetail(FilterName, null);
        }
        if (!hmSelectedValues.containsKey(CurrentField))
            hmSelectedValues.put(CurrentField, alsSelectedValuesList);

        //Log.d(TAG, String.format("onCreateView: HASH MAP: %s", hmSelectedValues));
//        if(!FirstField.equals("")) {
        if (FirstField.equals(CurrentField)) {
            NumberOfFilters = 0;
        }
        //  }
        if (isUltraFilterOn) {
            NumberOfFilters++;
            if (NumberOfFilters == 1) {
                FirstField = CurrentField;
            } else {
                if (hmFinal.isEmpty()) {
                    FirstField = CurrentField;
                }
            }
        }

//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch(event.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        x1 = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        x2 = event.getX();
//                        float deltaX = x2 - x1;
//                        if (Math.abs(deltaX) > MIN_DISTANCE)
//                        {
//                            sNavigationDrawer.openDrawer();
//                        }else{
//                            Log.d(TAG, "onTouch: cancelled");
//                        }
//                        break;
//                }
//                return false;
//            }
//        });


        PrintList(true, null, null, null);
        Pentagon.setOnClickListener(v -> {
            // this line


            if (!CurrentField.equals(mContext.getString(R.string.field_age))) {
                AutofitTextView ATV_RIGHT_VALUE = vDialog.findViewById(R.id.ATV_RIGHT_VALUE);
                AutofitTextView ATV_LEFT_VALUE = vDialog.findViewById(R.id.ATV_LEFT_VALUE);

                MinValue.setVisibility(View.GONE);
                MaxValue.setVisibility(View.GONE);
                ATV_RIGHT_VALUE.setVisibility(View.GONE);
                ATV_LEFT_VALUE.setVisibility(View.GONE);
                MinValue.setText(String.valueOf(min));
                MaxValue.setText(String.valueOf(max));
            }
            SearchBar.setHint("Search a particular " + FilterName);
            onSearchMade();
            if (isLimitUsed) {
                Log.d(TAG, "onCreateView: min: " + min);
                Log.d(TAG, "onCreateView: max: " + max);
                MinValue.setText(String.valueOf(min));
                MaxValue.setText(String.valueOf(max));
                reset.setText("Clear All");
            }
            Alertdialog.show();
        });
        return view;
    }


    private void PrintList(boolean PrintAll, String Name, ArrayList<String> PrintMe, HashMap<String, ArrayList<String>> PrintHashMe) {
        if (PrintAll) {
            Log.d(TAG, String.format("PrintList: VALUES LIST: %s", alsValuesList));
            Log.d(TAG, String.format("PrintList: FINAL UID TO FILTER List: %s", alsFinalUIDToFilter));
            Log.d(TAG, String.format("PrintList: SELECTED VALUES List: %s", alsSelectedValuesList));
            Log.d(TAG, String.format("PrintList: VALUES HASH MAP: %s", hmValues));
            Log.d(TAG, String.format("PrintList: MAX & MIN: %d %d", max, min));
            Log.d(TAG, String.format("PrintList: SELECTED VALUES HASH MAP: %s", hmSelectedValues));
        } else if (PrintMe != null) {
            Log.d(TAG, String.format("PrintList: %s: %s", Name, PrintMe));
        } else if (PrintHashMe != null) {
            Log.d(TAG, String.format("PrintList: %s: %s", Name, PrintHashMe));
        }

    }

    private void onSearchMade() {
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                if (!TextUtils.isEmpty(SearchBar.getText().toString())) {
                    try {
                        reset.setEnabled(false);
                        MinValue.setText("");
                        MaxValue.setText("");
                        alsSearchedValueList.clear();
                        if (alsValuesList.contains(SearchBar.getText().toString())) {
                            alsSearchedValueList.add(SearchBar.getText().toString());
                        } else {
                            alsSearchedValueList.add(mContext.getString(R.string.No_filter_results_available));
                        }

                        //  Log.d(TAG, "run: Filter Called 1");

                        mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsSearchedValueList, hmValues, hmSelectedValues, onValueClickListener, false);
                        rResults.setItemAnimator(new DefaultItemAnimator());
                        rResults.setAdapter(mResultAdapter);
                        mResultAdapter.notifyDataSetChanged();
                        rResults.scheduleLayoutAnimation();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged: ");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
//                if (TextUtils.isEmpty(s)) {
//
//                }
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void SetAgeFilter() {

        MinValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reset.setEnabled(true);
                SearchBar.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Runnable runnable = () -> reset.setOnClickListener(v -> {

            Log.d(TAG, "onClick: Enetered Reset on click ");
            if (reset.getText().toString().equals("Apply")) {
                isLimitUsed = true;
                min = Integer.parseInt(MinValue.getText().toString());
                max = Integer.parseInt(MaxValue.getText().toString());
                if (min == max) {
                    Toast.makeText(mContext, "Both entered values are same!", Toast.LENGTH_SHORT).show();
                } else if (min > max) {
                    Toast.makeText(mContext, "Minimum Value is greater than Maximum value!", Toast.LENGTH_SHORT).show();

                } else {
                    SearchBar.setEnabled(true);

                    for (String Value : alsSelectedValuesList) {
                        DeleteFromHashMap(Value);
                    }
                    alsSelectedValuesList.clear();

                    //Iterate Over All Values
                    Log.d(TAG, String.format("onClick: alsValuesList %s", alsValuesList));
                    for (String ageValue : alsValuesList) {
                        // Log.d(TAG, "valueChanged: ageValue " + ageValue);
                        if (Integer.parseInt(ageValue) < max && Integer.parseInt(ageValue) > min) {
                            Log.d(TAG, "onClick: ageValue " + ageValue);
                            alsSelectedValuesList.add(ageValue);
                            alsFieldsFiltered.add(CurrentField);
                            DevelopFilter();
                        }

                    }
                    Objects.requireNonNull(hmSelectedValues.put(CurrentField, alsSelectedValuesList));
                    //Log.d(TAG, String.format("onItemClick: alsFieldsFiltered: %s", alsFieldsFiltered));
                    if (!alsCurrentFields.contains(CurrentFilterName))
                        alsCurrentFields.add(CurrentFilterName);

                    if (alsSelectedValuesList.size() > 0) {

                        mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsSelectedValuesList, hmValues, hmSelectedValues, onValueClickListener, true);
                        rResults.setItemAnimator(new DefaultItemAnimator());
                        rResults.setAdapter(mResultAdapter);
                        mResultAdapter.notifyDataSetChanged();
                        rResults.scheduleLayoutAnimation();
                        reset.setText("Clear All");
                        Alertdialog.dismiss();
                    }
                }


            } else {
                isLimitUsed = false;
                MinValue.setText("");
                MaxValue.setText("");
                for (String ageValue : alsSelectedValuesList) {

                    DeleteFromHashMap(ageValue);
                    alsFieldsFiltered.remove(CurrentField);
                    alsCurrentFields.remove(CurrentFilterName);
                }
                Objects.requireNonNull(hmSelectedValues.get(CurrentField)).clear();
                hmSelectedValues.remove(CurrentField);
                alsSelectedValuesList.clear();
                reset.setText("Apply");

                mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsValuesList, hmValues, hmSelectedValues, onValueClickListener, false);
                rResults.setItemAnimator(new DefaultItemAnimator());
                rResults.setAdapter(mResultAdapter);
                mResultAdapter.notifyDataSetChanged();
                rResults.scheduleLayoutAnimation();
                Alertdialog.dismiss();

            }


        });

        Log.d(TAG, "SetAgeFilter: First Field: " + FirstField);
        if (isUltraFilterOn) {
            if (TextUtils.isEmpty(FirstField)) {
                GetSingleDetail(mContext.getString(R.string.field_age), runnable);
            } else {
                if (FirstField.equals(CurrentField))
                    GetSingleDetail(mContext.getString(R.string.field_age), runnable);
                else {
                    if (!hmFinal.isEmpty()) {
                        GetDetailsForUltraFilter(null);
                    } else {
                        GetSingleDetail(mContext.getString(R.string.field_age), null);
                    }
                }
            }
        } else {
            GetSingleDetail(mContext.getString(R.string.field_age), runnable);
        }


    }


    private void DevelopFilter() {
        HashMap<String, ArrayList<String>> hmTemp;
        ArrayList<String> alsTemp;

        // Log.d(TAG, String.format("DevelopFilter: Selected Values List: %s", alsSelectedValuesList));
        for (int i = 0; i < alsSelectedValuesList.size(); i++) {


            // Log.d(TAG, String.format("DevelopFilter: hmValues %s", hmValues));
            // Log.d(TAG, String.format("DevelopFilter: hmFinal %s", hmFinal));
            for (int j = 0; j < hmValues.get(alsSelectedValuesList.get(i)).size(); j++) {
                if (!hmFinal.containsKey(hmValues.get(alsSelectedValuesList.get(i)).get(j))) {


                    alsTemp = new ArrayList<>();
                    alsTemp.add(alsSelectedValuesList.get(i));
                    hmTemp = new HashMap<>();
                    hmTemp.put(CurrentField, alsTemp);
                    hmFinal.put(hmValues.get(alsSelectedValuesList.get(i)).get(j), hmTemp);
                } else {
                    if (!hmFinal.get(hmValues.get(alsSelectedValuesList.get(i)).get(j)).containsKey(CurrentField)) {
                        alsTemp = new ArrayList<>();
                        alsTemp.add(alsSelectedValuesList.get(i));
                        hmFinal.get(hmValues.get(alsSelectedValuesList.get(i)).get(j)).put(CurrentField, alsTemp);

                    } else {
                        if (!hmFinal.get(hmValues.get(alsSelectedValuesList.get(i)).get(j)).get(CurrentField).contains(alsSelectedValuesList.get(i))) {
                            hmFinal.get(hmValues.get(alsSelectedValuesList.get(i)).get(j)).get(CurrentField).add(alsSelectedValuesList.get(i));
                        }

                    }
                }
            }
        }
        if (isUltraFilterOn) {
            alsFixedUIDList.clear();
            alsFixedUIDList.addAll(hmFinal.keySet());
        }
        Log.d(TAG, "DevelopFilter: hmFinal: " + hmFinal);
        Log.d(TAG, String.format("DevelopFilter: alsFixedUIDList: %s", alsFixedUIDList));
        // Log.d(TAG, String.format("DevelopFilter: Final Filtered User ID List %s", mFinalUserIDList));
    }


    private void DeleteFromHashMap(String RemoveThisValue) {

        // Log.d(TAG, String.format("DevelopFilter: mUsersHashMap %s", mAgeAndUserIDHashMap));
        PrintList(false, "HASHMAP VALUES", null, hmValues);
        Log.d(TAG, String.format("DeleteFromHashMap: Remove This Value: %s", RemoveThisValue));
        for (int j = 0; j < hmValues.get(RemoveThisValue).size(); j++) {
            String UID = hmValues.get(RemoveThisValue).get(j);
            //Log.d(TAG, "DeleteFromHashMap: UID: " + UID);
            PrintList(false, "UID FROM HM VALUE: ", hmValues.get(RemoveThisValue), null);
            //Log.d(TAG, String.format("DeleteFromHashMap: Current FIeld List %s", hmFinal.get(UID)));
            Log.d(TAG, String.format("DeleteFromHashMap: Final HashMap: %s", hmFinal));
            hmFinal.get(UID).get(CurrentField).remove(RemoveThisValue);
            Log.d(TAG, String.format("DeleteFromHashMap: Final HashMap after: %s", hmFinal));
            //Log.d(TAG, String.format("DeleteFromHashMap: HM FINAL: %s", hmFinal));
            //Log.d(TAG, String.format("DeleteFromHashMap: Current Field List%s", hmFinal.get(UID).get(CurrentField)));
            for (String FilterName : alsFieldsFiltered) {
                if (hmFinal.get(UID).containsKey(FilterName)) {
                    //    Log.d(TAG, String.format("DeleteFromHashMap: Filter Name %s", FilterName));
                    //      Log.d(TAG, String.format("DeleteFromHashMap: IF EMPTY: %s", hmFinal.get(UID).get(FilterName)));

                    if (hmFinal.get(UID).get(FilterName).isEmpty()) {

                        hmFinal.get(UID).remove(FilterName);
                        //            Log.d(TAG, String.format("DeleteFromHashMap: After removal: %s", hmFinal.get(UID)));

                    }
                }
            }
            if (hmFinal.get(UID).isEmpty()) {
                hmFinal.remove(UID);
                if (FirstField.equals(CurrentField))
                    alsFixedUIDList.remove(UID);
            }
            Log.d(TAG, String.format("DeleteFromHashMap: Final HashMap: %s", hmFinal));
        }
        //Log.d(TAG, String.format("DeleteFromHashMap: hmTFinal: %s", hmTFinal));
        //alsValuesListForUID.remove(RemoveThisValue);
        //Log.d(TAG, String.format("DeleteFromHashMap: hmTFinal: %s", hmTFinal));

    }


    private void GetSingleDetail(String Detail, final Runnable runnable) {
        Query query = myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(Detail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Value = "";
                ArrayList<String> mUserIDList = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (CurrentFieldNO == 3 || CurrentFieldNO == 4 || CurrentFieldNO == 2 || CurrentFieldNO == 11
                            || CurrentFieldNO == 1 || CurrentFieldNO == 10 || CurrentFieldNO == 5) {

                        Value = dataSnapshot.getKey();
                        alsValuesList.add(dataSnapshot.getKey());
                        mUserIDList = new ArrayList<>();
                        for (DataSnapshot MembersSnapshot : dataSnapshot.getChildren()) {
                            mUserIDList.add(MembersSnapshot.getKey());
                        }
                    } else if (CurrentFieldNO == 0) {
                        for (DataSnapshot MembersSnapshot : dataSnapshot.getChildren()) {

                            Log.d(TAG, "onDataChange: UID " + MembersSnapshot.toString());
                            if (MembersSnapshot.getKey().equals(mContext.getString(R.string.field_course))) {
                                Value = MembersSnapshot.getValue().toString();
                                alsValuesList.add(Value);
                            }
                            mUserIDList = new ArrayList<>();

                            if (MembersSnapshot.getKey().equals("members")) {
                                for (DataSnapshot Members : MembersSnapshot.getChildren())
                                    mUserIDList.add(Members.getKey());
                            }
                        }

                    } else if (CurrentFieldNO == 6 || CurrentFieldNO == 7 || CurrentFieldNO == 8 || CurrentFieldNO == 9) {

                        Value = dataSnapshot.getKey();
                        alsValuesList.add(Value);
                        mUserIDList = new ArrayList<>();
                        for (DataSnapshot MembersSnapshot : dataSnapshot.getChildren()) {

                            if (MembersSnapshot.getKey().equals("members")) {
                                for (DataSnapshot Members : MembersSnapshot.getChildren())
                                    mUserIDList.add(Members.getKey());
                            }
                        }
                    }

                    if (mUserIDList.contains(userUID)) {
                        if (mUserIDList.size() > 1) {
                            mUserIDList.remove(userUID);
                            hmValues.put(Value, mUserIDList);
                        }else{
                            alsValuesList.remove(Value);
                        }
                    } else {
                        hmValues.put(Value, mUserIDList);
                    }

                }


                //  Log.d(TAG, String.format("onDataChange: ValuesHashMap %s", ValuesHashMap));
                //Log.d(TAG, String.format("onDataChange: Selected Values Hash Map: %s", hmSelectedValues));
                if (runnable != null) {
                    runnable.run();
                }
                //  Log.d(TAG, "onDataChange: ");
                //Log.d(TAG, "onClick: Filter Called 3  ");

                mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsValuesList, hmValues, hmSelectedValues, onValueClickListener, false);
                rResults.setItemAnimator(new

                        DefaultItemAnimator());
                rResults.setAdapter(mResultAdapter);
                mResultAdapter.notifyDataSetChanged();
                rResults.scheduleLayoutAnimation();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetDetailsForUltraFilter(final Runnable runnable) {

        if (CurrentFieldNO != 0) {
            Query query = myRef.child(mContext.getString(R.string.dbname_user_card));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (String Value : alsFixedUIDList) {
                        ArrayList<String> TempUIDList;

                        if (CurrentFieldNO == 3 || CurrentFieldNO == 4 || CurrentFieldNO == 2 || CurrentFieldNO == 11 || CurrentFieldNO == 1) {
                            TempUIDList = new ArrayList<>();
                            for (DataSnapshot Fields : dataSnapshot.child(Value).child(mContext.getString(R.string.field)).getChildren()) {
                                Log.d(TAG, "onDataChange: Fields: " + Fields);
                                if (Objects.equals(Objects.requireNonNull(Fields.getKey()), CurrentField)) {
                                    if (!"N/A".equals(Objects.requireNonNull(Fields.getValue()).toString()) && !alsValuesList.contains(Fields.getValue().toString())) {

                                        alsValuesList.add(Objects.requireNonNull(Fields.getValue()).toString());
                                        TempUIDList.add(Value);
                                        hmValues.put(Fields.getValue().toString(), TempUIDList);
                                    }
                                }
                            }
                        } else if (CurrentFieldNO == 5) {
                            for (DataSnapshot Fields : dataSnapshot.child(Value).child(mContext.getString(R.string.field)).getChildren()) {
                                Log.d(TAG, "onDataChange: Fields: " + Fields);
                                if (Objects.equals(Objects.requireNonNull(Fields.getKey()), CurrentField)) {
                                    for (DataSnapshot Values : Fields.getChildren()) {
                                        if (!"N/A".equals(Objects.requireNonNull(Values.getValue()).toString()) && !alsValuesList.contains(Values.getValue().toString())) {
                                            TempUIDList = new ArrayList<>();
                                            alsValuesList.add(Objects.requireNonNull(Values.getValue()).toString());
                                            TempUIDList.add(Value);
                                            hmValues.put(Values.getValue().toString(), TempUIDList);
                                        }
                                    }
                                }
                            }
                        } else if (CurrentFieldNO == 6 || CurrentFieldNO == 7 || CurrentFieldNO == 8 || CurrentFieldNO == 9) {
                            for (DataSnapshot Fields : dataSnapshot.child(Value).child(mContext.getString(R.string.field)).getChildren()) {
                                Log.d(TAG, "onDataChange: Fields: " + Fields);
                                if (Objects.equals(Objects.requireNonNull(Fields.getKey()), CurrentField)) {
                                    if (!Fields.getValue().toString().equals("N/A")) {
                                        for (DataSnapshot Values : Fields.getChildren()) {
                                            for (DataSnapshot Game : Values.getChildren()) {
                                                if (Game.getKey().equals(CurrentFilterNameField)) {
                                                    TempUIDList = new ArrayList<>();
                                                    alsValuesList.add(Objects.requireNonNull(Game.getValue()).toString());
                                                    TempUIDList.add(Value);
                                                    hmValues.put(Game.getValue().toString(), TempUIDList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (alsValuesList.isEmpty()) {
                        alsValuesList.add(mContext.getString(R.string.No_filter_results_available));
                        if (NumberOfFilters > 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setMessage("All the values selected in " + FirstField + " have no common " + CurrentField + " for selection! Change the filters a bit to find the unique people.");
                            builder.setTitle("No Values Found!");
                            builder.setCancelable(false);

                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                    if (runnable != null)
                        runnable.run();

                    mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsValuesList, hmValues, hmSelectedValues, onValueClickListener, false);
                    rResults.setItemAnimator(new DefaultItemAnimator());
                    rResults.setAdapter(mResultAdapter);
                    mResultAdapter.notifyDataSetChanged();
                    rResults.scheduleLayoutAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Query query = myRef.child(mContext.getString(R.string.dbname_users));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (String Value : alsFixedUIDList) {
                        ArrayList<String> TempUIDList;
                        TempUIDList = new ArrayList<>();
                        for (DataSnapshot Fields : dataSnapshot.child(Value).getChildren()) {
                            Log.d(TAG, "onDataChange: Fields: " + Fields);
                            if (Objects.equals(Objects.requireNonNull(Fields.getKey()), CurrentField)) {
                                if (!"N/A".equals(Objects.requireNonNull(Fields.getValue()).toString()) && !alsValuesList.contains(Fields.getValue().toString())) {

                                    alsValuesList.add(Objects.requireNonNull(Fields.getValue()).toString());
                                    TempUIDList.add(Value);
                                    hmValues.put(Fields.getValue().toString(), TempUIDList);
                                }
                            }
                        }

                    }
                    if (alsValuesList.isEmpty()) {
                        alsValuesList.add(mContext.getString(R.string.No_filter_results_available));
                        if (NumberOfFilters > 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setMessage("All the values selected in " + FirstField + " have no common " + CurrentField + " for selection! Change the filters a bit to find the unique people.");
                            builder.setTitle("No Values Found!");
                            builder.setCancelable(false);

                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                    if (runnable != null)
                        runnable.run();

                    mResultAdapter = new FilterResultRecyclerViewAdapter(CurrentField, alsValuesList, hmValues, hmSelectedValues, onValueClickListener, false);
                    rResults.setItemAnimator(new DefaultItemAnimator());
                    rResults.setAdapter(mResultAdapter);
                    mResultAdapter.notifyDataSetChanged();
                    rResults.scheduleLayoutAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private boolean CheckIfArrayListAreSame(ArrayList<String> als1, ArrayList<String> als2) {
        boolean result = false;
        if (als1.size() == als2.size()) {
            for (String Value : als2) {
                if (als1.contains(Value)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }


    @Override
    public void onItemClick(int position, RelativeLayout relativeLayout, ImageView UnChecked, ImageView Tick, AutofitTextView Value) {

        if (!Value.getText().toString().equals(mContext.getString(R.string.No_filter_results_available))) {
            if (Tick.getVisibility() != View.VISIBLE) {
                relativeLayout.setBackgroundResource(R.drawable.blue_green_gradient);
                Tick.setVisibility(View.VISIBLE);

                alsSelectedValuesList.add(alsValuesList.get(position));

                Objects.requireNonNull(hmSelectedValues.put(CurrentField, alsSelectedValuesList));
                // Log.d(TAG, "onItemClick: ");
                Log.d(TAG, "onItemClick: Number");

                Log.d(TAG, "onItemClick: hmValues: " + hmValues);
                Log.d(TAG, "onItemClick: alsValuesList: " + alsValuesList);
                DevelopFilter();


                alsFieldsFiltered.add(CurrentField);
                //Log.d(TAG, String.format("onItemClick: alsFieldsFiltered: %s", alsFieldsFiltered));
                if (!alsCurrentFields.contains(CurrentFilterName))
                    alsCurrentFields.add(CurrentFilterName);
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE);
                Tick.setVisibility(View.INVISIBLE);
                Objects.requireNonNull(hmSelectedValues.get(CurrentField)).remove(alsValuesList.get(position));
                alsSelectedValuesList.remove(alsValuesList.get(position));
                DeleteFromHashMap(alsValuesList.get(position));
                alsFieldsFiltered.remove(CurrentField);
                alsCurrentFields.remove(CurrentFilterName);

            }

            //Log.d(TAG, String.format("onItemClick: Selected Values HM %s", hmSelectedValues));
        }

    }


}
