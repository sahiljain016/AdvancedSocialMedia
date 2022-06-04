package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
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
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import me.grantland.widget.AutofitTextView;
import pl.droidsonroids.gif.GifImageView;

public class FillDetailsFragment extends Fragment implements FillDetailsRecyclerViewAdapter.OnDetailFillClicked {
    private static final String TAG = "FillDetailsFragment";
    private int REQUEST_LOCATION = 99;
    private Context mContext;
    private String sCurrentFragment, sUID, provider;
    boolean MyDetails = true, PrivacyDetails = true, MatchDetails = true;

    private Handler handler;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private FilterDetailsDao FD_Dao;
    private MatchFilterDetailsDao MFD_Dao;
    private FilterPrivacyDao FP_Dao;
    private ExecutorService databaseWriteExecutor;

    private ValueEventListener VEL_FILTER_DETAILS, VEL_MATCH_DETAILS, VEL_PRIVACY_DETAILS;

    private FirebaseMethods firebaseMethods;
    private SpotlightView.Builder SB_GP;

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
    private TextView TV_TEMP, TV_LOADING;
    private GifImageView GIV_LOADING;
    private FrameLayout FL_COVER;


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

        FilterDetailsDatabase FD_DETAILS = FilterDetailsDatabase.getDatabase(mContext);
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

        InitViews(view);

