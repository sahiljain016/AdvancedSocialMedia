package com.gic.memorableplaces.FilterFriends;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.Adapters.FiltersRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.SlideMenuDrawer.MenuItem;
import com.gic.memorableplaces.CustomLibs.SlideMenuDrawer.SNavigationDrawer;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.GetFilteredUID;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.GetRandomUserUIDs;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsCurrentFields;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsFieldsFiltered;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.alsFixedUIDList;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmFinal;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmSelectedFiltersHashMap;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmSelectedValues;
import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.isUltraFilterOn;

public class FilterFragment extends Fragment implements FiltersRecyclerViewAdapter.OnFilterClickListener {
    private static final String TAG = "FilterFragment";

    private long last_text_edit = 0;
    float x1 = 0, x2;
    float MIN_DISTANCE = 150;
    public static Context mContext;
    public static String CurrentFrag = "Age";

    public static AlertDialog.Builder builder;
    private AlertDialog ChooseFilterDialog;
    private ConstraintLayout CL_MAIN;
    private FrameLayout frameLayout;

    public static AutofitTextView APPLY_FILTERS, TV_CLEAR_FILTER;
    private ImageButton ChangeFilterType;
    public static ImageView Pentagon;
    public static SNavigationDrawer sNavigationDrawer;

    private LayoutInflater inflaterDialog;
    public static FiltersRecyclerViewAdapter.OnFilterClickListener onFilterClickListener;

    public static ArrayList<String> alsFilterName;
    public static ArrayList<String> alsSelectedFilterNames;
    public static ArrayList<Integer> aliIconsList;
    public static ArrayList<String> alsFinalUIDToFilter;
    public static List<MenuItem> menuItems;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;


    // private CardView cvCollegeCard,cvClassGroup,cvCRCard,cvMap,cvQuestion,cvBooks,cvSocieties,cvWebsite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        Log.d(TAG, "onCreateView: FilterFragment");

        mContext = getActivity();

        alsFilterName = new ArrayList<>();
        aliIconsList = new ArrayList<>();
        inflaterDialog = requireActivity().getLayoutInflater();

        builder = new AlertDialog.Builder(getActivity());

        APPLY_FILTERS = view.findViewById(R.id.TV_APPLY_FILTER);
        CL_MAIN = view.findViewById(R.id.CL_MAIN_FF);
        Pentagon = view.findViewById(R.id.pentagon);


        sNavigationDrawer = view.findViewById(R.id.navigationDrawer);
        ChangeFilterType = view.findViewById(R.id.IV_CHANGE_FILTER_BOLT);
        TV_CLEAR_FILTER = view.findViewById(R.id.TV_CLEAR_FILTER);
        frameLayout = view.findViewById(R.id.FrameLayoutDisplayFilters);


        alsFinalUIDToFilter = new ArrayList<>();
        alsSelectedFilterNames = new ArrayList<>();
        menuItems = new ArrayList<>();

        onFilterClickListener = this;

        CurrentFrag = mContext.getString(R.string.age);
        InitialiseFilters();

        InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);

        Fragment fragment = new FilterValuesFragment();
        String FragmentName = mContext.getString(R.string.filter_1_fragment);
        Bundle bundle = new Bundle();
        sNavigationDrawer.setAppbarTitleTV("Age Filter");
        sNavigationDrawer.setAppbarColor(Color.parseColor("#CCFFFFFF"));
        bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_age));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.FrameLayoutDisplayFilters, Objects.requireNonNull(fragment));
        transaction.addToBackStack(FragmentName);
        transaction.commit();


        TV_CLEAR_FILTER.setOnClickListener(v -> {

            if (!hmFinal.isEmpty()) {
                hmFinal.clear();
                alsFieldsFiltered.clear();
                alsFilterName.clear();
                aliIconsList.clear();
                InitialiseFilters();
                InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);
                alsFixedUIDList.clear();
                alsCurrentFields.clear();
                hmSelectedValues.clear();
            } else {
                Toast.makeText(getActivity(), "No filters are applied!", Toast.LENGTH_SHORT).show();
            }
            GetRandomUserUIDs();
            requireFragmentManager().beginTransaction().remove(FilterFragment.this).commit();


        });


        View vDialog = inflaterDialog.inflate(R.layout.layout_change_filter_type, null);  // this line
        builder.setView(vDialog);
        ChooseFilterDialog = builder.create();
        ChooseFilterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ChooseFilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ChooseFilterDialog.setCancelable(false);
        ChooseFilterDialog.setCanceledOnTouchOutside(false);
        ChooseFilterDialog.show();


        ChangeFilterType.setOnClickListener(v -> ChooseFilterDialog.show());


        final ImageView CrossDialog = vDialog.findViewById(R.id.Cross);
        CrossDialog.setOnClickListener(v -> ChooseFilterDialog.cancel());
        //final JellyToggleButton jellyToggleButton = vDialog.findViewById(R.id.ChangeFilterTypeSwitch);
        final RelativeLayout RL_EASY_FILTER = vDialog.findViewById(R.id.RL_EASY_FILTER);
        final RelativeLayout RL_STRICT_FILTER = vDialog.findViewById(R.id.RL_STRICT_FILTER);

        if (isUltraFilterOn)
            RL_STRICT_FILTER.setBackgroundColor(Color.parseColor("#FFE260"));
        else
            RL_EASY_FILTER.setBackgroundColor(Color.parseColor("#1167FB"));

        //jellyToggleButton.setChecked(isUltraFilterOn);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(() -> {

            RL_EASY_FILTER.setOnClickListener(v -> {
                if (isUltraFilterOn) {
                    ChangeFilterType.setVisibility(View.GONE);
                    isUltraFilterOn = false;
                    CL_MAIN.setBackgroundResource(R.drawable.gradient_blue);
                    hmFinal.clear();
                    alsFieldsFiltered.clear();
                    alsFilterName.clear();
                    aliIconsList.clear();
                    InitialiseFilters();
                    InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);
                    alsFixedUIDList.clear();
                    alsCurrentFields.clear();
                    hmSelectedValues.clear();
                    RL_STRICT_FILTER.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    RL_EASY_FILTER.setBackgroundColor(Color.parseColor("#1167FB"));
                }
            });
            RL_STRICT_FILTER.setOnClickListener(v -> {
                if (!isUltraFilterOn) {
                    ChangeFilterType.setVisibility(View.VISIBLE);
                    isUltraFilterOn = true;
                    CL_MAIN.setBackgroundResource(R.drawable.gradient_yellow);
                    hmFinal.clear();
                    alsFieldsFiltered.clear();
                    alsFilterName.clear();
                    aliIconsList.clear();
                    InitialiseFilters();
                    InflateFilterAdapter(alsFilterName, hmSelectedFiltersHashMap);
                    alsFixedUIDList.clear();
                    alsCurrentFields.clear();
                    hmSelectedValues.clear();
                    RL_STRICT_FILTER.setBackgroundColor(Color.parseColor("#FFE260"));
                    RL_EASY_FILTER.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            });
        }, 2000);


        APPLY_FILTERS.setOnClickListener(v -> {

//            CL_FILTERS.setBackgroundResource(R.drawable.top_curved_blue_background);

            if (isUltraFilterOn) {
//                CL_FILTERS.setBackgroundResource(R.drawable.top_curved_yellow_background);
                int size = 0;
                ArrayList<String> temp = new ArrayList<>(hmFinal.keySet());
                for (int i = 0; i < temp.size(); i++) {
                    if (hmFinal.get(temp.get(i)).size() > size) {
                        size = hmFinal.get(temp.get(i)).size();
                    }
                }
                for (int i = 0; i < temp.size(); i++) {
                    if (hmFinal.get(temp.get(i)).size() < size) {
                        hmFinal.remove(temp.get(i));
                        alsFixedUIDList.remove(temp.get(i));
                    }
                }
                Log.d(TAG, String.format("onClick: hmFinal: %s", hmFinal));
            }
            GetFilteredUID();

            requireFragmentManager().beginTransaction().remove(FilterFragment.this).commit();

        });
        //  frameLayout.performClick();
