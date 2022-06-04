package com.gic.memorableplaces.FilterFriends;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gic.memorableplaces.Adapters.CardDetailsRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.DespAndQARecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

;

public class DataDisplayFragment extends Fragment {
    private static final String TAG = "DataDisplayFragment";
    private Context mContext;

    private TextView tvTitle;
    private RecyclerView rDescription;
    private AnimatedRecyclerView rDetails;
    private ImageView IV_BACK;
    private ConstraintLayout CL_LOADING;

    private CardDetailsRecyclerViewAdapter raDetailAdapter;
    private DespAndQARecyclerViewAdapter raDescriptionAdapter;
    private ZoomRecyclerLayout lmDescription;

    private String UserUID, desp;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private Gson gson;

    private ArrayList<ArrayList<String>> alsAllDetails;
    private ArrayList<String> alsDespLists, alsQuesList, alsDetails;

    private FilterPrivacyDetails filterPrivacyDetails;
    private MatchFilterDetails mfd;
    private FilterDetails filterDetails;

    private Runnable rMusic, rMovies, rFinal;
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPref;
    // private CardView cvCollegeCard,cvClassGroup,cvCRCard,cvMap,cvQuestion,cvBooks,cvSocieties,cvWebsite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);
        Log.d(TAG, "onCreateView: AgeFilterFragment");

        mContext = getActivity();
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        gson = new Gson();

        filterPrivacyDetails = new FilterPrivacyDetails();
        filterDetails = new FilterDetails();
        filterDetails.SetAllDefault("N/A");

        tvTitle = view.findViewById(R.id.CD_Heading);
        rDescription = view.findViewById(R.id.CD_RecyclerView_DESP_QA);
        CL_LOADING = view.findViewById(R.id.CL_DATA_LOADING_SCREEN);
        rDetails = view.findViewById(R.id.CD_RecylerView_Details);
        IV_BACK = view.findViewById(R.id.IV_BACK_CD);