        databaseWriteExecutor.execute(() -> {
            Log.d(TAG, "onCreateView: GetAllFilterDetails: " + FD_Dao.GetAllFilterDetails().size());
            Log.d(TAG, "onCreateView: GetAllMatchFilterDetails: " + MFD_Dao.GetAllMatchFilterDetails().size());
            Log.d(TAG, "onCreateView: GetAllPrivacyDetails: " + FP_Dao.GetAllPrivacyDetails().size());
            if (FD_Dao.GetAllFilterDetails().size() == 0) {
                MyDetails = false;
            }
            if (MFD_Dao.GetAllMatchFilterDetails().size() == 0) {
                MatchDetails = false;
            }
            if (FP_Dao.GetAllPrivacyDetails().size() == 0) {
                PrivacyDetails = false;
            }

            if (!MyDetails) {
                GetMyDetailsFromFirebase();
                SwitchLoadingViews();
            } else if (!MatchDetails) {
                GetMatchDetailsFromFirebase();
                SwitchLoadingViews();
            } else if (!PrivacyDetails) {
                GetPrivacyDetailsFromFirebase();
                SwitchLoadingViews();
            }


            if (MyDetails && MatchDetails && PrivacyDetails) {
                FP_Dao.InsertPrivacyDetails(fpdDetails);

                fdMyDetails.setType(mContext.getString(R.string.field_my_details));
                fdMyDetails.SetAllDefault(mContext.getString(R.string.not_available));
                FD_Dao.InsertNewDetail(fdMyDetails);

                mfdMatchDetails.setType(mContext.getString(R.string.field_match_details));
                mfdFilledDetails.setType(mContext.getString(R.string.field_visited_status));
                mfdMatchDetails.SetAllDefault(mContext.getString(R.string.not_available));
                mfdFilledDetails.SetAllDefault("none");
                MFD_Dao.InsertNewDetail(mfdMatchDetails);
                MFD_Dao.InsertNewDetail(mfdFilledDetails);


                fdMyDetails = FD_Dao.GetAllFilterDetails().get(0);
                mfdMatchDetails = MFD_Dao.GetAllMatchFilterDetails().get(0);
                mfdFilledDetails = MFD_Dao.GetAllMatchFilterDetails().get(1);
                fpdDetails = FP_Dao.GetAllPrivacyDetails().get(0);
                handler.post(() -> {
                    try {
                        SetGeneralCheck();
                        SB_GP.show();
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                });
            }

            Log.d(TAG, "onCreateView: fdMatchDetails: " + mfdMatchDetails);
            Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);


        });


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
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_titles_posts))) {
                SetTitleCheck(result);
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
            } else if (sCurrentFragment.equals(mContext.getString(R.string.fragment_dream_destinations))) {
                SetDDCheck(result);
            }
        });

        MoveNext();

        IFV_BACK.setOnClickListener(v -> {

            requireFragmentManager().beginTransaction().remove(FillDetailsFragment.this).commit();
            CourseAndFullNameCardFragment fragment = new CourseAndFullNameCardFragment();
            FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FrameLayoutCard, fragment);
            Transaction.addToBackStack(mContext.getString(R.string.course_and_full_name_Card_fragment));
            Transaction.commit();
        });


        return view;
    }

    private void SwitchLoadingViews() {
        handler.post(() -> {
            if (GIV_LOADING.getTag().equals("visible")) {
                GIV_LOADING.setVisibility(View.GONE);
                TV_LOADING.setVisibility(View.GONE);
                FL_COVER.setBackgroundColor(0);
                GIV_LOADING.setTag("gone");
            } else {
                GIV_LOADING.setVisibility(View.VISIBLE);
                TV_LOADING.setVisibility(View.VISIBLE);
                FL_COVER.setBackgroundColor(Color.parseColor("#CD000000"));
                GIV_LOADING.setTag("visible");
            }
        });
    }

    private void GetPrivacyDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_is_private));

        VEL_PRIVACY_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    fpdDetails = snapshot.getValue(FilterPrivacyDetails.class);
                    databaseWriteExecutor.execute(() -> {
                        FP_Dao.InsertPrivacyDetails(fpdDetails);
                        handler.post(() -> {
                            try {
                                SetGeneralCheck();
                                SwitchLoadingViews();
                                SB_GP.show();
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                e.printStackTrace();
                            }

                        });
                    });
                } else {
                    SwitchLoadingViews();
                    SB_GP.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_PRIVACY_DETAILS);
    }


    private void GetMatchDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_match_details));

        VEL_MATCH_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mfdMatchDetails.setType(mContext.getString(R.string.field_match_details));
                    mfdMatchDetails.SetAllDefault(mContext.getString(R.string.not_available));

                    if (snapshot.hasChild(mContext.getString(R.string.field_match_age_range))) {
                        mfdMatchDetails.setMatch_age_range(String.valueOf(snapshot.child(mContext.getString(R.string.field_match_age_range)).getValue()));
                        if (mfdFilledDetails.getMatch_age_range().equals(mContext.getString(R.string.field_green))) {
                            if (mfdFilledDetails.getMatch_age_range().equals(mContext.getString(R.string.not_available))) {
                                mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_red));
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_general_details))) {
                        ArrayList<String> general_details = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_general_details)).getChildren()) {
                            general_details.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_general_details(general_details);

                        if (mfdFilledDetails.getMatch_general_details().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (general_details.size() == 1 && general_details.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_general_details(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_books))) {
                        ArrayList<String> books = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_books)).getChildren()) {
                            books.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_books(books);

                        if (mfdFilledDetails.getMatch_books().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (books.size() == 1 && books.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_books(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_college_year))) {
                        ArrayList<String> college_year = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_college_year)).getChildren()) {
                            college_year.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_college_year(college_year);

                        if (mfdFilledDetails.getMatch_college_year().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (college_year.size() == 1 && college_year.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_college_year(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_hobbies))) {
                        ArrayList<String> hobbies = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_hobbies)).getChildren()) {
                            hobbies.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_hobbies(hobbies);

                        if (mfdFilledDetails.getMatch_hobbies().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (hobbies.size() == 1 && hobbies.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_hobbies(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_gender))) {
                        ArrayList<String> gender = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_gender)).getChildren()) {
                            gender.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_gender(gender);

                        if (mfdFilledDetails.getMatch_gender().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (gender.size() == 1 && gender.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_gender(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_movie))) {
                        ArrayList<String> movie = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_movie)).getChildren()) {
                            movie.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_movie(movie);

                        if (mfdFilledDetails.getMatch_movie().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (movie.size() == 1 && movie.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_movie(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_music))) {
                        ArrayList<String> music = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_music)).getChildren()) {
                            music.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_music(music);

                        if (mfdFilledDetails.getMatch_music().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (music.size() == 1 && music.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_music(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_society_in_college))) {
                        ArrayList<String> society = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_society_in_college)).getChildren()) {
                            society.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_society_in_college(society);

                        if (mfdFilledDetails.getMatch_society_in_college().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (society.size() == 1 && society.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_society_in_college(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_video_games))) {
                        ArrayList<String> video_games = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_video_games)).getChildren()) {
                            video_games.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_video_games(video_games);

                        if (mfdFilledDetails.getMatch_video_games().get(0).equals(mContext.getString(R.string.field_green))) {
                            ArrayList<String> tickType = new ArrayList<>(1);
                            if (video_games.size() == 1 && video_games.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType.add(mContext.getString(R.string.field_red));
                                mfdFilledDetails.setMatch_video_games(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_dd))) {
                        ArrayList<String> match_dd = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_match_dd)).getChildren()) {
                            match_dd.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        mfdMatchDetails.setMatch_dd(match_dd);

                        if (mfdFilledDetails.getMatch_loc().equals(mContext.getString(R.string.field_green))) {
                            String tickType = "";
                            if (match_dd.size() == 1 && match_dd.get(0).equals(mContext.getString(R.string.not_available))) {
                                tickType = mContext.getString(R.string.field_red);
                                mfdFilledDetails.setMatch_loc(tickType);
                            }
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_match_loc))) {
                        mfdMatchDetails.setMatch_loc(String.valueOf(snapshot.child(mContext.getString(R.string.field_match_loc)).getValue()));
                        if (mfdFilledDetails.getMatch_loc().equals(mContext.getString(R.string.field_green))) {
                            String tickType = "";
                            if (mfdMatchDetails.getMatch_loc().equals(mContext.getString(R.string.not_available))) {
                                tickType = mContext.getString(R.string.field_red);
                                mfdFilledDetails.setMatch_loc(tickType);
                            }
                        }
                    }

                    databaseWriteExecutor.execute(() -> {
                        MFD_Dao.InsertNewDetail(mfdMatchDetails);
                        MFD_Dao.InsertNewDetail(mfdFilledDetails);
                        if (!PrivacyDetails) {
                            GetPrivacyDetailsFromFirebase();
                        } else {
                            SwitchLoadingViews();
                            SB_GP.show();
                        }
                    });
                } else {
                    SwitchLoadingViews();
                    SB_GP.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_MATCH_DETAILS);
    }

    private void GetMyDetailsFromFirebase() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field));

        VEL_FILTER_DETAILS = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    fdMyDetails.setType(mContext.getString(R.string.field_my_details));
                    mfdFilledDetails.setType(mContext.getString(R.string.field_visited_status));
                    mfdFilledDetails.SetAllDefault("none");
                    fdMyDetails.SetAllDefault(mContext.getString(R.string.not_available));

                    Log.d(TAG, String.format("onDataChange: My snapshot: %s", snapshot));
                    if (snapshot.hasChild(mContext.getString(R.string.field_age))) {
                        fdMyDetails.setAge((Long) snapshot.child(mContext.getString(R.string.field_age)).getValue());
                        if (fdMyDetails.getAge() == 0L) {
                            mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_red));
                        } else {

                            mfdFilledDetails.setMatch_age_range(mContext.getString(R.string.field_green));
                        }
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_birth_date))) {
                        fdMyDetails.setBirthdate(String.valueOf(snapshot.child(mContext.getString(R.string.field_birth_date)).getValue()));
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_books))) {
                        ArrayList<String> books = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_books)).getChildren()) {
                            books.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setBooks(books);
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (books.size() == 1 && books.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_books(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_college_year))) {
                        fdMyDetails.setCollege_year(String.valueOf(snapshot.child(mContext.getString(R.string.field_college_year)).getValue()));
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (fdMyDetails.getCollege_year().equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_college_year(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_gender))) {
                        fdMyDetails.setGender(String.valueOf(snapshot.child(mContext.getString(R.string.field_gender)).getValue()));
                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (fdMyDetails.getGender().equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_gender(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_general_details))) {
                        ArrayList<String> general_details = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_general_details)).getChildren()) {
                            general_details.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setGeneral_details(general_details);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (general_details.size() == 1 && general_details.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_general_details(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_pronouns))) {
                        fdMyDetails.setPronouns(String.valueOf(snapshot.child(mContext.getString(R.string.field_pronouns)).getValue()));
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_hobbies))) {
                        ArrayList<String> hobbies = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_hobbies)).getChildren()) {
                            hobbies.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setHobbies(hobbies);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (hobbies.size() == 1 && hobbies.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_hobbies(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_movie))) {
                        ArrayList<String> movie = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_movie)).getChildren()) {
                            movie.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMovies(movie);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (movie.size() == 1 && movie.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_movie(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_music))) {
                        ArrayList<String> music = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_music)).getChildren()) {
                            music.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMusic(music);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (music.size() == 1 && music.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_music(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_society_in_college))) {
                        ArrayList<String> soceties = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_society_in_college)).getChildren()) {
                            soceties.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setSociety_in_college(soceties);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (soceties.size() == 1 && soceties.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_society_in_college(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_titles_posts))) {
                        ArrayList<String> titles = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_titles_posts)).getChildren()) {
                            titles.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setTitles_posts(titles);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (titles.size() == 1 && titles.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_titles_posts(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_video_games))) {
                        ArrayList<String> video_games = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_video_games)).getChildren()) {
                            video_games.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setVideo_games(video_games);

                        ArrayList<String> tickType = new ArrayList<>(1);
                        if (video_games.size() == 1 && video_games.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType.add(mContext.getString(R.string.field_red));
                        } else {
                            tickType.add(mContext.getString(R.string.field_green));
                        }
                        mfdFilledDetails.setMatch_video_games(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_my_loc))) {

                        fdMyDetails.setMy_loc(String.valueOf(snapshot.child(mContext.getString(R.string.field_my_loc)).getValue()));

                        String tickType = "";
                        if (fdMyDetails.getMy_loc().equals(mContext.getString(R.string.not_available))) {
                            tickType = mContext.getString(R.string.field_red);
                        } else {
                            tickType = mContext.getString(R.string.field_green);
                        }
                        mfdFilledDetails.setMatch_loc(tickType);
                    }
                    if (snapshot.hasChild(mContext.getString(R.string.field_my_dd))) {
                        ArrayList<String> my_dd = new ArrayList<>(5);
                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_my_dd)).getChildren()) {
                            my_dd.add(String.valueOf(dataSnapshot.getValue()));
                        }
                        fdMyDetails.setMy_dd(my_dd);

                        String tickType = "";
                        if (my_dd.size() == 1 && my_dd.get(0).equals(mContext.getString(R.string.not_available))) {
                            tickType = mContext.getString(R.string.field_red);
                        } else {
                            tickType = mContext.getString(R.string.field_green);
                        }
                        mfdFilledDetails.setMatch_loc(tickType);
                    }

                    Log.d(TAG, String.format("onDataChange: fdMyDetails: %s", fdMyDetails));

                    databaseWriteExecutor.execute(() -> {
                        FD_Dao.InsertNewDetail(fdMyDetails);
                        if (!MatchDetails) {
                            GetMatchDetailsFromFirebase();
                        } else {
                            SwitchLoadingViews();
                            SB_GP.show();
                        }
                    });
                } else {
                    SwitchLoadingViews();
                    SB_GP.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(VEL_FILTER_DETAILS);
    }

    private void SetGeneralCheck() throws IllegalAccessException, NoSuchFieldException {

        boolean StopPointAchieved = false;

        ArrayList<String> status = new ArrayList<>(15);
        for (int i = 0; i < 12; i++) {
            status.add(mContext.getString(R.string.field_none));
        }
        Field[] fields = mfdFilledDetails.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value;
            if (field.get(mfdFilledDetails) instanceof String) {
                value = (String) field.get(mfdFilledDetails);
            } else {
                value = ((ArrayList<?>) field.get(mfdFilledDetails)).get(0).toString();
            }
            if (value != null && !value.equals(mContext.getString(R.string.field_match_dd))) {
                if (field.getName().equals(mContext.getString(R.string.field_match_age_range))) {
                    status.set(0, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_gender))) {
                    status.set(1, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_general_details))) {
                    status.set(2, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_college_year))) {

                    status.set(3, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_society_in_college))) {
                    // counter = 4;
                    status.set(4, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_titles_posts))) {
                    // counter = 5;
                    status.set(5, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_hobbies))) {
                    // counter = 6;
                    status.set(6, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_video_games))) {
                    // counter = 7;
                    status.set(7, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_music))) {
                    // counter = 8;
                    status.set(8, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_books))) {
                    // counter = 9;
                    status.set(9, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_movie))) {
                    // counter = 10;
                    status.set(10, value);
                } else if (field.getName().equals(mContext.getString(R.string.field_match_loc))) {
                    // counter = 11;
                    status.set(11, value);
                }

            }
        }
        for (int i = 0; i < status.size(); i++) {
            if (!status.get(i).equals("visited_status")) {
                if (status.get(i).equals(mContext.getString(R.string.field_none))) {
                    if (!StopPointAchieved) {
                        StopPointAchieved = true;
                        PerformSelection(altTextViewList.get(i), aliImageViewList.get(i));
                        MoveToSelected(i, alcColoursList.get(i));
                    }
                } else if (status.get(i).equals(mContext.getString(R.string.field_red))) {
                    if (!StopPointAchieved) {
                        StopPointAchieved = true;
                        PerformSelection(altTextViewList.get(i), aliImageViewList.get(i));
                        MoveToSelected(i, alcColoursList.get(i));
                    }
                    SetRedCross(aliImageViewList.get(i), altTextViewList.get(i));
                } else if (status.get(i).equals(mContext.getString(R.string.field_green))) {
                    SetGreenTick(aliImageViewList.get(i), null, altTextViewList.get(i), null, 100, null);

                }
            }
        }
        if (!StopPointAchieved) {
            PerformSelection(altTextViewList.get(10), aliImageViewList.get(10));
            MoveToSelected(10, alcColoursList.get(10));
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

        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.age), String.valueOf(fdMyDetails.getAge()), sUID);
        fdMyDetails.setAge(result.getLong(mContext.getString(R.string.field_age)));
        fdMyDetails.setBirthdate(result.getString(mContext.getString(R.string.field_birth_date)));
        mfdMatchDetails.setMatch_age_range(result.getString(mContext.getString(R.string.field_match_age_range)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setAb_p(isPrivate);

        String finalTickType = TickType;

        databaseWriteExecutor.execute(() -> {
            Log.d(TAG, "SetAgeCheck: fdMyDetials:  " + fdMyDetails);
            Log.d(TAG, "SetAgeCheck: mfdMatchDetails:  " + mfdMatchDetails);
            FD_Dao.UpdateAgeAndBirthdate(fdMyDetails.getAge(), fdMyDetails.getBirthdate(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchAgeRange(mfdMatchDetails.getMatch_age_range(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchAgeRange(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateAgeAndBirthdatePrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_age), fdMyDetails.getAge());
        firebaseMethods.Set4ChildrenValue(mContext.getString(R.string.dbname_user_common_details), sUID, mContext.getString(R.string.field_age), fdMyDetails.getAge());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_birth_date), fdMyDetails.getBirthdate());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_age_range), mfdMatchDetails.getMatch_age_range());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.age), String.valueOf(fdMyDetails.getAge()), sUID, "user_id");
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_ab_p), isPrivate);

    }

    private void SetGenderCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);
        if (sCurrentFragment.equals(mContext.getString(R.string.fragment_gender_and_pronouns))) {

            if (CheckForNA(result.getString(mContext.getString(R.string.field_gender)))
                    || result.getStringArrayList(mContext.getString(R.string.field_match_gender)).contains(mContext.getString(R.string.not_available))) {
                SetRedCross(aliImageViewList.get(1), altTextViewList.get(1));
                TickType = mContext.getString(R.string.field_red);
            } else {
                SetGreenTick(aliImageViewList.get(1), aliImageViewList.get(2), altTextViewList.get(1), altTextViewList.get(2), 2, alcColoursList.get(2));
                TickType = mContext.getString(R.string.field_green);

            }
        }
        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.gender), String.valueOf(fdMyDetails.getGender()), sUID);

        fdMyDetails.setGender(result.getString(mContext.getString(R.string.field_gender)));
        fdMyDetails.setPronouns(result.getString(mContext.getString(R.string.field_pronouns)));
        mfdMatchDetails.setMatch_gender(result.getStringArrayList(mContext.getString(R.string.field_match_gender)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setGp_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateGenderAndPronouns(fdMyDetails.getGender(), fdMyDetails.getPronouns(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchGender(mfdMatchDetails.getMatch_gender(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchGender(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateGenderAndPronounsPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_gender), fdMyDetails.getGender());
        firebaseMethods.Set4ChildrenValue(mContext.getString(R.string.dbname_user_common_details), sUID, mContext.getString(R.string.field_gender), fdMyDetails.getGender());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_pronouns), fdMyDetails.getPronouns());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_gender), mfdMatchDetails.getMatch_gender());
        if (!fdMyDetails.getGender().equals(mContext.getString(R.string.not_available)))
            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.gender), String.valueOf(fdMyDetails.getGender()), sUID, "user_id");
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_gp_p), isPrivate);

    }

    private void SetGeneralDetailCheck(Bundle result) {
        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_general_details)).contains(mContext.getString(R.string.not_available)) ||
                result.getStringArrayList(mContext.getString(R.string.field_match_general_details)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(2), altTextViewList.get(2));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(2), aliImageViewList.get(3), altTextViewList.get(2), altTextViewList.get(3), 3, alcColoursList.get(3));
            TickType = mContext.getString(R.string.field_green);
        }
        Log.d(TAG, "SetGeneralDetailCheck: general details: " + fdMyDetails.getGeneral_details());
        if (fdMyDetails.getGeneral_details().size() != 1) {
            String field = "", value = "";
            for (int i = 0; i < fdMyDetails.getGeneral_details().size(); i++) {
                if (i == 0) {
                    field = mContext.getString(R.string.field_height);
                    value = fdMyDetails.getGeneral_details().get(0);
                } else if (i == 1) {
                    field = mContext.getString(R.string.field_education_level);
                    value = fdMyDetails.getGeneral_details().get(1);
                } else if (i == 2) {
                    field = mContext.getString(R.string.field_exercise);
                    value = fdMyDetails.getGeneral_details().get(2);
                } else if (i == 3) {
                    field = mContext.getString(R.string.field_drinking);
                    value = fdMyDetails.getGeneral_details().get(3);
                } else if (i == 4) {
                    field = mContext.getString(R.string.field_smoking);
                    value = fdMyDetails.getGeneral_details().get(4);
                } else if (i == 5) {
                    field = mContext.getString(R.string.field_looking_for_friend);
                    value = fdMyDetails.getGeneral_details().get(5);
                    if (!value.equals(mContext.getString(R.string.not_available))) {
                        String subVal = "";
                        subVal = value.substring(0, value.indexOf(","));
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_friend))
                                    .child(value.substring(0, value.indexOf(",")))
                                    .child(sUID)
                                    .removeValue();
                        }
                        subVal = value.substring(value.indexOf(",") + 1, value.indexOf("*"));
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_dating))
                                    .child(subVal)
                                    .child(sUID)
                                    .removeValue();
                        }
                        subVal = value.substring(value.indexOf("*") + 1);
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_other))
                                    .child(subVal)
                                    .child(sUID)
                                    .removeValue();
                        }
                    }
                } else if (i == 6) {
                    field = mContext.getString(R.string.field_sexual_preference);
                    value = fdMyDetails.getGeneral_details().get(6);
                } else if (i == 7) {
                    field = mContext.getString(R.string.field_politics);
                    value = fdMyDetails.getGeneral_details().get(7);
                } else if (i == 8) {
                    field = mContext.getString(R.string.field_zodiac_sign);
                    value = fdMyDetails.getGeneral_details().get(8);
                } else if (i == 9) {
                    field = mContext.getString(R.string.field_religion);
                    value = fdMyDetails.getGeneral_details().get(9);
                }
                Log.d(TAG, "SetGeneralDetailCheck: field: " + field);
                Log.d(TAG, "SetGeneralDetailCheck: value: " + value);
                if (i != 5) {
                    if (!value.equals(mContext.getString(R.string.not_available))) {
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_general_details))
                                .child(field)
                                .child(value)
                                .child(sUID)
                                .removeValue();
                    }
                }
            }
        }

        fdMyDetails.setGeneral_details(result.getStringArrayList(mContext.getString(R.string.field_general_details)));
        mfdMatchDetails.setMatch_general_details(result.getStringArrayList(mContext.getString(R.string.field_match_general_details)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setGd_p(isPrivate);

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

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_general_details), fdMyDetails.getGeneral_details());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_general_details), mfdMatchDetails.getMatch_general_details());
        if (fdMyDetails.getGeneral_details().size() != 1) {
            String field = "", value = "";
            for (int i = 0; i < fdMyDetails.getGeneral_details().size(); i++) {
                if (i == 0) {
                    field = mContext.getString(R.string.field_height);
                    value = fdMyDetails.getGeneral_details().get(0);
                } else if (i == 1) {
                    field = mContext.getString(R.string.field_education_level);
                    value = fdMyDetails.getGeneral_details().get(1);
                } else if (i == 2) {
                    field = mContext.getString(R.string.field_exercise);
                    value = fdMyDetails.getGeneral_details().get(2);
                } else if (i == 3) {
                    field = mContext.getString(R.string.field_drinking);
                    value = fdMyDetails.getGeneral_details().get(3);
                } else if (i == 4) {
                    field = mContext.getString(R.string.field_smoking);
                    value = fdMyDetails.getGeneral_details().get(4);
                } else if (i == 5) {
                    field = mContext.getString(R.string.field_looking_for_friend);
                    value = fdMyDetails.getGeneral_details().get(5);
                    if (!value.equals(mContext.getString(R.string.not_available))) {
                        String subVal = "";
                        subVal = value.substring(0, value.indexOf(","));
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_friend))
                                    .child(subVal)
                                    .child(sUID)
                                    .setValue("user_id");
                        }
                        subVal = value.substring(value.indexOf(",") + 1, value.indexOf("*"));
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_dating))
                                    .child(subVal)
                                    .child(sUID)
                                    .setValue("user_id");
                        }
                        subVal = value.substring(value.indexOf("*") + 1);
                        if (!subVal.equals(mContext.getString(R.string.not_available))) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_general_details))
                                    .child(mContext.getString(R.string.field_looking_for_other))
                                    .child(subVal)
                                    .child(sUID)
                                    .setValue("user_id");
                        }
                    }
                } else if (i == 6) {
                    field = mContext.getString(R.string.field_sexual_preference);
                    value = fdMyDetails.getGeneral_details().get(6);
                } else if (i == 7) {
                    field = mContext.getString(R.string.field_politics);
                    value = fdMyDetails.getGeneral_details().get(7);
                } else if (i == 8) {
                    field = mContext.getString(R.string.field_zodiac_sign);
                    value = fdMyDetails.getGeneral_details().get(8);
                } else if (i == 9) {
                    field = mContext.getString(R.string.field_religion);
                    value = fdMyDetails.getGeneral_details().get(9);
                }
                if (i != 5) {
                    if (!value.equals(mContext.getString(R.string.not_available))) {
                        myRef.child(mContext.getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_general_details))
                                .child(field)
                                .child(value)
                                .child(sUID)
                                .setValue("user_id");
                    }
                }
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_gd_p), isPrivate);
    }

    private void SetCollegeYearCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);
        if (sCurrentFragment.equals(mContext.getString(R.string.fragment_college_year))) {

            if (CheckForNA(result.getString(mContext.getString(R.string.field_college_year)))
                    || result.getStringArrayList(mContext.getString(R.string.field_match_college_year)).contains(mContext.getString(R.string.not_available))) {
                SetRedCross(aliImageViewList.get(3), altTextViewList.get(3));
                TickType = mContext.getString(R.string.field_red);
            } else {
                SetGreenTick(aliImageViewList.get(3), aliImageViewList.get(4), altTextViewList.get(3), altTextViewList.get(4), 4, alcColoursList.get(4));
                TickType = mContext.getString(R.string.field_green);

            }
        }

        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_college_year), String.valueOf(fdMyDetails.getCollege_year()), sUID);


        fdMyDetails.setCollege_year(result.getString(mContext.getString(R.string.field_college_year)));
        mfdMatchDetails.setMatch_college_year(result.getStringArrayList(mContext.getString(R.string.field_match_college_year)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setCy_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateCollegeYear(fdMyDetails.getCollege_year(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchCollegeYear(mfdMatchDetails.getMatch_college_year(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchCollegeYear(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateCollegeYearPrivacy(isPrivate);
        });

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_college_year), fdMyDetails.getCollege_year());
        if (!fdMyDetails.getCollege_year().equals(mContext.getString(R.string.not_available)))
            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_college_year), String.valueOf(fdMyDetails.getCollege_year()), sUID, "user_id");
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_college_year), mfdMatchDetails.getMatch_college_year());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_cy_p), isPrivate);

    }

    private void SetSocietyCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_society_in_college)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_society_in_college)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(4), altTextViewList.get(4));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(4), aliImageViewList.get(5), altTextViewList.get(4), altTextViewList.get(5), 5, alcColoursList.get(5));
            TickType = mContext.getString(R.string.field_green);

        }
        Log.d(TAG, "SetSocietyCheck: fdMyDetails: " + fdMyDetails.getSociety_in_college());
        for (String string : fdMyDetails.getSociety_in_college())
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_society_in_college), string, sUID);


        fdMyDetails.setSociety_in_college(result.getStringArrayList(mContext.getString(R.string.field_society_in_college)));
        mfdMatchDetails.setMatch_society_in_college(result.getStringArrayList(mContext.getString(R.string.field_match_society_in_college)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setSic_p(isPrivate);

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

        for (String string : fdMyDetails.getSociety_in_college()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_society_in_college), string, sUID, "user_id");
            }
        }

        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_society_in_college), fdMyDetails.getSociety_in_college());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_society_in_college), mfdMatchDetails.getMatch_society_in_college());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_sic_p), isPrivate);

    }

    private void SetTitleCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_titles_posts)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(5), altTextViewList.get(5));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(5), aliImageViewList.get(6), altTextViewList.get(5), altTextViewList.get(6), 6, alcColoursList.get(6));
            TickType = mContext.getString(R.string.field_green);

        }
        for (String string : fdMyDetails.getTitles_posts()) {
            Log.d(TAG, "SetTitleCheck: string: " + string);
            if (!string.equals(mContext.getString(R.string.not_available))) {
                String value = string.substring(0, string.indexOf("$o*")) + " at " + string.substring(string.indexOf("$o*") + 3);
                firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_titles_posts), value, sUID);
            }
        }

        fdMyDetails.setTitles_posts(result.getStringArrayList(mContext.getString(R.string.field_titles_posts)));
        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setTp_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            //Log.d(TAG, "onCreateView: fdMyDetails: " + fdMyDetails);
            //Log.d(TAG, "onCreateView: fdMatchDetails: " + fdMatchDetails);
            FD_Dao.UpdateTitles(fdMyDetails.getTitles_posts(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchTitles(mfdMatchDetails.getMatch_titles_posts(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchTitles(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateTitlesPrivacy(isPrivate);
        });

        for (String string : fdMyDetails.getTitles_posts()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                String value = string.substring(0, string.indexOf("$o*")) + " at " + string.substring(string.indexOf("$o*") + 3);
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_titles_posts), value, sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_titles_posts), fdMyDetails.getTitles_posts());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_tp_p), isPrivate);

    }

    private void SetHobbiesCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_hobbies)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_hobbies)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(6), altTextViewList.get(6));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(6), aliImageViewList.get(7), altTextViewList.get(6), altTextViewList.get(7), 7, alcColoursList.get(7));
            TickType = mContext.getString(R.string.field_green);

        }

        for (String string : fdMyDetails.getHobbies()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.hobbies), string, sUID);
        }

        fdMyDetails.setHobbies(result.getStringArrayList(mContext.getString(R.string.field_hobbies)));
        mfdMatchDetails.setMatch_hobbies(result.getStringArrayList(mContext.getString(R.string.field_match_hobbies)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setH_p(isPrivate);

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
        for (String string : fdMyDetails.getHobbies()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.hobbies), string, sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_hobbies), fdMyDetails.getHobbies());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_hobbies), mfdMatchDetails.getMatch_hobbies());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_h_p), isPrivate);

    }

    private void SetVideoGameCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_video_games)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_video_games)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(7), altTextViewList.get(7));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(7), aliImageViewList.get(8), altTextViewList.get(7), altTextViewList.get(8), 8, alcColoursList.get(8));
            TickType = mContext.getString(R.string.field_green);

        }

        for (String string : fdMyDetails.getVideo_games()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_video_games), string, sUID);
        }


        fdMyDetails.setVideo_games(result.getStringArrayList(mContext.getString(R.string.field_video_games)));
        mfdMatchDetails.setMatch_video_games(result.getStringArrayList(mContext.getString(R.string.field_match_video_games)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setVg_p(isPrivate);

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
        for (String string : fdMyDetails.getVideo_games()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_video_games), string, sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_video_games), fdMyDetails.getVideo_games());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_video_games), mfdMatchDetails.getMatch_video_games());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_vg_p), isPrivate);

    }

    private void SetMusicCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_music)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_music)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(8), altTextViewList.get(8));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(8), aliImageViewList.get(9), altTextViewList.get(8), altTextViewList.get(9), 9, alcColoursList.get(9));
            TickType = mContext.getString(R.string.field_green);

        }

        for (String string : fdMyDetails.getMusic()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.music), string, sUID);
        }

        fdMyDetails.setMusic(result.getStringArrayList(mContext.getString(R.string.field_music)));
        mfdMatchDetails.setMatch_music(result.getStringArrayList(mContext.getString(R.string.field_match_music)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setMu_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateMusic(fdMyDetails.getMusic(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchMusic(mfdMatchDetails.getMatch_music(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchMusic(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateMusicPrivacy(isPrivate);
        });

        for (String string : fdMyDetails.getMusic()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.music), string, sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_music), fdMyDetails.getMusic());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_music), mfdMatchDetails.getMatch_music());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_mu_p), isPrivate);
    }

    private void SetBooksCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_books)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_books)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(9), altTextViewList.get(9));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(9), aliImageViewList.get(10), altTextViewList.get(9), altTextViewList.get(10), 10, alcColoursList.get(10));
            TickType = mContext.getString(R.string.field_green);

        }

        for (String string : fdMyDetails.getBooks()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.books), string, sUID);
        }

        fdMyDetails.setBooks(result.getStringArrayList(mContext.getString(R.string.field_books)));
        mfdMatchDetails.setMatch_books(result.getStringArrayList(mContext.getString(R.string.field_match_books)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setBo_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateBooks(fdMyDetails.getBooks(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchBooks(mfdMatchDetails.getMatch_books(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchBooks(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateBooksPrivacy(isPrivate);
        });
        for (String string : fdMyDetails.getBooks()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.books), string, sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_books), fdMyDetails.getBooks());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_books), mfdMatchDetails.getMatch_books());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_bo_p), isPrivate);
    }

    private void SetMovieCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (result.getStringArrayList(mContext.getString(R.string.field_movie)).contains(mContext.getString(R.string.not_available))
                || result.getStringArrayList(mContext.getString(R.string.field_match_movie)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(10), altTextViewList.get(10));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(10), aliImageViewList.get(11), altTextViewList.get(10), altTextViewList.get(11), 11, alcColoursList.get(11));
            TickType = mContext.getString(R.string.field_green);

        }

        for (String string : fdMyDetails.getMovies()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.movie), string.replaceAll("\\$", "*"), sUID);
        }

        fdMyDetails.setMovies(result.getStringArrayList(mContext.getString(R.string.field_movie)));
        mfdMatchDetails.setMatch_movie(result.getStringArrayList(mContext.getString(R.string.field_match_movie)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setMo_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateMovie(fdMyDetails.getMovies(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchMovie(mfdMatchDetails.getMatch_movie(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchMovie(finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateMoviePrivacy(isPrivate);
        });
        for (String string : fdMyDetails.getMovies()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.movie), string.replaceAll("\\$", "*"), sUID, "user_id");
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_movie), fdMyDetails.getMovies());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_movie), mfdMatchDetails.getMatch_movie());
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_mo_p), isPrivate);
    }

    private void SetDDCheck(Bundle result) {

        boolean isPrivate;
        String TickType = mContext.getString(R.string.field_none);

        if (CheckForNA(result.getString(mContext.getString(R.string.field_my_loc)))
                || result.getStringArrayList(mContext.getString(R.string.field_my_dd)).contains(mContext.getString(R.string.not_available))
                || CheckForNA(result.getString(mContext.getString(R.string.field_match_loc)))
                || result.getStringArrayList(mContext.getString(R.string.field_match_dd)).contains(mContext.getString(R.string.not_available))) {
            SetRedCross(aliImageViewList.get(11), altTextViewList.get(11));
            TickType = mContext.getString(R.string.field_red);
        } else {
            SetGreenTick(aliImageViewList.get(11), null, altTextViewList.get(11), null, 100, null);
            TickType = mContext.getString(R.string.field_green);

        }
        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_my_loc), fdMyDetails.getMy_loc(), sUID);
        for (String string : fdMyDetails.getMy_dd()) {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_my_dd), string, sUID);
        }
        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_dd));
        firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_my_dd));

        fdMyDetails.setMy_loc(result.getString(mContext.getString(R.string.field_my_loc)));
        fdMyDetails.setMy_dd(result.getStringArrayList(mContext.getString(R.string.field_my_dd)));
        mfdMatchDetails.setMatch_loc(result.getString(mContext.getString(R.string.field_match_loc)));
        mfdMatchDetails.setMatch_dd(result.getStringArrayList(mContext.getString(R.string.field_match_dd)));

        isPrivate = result.getBoolean(mContext.getString(R.string.field_is_private));
        fpdDetails.setDd_p(isPrivate);

        ArrayList<String> finalTickType = new ArrayList<>(1);
        finalTickType.add(TickType);
        String finalTickType1 = TickType;
        databaseWriteExecutor.execute(() -> {
            FD_Dao.UpdateMyLocAndDD(fdMyDetails.getMy_loc(), fdMyDetails.getMy_dd(), mContext.getString(R.string.field_my_details));
            MFD_Dao.UpdateMatchLocAndDD(mfdMatchDetails.getMatch_loc(), mfdMatchDetails.getMatch_dd(), mContext.getString(R.string.field_match_details));
            MFD_Dao.UpdateMatchLocAndDD(finalTickType1, finalTickType, mContext.getString(R.string.field_visited_status));
            FP_Dao.UpdateLocAndDDPrivacy(isPrivate);
        });
        int count = 0;
        for (String string : fdMyDetails.getMy_dd()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set6ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_my_dd), String.valueOf(count), string);
                firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_my_dd), string, sUID, "user_id");
                count++;
            }

        }
        if (!CheckForNA(fdMyDetails.getMy_loc())) {
            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_filter_data), mContext.getString(R.string.field_my_loc), fdMyDetails.getMy_loc(), sUID, "user_id");
            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_my_loc), fdMyDetails.getMy_loc());
        } else {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field), mContext.getString(R.string.field_my_loc));
        }
        if (!CheckForNA(mfdMatchDetails.getMatch_loc())) {
            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_loc), mfdMatchDetails.getMatch_loc());
        } else {
            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_loc));
        }
        count = 0;
        for (String string : mfdMatchDetails.getMatch_dd()) {
            if (!string.equals(mContext.getString(R.string.not_available))) {
                firebaseMethods.Set6ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_match_details), mContext.getString(R.string.field_match_dd), String.valueOf(count), string);
                count++;
            }
        }
        firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), sUID, mContext.getString(R.string.field_is_private), mContext.getString(R.string.field_dd_p), isPrivate);
    }

    private void InitViews(View view) {

        arvCourses = view.findViewById(R.id.RV_DETAILS_FD);
        ATV_TITLE = view.findViewById(R.id.ATV_TITLE_FD);
        IFV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_FD);
        GIV_LOADING = view.findViewById(R.id.GIV_LOADING_FD);
        TV_LOADING = view.findViewById(R.id.TV_LOADING_FD);
        IFV_NEXT = view.findViewById(R.id.IFV_NEXT_BUTTON_FD);
        FL_COVER = view.findViewById(R.id.FL_DETAILS_FD);
        //ET_SEARCH_BAR = view.findViewById(R.id.ET_SEARCH_BAR_FD);
        V_COVER = view.findViewById(R.id.V_COVER_FD);

        ImageView IV_DOT_AB = view.findViewById(R.id.IV_DOT_AB_FD);
        ImageView IV_DOT_GP = view.findViewById(R.id.IV_DOT_GP_FD);
        ImageView IV_DOT_GD = view.findViewById(R.id.IV_DOT_GD_FD);
        ImageView IV_DOT_CY = view.findViewById(R.id.IV_DOT_CY_FD);
        ImageView IV_DOT_SIC = view.findViewById(R.id.IV_DOT_SIC_FD);
        ImageView IV_DOT_TIO = view.findViewById(R.id.IV_DOT_TIO_FD);
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
        TextView TV_TIO = view.findViewById(R.id.TV_TIO_FD);
        TextView TV_HI = view.findViewById(R.id.TV_HI_FD);
        TextView TV_VG = view.findViewById(R.id.TV_VG_FD);
        TextView TV_MU = view.findViewById(R.id.TV_MU_FD);
        TextView TV_BO = view.findViewById(R.id.TV_BO_FD);
        TextView TV_MO = view.findViewById(R.id.TV_MO_FD);
        TextView TV_DD = view.findViewById(R.id.TV_DD_FD);

        SpotlightConfig spotlightConfig = new SpotlightConfig();

        spotlightConfig.setIntroAnimationDuration(400);
        spotlightConfig.setRevealAnimationEnabled(true);
        spotlightConfig.setPerformClick(true);
        spotlightConfig.setFadingTextDuration(400);
        spotlightConfig.setHeadingTvColor(Color.parseColor("#FFFFFF"));
        spotlightConfig.setHeadingTvSize(32);
        spotlightConfig.setSubHeadingTvColor(Color.parseColor("#DCDCDC"));
        spotlightConfig.setSubHeadingTvSize(16);
        spotlightConfig.setMaskColor(Color.parseColor("#dc000000"));
        spotlightConfig.setLineAnimationDuration(400);
        spotlightConfig.setLineAndArcColor(Color.parseColor("#E30040"));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);

        SB_GP = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Details In Profile")
                .subHeadingTvText("Click on these titles to navigate to various pages where you can fill these details.")
                .target(TV_GP)
                .usageId("TV_GP")
                .setListener(spotlightViewId -> {
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        aliImageViewList.add(IV_DOT_AB);
        aliImageViewList.add(IV_DOT_GP);
        aliImageViewList.add(IV_DOT_GD);
        aliImageViewList.add(IV_DOT_CY);
        aliImageViewList.add(IV_DOT_SIC);
        aliImageViewList.add(IV_DOT_TIO);
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
        altTextViewList.add(TV_TIO);
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
        alcColoursList.add("#FF7CEFCF");
        alcColoursList.add("#fdffb6");
        alcColoursList.add("#FFB9B5B5");
        alcColoursList.add("#ffaac9");
        alcColoursList.add("#ffd6a5");
        alcColoursList.add("#a0c4ff");
        alcColoursList.add("#ff9f9e");


        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 12; i++)
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


    private void MoveNext() {
        IFV_NEXT.setOnClickListener(v -> {
            StringBuilder MissingMessage = new StringBuilder();
            ArrayList<String> alsMissedDetails = new ArrayList<>(12);
            boolean isAllFilled = true, isCompulsoryDetailMissed = false;

            for (int i = 0; i < aliImageViewList.size(); i++) {
                if (aliImageViewList.get(i).getTag().equals("none")) {
                    MissingMessage.append("\n⦿ ").append(altTextViewList.get(i).getText().toString());
                    if (altTextViewList.get(i).getText().toString().equals("Age & Birthdate")
                            || altTextViewList.get(i).getText().toString().equals("Gender/Pronouns")
                            || altTextViewList.get(i).getText().toString().equals("College Year")) {
                        isCompulsoryDetailMissed = true;
                    }
                    alsMissedDetails.add(altTextViewList.get(i).getText().toString());
                    isAllFilled = false;
                } else if (aliImageViewList.get(i).getTag().equals("red")) {
                    MissingMessage.append("\n⦿ ").append(altTextViewList.get(i).getText().toString());
                    if (altTextViewList.get(i).getText().toString().equals("Age & Birthdate")
                            || altTextViewList.get(i).getText().toString().equals("Gender/Pronouns")
                            || altTextViewList.get(i).getText().toString().equals("College Year")) {
                        isCompulsoryDetailMissed = true;
                    }
                    alsMissedDetails.add(altTextViewList.get(i).getText().toString());
                    isAllFilled = false;
                }
            }

            if (isAllFilled) {
                Log.d(TAG, "MoveNext: all filled");
                QuestionsFragment fragment = new QuestionsFragment();
                requireFragmentManager().beginTransaction().remove(FillDetailsFragment.this).commit();
                FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCard, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.questions_fragment));
                Transaction.commit();
            } else {
                if (isCompulsoryDetailMissed) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < altTextViewList.size(); i++) {
                        if (alsMissedDetails.contains(altTextViewList.get(i).getText().toString())
                                && (altTextViewList.get(i).getText().toString().equals("Age & Birthdate")
                                || altTextViewList.get(i).getText().toString().equals("Gender/Pronouns")
                                || altTextViewList.get(i).getText().toString().equals("College Year"))) {
                            makeMeShake(altTextViewList.get(i), 100, 10);
                            stringBuilder.append("<Br>⦿ ").append(altTextViewList.get(i).getText().toString());
                        }
                    }

                    Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.dialog_missing_details);
                    AutofitTextView ATV_POS = dialog.findViewById(R.id.ATV_POS_MD);
                    AutofitTextView ATV_NEG = dialog.findViewById(R.id.ATV_NEG_MD);
                    AutofitTextView ATV_GO_BACK = dialog.findViewById(R.id.ATV_GO_BACK_MD);
                    View V_DIVIDER = dialog.findViewById(R.id.V_LINE_3_MD);
                    TextView TV_DESP_MISSING_DETAILS = dialog.findViewById(R.id.TV_DESP_MISSING_DETAILS);

                    ATV_POS.setVisibility(View.GONE);
                    ATV_NEG.setVisibility(View.GONE);
                    V_DIVIDER.setVisibility(View.GONE);
                    ATV_GO_BACK.setVisibility(View.VISIBLE);
                    TV_DESP_MISSING_DETAILS.setText(Html.fromHtml("The following fields are necessary to move forward, please fill them: " + "<B>" + stringBuilder.toString() + "</B>", Html.FROM_HTML_MODE_LEGACY));
                    dialog.show();

                    ATV_GO_BACK.setOnClickListener(v13 -> dialog.cancel());
                } else {
                    Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.dialog_missing_details);
                    AutofitTextView ATV_POS = dialog.findViewById(R.id.ATV_POS_MD);
                    AutofitTextView ATV_NEG = dialog.findViewById(R.id.ATV_NEG_MD);
                    TextView TV_DESP_MISSING_DETAILS = dialog.findViewById(R.id.TV_DESP_MISSING_DETAILS);

                    ATV_NEG.setOnClickListener(v1 -> {
                        dialog.cancel();
                        for (int i = 0; i < altTextViewList.size(); i++) {
                            if (alsMissedDetails.contains(altTextViewList.get(i).getText().toString())) {
                                makeMeShake(altTextViewList.get(i), 100, 10);
                            }
                        }
                    });
                    ATV_POS.setOnClickListener(v12 -> {
                        dialog.cancel();
                        QuestionsFragment fragment = new QuestionsFragment();
                        requireFragmentManager().beginTransaction().remove(FillDetailsFragment.this).commit();
                        FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Transaction.replace(R.id.FrameLayoutCard, fragment);
                        Transaction.addToBackStack(mContext.getString(R.string.questions_fragment));
                        Transaction.commit();
                    });
                    TV_DESP_MISSING_DETAILS.setText(MissingMessage.toString());
                    dialog.show();
                }


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
        return Check.equals(mContext.getString(R.string.not_available));
    }

    private void SetRedCross(ImageView IV, TextView TV) {

        IV.setPadding(0, 0, 0, 0);
        IV_TEMP.setImageResource(0);
        IV.setBackgroundResource(R.drawable.ic_cross_red);
        TV.setTextColor(Color.parseColor("#EC0000"));
        IV.setTag(mContext.getString(R.string.field_red));
    }

    private void SetGreenTick(ImageView IV, ImageView IV_NEXT, TextView TV, TextView
            TV_NEXT, int pos, String color) {
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
            stringBuilder.append(R.drawable.special_bg_dd_green).append(",")
                    .append(R.drawable.ic_posts_titles).append("*")
                    .append("Posts/Titles");
        } else if (pos == 6) {
            stringBuilder.append(R.drawable.special_bg_hi_yellow).append(",")
                    .append(R.drawable.ic_filter_hobbies).append("*")
                    .append("Hobbies/Interest");
        } else if (pos == 7) {
            stringBuilder.append(R.drawable.special_bg_vg_white).append(",")
                    .append(R.drawable.ic_filter_video_games).append("*")
                    .append("Video Games");

        } else if (pos == 8) {
            stringBuilder.append(R.drawable.special_bg_mu_pink).append(",")
                    .append(R.drawable.ic_filter_music).append("*")
                    .append("Music");
        } else if (pos == 9) {
            stringBuilder.append(R.drawable.special_bg_bo_orange).append(",")
                    .append(R.drawable.ic_filter_book).append("*")
                    .append("Books");
        } else if (pos == 10) {
            stringBuilder.append(R.drawable.special_bg_mo_dark_blue).append(",")
                    .append(R.drawable.ic_filter_movie).append("*")
                    .append("Movie/Tv Shows");
        } else if (pos == 11) {
            stringBuilder.append(R.drawable.special_bg_dd_light_red).append(",")
                    .append(R.drawable.ic_filter_places).append("*")
                    .append("Dream Destinations");
        }
        return stringBuilder;
    }

    private View makeMeShake(View view, int duration, int offset) {
        if (view != null) {
            Animation anim = new TranslateAnimation(-offset, offset, 0, 0);
            anim.setDuration(duration);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(5);
            view.startAnimation(anim);
        }
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
                mfdMatchDetails.setMatch_age_range(mContext.getString(R.string.not_available));
            bundle.putString(mContext.getString(R.string.field_match_age_range), mfdMatchDetails.getMatch_age_range());

            if (TextUtils.isEmpty(fdMyDetails.getBirthdate()))
                fdMyDetails.setBirthdate(mContext.getString(R.string.not_available));
            bundle.putString(mContext.getString(R.string.field_birth_date), fdMyDetails.getBirthdate());

            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isAb_p());

            fragment_name = mContext.getString(R.string.fragment_age_and_birthdate);
            sCurrentFragment = mContext.getString(R.string.fragment_age_and_birthdate);

        } else if (Detail == R.drawable.special_bg_gp_orange) {
            fragment = new GenderAndPronounsFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_match_gender), mfdMatchDetails.getMatch_gender());
            bundle.putString(mContext.getString(R.string.field_gender), fdMyDetails.getGender());
            bundle.putString(mContext.getString(R.string.field_pronouns), fdMyDetails.getPronouns());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isGp_p());

            fragment_name = mContext.getString(R.string.fragment_gender_and_pronouns);
            sCurrentFragment = mContext.getString(R.string.fragment_gender_and_pronouns);
        } else if (Detail == R.drawable.special_bg_gd_grey) {
            fragment = new GeneralDetailsFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_general_details), fdMyDetails.getGeneral_details());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_general_details), mfdMatchDetails.getMatch_general_details());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isGd_p());
            fragment_name = mContext.getString(R.string.fragment_general_details);
            sCurrentFragment = mContext.getString(R.string.fragment_general_details);
        } else if (Detail == R.drawable.special_bg_cy_red) {
            fragment = new CollegeYearFragment();
            bundle.putString(mContext.getString(R.string.field_college_year), fdMyDetails.getCollege_year());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_college_year), mfdMatchDetails.getMatch_college_year());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isCy_p());
            fragment_name = mContext.getString(R.string.fragment_college_year);
            sCurrentFragment = mContext.getString(R.string.fragment_college_year);
        } else if (Detail == R.drawable.special_bg_sic_green) {
            fragment = new SocietiesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_society_in_college), fdMyDetails.getSociety_in_college());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_society_in_college), mfdMatchDetails.getMatch_society_in_college());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isSic_p());
            fragment_name = mContext.getString(R.string.fragment_society_in_college);
            sCurrentFragment = mContext.getString(R.string.fragment_society_in_college);
        } else if (Detail == R.drawable.special_bg_dd_green) {
            fragment = new PositionsFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_titles_posts), fdMyDetails.getTitles_posts());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_titles_posts), mfdMatchDetails.getMatch_titles_posts());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isTp_p());

            fragment_name = mContext.getString(R.string.fragment_titles_posts);
            sCurrentFragment = mContext.getString(R.string.fragment_titles_posts);
        } else if (Detail == R.drawable.special_bg_hi_yellow) {
            fragment = new HobbiesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_hobbies), fdMyDetails.getHobbies());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_hobbies), mfdMatchDetails.getMatch_hobbies());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isH_p());
            fragment_name = mContext.getString(R.string.fragment_hobbies);
            sCurrentFragment = mContext.getString(R.string.fragment_hobbies);
        } else if (Detail == R.drawable.special_bg_vg_white) {
            fragment = new GamesFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_video_games), fdMyDetails.getVideo_games());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_video_games), mfdMatchDetails.getMatch_video_games());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isVg_p());
            fragment_name = mContext.getString(R.string.fragment_video_games);
            sCurrentFragment = mContext.getString(R.string.fragment_video_games);
        } else if (Detail == R.drawable.special_bg_mu_pink) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_music), fdMyDetails.getMusic());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_music), mfdMatchDetails.getMatch_music());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isMu_p());
            fragment_name = mContext.getString(R.string.fragment_music);
            sCurrentFragment = mContext.getString(R.string.fragment_music);
        } else if (Detail == R.drawable.special_bg_bo_orange) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_books), fdMyDetails.getBooks());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_books), mfdMatchDetails.getMatch_books());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isBo_p());
            fragment_name = mContext.getString(R.string.fragment_books);
            sCurrentFragment = mContext.getString(R.string.fragment_books);
        } else if (Detail == R.drawable.special_bg_mo_dark_blue) {
            fragment = new MusicMoviesBookFragment();
            bundle.putStringArrayList(mContext.getString(R.string.field_movie), fdMyDetails.getMovies());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_movie), mfdMatchDetails.getMatch_movie());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isMo_p());
            fragment_name = mContext.getString(R.string.fragment_movie);
            sCurrentFragment = mContext.getString(R.string.fragment_movie);
        } else if (Detail == R.drawable.special_bg_dd_light_red) {
            fragment = new DreamDestinationFragment();
            bundle.putString(mContext.getString(R.string.field_my_loc), fdMyDetails.getMy_loc());
            bundle.putStringArrayList(mContext.getString(R.string.field_my_dd), fdMyDetails.getMy_dd());
            bundle.putString(mContext.getString(R.string.field_match_loc), mfdMatchDetails.getMatch_loc());
            bundle.putStringArrayList(mContext.getString(R.string.field_match_dd), mfdMatchDetails.getMatch_dd());
            bundle.putBoolean(mContext.getString(R.string.field_is_private), fpdDetails.isDd_p());
            fragment_name = mContext.getString(R.string.fragment_dream_destinations);
            sCurrentFragment = mContext.getString(R.string.fragment_dream_destinations);
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

        for (int i = 0; i < 12; i++) {
            int finalI = i;
            altTextViewList.get(i).setOnClickListener(v -> {
                PerformSelection(altTextViewList.get(finalI), aliImageViewList.get(finalI));
                MoveToSelected(finalI, alcColoursList.get(finalI));

            });

        }
    }


}


