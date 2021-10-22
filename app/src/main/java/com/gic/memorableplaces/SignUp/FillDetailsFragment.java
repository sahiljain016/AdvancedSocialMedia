package com.gic.memorableplaces.SignUp;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.gic.memorableplaces.Adapters.FillDetailsRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.ZoomRecyclerLayout;
import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.FilterDetailsDatabase;
import com.gic.memorableplaces.interfaces.FilterDetailsDao;
import com.gic.memorableplaces.interfaces.FilterPrivacyDao;
import com.gic.memorableplaces.interfaces.MatchFilterDetailsDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import me.grantland.widget.AutofitTextView;

public class FillDetailsFragment extends Fragment implements FillDetailsRecyclerViewAdapter.OnDetailFillClicked {
    private static final String TAG = "FillDetailsFragment";
    private Context mContext;
    private String sCurrentFragment, sUID;

    private Handler handler;

    private FilterDetailsDatabase FD_DETAILS;
    private FilterDetailsDao FD_Dao;
    private MatchFilterDetailsDao MFD_Dao;
    private FilterPrivacyDao FP_Dao;
    private ExecutorService databaseWriteExecutor;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;

    private ArrayList<String> alsFilterDetails, alcColoursList;
    private ArrayList<ImageView> aliImageViewList;
    private ArrayList<TextView> altTextViewList;

    private FilterDetails fdMyDetails;
    private MatchFilterDetails mfdMatchDetails, mfdFilledDetails;
    private FilterPrivacyDetails fpdDetails;