        alsAllDetails = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            ArrayList<String> alsTemp = new ArrayList<>(1);
            alsTemp.add(mContext.getString(R.string.not_available));
            alsAllDetails.add(alsTemp);
        }


        alsDespLists = new ArrayList<>(10);
        alsQuesList = new ArrayList<>(10);
        alsDetails = new ArrayList<>(10);

        if (getArguments() != null) {
            UserUID = getArguments().getString(mContext.getString(R.string.field_user_id));
            desp = getArguments().getString(mContext.getString(R.string.field_auto_desp));
            mfd = (MatchFilterDetails) getArguments().getSerializable(mContext.getString(R.string.field_filter));
        }

        Log.d(TAG, "onCreateView: mfd: " + mfd);
        MiscTools.ToggleLoadingScreen(CL_LOADING);
        sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        IV_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireFragmentManager().beginTransaction().remove(DataDisplayFragment.this).commit();
            }
        });
        if (sharedPref.contains(UserUID + " Q")) {
            alsQuesList.add("An insight of my world");
            Gson gson = new Gson();
            String despList = sharedPref.getString(UserUID + " Q", "");
            alsDespLists = gson.fromJson(despList, ArrayList.class);
            for (String string : alsDespLists) {
                alsQuesList.add("Q. " + string);
            }
            if (alsDespLists.size() == 1) {
                alsDespLists.add("I haven't answered any questions yet.");
                alsQuesList.add("Questions");
            }
            rDescription.setHasFixedSize(true);
            lmDescription = new ZoomRecyclerLayout(mContext);
            lmDescription.setOrientation(LinearLayoutManager.HORIZONTAL);
            rDescription.setLayoutManager(lmDescription);
            raDescriptionAdapter = new DespAndQARecyclerViewAdapter(alsDespLists, alsQuesList, mContext, false, "", "",
                    "", "", "", null, null, null);
            rDescription.setAdapter(raDescriptionAdapter);

        } else {

            GetDescription();
        }
        if (sharedPref.contains(UserUID + " FD")) {
            Gson gson = new Gson();
            String sfd = sharedPref.getString(UserUID + " FD", "");
            String sfpd = sharedPref.getString(UserUID + " FP", "");
            String sad = sharedPref.getString(UserUID + " AD", "");
            filterDetails = gson.fromJson(sfd, FilterDetails.class);
            filterPrivacyDetails = gson.fromJson(sfpd, FilterPrivacyDetails.class);
            alsAllDetails = gson.fromJson(sad, ArrayList.class);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            rDetails.setLayoutManager(staggeredGridLayoutManager);

            raDetailAdapter = new CardDetailsRecyclerViewAdapter(alsAllDetails, filterDetails, mfd, filterPrivacyDetails, mContext);
            rDetails.setItemAnimator(new DefaultItemAnimator());
            rDetails.setAdapter(raDetailAdapter);
            raDetailAdapter.notifyDataSetChanged();
            rDetails.scheduleLayoutAnimation();
            MiscTools.ToggleLoadingScreen(CL_LOADING);
        } else {
            rFinal = () -> {
                Gson gson = new Gson();
                prefsEditor = sharedPref.edit();
                String sFilterDetails = gson.toJson(filterDetails);
                String sFilterPrivacy = gson.toJson(filterDetails);
                String sAllDetails = gson.toJson(alsAllDetails);
                prefsEditor.putString(UserUID + " FD", sFilterDetails);
                prefsEditor.putString(UserUID + " FP", sFilterPrivacy);
                prefsEditor.putString(UserUID + " AD", sAllDetails);
                prefsEditor.apply();
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                rDetails.setLayoutManager(staggeredGridLayoutManager);

                raDetailAdapter = new CardDetailsRecyclerViewAdapter(alsAllDetails, filterDetails, mfd, filterPrivacyDetails, mContext);
                rDetails.setItemAnimator(new DefaultItemAnimator());
                rDetails.setAdapter(raDetailAdapter);
                raDetailAdapter.notifyDataSetChanged();
                rDetails.scheduleLayoutAnimation();
                Log.d(TAG, "onCreateView: Reached Here 2:" + "");
                MiscTools.ToggleLoadingScreen(CL_LOADING);
            };
            MiscTools miscTools = new MiscTools(myRef, mAuth, mContext, filterPrivacyDetails, filterDetails, mfd, alsDetails, alsAllDetails, rFinal);

            miscTools.GetCardDetails(UserUID);
        }
        //GetDescription();
        Log.d(TAG, "onCreateView: contains: " + sharedPref.contains(UserUID + " FD"));
        Log.d(TAG, "onCreateView: filterDetails: " + filterDetails);
        Log.d(TAG, "onCreateView: filterPrivacyDetails: " + filterPrivacyDetails);
        Log.d(TAG, "onCreateView: alsAllDetails: " + alsAllDetails);

        return view;
    }


    private void GetDescription() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(UserUID)
                .child(mContext.getString(R.string.field_questions));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alsDespLists.add(desp);
                alsQuesList.add("An insight of my world");
                MiscTools miscTools = new MiscTools();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null)
                        alsQuesList.add("Q. " + miscTools.alsQuesList.get(Integer.parseInt(dataSnapshot.getKey()) - 1));
                    alsDespLists.add("Ans) " + dataSnapshot.getValue());
                }
                if (alsDespLists.size() == 1) {
                    alsDespLists.add("I haven't answered any questions yet.");
                    alsQuesList.add("Questions");
                }
                Gson gson = new Gson();
                prefsEditor = sharedPref.edit();
                String despList = gson.toJson(alsDespLists);
                prefsEditor.putString(UserUID + " Q", despList);
                prefsEditor.apply();
                rDescription.setHasFixedSize(true);
                lmDescription = new ZoomRecyclerLayout(mContext);
                lmDescription.setOrientation(LinearLayoutManager.HORIZONTAL);
                rDescription.setLayoutManager(lmDescription);
                raDescriptionAdapter = new DespAndQARecyclerViewAdapter(alsDespLists, alsQuesList, mContext, false, "", "",
                        "", "", "", null, null, null);
                rDescription.setAdapter(raDescriptionAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void GetCardDetails() {
//        // Log.d(TAG, "GetCardDetails: entered :" + UserUID);
//        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
//                .child(UserUID)
//                .child(mContext.getString(R.string.field_is_private));
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                filterPrivacyDetails = snapshot.getValue(FilterPrivacyDetails.class);
//
//                Query Q_Details = myRef.child(mContext.getString(R.string.dbname_user_card))
//                        .child(UserUID)
//                        .child(mContext.getString(R.string.field));
//                Q_Details.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//
//                            // Log.d(TAG, String.format("onDataChange: My snapshot: %s", snapshot));
//                            //AGE DETAIL 0
//                            if (!filterPrivacyDetails.isAb_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_age))) {
//                                    filterDetails.setAge((Long) snapshot.child(mContext.getString(R.string.field_age)).getValue());
//                                    if (snapshot.hasChild(mContext.getString(R.string.field_birth_date))) {
//                                        filterDetails.setBirthdate(String.valueOf(snapshot.child(mContext.getString(R.string.field_birth_date)).getValue()));
//                                    }
//                                }
//                            }
//                            AddDetailsToArrayList(0, "0", filterPrivacyDetails.isAb_p(), "Age\nBirthDate", "Age: " + filterDetails.getAge() + "\nBirthDate: " + filterDetails.getBirthdate(), R.drawable.ic_filter_age, String.valueOf(filterDetails.getAge()), filterDetails.getBirthdate(), mfd.getMatch_age_range(), mContext.getString(R.string.not_available));
//                            //Title Posts DETAIL 1
//                            StringBuilder sbTitles = new StringBuilder();
//                            if (!filterPrivacyDetails.isTp_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_titles_posts))) {
//                                    ArrayList<String> titles = new ArrayList<>(5);
//                                    sbTitles.append("Titles & Posts\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_titles_posts)).getChildren()) {
//                                        titles.add(String.valueOf(dataSnapshot.getValue()).replace("$o*", ", "));
//                                        //   Log.d(TAG, "onDataChange: titles: " + titles);
//                                        sbTitles.append(count).append(". ").append(titles.get(count - 1)).append("\n");
//                                        count++;
//                                    }
//                                    // Log.d(TAG, "onDataChange: StringBuidler Titles: " + sbTitles.toString());
//                                    filterDetails.setTitles_posts(titles);
//                                }
//                            }
//                            AddDetailsToArrayList(1, "4", filterPrivacyDetails.isTp_p(), "Titles & Posts", sbTitles.toString(), R.drawable.ic_posts_titles, gson.toJson(filterDetails.getTitles_posts()), mContext.getString(R.string.not_available), mContext.getString(R.string.not_available), mContext.getString(R.string.not_available));
//
//                            rFinal = () -> {
//                                //  Log.d(TAG, "onDataChange: movie: " + alsAllDetails.get(10));
//                                //  Log.d(TAG, "onDataChange: music: " + alsAllDetails.get(4));
//                                //    Log.d(TAG, "onDataChange: books: " + alsAllDetails.get(2));
//                                Gson gson = new Gson();
//                                prefsEditor = sharedPref.edit();
//                                String sFilterDetails = gson.toJson(filterDetails);
//                                String sFilterPrivacy = gson.toJson(filterDetails);
//                                String sAllDetails = gson.toJson(alsAllDetails);
//                                prefsEditor.putString(UserUID + " FD", sFilterDetails);
//                                prefsEditor.putString(UserUID + " FP", sFilterPrivacy);
//                                prefsEditor.putString(UserUID + " AD", sAllDetails);
//                                prefsEditor.apply();
//                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
//                                rDetails.setLayoutManager(staggeredGridLayoutManager);
//
//                                raDetailAdapter = new CardDetailsRecyclerViewAdapter(alsAllDetails, filterDetails, mfd, filterPrivacyDetails, mContext);
//                                rDetails.setItemAnimator(new DefaultItemAnimator());
//                                rDetails.setAdapter(raDetailAdapter);
//                                raDetailAdapter.notifyDataSetChanged();
//                                rDetails.scheduleLayoutAnimation();
//                                MiscTools.ToggleLoadingScreen(CL_LOADING);
//                            };
//                            //MOVIE 10
//
//                            rMovies = () -> {
//                                if (!filterPrivacyDetails.isMo_p()) {
//                                    if (snapshot.hasChild(mContext.getString(R.string.field_movie))) {
//                                        ArrayList<String> movie = new ArrayList<>(5);
//                                        StringBuilder stringBuilder = new StringBuilder();
//                                        stringBuilder.append("Favourite Movies\n");
//                                        int count = 1;
//                                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_movie)).getChildren()) {
//                                            movie.add(String.valueOf(dataSnapshot.getValue()));
//                                        }
//                                        GetMovieFromID(count, movie, stringBuilder, count == movie.size());
//                                        filterDetails.setMovies(movie);
//
//                                    }
//                                } else {
//                                    AddDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", null, R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
//                                    rFinal.run();
//                                }
//                            };
//
//                            //MUSIC 4
//                            rMusic = () -> {
//                                if (!filterPrivacyDetails.isMu_p()) {
//                                    if (snapshot.hasChild(mContext.getString(R.string.field_music))) {
//                                        ArrayList<String> music = new ArrayList<>(5);
//                                        StringBuilder stringBuilder = new StringBuilder();
//                                        stringBuilder.append("Music I Love\n");
//                                        int count = 1;
//                                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_music)).getChildren()) {
//                                            music.add(String.valueOf(dataSnapshot.getValue()));
//                                        }
//                                        GetMusicFromID(count, music, stringBuilder, count == music.size());
//                                        filterDetails.setMusic(music);
//
//                                    }
//                                } else {
//                                    AddDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", null, R.drawable.ic_filter_music, null, null, null, null);
//                                    rMovies.run();
//                                }
//                            };
//
//                            //Books DETAIL 2
//                            if (!filterPrivacyDetails.isBo_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_books))) {
//                                    ArrayList<String> books = new ArrayList<>(5);
//                                    StringBuilder stringBuilder = new StringBuilder();
//                                    stringBuilder.append("Fav Books\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_books)).getChildren()) {
//                                        books.add(String.valueOf(dataSnapshot.getValue()));
//                                    }
//                                    GetBooksFromID(count, books, stringBuilder, count == books.size());
//                                    filterDetails.setBooks(books);
//                                }
//                            } else {
//                                AddDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", null, R.drawable.ic_filter_book, null, null, null, null);
//                                rMusic.run();
//                            }
//
//                            //HOBBIES 3
//                            StringBuilder sbHobbies = new StringBuilder();
//                            if (!filterPrivacyDetails.isH_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_hobbies))) {
//                                    ArrayList<String> hobbies = new ArrayList<>(5);
//
//                                    sbHobbies.append("My Hobbies\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_hobbies)).getChildren()) {
//                                        hobbies.add(String.valueOf(dataSnapshot.getValue()));
//                                        sbHobbies.append(count).append(". ").append(hobbies.get(count - 1)).append("\n");
//                                        count++;
//                                    }
//                                    filterDetails.setHobbies(hobbies);
//
//                                }
//                            }
//                            AddDetailsToArrayList(3, "2", filterPrivacyDetails.isH_p(), "My Hobbies", sbHobbies.toString(), R.drawable.ic_filter_hobbies, gson.toJson(filterDetails.getHobbies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_hobbies()), mContext.getString(R.string.not_available));
//
//                            //Society In College 5
//                            StringBuilder sbSociety = new StringBuilder();
//                            if (!filterPrivacyDetails.isSic_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_society_in_college))) {
//                                    ArrayList<String> society = new ArrayList<>(5);
//
//                                    sbSociety.append("My Societies in College\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_society_in_college)).getChildren()) {
//                                        society.add(String.valueOf(dataSnapshot.getValue()));
//                                        sbSociety.append(count).append(". ").append(society.get(count - 1)).append("\n");
//                                        count++;
//                                    }
//                                    filterDetails.setSociety_in_college(society);
//                                }
//                            }
//                            AddDetailsToArrayList(5, "2", filterPrivacyDetails.isSic_p(), "My Societies in College", sbSociety.toString(), R.drawable.ic_filter_society, gson.toJson(filterDetails.getSociety_in_college()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_society_in_college()), mContext.getString(R.string.not_available));
//
//                            //Games 8
//                            StringBuilder sbGames = new StringBuilder();
//                            if (!filterPrivacyDetails.isVg_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_video_games))) {
//                                    ArrayList<String> games = new ArrayList<>(5);
//
//                                    sbGames.append("Top Games\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_video_games)).getChildren()) {
//                                        games.add(String.valueOf(dataSnapshot.getValue()));
//                                        sbGames.append(count).append(". ").append(games.get(count - 1).replace("-", " ")).append("\n");
//                                        count++;
//                                    }
//                                    filterDetails.setVideo_games(games);
//
//                                }
//                            }
//                            AddDetailsToArrayList(8, "2", filterPrivacyDetails.isVg_p(), "Top Games", sbGames.toString(), R.drawable.ic_filter_video_games, gson.toJson(filterDetails.getVideo_games()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_video_games()), mContext.getString(R.string.not_available));
//
//                            //GENDER DETAIL 6
//                            if (!filterPrivacyDetails.isGp_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_gender))) {
//                                    filterDetails.setGender(String.valueOf(snapshot.child(mContext.getString(R.string.field_gender)).getValue()));
//                                    if (snapshot.hasChild(mContext.getString(R.string.field_pronouns))) {
//                                        filterDetails.setPronouns(String.valueOf(snapshot.child(mContext.getString(R.string.field_pronouns)).getValue()));
//                                    }
//
//                                }
//                            }
//                            AddDetailsToArrayList(6, "1", filterPrivacyDetails.isGp_p(), "Gender\nPronoun", "Gender: " + filterDetails.getGender() + "\nPronoun: " + filterDetails.getPronouns(), R.drawable.ic_filter_gender, filterDetails.getGender(), filterDetails.getPronouns(), gson.toJson(mfd.getMatch_gender()), mContext.getString(R.string.not_available));
//
//                            //COLLEGE YEAR DETAILS 7
//                            if (!filterPrivacyDetails.isCy_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_college_year))) {
//                                    filterDetails.setCollege_year(String.valueOf(snapshot.child(mContext.getString(R.string.field_college_year)).getValue()));
//                                }
//                            }
//                            AddDetailsToArrayList(7, "1", filterPrivacyDetails.isCy_p(), "College year", "College year: " + filterDetails.getCollege_year(), R.drawable.ic_filter_college_year, filterDetails.getCollege_year(), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_college_year()), mContext.getString(R.string.not_available));
//
//                            //GENERAL DETAILS 9
//                            StringBuilder sbGeneral = new StringBuilder();
//                            if (!filterPrivacyDetails.isCy_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_general_details))) {
//                                    ArrayList<String> general_details = new ArrayList<>(10);
//
//                                    sbGeneral.append("General Details\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_general_details)).getChildren()) {
//                                        general_details.add(String.valueOf(dataSnapshot.getValue()));
//                                        sbGeneral.append(count).append(". ");
//                                        switch (count) {
//                                            case 1:
//                                                mfd.getMatch_general_details().set(0, "100-250");
//                                                String height = mfd.getMatch_general_details().get(0);
//                                                int minHeight = Integer.parseInt(height.substring(0, height.indexOf("-")));
//                                                int maxHeight = Integer.parseInt(height.substring(height.indexOf("-") + 1));
//                                                int myHeight = Integer.parseInt(general_details.get(0));
//                                                sbGeneral.append("Height: ").append(general_details.get(0)).append(" cm");
//                                                if ((myHeight > minHeight && myHeight < maxHeight) || (myHeight == minHeight || myHeight == maxHeight))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//
//                                                break;
//                                            case 2:
//                                                sbGeneral.append("Education Level: ").append(general_details.get(1));
//                                                if (general_details.get(1).equals(mfd.getMatch_general_details().get(1)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 3:
//                                                sbGeneral.append("Exercise: ").append(general_details.get(2));
//                                                if (general_details.get(2).equals(mfd.getMatch_general_details().get(2)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 4:
//                                                sbGeneral.append("Drinking: ").append(general_details.get(3));
//                                                if (general_details.get(3).equals(mfd.getMatch_general_details().get(3)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 5:
//                                                sbGeneral.append("Smoking: ").append(general_details.get(4));
//                                                if (general_details.get(4).equals(mfd.getMatch_general_details().get(4)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 6:
//                                                sbGeneral.append("Looking For: ").append("\n");
//                                                String user = general_details.get(5);
//                                                String uFriends = user.substring(0, user.indexOf(","));
//                                                String uDating = user.substring(user.indexOf(",") + 1, user.indexOf("*"));
//                                                String uCareer = user.substring(user.indexOf("*"));
//
//                                                String myUser = mfd.getMatch_general_details().get(5);
//                                                // Log.d(TAG, "onDataChange: general details: " + mfd.getMatch_general_details());
//                                                String mFriends = myUser.substring(0, myUser.indexOf(","));
//                                                String mDating = myUser.substring(myUser.indexOf(",") + 1, myUser.indexOf("*"));
//                                                String mCareer = myUser.substring(myUser.indexOf("*"));
//
//                                                sbGeneral.append("a) ").append("Friends").append(uFriends);
//                                                if (uFriends.equals(mFriends))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//
//                                                sbGeneral.append("b) ").append("Dating").append(uDating);
//                                                if (uDating.equals(mDating))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//
//                                                sbGeneral.append("c) ").append("Career").append(uCareer);
//                                                if (uCareer.equals(mCareer))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 7:
//                                                sbGeneral.append("Sexual preference: ").append(general_details.get(6));
//                                                if (general_details.get(6).equals(mfd.getMatch_general_details().get(6)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 8:
//                                                sbGeneral.append("Political Inclination: ").append(general_details.get(7));
//                                                if (general_details.get(7).equals(mfd.getMatch_general_details().get(7)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 9:
//                                                sbGeneral.append("Zodiac Sign: ").append(general_details.get(8));
//                                                if (general_details.get(8).equals(mfd.getMatch_general_details().get(8)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                            case 10:
//                                                sbGeneral.append("Religion: ").append(general_details.get(9));
//                                                if (general_details.get(9).equals(mfd.getMatch_general_details().get(9)))
//                                                    sbGeneral.append(" U+2705\n");
//                                                else
//                                                    sbGeneral.append(" U+274C\n");
//                                                break;
//                                        }
//                                        sbGeneral.append("\n");
//                                        count++;
//                                    }
//                                    // Log.d(TAG, "onDataChange: general_details: " + general_details);
//                                    filterDetails.setGeneral_details(general_details);
//                                }
//                            }
//                            AddDetailsToArrayList(9, "4", filterPrivacyDetails.isGd_p(), "General Details", sbGeneral.toString(), R.drawable.ic_filter_places, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());
//
//
//                            //MY DD 11
//                            StringBuilder sbLoc = new StringBuilder();
//                            if (!filterPrivacyDetails.isDd_p()) {
//                                if (snapshot.hasChild(mContext.getString(R.string.field_my_dd))) {
//
//
//                                    ArrayList<String> my_dd = new ArrayList<>(5);
//
//                                    sbLoc.append("Dream Destinations\n");
//                                    int count = 1;
//                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_my_dd)).getChildren()) {
//                                        my_dd.add(String.valueOf(dataSnapshot.getValue()));
//                                        sbLoc.append(count).append(". ").append(my_dd.get(count - 1)).append("\n");
//                                        count++;
//
//                                    }
//                                    if (snapshot.hasChild(mContext.getString(R.string.field_my_loc)))
//                                        sbLoc.append("Hometown/location: ").append(snapshot.child(mContext.getString(R.string.field_my_loc)).getValue());
//                                    else
//                                        sbLoc.append("Hometown/location: ").append(mContext.getString(R.string.not_available));
//                                    filterDetails.setMy_dd(my_dd);
//                                    AddDetailsToArrayList(11, "3", filterPrivacyDetails.isDd_p(), "Dream Destinations", sbLoc.toString(), R.drawable.ic_filter_places, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());
//
//                                }
//
//
//                            } else {
//                                AddDetailsToArrayList(11, "3", filterPrivacyDetails.isDd_p(), "Dream Destinations", sbLoc.toString(), R.drawable.ic_filter_places, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                //Log.d(TAG, String.format("onDataChange: alsKeySet: %s", alsLeftKeySet));
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//    private void GetBooksFromID(int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
//        Log.d(TAG, "GetBooksFromID: query: " + query);
//        try {
//            Log.d(TAG, "GetBooksFromID: pos: " + pos);
//            Log.d(TAG, "GetBooksFromID: pos: " + pos);
//            String id = query.get(pos - 1);
//            OkHttpClient client = new OkHttpClient();
//
//            Request request = new Request.Builder()
//                    .url("https://www.googleapis.com/books/v1/volumes/"
//                            + id)
//                    .get()
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    // Do something when request failed
//                    //Log.d(TAG, "onFailure: error " + e.getMessage());
//                    //Log.d(TAG, "Request Failed.");
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    if (!response.isSuccessful()) {
//                        throw new IOException("Error : " + response);
//                    } else {
//                        Log.d(TAG, "Request Successful.");
//                    }
//
//                    // Read data in the worker thread
//                    final String data = response.body().string();
//                    //Log.d(TAG, "onResponse: data: " + data);
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(data);
//                        JSONObject volumeInfoObject = new JSONObject(jsonObject.getString("volumeInfo"));
//                        //StringBuilder stringBuilder = new StringBuilder();
//                        sbFinal.append(pos).append(". ");
//                        if (jsonObject.has("id")) {
//                            if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
//                                sbFinal.append(mContext.getString(R.string.not_available));
//                            } else {
//                                sbFinal.append(volumeInfoObject.getString("title"));
//                            }
//                            sbFinal.append(" by ");
//                            String authors = null;
//                            if (volumeInfoObject.has("authors")) {
//                                JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
//                                authors = authorsArray.getString(0);
//                            } else {
//                                authors = mContext.getString(R.string.not_available);
//                            }
//                            if (TextUtils.isEmpty(authors)) {
//                                sbFinal.append(mContext.getString(R.string.not_available));
//                            } else {
//                                sbFinal.append(authors);
//                            }
//                            sbFinal.append("\n");
//                        }
//                        Log.d(TAG, "GetBooksFromID: a pos: " + pos);
//                        Log.d(TAG, "GetBooksFromID: a pos: " + pos);
//
//                        if (isLast) {
//                            AddDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
//                            rMusic.run();
//                        } else {
//                            GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//                        }
//                    } catch (Exception e) {
////
////                        Handler handler = new Handler(Looper.getMainLooper());
////                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
//                        sbFinal.append(mContext.getString(R.string.not_available));
//                        if (isLast) {
//                            AddDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
//                            rMusic.run();
//                        } else {
//                            GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//                        }
//                        e.printStackTrace();
//                    }
//                    //Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
//                }
//            });
//        } catch (Exception e) {
////
////            Handler handler = new Handler(Looper.getMainLooper());
////            handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
//            sbFinal.append(mContext.getString(R.string.not_available));
//            if (isLast) {
//                AddDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
//                rMusic.run();
//            } else {
//                GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//            }
//            e.printStackTrace();
//        }
//    }
//
//    private void GetMusicFromID(int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
//        String id = query.get(pos - 1);
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("https://deezerdevs-deezer.p.rapidapi.com/track/" + id)
//                .get()
//                .addHeader("x-rapidapi-key", mContext.getString(R.string.Rapid_MUSIC_API_DEEZER))
//                .addHeader("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                // Do something when request failed
//                Log.d(TAG, "onFailure: error " + e.getMessage());
//                Log.d(TAG, "Request Failed.");
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Error : " + response);
//                } else {
//                    Log.d(TAG, "Request Successful.");
//                }
//
//                // Read data in the worker thread
//                final String data = Objects.requireNonNull(response.body()).string();
//                JSONObject jsonObject;
//                try {
//                    jsonObject = new JSONObject(data);
//                    sbFinal.append(pos).append(". ");
//                    if (!TextUtils.isEmpty(jsonObject.getString("id"))) {
//                        if (TextUtils.isEmpty(jsonObject.getString("title"))) {
//                            sbFinal.append("N/A");
//                        } else {
//                            sbFinal.append(jsonObject.getString("title"));
//                        }
//                        sbFinal.append(" by ");
//                        JSONObject jsonObjectNew = new JSONObject(jsonObject.getString("artist"));
//                        if (TextUtils.isEmpty(jsonObjectNew.getString("name"))) {
//                            sbFinal.append("N/A");
//                        } else {
//                            sbFinal.append(jsonObjectNew.getString("name"));
//                        }
//                    }
//
//                    if (isLast) {
//                        AddDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", sbFinal.toString(), R.drawable.ic_filter_music, gson.toJson(filterDetails.getMusic()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_music()), mContext.getString(R.string.not_available));
//                        rMovies.run();
//                    } else {
//                        GetMusicFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//                    }
//
//                } catch (Exception e) {
//                    if (isLast) {
//                        AddDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", sbFinal.toString(), R.drawable.ic_filter_music, gson.toJson(filterDetails.getMusic()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_music()), mContext.getString(R.string.not_available));
//                        rMovies.run();
//                    } else {
//                        GetMusicFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//                    }
//                    e.printStackTrace();
//                }
//                // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
//            }
//        });
//
//    }
//
//    private void GetMovieFromID(final int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
//        try {
//            Log.d(TAG, "GetMovieFromID: query: " + query);
//            String type = query.get(pos - 1).substring(query.get(pos - 1).indexOf("$4$") + 3);
//            Log.d(TAG, "GetMovieFromID: type" + type);
//            String name = query.get(pos - 1).substring(0, query.get(pos - 1).indexOf("$4$"));
//            Log.d(TAG, "GetMovieFromID: name" + name);
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("https://api.themoviedb.org/3/"
//                            + type + "/"
//                            + name
//                            + "?api_key="
//                            + mContext.getString(R.string.The_Movie_DB_API_KEY)
//                            + "&page=1"
//                            + "&include_adult=true")
//                    .get()
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    // Do something when request failed
//                    Log.d(TAG, "onFailure: error " + e.getMessage());
//                    Log.d(TAG, "Request Failed.");
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    if (!response.isSuccessful()) {
//                        throw new IOException("Error : " + response);
//                    } else {
//                        Log.d(TAG, "Request Successful.");
//                    }
//
//                    // Read data in the worker thread
//                    final String data = response.body().string();
//                    JSONObject jsonObject = null;
//
//                    try {
//                        jsonObject = new JSONObject(data);
//                        sbFinal.append(pos).append(". ");
//                        if (jsonObject.has("id")) {
//
//
//                            if (type.equals("movie")) {
//                                if (!jsonObject.has("title")) {
//                                    sbFinal.append(mContext.getString(R.string.not_available));
//                                } else {
//                                    sbFinal.append(jsonObject.getString("title"));
//
//                                }
//                            } else if (type.equals("tv")) {
//                                if (!jsonObject.has("name")) {
//                                    sbFinal.append(mContext.getString(R.string.not_available));
//                                } else {
//                                    sbFinal.append(jsonObject.getString("name"));
//
//                                }
//                            }
//
//                            sbFinal.append(", [");
//                            sbFinal.append(type);
//                            sbFinal.append("]\n");
//
//                        }
//                        Log.d(TAG, "onResponse: pos: " + pos);
//                        if (isLast) {
//                            AddDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(() -> rFinal.run());
//                        } else {
//                            GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//                        }
//                        response.close();
//                    } catch (Exception e) {
//                        if (isLast) {
//                            AddDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(() -> rFinal.run());
//                        } else {
//                            GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            if (isLast) {
//                AddDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(() -> rFinal.run());
//            } else {
//                GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
//
//            }
//            e.printStackTrace();
//        }
//    }
//
//    private void AddDetailsToArrayList(int pos, String TypeOfField, boolean Privacy, String
//            PrivacyContent, String Content, int drawable,
//                                       String Value1, String Value2, String MatchValue, String MatchValue2) {
//        alsDetails.add(TypeOfField);//0 (Type Of Field)
//        alsDetails.add(String.valueOf(Privacy));//1 (PRivacy)
//        if (Privacy) {
//            alsDetails.add(PrivacyContent);//2
//        } else {
//            alsDetails.add(Content);//2
//        }
//        alsDetails.add(String.valueOf(drawable));//3
//        alsDetails.add(String.valueOf(Value1));//4
//        alsDetails.add(Value2);//5
//        alsDetails.add(MatchValue);//6
//        alsDetails.add(MatchValue2);//7
//
//        alsAllDetails.set(pos, alsDetails);
//        alsDetails = new ArrayList<>(10);
//    }
}