//        frameLayout.setOnTouchListener((v, event) -> {
//            switch(event.getAction())
//            {
//                case MotionEvent.ACTION_DOWN:
//                    x1 = event.getX();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    x2 = event.getX();
//                    float deltaX = x2 - x1;
//                    if (Math.abs(deltaX) > MIN_DISTANCE)
//                    {
//                        sNavigationDrawer.openDrawer();
//                    }else{
//                        Log.d(TAG, "onTouch: cancelled");
//                    }
//                    break;
//            }
//            return false;
//        });


        return view;
    }


    private void InitialiseFilters() {
        alsFilterName.add(mContext.getString(R.string.age));
        alsFilterName.add(mContext.getString(R.string.course));
        alsFilterName.add(mContext.getString(R.string.college_year));
        alsFilterName.add(mContext.getString(R.string.gender));
        alsFilterName.add(mContext.getString(R.string.zodiac_sign));
        alsFilterName.add(mContext.getString(R.string.hobbies));
        alsFilterName.add(mContext.getString(R.string.games));
        alsFilterName.add(mContext.getString(R.string.music));
        alsFilterName.add(mContext.getString(R.string.movie));
        alsFilterName.add(mContext.getString(R.string.books));
        alsFilterName.add(mContext.getString(R.string.societies));
        alsFilterName.add(mContext.getString(R.string.pronouns));

        for (int i = 0; i < alsFilterName.size(); i++)
            hmSelectedFiltersHashMap.put(alsFilterName.get(i), false);

        aliIconsList.add(R.drawable.ic_age_filter);
        aliIconsList.add(R.drawable.ic_course_icon);
        aliIconsList.add(R.drawable.ic_university);
        aliIconsList.add(R.drawable.ic_gender_symbol);
        aliIconsList.add(R.drawable.ic_zodiac_filter);
        aliIconsList.add(R.drawable.ic_hobbies);
        aliIconsList.add(R.drawable.ic_controller);
        aliIconsList.add(R.drawable.ic_music);
        aliIconsList.add(R.drawable.ic_movie);
        aliIconsList.add(R.drawable.ic_book);
        aliIconsList.add(R.drawable.ic_society_filter);
        aliIconsList.add(R.drawable.ic_gender_symbol);
        if (alsFieldsFiltered.size() > 0) {

            Log.d(TAG, String.format("InitialiseFilters: alsFieldsFiltered: %s", alsFieldsFiltered));
            for (String DetailName : alsCurrentFields) {
                int pos = alsCurrentFields.indexOf(DetailName);
                alsFilterName.remove(DetailName);
                alsFilterName.add(0, DetailName);

                hmSelectedFiltersHashMap.remove(DetailName);
                hmSelectedFiltersHashMap.put(DetailName, true);
                //ConvertFilterNameToField(DetailName);
                Log.d(TAG, "InitialiseFilters: pos " + pos);
                Integer IconDrawable = aliIconsList.get(pos);
                aliIconsList.remove(IconDrawable);
                aliIconsList.add(0, IconDrawable);
            }

        }
    }

    public static void InflateFilterAdapter
            (ArrayList<String> FilterName, HashMap<String, Boolean> mSelectedFiltersHashMap) {
        menuItems.add(new MenuItem(mContext.getString(R.string.age), FilterName, aliIconsList, alsSelectedFilterNames, onFilterClickListener, mSelectedFiltersHashMap));
        Log.d(TAG, String.format("InflateFilterAdapter: menuItems:%s", menuItems));
        sNavigationDrawer.setMenuItemList(menuItems, CurrentFrag);
//        rFilters.setHasFixedSize(true);
//        mFilterLayoutManager = new ZoomRecyclerLayout(mContext);
//        mFilterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rFilters.setLayoutManager(mFilterLayoutManager);
//        mFilterAdapter = new FiltersRecyclerViewAdapter(FilterName, aliIconsList, alsSelectedFilterNames, mSelectedFiltersHashMap, mContext, onFilterClickListener);
//        rFilters.setAdapter(mFilterAdapter);

    }

    public static String ConvertFilterNameToField(String DetailName) {
        String s = null;
        if (DetailName.equals(mContext.getString(R.string.age))) {
            s = mContext.getString(R.string.field_age);
        } else if (DetailName.equals(mContext.getString(R.string.course))) {
            s = mContext.getString(R.string.field_course);
        } else if (DetailName.equals(mContext.getString(R.string.gender))) {
            s = mContext.getString(R.string.field_gender);
        } else if (DetailName.equals(mContext.getString(R.string.college_year))) {
            s = mContext.getString(R.string.field_college_year);
        } else if (DetailName.equals(mContext.getString(R.string.zodiac_sign))) {
            s = mContext.getString(R.string.field_zodiac_sign);
        } else if (DetailName.equals(mContext.getString(R.string.hobbies))) {
            s = mContext.getString(R.string.field_hobbies);
        } else if (DetailName.equals(mContext.getString(R.string.games))) {
            s = mContext.getString(R.string.field_games);
        } else if (DetailName.equals(mContext.getString(R.string.music))) {
            s = mContext.getString(R.string.field_music);
        } else if (DetailName.equals(mContext.getString(R.string.movie))) {
            s = mContext.getString(R.string.field_movie);
        } else if (DetailName.equals(mContext.getString(R.string.books))) {
            s = mContext.getString(R.string.field_books);
        } else if (DetailName.equals(mContext.getString(R.string.societies))) {
            s = mContext.getString(R.string.field_society);
        } else if (DetailName.equals(mContext.getString(R.string.pronouns))) {
            s = mContext.getString(R.string.field_pronouns);
        }
        return s;
    }


    @Override
    public void onItemClick(int position, AutofitTextView autofitTextView, AutofitTextView NumberOfValues) {
        Fragment fragment = null;
        String FragmentName = "";
        Bundle bundle = new Bundle();
        FragmentTransaction transaction = null;

        CurrentFrag = autofitTextView.getText().toString();

        // Log.d(TAG, "onScrollStateChanged: item position " + mFilterLayoutManager.findFirstVisibleItemPosition());
        if (autofitTextView.getText().toString().equals(mContext.getString(R.string.age))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_age));
            sNavigationDrawer.setAppbarTitleTV("Age Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);
        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.course))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_course));
            sNavigationDrawer.setAppbarTitleTV("Course Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);
        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.gender))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_gender));
            sNavigationDrawer.setAppbarTitleTV("Gender Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);
        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.college_year))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_college_year));
            sNavigationDrawer.setAppbarTitleTV("College Year Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);
        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.zodiac_sign))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_zodiac_sign));
            sNavigationDrawer.setAppbarTitleTV("Zodiac Sign Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.hobbies))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_hobbies));
            sNavigationDrawer.setAppbarTitleTV("Hobbies Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.games))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_games));
            sNavigationDrawer.setAppbarTitleTV("Games Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.music))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_music));
            sNavigationDrawer.setAppbarTitleTV("Music Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.movie))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_movie));
            sNavigationDrawer.setAppbarTitleTV("Movie Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.books))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_books));
            sNavigationDrawer.setAppbarTitleTV("Books Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.societies))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_society));
            sNavigationDrawer.setAppbarTitleTV("Societies Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        } else if (autofitTextView.getText().toString().equals(mContext.getString(R.string.pronouns))) {
            fragment = new FilterValuesFragment();
            bundle.putString(mContext.getString(R.string.field_filter), mContext.getString(R.string.field_pronouns));
            sNavigationDrawer.setAppbarTitleTV("Pronouns Filter");
            FragmentName = mContext.getString(R.string.filter_1_fragment);

        }
        autofitTextView.setTextColor(Color.WHITE);
        sNavigationDrawer.closeDrawer();

        if (fragment != null) {
            transaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            transaction.replace(R.id.FrameLayoutDisplayFilters, Objects.requireNonNull(fragment));
            transaction.addToBackStack(FragmentName);
            transaction.commit();
        }
    }
}