    private RecyclerView arvCourses;
    private AutofitTextView ATV_TITLE;
    private ImageFilterView IFV_BACK, IFV_NEXT;
    private View V_COVER;
    private ImageView IV_TEMP;
    private TextView TV_TEMP;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_details, container, false);
        Log.d(TAG, "onCreateView: FillDetailsFragment");
        mContext = getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        sUID = mAuth.getCurrentUser().getUid();
        firebaseMethods = new FirebaseMethods(mContext);

        handler = new Handler(Looper.getMainLooper());

        FD_DETAILS = FilterDetailsDatabase.getDatabase(mContext);
        FD_Dao = FD_DETAILS.filterDetailsDao();
        MFD_Dao = FD_DETAILS.matchFilterDetailsDao();
        FP_Dao = FD_DETAILS.filterPrivacyDao();
        databaseWriteExecutor = FD_DETAILS.databaseWriteExecutor;

        alsFilterDetails = new ArrayList<>();
        aliImageViewList = new ArrayList<>();
        alcColoursList = new ArrayList<>();
        altTextViewList = new ArrayList<>();
        fdMyDetails = new FilterDetails();
        mfdFilledDetails = new MatchFilterDetails();
        mfdMatchDetails = new MatchFilterDetails();
        fpdDetails = new FilterPrivacyDetails();

        databaseWriteExecutor.execute(() -> {
            FP_Dao.InsertPrivacyDetails(fpdDetails);

            fdMyDetails.setType(mContext.getString(R.string.field_my_details));
            fdMyDetails.SetAllDefault("N/A");
            FD_Dao.InsertNewDetail(fdMyDetails);

            mfdMatchDetails.setType(mContext.getString(R.string.field_match_details));
            mfdFilledDetails.setType(mContext.getString(R.string.field_visited_status));
            mfdMatchDetails.SetAllDefault("N/A");
            mfdFilledDetails.SetAllDefault("none");
            MFD_Dao.InsertNewDetail(mfdMatchDetails);
            MFD_Dao.InsertNewDetail(mfdFilledDetails);


            fdMyDetails.setType("");
            mfdMatchDetails.setType("");
            mfdFilledDetails.setType("");

            fdMyDetails = FD_Dao.GetAllFilterDetails().get(0);
            mfdMatchDetails = MFD_Dao.GetAllMatchFilterDetails().get(0);
            mfdFilledDetails = MFD_Dao.GetAllMatchFilterDetails().get(1);
            fpdDetails = FP_Dao.GetAllPrivacyDetails().get(0);

            Log.d(TAG, "onCreateView: fdMatchDetails: " + mfdMatchDetails);
            Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);


            handler.post(() -> {
                try {
                    SetGeneralCheck();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }

            });
        });

        InitViews(view);

        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/Abril_fatface.ttf");
        ATV_TITLE.setTypeface(tf, Typeface.NORMAL);

        SetOnClickListeners();

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {

            if (sCurrentFragment.equals(mContext.getString(R.string.fragment_age_and_birthdate))) {
                SetAgeCheck(result);

            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_gender_and_pronouns))) {
                SetGenderCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_general_details))) {
                SetGeneralDetailCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_college_year))) {
                SetCollegeYearCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_society_in_college))) {
                SetSocietyCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_hobbies))) {
                SetHobbiesCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_video_games))) {
                SetVideoGameCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_music))) {
                SetMusicCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_movie))) {
                SetMovieCheck(result);
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_books))) {
                SetBooksCheck(result);
            }
        });


        return view;
    }


    @Override
    public void onItemClick(int position, RelativeLayout relativeLayout) {
        //Log.d(TAG, "onItemClick: position: " + position);
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        String fragment_name = "";
        int Detail = Integer.parseInt(alsFilterDetails.get(position).substring(0, alsFilterDetails.get(position).indexOf(',')));
        if (Detail == (R.drawable.special_bg_ab_blue)) {

            fragment = new AgeAndBirthdateFragment();
            bundle.putLong(mContext.getString(R.string.field_my_age), fdMyDetails.getAge());

            if (TextUtils.isEmpty(mfdMatchDetails.getMatch_age_range()))
                mfdMatchDetails.setMatch_age_range("N/A");
            bundle.putString(mContext.getString(R.string.field_match_age_range), mfdMatchDetails.getMatch_age_range());

            if (TextUtils.isEmpty(fdMyDetails.getBirthdate())) fdMyDetails.setBirthdate("N/A");
            bundle.putString(mContext.getString(R.string.field_birth_date), fdMyDetails.getBirthdate());

            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isAge_birthdate_p());

            fragment_name = mContext.getString(R.string.fragment_age_and_birthdate);
            sCurrentFragment = mContext.getString(R.string.fragment_age_and_birthdate);

        } else if (Detail == R.drawable.special_bg_gp_orange) {
            fragment = new GenderAndPronounsFragment();
            bundle.putString(mContext.getString(R.string.field_match_gender), mfdMatchDetails.getMatch_gender());
            bundle.putString(mContext.getString(R.string.field_gender), fdMyDetails.getGender());
            bundle.putString(mContext.getString(R.string.field_pronouns), fdMyDetails.getPronouns());

            fragment_name = mContext.getString(R.string.fragment_gender_and_pronouns);
            sCurrentFragment = mContext.getString(R.string.fragment_gender_and_pronouns);
        } else if (Detail == R.drawable.special_bg_gd_grey) {
            fragment = new GeneralDetailsFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_general_details), fdMyDetails.getGeneral_details());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_general_details), mfdMatchDetails.getMatch_general_details());
            fragment_name = mContext.getString(R.string.fragment_general_details);
            sCurrentFragment = mContext.getString(R.string.fragment_general_details);
        } else if (Detail == R.drawable.special_bg_cy_red) {
            fragment = new CollegeYearFragment();
            bundle.putString(mContext.getString(R.string.field_college_year), fdMyDetails.getCollege_year());
            bundle.putString(mContext.getString(R.string.field_match_college_year), mfdMatchDetails.getMatch_college_year());
            fragment_name = mContext.getString(R.string.fragment_college_year);
            sCurrentFragment = mContext.getString(R.string.fragment_college_year);
        } else if (Detail == R.drawable.special_bg_sic_green) {
            fragment = new SocietiesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_society_in_college), fdMyDetails.getSociety_in_college());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_society_in_college), mfdMatchDetails.getMatch_society_in_college());
            fragment_name = mContext.getString(R.string.fragment_society_in_college);
            sCurrentFragment = mContext.getString(R.string.fragment_society_in_college);
        } else if (Detail == R.drawable.special_bg_hi_yellow) {
            fragment = new HobbiesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_hobbies), fdMyDetails.getHobbies());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_hobbies), mfdMatchDetails.getMatch_hobbies());
            fragment_name = mContext.getString(R.string.fragment_hobbies);
            sCurrentFragment = mContext.getString(R.string.fragment_hobbies);
        } else if (Detail == R.drawable.special_bg_vg_white) {
            fragment = new GamesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_video_games), fdMyDetails.getVideo_games());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_video_games), mfdMatchDetails.getMatch_video_games());
            fragment_name = mContext.getString(R.string.fragment_video_games);
            sCurrentFragment = mContext.getString(R.string.fragment_video_games);
        } else if (Detail == R.drawable.special_bg_mu_pink) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_music), fdMyDetails.getMusic());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_music), mfdMatchDetails.getMatch_music());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isMusic_p());
            fragment_name = mContext.getString(R.string.fragment_music);
            sCurrentFragment = mContext.getString(R.string.fragment_music);
        } else if (Detail == R.drawable.special_bg_bo_orange) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_books), fdMyDetails.getBooks());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_books), mfdMatchDetails.getMatch_books());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isBooks_p());
            fragment_name = mContext.getString(R.string.fragment_books);
            sCurrentFragment = mContext.getString(R.string.fragment_books);
        } else if (Detail == R.drawable.special_bg_mo_dark_blue) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_movie), fdMyDetails.getMovies());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_movie), mfdMatchDetails.getMatch_movie());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isMovie_p());
            fragment_name = mContext.getString(R.string.fragment_movie);
            sCurrentFragment = mContext.getString(R.string.fragment_movie);
        } else if (Detail == R.drawable.special_bg_dd_green) {
            Intent intent = new Intent(getActivity(), DreamDestinationFragment.class);
            startActivity(intent);
           /* fragment = new DreamDestinationFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_movie), fdMyDetails.getMovies());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_movie), mfdMatchDetails.getMatch_movie());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isMovie_p());
            fragment_name = mContext.getString(R.string.fragment_movie);
            sCurrentFragment = mContext.getString(R.string.fragment_movie);*/
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FL_DETAILS_FD, fragment);
            Transaction.addToBackStack(fragment_name);
            Transaction.commit();
        }
    }


    private void SetOnClickListeners() {
        V_COVER.setOnClickListener(v -> Log.d(TAG, "onClick: success"));
        IV_TEMP = aliImageViewList.get(0);
        TV_TEMP = altTextViewList.get(0);

        for (int i = 0; i < 11; i++) {
            int finalI = i;
            altTextViewList.get(i).setOnClickListener(v -> {
                PerformSelection(altTextViewList.get(finalI), aliImageViewList.get(finalI));
                MoveToSelected(finalI, alcColoursList.get(finalI));

            });

        }
    }

    private void SetGeneralCheck() throws IllegalAccessException, NoSuchFieldException {

        //int i = 0;
        int counter = 0;
        boolean StopPointAchieved = false;

        Field[] fields = mfdFilledDetails.getClass().getDeclaredFields();

        for (Field field : fields) {

//            Log.d(TAG, "SetGeneralCheck: field 0: " + mfdFilledDetails.getClass().getDeclaredField(String.valueOf(field.get(mfdFilledDetails))));
            String value;
            if (field.get(mfdFilledDetails) instanceof String) {
                value = (String) field.get(mfdFilledDetails);
            } else {

                value = ((ArrayList<?>) field.get(mfdFilledDetails)).get(0).toString();

            }
            counter = 0;
            if (field.getName().equals(mContext.getString(R.string.field_match_gender))) {
                counter = 1;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_general_details))) {
                counter = 2;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_college_year))) {
                counter = 3;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_society_in_college))) {
                counter = 4;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_hobbies))) {
                counter = 5;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_video_games))) {
                counter = 6;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_music))) {
                counter = 7;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_books))) {
                counter = 8;
            } else if (field.getName().equals(mContext.getString(R.string.field_match_movie))) {
                counter = 9;
            }
            Log.d(TAG, "SetGeneralCheck: Field: " + field.getName() + ", Value: " + value);
            if (value != null && !value.equals("visited_status")) {
                if (value.equals(mContext.getString(R.string.field_none))) {
                    if (!StopPointAchieved) {
                        StopPointAchieved = true;
                        PerformSelection(altTextViewList.get(counter), aliImageViewList.get(counter));
                        MoveToSelected(counter, alcColoursList.get(counter));
                    }
                } else if (value.equals(mContext.getString(R.string.field_red))) {
                    if (!StopPointAchieved) {
                        StopPointAchieved = true;
                        PerformSelection(altTextViewList.get(counter), aliImageViewList.get(counter));
                        MoveToSelected(counter, alcColoursList.get(counter));
                    }
                    SetRedCross(aliImageViewList.get(counter), altTextViewList.get(counter));
                } else if (value.equals(mContext.getString(R.string.field_green))) {
                    SetGreenTick(aliImageViewList.get(counter), null, altTextViewList.get(counter), null, 100, null);

                }
            }
        }

        counter -= 1;
        if (!StopPointAchieved) {
            PerformSelection(altTextViewList.get(counter), aliImageViewList.get(counter));
            MoveToSelected(counter, alcColoursList.get(counter));
        }

    }

    private void SetAgeCheck(Bundle result) {
        boolean isPrivate;
        String TickType;


        if (result.getLong(mContext.getString(R.string.field_age)) == 0
                || CheckForNA(result.getString(mContext.getString(R.string.field_birth_date)))
                || CheckForNA(result.getString(mContext.getString(R.string.field_match_age_range)))) {
            SetRedCross(aliImageViewList.get(0), altTextViewList.get(0));
            TickType = mContext.getString(R.string.field_red);

        } else {
            SetGreenTick(aliImageViewList.get(0), aliImageViewList.get(1), altTextViewList.get(0), altTextViewList.get(1), 1, alcColoursList.get(1));
            TickType = mContext.getString(R.string.field_green);
        }

        fdMyDetails.setAge(result.getLong(mContext.getString(R.string.field_age)));
        fdMyDetails.setBirthdate(result.getString(mContext.getString(R.string.field_birth_date)));
        mfdMatchDetails.setMatch_age_range(result.getString(mContext.getString(R.string.field_match_age_range)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setAge_birthdate_p(isPrivate);

        String finalTickType = TickType;
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateAgeAndBirthdate(fdMyDetails.getAge(), fdMyDetails.getBirthdate(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchAgeRange(mfdMatchDetails.getMatch_age_range(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchAgeRange(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateAgeAndBirthdatePrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_age), fdMyDetails.getAge());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_birth_date), fdMyDetails.getBirthdate());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_age_range), mfdMatchDetails.getMatch_age_range());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_ab_p), isPrivate);

    }

    private void SetGenderCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);
        if (sCurrentFragment.equals(mContext.getString(R.string.fragment_gender_and_pronouns))) {

            if (CheckForNA(result.getString(mContext.getString(R.string.field_gender)))
                    || CheckForNA(result.getString(mContext.getString(R.string.field_match_gender)))) {
                SetRedCross(aliImageViewList.get(1), altTextViewList.get(1));
                TickType = mContext.getString(R.string.field_red);
            } else {
                SetGreenTick(aliImageViewList.get(1), aliImageViewList.get(2), altTextViewList.get(1), altTextViewList.get(2), 2, alcColoursList.get(2));
                TickType = mContext.getString(R.string.field_green);

            }
        }

        fdMyDetails.setGender(result.getString(mContext.getString(R.string.field_gender)));
        fdMyDetails.setPronouns(result.getString(mContext.getString(R.string.field_pronouns)));
        mfdMatchDetails.setMatch_gender(result.getString(mContext.getString(R.string.field_match_gender)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setGender_pronouns_p(isPrivate);

        String finalTickType = TickType;
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateGenderAndPronouns(fdMyDetails.getGender(), fdMyDetails.getPronouns(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchGender(mfdMatchDetails.getMatch_gender(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchGender(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateGenderAndPronounsPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_gender), fdMyDetails.getGender());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_pronouns), fdMyDetails.getPronouns());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_gender), mfdMatchDetails.getMatch_gender());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_gp_p), isPrivate);

    }

    private void SetGeneralDetailCheck(Bundle result) {
        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_general_details)).contains("N/A") ||
                result.getStringArrayList(mContext.getString(R.string.field_match_general_details)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(2), altTextViewList.get(2));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(2), aliImageViewList.get(3), altTextViewList.get(2), altTextViewList.get(3), 3, alcColoursList.get(3));
            TickType = mContext.getString(R.string.field_green);

        }

        fdMyDetails.setGeneral_details(result.getStringArrayList(mContext.getString(R.string.field_general_details)));
        mfdMatchDetails.setMatch_general_details(result.getStringArrayList(mContext.getString(R.string.field_match_general_details)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setGeneral_details_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateGeneralDetails(fdMyDetails.getGeneral_details(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchGeneralDetails(mfdMatchDetails.getMatch_general_details(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchGeneralDetails(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateGeneralDetailsPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_general_details), fdMyDetails.getGeneral_details().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_general_details), mfdMatchDetails.getMatch_general_details());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_gd_p), isPrivate);
    }

    private void SetCollegeYearCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);
        if (sCurrentFragment.equals(mContext.getString(R.string.fragment_college_year))) {

            if (CheckForNA(result.getString(mContext.getString(R.string.field_college_year)))
                    || CheckForNA(result.getString(mContext.getString(R.string.field_match_college_year)))) {
                SetRedCross(aliImageViewList.get(3), altTextViewList.get(3));
                TickType = mContext.getString(R.string.field_red);
            } else {
                SetGreenTick(aliImageViewList.get(3), aliImageViewList.get(4), altTextViewList.get(3), altTextViewList.get(4), 4, alcColoursList.get(4));
                TickType = mContext.getString(R.string.field_green);

            }
        }

        fdMyDetails.setCollege_year(result.getString(mContext.getString(R.string.field_college_year)));
        mfdMatchDetails.setMatch_college_year(result.getString(mContext.getString(R.string.field_match_college_year)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setCollege_year_p(isPrivate);

        String finalTickType = TickType;
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateCollegeYear(fdMyDetails.getCollege_year(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchCollegeYear(mfdMatchDetails.getMatch_college_year(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchCollegeYear(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateCollegeYearPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_college_year), fdMyDetails.getCollege_year());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_college_year), mfdMatchDetails.getMatch_college_year());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_cy_p), isPrivate);

    }

    private void SetSocietyCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_society_in_college)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_society_in_college)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(4), altTextViewList.get(4));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(4), aliImageViewList.get(5), altTextViewList.get(4), altTextViewList.get(5), 5, alcColoursList.get(5));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setSociety_in_college(result.getStringArrayList(mContext.getString(R.string.field_society_in_college)));
        mfdMatchDetails.setMatch_society_in_college(result.getStringArrayList(mContext.getString(R.string.field_match_society_in_college)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setSociety_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateSociety(fdMyDetails.getSociety_in_college(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchSociety(mfdMatchDetails.getMatch_society_in_college(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchSociety(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateSocietyPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_hobbies), fdMyDetails.getHobbies().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_hobbies), mfdMatchDetails.getMatch_hobbies().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_h_p), isPrivate);

    }

    private void SetHobbiesCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_hobbies)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_hobbies)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(5), altTextViewList.get(5));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(5), aliImageViewList.get(6), altTextViewList.get(5), altTextViewList.get(6), 6, alcColoursList.get(6));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setHobbies(result.getStringArrayList(mContext.getString(R.string.field_hobbies)));
        mfdMatchDetails.setMatch_hobbies(result.getStringArrayList(mContext.getString(R.string.field_match_hobbies)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setHobbies_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateHobbies(fdMyDetails.getHobbies(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchHobbies(mfdMatchDetails.getMatch_hobbies(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchHobbies(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateHobbiesPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_hobbies), fdMyDetails.getHobbies().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_hobbies), mfdMatchDetails.getMatch_hobbies().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_h_p), isPrivate);

    }

    private void SetVideoGameCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_video_games)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_video_games)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(6), altTextViewList.get(6));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(6), aliImageViewList.get(7), altTextViewList.get(6), altTextViewList.get(7), 7, alcColoursList.get(7));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setVideo_games(result.getStringArrayList(mContext.getString(R.string.field_video_games)));
        mfdMatchDetails.setMatch_video_games(result.getStringArrayList(mContext.getString(R.string.field_match_video_games)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setVideo_games_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateVideoGames(fdMyDetails.getVideo_games(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchVideoGames(mfdMatchDetails.getMatch_video_games(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchVideoGames(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateVideoGamesPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_video_games), fdMyDetails.getVideo_games().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_video_games), mfdMatchDetails.getMatch_video_games().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_vg_p), isPrivate);

    }

    private void SetMusicCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_music)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_music)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(7), altTextViewList.get(7));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(7), aliImageViewList.get(8), altTextViewList.get(7), altTextViewList.get(8), 8, alcColoursList.get(8));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setMusic(result.getStringArrayList(mContext.getString(R.string.field_music)));
        mfdMatchDetails.setMatch_music(result.getStringArrayList(mContext.getString(R.string.field_match_music)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setMusic_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateMusic(fdMyDetails.getMusic(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchMusic(mfdMatchDetails.getMatch_music(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchMusic(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateMusicPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_music), fdMyDetails.getMusic().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_music), mfdMatchDetails.getMatch_music().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_mu_p), isPrivate);
    }

    private void SetBooksCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_books)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_books)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(8), altTextViewList.get(8));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(8), aliImageViewList.get(9), altTextViewList.get(8), altTextViewList.get(9), 9, alcColoursList.get(9));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setBooks(result.getStringArrayList(mContext.getString(R.string.field_books)));
        mfdMatchDetails.setMatch_books(result.getStringArrayList(mContext.getString(R.string.field_match_books)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setBooks_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateBooks(fdMyDetails.getBooks(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchBooks(mfdMatchDetails.getMatch_books(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchBooks(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateBooksPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_books), fdMyDetails.getBooks().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_books), mfdMatchDetails.getMatch_books().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_bo_p), isPrivate);
    }

    private void SetMovieCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_movie)).contains("N/A")
                || result.getStringArrayList(mContext.getString(R.string.field_match_movie)).contains("N/A")) {
            SetRedCross(aliImageViewList.get(9), altTextViewList.get(9));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(9), aliImageViewList.get(10), altTextViewList.get(9), altTextViewList.get(10), 10, alcColoursList.get(10));
            TickType = mContext.getString(R.string.field_green);

        }


        fdMyDetails.setMovies(result.getStringArrayList(mContext.getString(R.string.field_movie)));
        mfdMatchDetails.setMatch_movie(result.getStringArrayList(mContext.getString(R.string.field_match_movie)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setMovie_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateMovie(fdMyDetails.getMovies(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchMovie(mfdMatchDetails.getMatch_movie(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchMovie(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateMoviePrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_movie), fdMyDetails.getMovies().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_movie), mfdMatchDetails.getMatch_movie().toString());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_mo_p), isPrivate);
    }

    private void InitViews(View view) {

        arvCourses = view.findViewById(R.id.RV_DETAILS_FD);
        ATV_TITLE = view.findViewById(R.id.ATV_TITLE_FD);
        IFV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_FD);
        IFV_NEXT = view.findViewById(R.id.IFV_NEXT_BUTTON_FD);
        //ET_SEARCH_BAR = view.findViewById(R.id.ET_SEARCH_BAR_FD);
        V_COVER = view.findViewById(R.id.V_COVER_FD);

        ImageView IV_DOT_AB = view.findViewById(R.id.IV_DOT_AB_FD);
        ImageView IV_DOT_GP = view.findViewById(R.id.IV_DOT_GP_FD);
        ImageView IV_DOT_GD = view.findViewById(R.id.IV_DOT_GD_FD);
        ImageView IV_DOT_CY = view.findViewById(R.id.IV_DOT_CY_FD);
        ImageView IV_DOT_SIC = view.findViewById(R.id.IV_DOT_SIC_FD);
        ImageView IV_DOT_HI = view.findViewById(R.id.IV_DOT_HI_FD);
        ImageView IV_DOT_VG = view.findViewById(R.id.IV_DOT_VG_FD);
        ImageView IV_DOT_MU = view.findViewById(R.id.IV_DOT_MU_FD);
        ImageView IV_DOT_BO = view.findViewById(R.id.IV_DOT_BO_FD);
        ImageView IV_DOT_MO = view.findViewById(R.id.IV_DOT_MO_FD);
        ImageView IV_DOT_DD = view.findViewById(R.id.IV_DOT_DD_FD);

        TextView TV_AB = view.findViewById(R.id.TV_AB_FD);
        TextView TV_GP = view.findViewById(R.id.TV_GP_FD);
        TextView TV_GD = view.findViewById(R.id.TV_GD_FD);
        TextView TV_CY = view.findViewById(R.id.TV_CY_FD);
        TextView TV_SIC = view.findViewById(R.id.TV_SIC_FD);
        TextView TV_HI = view.findViewById(R.id.TV_HI_FD);
        TextView TV_VG = view.findViewById(R.id.TV_VG_FD);
        TextView TV_MU = view.findViewById(R.id.TV_MU_FD);
        TextView TV_BO = view.findViewById(R.id.TV_BO_FD);
        TextView TV_MO = view.findViewById(R.id.TV_MO_FD);
        TextView TV_DD = view.findViewById(R.id.TV_DD_FD);

        aliImageViewList.add(IV_DOT_AB);
        aliImageViewList.add(IV_DOT_GP);
        aliImageViewList.add(IV_DOT_GD);
        aliImageViewList.add(IV_DOT_CY);
        aliImageViewList.add(IV_DOT_SIC);
        aliImageViewList.add(IV_DOT_HI);
        aliImageViewList.add(IV_DOT_VG);
        aliImageViewList.add(IV_DOT_MU);
        aliImageViewList.add(IV_DOT_BO);
        aliImageViewList.add(IV_DOT_MO);
        aliImageViewList.add(IV_DOT_DD);

        altTextViewList.add(TV_AB);
        altTextViewList.add(TV_GP);
        altTextViewList.add(TV_GD);
        altTextViewList.add(TV_CY);
        altTextViewList.add(TV_SIC);
        altTextViewList.add(TV_HI);
        altTextViewList.add(TV_VG);
        altTextViewList.add(TV_MU);
        altTextViewList.add(TV_BO);
        altTextViewList.add(TV_MO);
        altTextViewList.add(TV_DD);

        alcColoursList.add("#9bf6ff");
        alcColoursList.add("#FFFFD6A5");
        alcColoursList.add("#e0dede");
        alcColoursList.add("#ffadad");
        alcColoursList.add("#caffbf");
        alcColoursList.add("#fdffb6");
        alcColoursList.add("#f8f9fa");
        alcColoursList.add("#ffaac9");
        alcColoursList.add("#ffd6a5");
        alcColoursList.add("#a0c4ff");
        alcColoursList.add("#c0ffee");


        StringBuilder stringBuilder = new StringBuilder();


        for (int i = 0; i < 11; i++)
            alsFilterDetails.add(SetDetails(i, stringBuilder).toString());

        com.gic.memorableplaces.CustomLibs.ZoomRecyclerLayout mLayoutManager;
        mLayoutManager = new ZoomRecyclerLayout(mContext);

        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        arvCourses.setHasFixedSize(true);
        arvCourses.setLayoutManager(mLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(arvCourses);
        arvCourses.setNestedScrollingEnabled(false);

        // Log.d(TAG, String.format("onCreateView: alsFilterDetails: %s", alsFilterDetails));
        FillDetailsRecyclerViewAdapter aCourses = new FillDetailsRecyclerViewAdapter(alsFilterDetails, this, getActivity());
        arvCourses.setAdapter(aCourses);
        aCourses.notifyDataSetChanged();

    }

    private void MoveNext(){
        IFV_NEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void MoveToSelected(int pos, String color) {
        V_COVER.setVisibility(View.VISIBLE);
        arvCourses.smoothScrollToPosition(pos);
        handler.postDelayed(() -> V_COVER.setVisibility(View.GONE), 1000);
        IFV_NEXT.setImageTintList(ColorStateList.valueOf(Color.parseColor(color)));
        IFV_BACK.setImageTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }

    private void PerformSelection(TextView TV, ImageView IV) {
        ClearSelection();

        IV.setImageResource(0);
        IV.setBackgroundResource(R.drawable.rounded_corners_black);
        //IV.setPaddingRelative(0, 0, paddingPixel, 0);
        TV.setTextColor(Color.parseColor("#000000"));
        TV_TEMP = TV;
        IV_TEMP = IV;
    }

    private void ClearSelection() {
        Log.d(TAG, "ClearSelection: tag: " + IV_TEMP.getTag());
        IV_TEMP.setImageResource(0);
        if (IV_TEMP.getTag().equals(mContext.getString(R.string.field_none))) {
            TV_TEMP.setTextColor(Color.parseColor("#959595"));
            IV_TEMP.setBackgroundResource(R.drawable.rounded_corners_imageview);

        } else if (IV_TEMP.getTag().equals(mContext.getString(R.string.field_green))) {
            IV_TEMP.setPadding(0, 0, 0, 0);
            IV_TEMP.setBackgroundResource(R.drawable.ic_green_tick_round);
            TV_TEMP.setTextColor(Color.parseColor("#00D447"));
        } else if (IV_TEMP.getTag().equals(mContext.getString(R.string.field_red))) {
            IV_TEMP.setPadding(0, 0, 0, 0);
            IV_TEMP.setBackgroundResource(R.drawable.ic_cross_red);
            TV_TEMP.setTextColor(Color.parseColor("#EC0000"));
        }
    }

    private boolean CheckForNA(String Check) {
        if (TextUtils.isEmpty(Check)) {
            return true;
        }
        return Check.equals("N/A");
    }

    private void SetRedCross(ImageView IV, TextView TV) {

        IV.setPadding(0, 0, 0, 0);
        IV_TEMP.setImageResource(0);
        IV.setBackgroundResource(R.drawable.ic_cross_red);
        TV.setTextColor(Color.parseColor("#EC0000"));
        IV.setTag(mContext.getString(R.string.field_red));
    }

    private void SetGreenTick(ImageView IV, ImageView IV_NEXT, TextView TV, TextView TV_NEXT, int pos, String color) {
        IV.setPadding(0, 0, 0, 0);
        IV_TEMP.setImageResource(0);
        IV.setBackgroundResource(R.drawable.ic_green_tick_round);
        TV.setTextColor(Color.parseColor("#00D447"));
        IV.setTag(mContext.getString(R.string.field_green));

        if (pos != 100) {
            PerformSelection(TV_NEXT, IV_NEXT);
            MoveToSelected(pos, color);
        }
    }


    private StringBuilder SetDetails(int pos, StringBuilder stringBuilder) {
        stringBuilder.delete(0, stringBuilder.length());
        if (pos == 0) {
            stringBuilder.append(R.drawable.special_bg_ab_blue).append(",")
                    .append(R.drawable.ic_filter_age).append("*")
                    .append("Age & Birthdate");
        } else if (pos == 1) {
            stringBuilder.append(R.drawable.special_bg_gp_orange).append(",")
                    .append(R.drawable.ic_filter_gender).append("*")
                    .append("Gender/Pronouns");
        } else if (pos == 2) {
            stringBuilder.append(R.drawable.special_bg_gd_grey).append(",")
                    .append(R.drawable.ic_filter_general_details).append("*")
                    .append("General Details");
        } else if (pos == 3) {
            stringBuilder.append(R.drawable.special_bg_cy_red).append(",")
                    .append(R.drawable.ic_filter_college_year).append("*")
                    .append("College Year");
        } else if (pos == 4) {
            stringBuilder.append(R.drawable.special_bg_sic_green).append(",")
                    .append(R.drawable.ic_filter_society).append("*")
                    .append("Societies in College");
        } else if (pos == 5) {
            stringBuilder.append(R.drawable.special_bg_hi_yellow).append(",")
                    .append(R.drawable.ic_filter_hobbies).append("*")
                    .append("Hobbies/Interest");
        } else if (pos == 6) {
            stringBuilder.append(R.drawable.special_bg_vg_white).append(",")
                    .append(R.drawable.ic_filter_video_games).append("*")
                    .append("Video Games");

        } else if (pos == 7) {
            stringBuilder.append(R.drawable.special_bg_mu_pink).append(",")
                    .append(R.drawable.ic_filter_music).append("*")
                    .append("Music");
        } else if (pos == 8) {
            stringBuilder.append(R.drawable.special_bg_bo_orange).append(",")
                    .append(R.drawable.ic_filter_book).append("*")
                    .append("Books");
        } else if (pos == 9) {
            stringBuilder.append(R.drawable.special_bg_mo_dark_blue).append(",")
                    .append(R.drawable.ic_filter_movie).append("*")
                    .append("Movie/Tv Shows");


        } else if (pos == 10) {
            stringBuilder.append(R.drawable.special_bg_dd_green).append(",")
                    .append(R.drawable.ic_filter_places).append("*")
                    .append("Dream Destinations");
        }
        return stringBuilder;
    }

}
