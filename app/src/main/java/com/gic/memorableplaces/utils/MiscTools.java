package com.gic.memorableplaces.utils;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MiscTools {
    private static final String TAG = "DatePostedLogic";
    private boolean Days = false;
    private boolean Hours = false;
    private boolean Minutes = false;
    private boolean Seconds = false;
    public ArrayList<String> alsQuesList = new ArrayList<>(21);


    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FilterPrivacyDetails filterPrivacyDetails;
    private FilterDetails filterDetails;
    private MatchFilterDetails mfd;
    private Context mContext;
    private ArrayList<String> alsDetails;
    private ArrayList<String> alsFilterNameList;
    private ArrayList<ArrayList<String>> alsAllDetails;
    private Runnable rMusic, rMovies, rFinal;
    private Gson gson;

    public MiscTools() {
        alsQuesList.add("What are your some of your dream destinations?");
        alsQuesList.add("What are your favourite restaurants in your locality?");
        alsQuesList.add("What is your idea of a fun night?");
        alsQuesList.add("Do you agree with splitting the bill on the first date?");
        alsQuesList.add("What superpower do you wish for?");
        alsQuesList.add("What are some pros of dating me?");
        alsQuesList.add("Are you comfortable with giving/receiving expensive gifts?");
        alsQuesList.add("What is your idea of a perfect first date?");
        alsQuesList.add("What is something that you can't tolerate?");
        alsQuesList.add("What are your three wishes, in case you find a genie?");
        alsQuesList.add("Can you have a long distance distance relationship?");
        alsQuesList.add("What is the one thing that you can't live without?");
        alsQuesList.add("Something that I will definitely want my partner to try");
        alsQuesList.add("How do you feel about marriage?");
        alsQuesList.add("One thing that scares you the most?");
        alsQuesList.add("What is the one thing that you do daily?");
        alsQuesList.add("Do you engage in social work?");
        alsQuesList.add("You need swipe right, if....");
        alsQuesList.add("How possessive are you in a relationship?");
        alsQuesList.add("Can you find time for both your carrier and relationship?");

    }

    public MiscTools(boolean CreateFilterList, Context context) {
        mContext = context;
        if (CreateFilterList) {
            alsFilterNameList = new ArrayList<>(12);
            alsFilterNameList.add(0, mContext.getString(R.string.age));
            alsFilterNameList.add(1, mContext.getString(R.string.gender));
            alsFilterNameList.add(2, mContext.getString(R.string.field_general_details));
            alsFilterNameList.add(3, mContext.getString(R.string.field_college_year));
            alsFilterNameList.add(4, mContext.getString(R.string.title_post));
            alsFilterNameList.add(5, mContext.getString(R.string.books));
            alsFilterNameList.add(6, mContext.getString(R.string.movie));
            alsFilterNameList.add(7, mContext.getString(R.string.music));
            alsFilterNameList.add(8, mContext.getString(R.string.hobbies));
            alsFilterNameList.add(9, mContext.getString(R.string.societies));
            alsFilterNameList.add(10, mContext.getString(R.string.games));
            alsFilterNameList.add(11, mContext.getString(R.string.dream_destination));
        }

    }

    public MiscTools(DatabaseReference myRef,
                     FirebaseAuth mAuth, Context context, FilterPrivacyDetails filterPrivacyDetails,
                     FilterDetails filterDetails, MatchFilterDetails mfd, ArrayList<String> alsDetails,
                     ArrayList<ArrayList<String>> alsAllDetails, Runnable rFinal) {

        this.myRef = myRef;
        this.mAuth = mAuth;
        this.mContext = context;
        this.filterDetails = filterDetails;
        this.filterPrivacyDetails = filterPrivacyDetails;
        this.alsDetails = alsDetails;
        this.rFinal = rFinal;
        this.alsAllDetails = alsAllDetails;
        this.mfd = mfd;
        gson = new Gson();
    }

    public String SetUpDateWidget(String ClassName, boolean comment, String TimeStamp) {
        String TimeDifference = "";
        String TimePassed = null;
        //Log.d(TAG, String.format("SetUpDateWidget: ClassName %s", ClassName));
        TimeDifference = getTimeStampDifference(ClassName, TimeStamp);
        //Log.d(TAG, String.format("SetUpDateWidget: TimeDifference %s", TimeDifference));
        // Log.d(TAG, String.format("getTimeStampDifference: photo %s", PostTimeStamp));

        if (!comment) {
            if (Days) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s day ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s days ago", TimeDifference));
                }
            } else if (Hours) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s hour ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s hours ago", TimeDifference));
                }
            } else if (Minutes) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s minute ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s minutes ago", TimeDifference));
                }
            } else if (Seconds) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s second ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s seconds ago", TimeDifference));
                }
            }
        } else {
            if (Days) {
                TimePassed = (String.format("%sd", TimeDifference));
            } else if (Hours) {

                TimePassed = (String.format("%sh", TimeDifference));

            } else if (Minutes) {
                TimePassed = (String.format("%sm", TimeDifference));

            } else if (Seconds) {

                TimePassed = (String.format("%ss", TimeDifference));

            }
        }
        return TimePassed;
    }

    public String getTimeStampDifference(String Date_added, String TimeStamp) {

        String difference = "";
        int differenceD = 0, differenceH = 0, differenceM = 0, differenceS = 0;
        // Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        Date today = null;
        try {
            today = sdf.parse(TimeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //sdf.format(today);
        Date timeStamp;


        try {
            timeStamp = sdf.parse(Date_added);
            assert timeStamp != null;
            // Log.d(TAG, "getTimeStampDifference: timeStamp " + timeStamp.getTime());
            // Log.d(TAG, "getTimeStampDifference: today " + today.getTime());
            differenceD = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60 / 60 / 24);
            // Log.d(TAG, "getTimeStampDifference: differenceD " + differenceD);
            difference = String.valueOf(differenceD);
            Days = true;
            Hours = false;
            Minutes = false;
            Seconds = false;
            // Log.d(TAG, String.format("getTimeStampDifference: difference %s", difference));
            if (differenceD == 0) {

                differenceH = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60 / 60);
                difference = String.valueOf(differenceH);
                // Log.d(TAG, "getTimeStampDifference: differenceH " + differenceH);
                Days = false;
                Hours = true;
                Minutes = false;
                Seconds = false;
            }
            if (differenceH > 24 || differenceH == 0 && differenceD == 0) {
                differenceM = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60);
                difference = String.valueOf(differenceM);
                //  Log.d(TAG, "getTimeStampDifference: differenceH " + differenceM);
                Days = false;
                Hours = false;
                Minutes = true;
                Seconds = false;
            }
            if (differenceM > 60 || differenceM == 0 && differenceD == 0 && differenceH == 0) {
                differenceS = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000);
                difference = String.valueOf(differenceS);
                //  Log.d(TAG, "getTimeStampDifference: differenceH " + differenceS);
                Days = false;
                Hours = false;
                Minutes = false;
                Seconds = true;
            }

        } catch (ParseException e) {
            // Log.d(TAG, "getTimeStampDifference: ParseException " + e.getMessage());
            difference = "0";
        }
        //Log.d(TAG, String.format("getTimeStampDifference: difference %s", difference));
        return difference;
    }

    public static void InflateBalloonTooltip(Context mContext, String Title, int icon, View v) {
        final Balloon balloon;
        if (icon == 0) {
            balloon = new Balloon.Builder(mContext)
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(BalloonSizeSpec.WRAP)
                    .setTextSize(18f)
                    .setPadding(10)
                    .setCornerRadius(4f)
                    .setAlpha(0.9f)
                    .setText(Title)
                    .setTextColor(Color.WHITE)
                    .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                    .build();
        } else {
            balloon = new Balloon.Builder(mContext)
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(BalloonSizeSpec.WRAP)
                    .setTextSize(18f)
                    .setPadding(10)
                    .setCornerRadius(4f)
                    .setAlpha(0.9f)
                    .setText(Title)
                    .setTextColor(Color.WHITE)
                    .setIconDrawable(ContextCompat.getDrawable(mContext, icon))
                    .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                    .build();
        }
        balloon.showAlignTop(v);
    }

    public static int convertDpToPixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int GetFilterIcon(String DetailName, Context mContext) {
        int icon = 0;
        if (DetailName.equals(mContext.getString(R.string.field_age))) {
            icon = R.drawable.ic_filter_age;
        } else if (DetailName.equals(mContext.getString(R.string.field_course))) {
            icon = R.drawable.ic_course_icon;
        } else if (DetailName.equals(mContext.getString(R.string.field_gender))) {
            icon = R.drawable.ic_filter_gender;
        } else if (DetailName.equals(mContext.getString(R.string.field_college_year))) {
            icon = R.drawable.ic_filter_college_year;
        } else if (DetailName.equals(mContext.getString(R.string.field_zodiac_sign))) {
            icon = R.drawable.ic_filter_zodiac_scorpion;
        } else if (DetailName.equals(mContext.getString(R.string.field_hobbies))) {
            icon = R.drawable.ic_filter_hobbies;
        } else if (DetailName.equals(mContext.getString(R.string.field_games))) {
            icon = R.drawable.ic_filter_video_games;
        } else if (DetailName.equals(mContext.getString(R.string.field_music))) {
            icon = R.drawable.ic_filter_music;
        } else if (DetailName.equals(mContext.getString(R.string.field_movie))) {
            icon = R.drawable.ic_filter_movie;
        } else if (DetailName.equals(mContext.getString(R.string.field_books))) {
            icon = R.drawable.ic_filter_book;
        } else if (DetailName.equals(mContext.getString(R.string.field_society))) {
            icon = R.drawable.ic_filter_society;
        } else if (DetailName.equals(mContext.getString(R.string.field_pronouns))) {
            icon = R.drawable.ic_filter_gender;
        }
        return icon;
    }

    public static String GetTimeStamp(long milliseconds) {

        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return formatter.format(calendar.getTime());
    }

    public static String GetKeyForFilter(String DetailName, Context mContext) {
        String key = "";
        if (DetailName.equals(mContext.getString(R.string.field_games))) {
            key = mContext.getString(R.string.field_game_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_music))) {
            key = mContext.getString(R.string.field_track_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_movie))) {
            key = mContext.getString(R.string.field_movie_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_books))) {
            key = mContext.getString(R.string.field_book_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_society))) {
            key = mContext.getString(R.string.society_name);
        }
        return key;
    }

    public static String ChangeKeyForFirebase(String key) {
        String newkey = key.replaceAll("\\.", " ");
        newkey = newkey.replaceAll("#", " ");
        newkey = newkey.replaceAll("\\$", " ");
        newkey = newkey.replaceAll("\\[", " ");
        newkey = newkey.replaceAll("]", " ");

        return newkey;
    }

    public static Dialog InflateDialog(Context mContext, int layout) {

        final Dialog dialog = new Dialog(mContext);
        //boolean isLocked  = false;
        requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(layout);

        return dialog;
    }

    public static void ToggleLoadingScreen(ConstraintLayout CL_LOADING) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (CL_LOADING.getTag().equals("visible")) {
                CL_LOADING.setVisibility(View.GONE);
                CL_LOADING.setTag("gone");
            } else {
                CL_LOADING.setVisibility(View.VISIBLE);
                CL_LOADING.setTag("visible");
            }
        });

    }

    public ArrayList<String> Get6Filters(FilterDetails fd, FilterPrivacyDetails fpd) {

        ArrayList<String> alsSelectedFilters = new ArrayList<>(6);
        int count = 0;
        if (!fpd.isAb_p()) {
            alsSelectedFilters.add(String.valueOf(fd.getAge()));//0
            alsSelectedFilters.add(String.valueOf(R.drawable.ic_filter_age));//1
            count++;
        }
        if (!fpd.isGp_p()) {
            alsSelectedFilters.add(String.valueOf(fd.getGender()));//2

            alsSelectedFilters.add(String.valueOf(R.drawable.ic_filter_gender));//3
            count++;
        }
        if (!fpd.isCy_p()) {
            alsSelectedFilters.add(String.valueOf(fd.getCollege_year()));//4

            alsSelectedFilters.add(String.valueOf(R.drawable.ic_filter_college_year));//5
            count++;
        }
        if (fd.getGeneral_details().size() > 1) {
            if (!fpd.isGd_p()) {
                alsSelectedFilters.add(String.valueOf(fd.getGeneral_details().get(6)));//6
                alsSelectedFilters.add(String.valueOf(R.drawable.ic_filter_general_details));//7
                alsSelectedFilters.add(  String.valueOf(fd.getGeneral_details().get(6)));//8
                alsSelectedFilters.add(String.valueOf(fd.getGeneral_details().get(6)));//9
                count++;
            }

        }
        if (count == 3) {
            if (!fpd.isGp_p() || fd.getPronouns().equals(mContext.getString(R.string.not_available))) {
                alsSelectedFilters.add(fd.getPronouns());//6
                alsSelectedFilters.add(String.valueOf(R.drawable.ic_filter_gender));//7
            }

        }

        return alsSelectedFilters;
    }

    public void GetCardDetails(String UserUID) {
        // Log.d(TAG, "GetCardDetails: entered :" + UserUID);
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(UserUID)
                .child(mContext.getString(R.string.field_is_private));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filterPrivacyDetails = snapshot.getValue(FilterPrivacyDetails.class);

                Query Q_Details = myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(UserUID)
                        .child(mContext.getString(R.string.field));
                Q_Details.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {

                            // Log.d(TAG, String.format("onDataChange: My snapshot: %s", snapshot));
                            //AGE DETAIL 0
                            if (!filterPrivacyDetails.isAb_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_age))) {
                                    filterDetails.setAge((Long) snapshot.child(mContext.getString(R.string.field_age)).getValue());
                                    if (snapshot.hasChild(mContext.getString(R.string.field_birth_date))) {
                                        filterDetails.setBirthdate(String.valueOf(snapshot.child(mContext.getString(R.string.field_birth_date)).getValue()));
                                    }
                                }
                            }
                            AddCardDetailsToArrayList(0, "0", filterPrivacyDetails.isAb_p(), "Age\nBirthDate", "Age: " + filterDetails.getAge() + "\nBirthDate: " + filterDetails.getBirthdate(), R.drawable.ic_filter_age, String.valueOf(filterDetails.getAge()), filterDetails.getBirthdate(), mfd.getMatch_age_range(), mContext.getString(R.string.not_available));
                            //Title Posts DETAIL 1
                            StringBuilder sbTitles = new StringBuilder();
                            if (!filterPrivacyDetails.isTp_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_titles_posts))) {
                                    ArrayList<String> titles = new ArrayList<>(5);
                                    sbTitles.append("Titles & Posts\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_titles_posts)).getChildren()) {
                                        titles.add(String.valueOf(dataSnapshot.getValue()).replace("$o*", ", "));
                                        //   Log.d(TAG, "onDataChange: titles: " + titles);
                                        sbTitles.append(count).append(". ").append(titles.get(count - 1)).append("\n");
                                        count++;
                                    }
                                    // Log.d(TAG, "onDataChange: StringBuidler Titles: " + sbTitles.toString());
                                    filterDetails.setTitles_posts(titles);
                                }
                            }
                            AddCardDetailsToArrayList(1, "4", filterPrivacyDetails.isTp_p(), "Titles & Posts", sbTitles.toString(), R.drawable.ic_posts_titles, gson.toJson(filterDetails.getTitles_posts()), mContext.getString(R.string.not_available), mContext.getString(R.string.not_available), mContext.getString(R.string.not_available));

                            //MOVIE 10

                            rMovies = () -> {
                                if (!filterPrivacyDetails.isMo_p()) {
                                    if (snapshot.hasChild(mContext.getString(R.string.field_movie))) {
                                        ArrayList<String> movie = new ArrayList<>(5);
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("Favourite Movies\n");
                                        int count = 1;
                                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_movie)).getChildren()) {
                                            movie.add(String.valueOf(dataSnapshot.getValue()));
                                        }
                                        Log.d(TAG, "onDataChange: reached here");
                                        GetMovieFromID(count, movie, stringBuilder, count == movie.size());
                                        filterDetails.setMovies(movie);

                                    }
                                } else {

                                    Log.d(TAG, "onDataChange: reached here 3");
                                    AddCardDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", null, R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
                                    rFinal.run();
                                }
                            };

                            //MUSIC 4
                            rMusic = () -> {
                                if (!filterPrivacyDetails.isMu_p()) {
                                    if (snapshot.hasChild(mContext.getString(R.string.field_music))) {
                                        ArrayList<String> music = new ArrayList<>(5);
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("Music I Love\n");
                                        int count = 1;
                                        for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_music)).getChildren()) {
                                            music.add(String.valueOf(dataSnapshot.getValue()));
                                        }
                                        GetMusicFromID(count, music, stringBuilder, count == music.size());
                                        filterDetails.setMusic(music);

                                    }
                                } else {
                                    AddCardDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", null, R.drawable.ic_filter_music, null, null, null, null);
                                    rMovies.run();
                                }
                            };

                            //Books DETAIL 2
                            if (!filterPrivacyDetails.isBo_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_books))) {
                                    ArrayList<String> books = new ArrayList<>(5);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Fav Books\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_books)).getChildren()) {
                                        books.add(String.valueOf(dataSnapshot.getValue()));
                                    }
                                    GetBooksFromID(count, books, stringBuilder, count == books.size());
                                    filterDetails.setBooks(books);
                                }
                            } else {
                                AddCardDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", null, R.drawable.ic_filter_book, null, null, null, null);
                                Log.d(TAG, "onDataChange: books completed:");
                                rMusic.run();
                            }

                            //HOBBIES 3
                            StringBuilder sbHobbies = new StringBuilder();
                            if (!filterPrivacyDetails.isH_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_hobbies))) {
                                    ArrayList<String> hobbies = new ArrayList<>(5);

                                    sbHobbies.append("My Hobbies\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_hobbies)).getChildren()) {
                                        hobbies.add(String.valueOf(dataSnapshot.getValue()));
                                        sbHobbies.append(count).append(". ").append(hobbies.get(count - 1)).append("\n");
                                        count++;
                                    }
                                    filterDetails.setHobbies(hobbies);

                                }
                            }
                            AddCardDetailsToArrayList(3, "2", filterPrivacyDetails.isH_p(), "My Hobbies", sbHobbies.toString(), R.drawable.ic_filter_hobbies, gson.toJson(filterDetails.getHobbies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_hobbies()), mContext.getString(R.string.not_available));

                            //Society In College 5
                            StringBuilder sbSociety = new StringBuilder();
                            if (!filterPrivacyDetails.isSic_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_society_in_college))) {
                                    ArrayList<String> society = new ArrayList<>(5);

                                    sbSociety.append("My Societies in College\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_society_in_college)).getChildren()) {
                                        society.add(String.valueOf(dataSnapshot.getValue()));
                                        sbSociety.append(count).append(". ").append(society.get(count - 1)).append("\n");
                                        count++;
                                    }
                                    filterDetails.setSociety_in_college(society);
                                }
                            }
                            AddCardDetailsToArrayList(5, "2", filterPrivacyDetails.isSic_p(), "My Societies in College", sbSociety.toString(), R.drawable.ic_filter_society, gson.toJson(filterDetails.getSociety_in_college()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_society_in_college()), mContext.getString(R.string.not_available));

                            //Games 8
                            StringBuilder sbGames = new StringBuilder();
                            if (!filterPrivacyDetails.isVg_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_video_games))) {
                                    ArrayList<String> games = new ArrayList<>(5);

                                    sbGames.append("Top Games\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_video_games)).getChildren()) {
                                        games.add(String.valueOf(dataSnapshot.getValue()));
                                        sbGames.append(count).append(". ").append(games.get(count - 1).replace("-", " ")).append("\n");
                                        count++;
                                    }
                                    filterDetails.setVideo_games(games);

                                }
                            }
                            AddCardDetailsToArrayList(8, "2", filterPrivacyDetails.isVg_p(), "Top Games", sbGames.toString(), R.drawable.ic_filter_video_games, gson.toJson(filterDetails.getVideo_games()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_video_games()), mContext.getString(R.string.not_available));

                            //GENDER DETAIL 6
                            if (!filterPrivacyDetails.isGp_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_gender))) {
                                    filterDetails.setGender(String.valueOf(snapshot.child(mContext.getString(R.string.field_gender)).getValue()));
                                    if (snapshot.hasChild(mContext.getString(R.string.field_pronouns))) {
                                        filterDetails.setPronouns(String.valueOf(snapshot.child(mContext.getString(R.string.field_pronouns)).getValue()));
                                    }

                                }
                            }
                            AddCardDetailsToArrayList(6, "1", filterPrivacyDetails.isGp_p(), "Gender\nPronoun", "Gender: " + filterDetails.getGender() + "\nPronoun: " + filterDetails.getPronouns(), R.drawable.ic_filter_gender, filterDetails.getGender(), filterDetails.getPronouns(), gson.toJson(mfd.getMatch_gender()), mContext.getString(R.string.not_available));

                            //COLLEGE YEAR DETAILS 7
                            if (!filterPrivacyDetails.isCy_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_college_year))) {
                                    filterDetails.setCollege_year(String.valueOf(snapshot.child(mContext.getString(R.string.field_college_year)).getValue()));
                                }
                            }
                            AddCardDetailsToArrayList(7, "1", filterPrivacyDetails.isCy_p(), "College year", "College year: " + filterDetails.getCollege_year(), R.drawable.ic_filter_college_year, filterDetails.getCollege_year(), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_college_year()), mContext.getString(R.string.not_available));

                            //GENERAL DETAILS 9
                            StringBuilder sbGeneral = new StringBuilder();
                            if (!filterPrivacyDetails.isCy_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_general_details))) {
                                    ArrayList<String> general_details = new ArrayList<>(10);

                                    sbGeneral.append("General Details\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_general_details)).getChildren()) {

                                        general_details.add(String.valueOf(dataSnapshot.getValue()));
                                        Log.d(TAG, "onDataChange: general_details: " + general_details);
                                        Log.d(TAG, "onDataChange: datasnapshot: " + dataSnapshot);
                                        sbGeneral.append(count).append(". ");
                                        switch (count) {
                                            case 1:
                                                int minHeight = 0;
                                                int maxHeight = 0;
                                                int myHeight = 0;
                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    String height = mfd.getMatch_general_details().get(0);
                                                    Log.d(TAG, "onDataChange: height: " + height);
                                                    minHeight = Integer.parseInt(height.substring(0, height.indexOf("-")));
                                                    maxHeight = Integer.parseInt(height.substring(height.indexOf("-") + 1));
                                                    myHeight = Integer.parseInt(general_details.get(0));
                                                }
                                                sbGeneral.append("Height: ").append(general_details.get(0)).append(" cm");
                                                if ((myHeight > minHeight && myHeight < maxHeight) || (myHeight == minHeight || myHeight == maxHeight))
                                                    sbGeneral.append(" ✅\n");
                                                else
                                                    sbGeneral.append(" ❌\n");
                                                break;
                                            case 2:
                                                sbGeneral.append("Education Level: ").append(general_details.get(1));
                                                Log.d(TAG, "onDataChange: general_detailsssss: " + general_details);
                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(1).equals(mfd.getMatch_general_details().get(1)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 3:
                                                sbGeneral.append("Exercise: ").append(general_details.get(2));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(2).equals(mfd.getMatch_general_details().get(2)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 4:
                                                sbGeneral.append("Drinking: ").append(general_details.get(3));


                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(3).equals(mfd.getMatch_general_details().get(3)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 5:
                                                sbGeneral.append("Smoking: ").append(general_details.get(4));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(4).equals(mfd.getMatch_general_details().get(4)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 6:
                                                sbGeneral.append("Looking For: ").append("\n");
                                                String user = general_details.get(5);
                                                String uFriends = user.substring(0, user.indexOf(","));
                                                String uDating = user.substring(user.indexOf(",") + 1, user.indexOf("*"));
                                                String uCareer = user.substring(user.indexOf("*"));
                                                String myUser;
                                                if (mfd.getMatch_general_details().size() != 1) {

                                                    myUser = mfd.getMatch_general_details().get(5);
                                                } else {
                                                    myUser = "N/A,N/A*N/A";
                                                }
                                                // Log.d(TAG, "onDataChange: general details: " + mfd.getMatch_general_details());
                                                String mFriends = myUser.substring(0, myUser.indexOf(","));
                                                String mDating = myUser.substring(myUser.indexOf(",") + 1, myUser.indexOf("*"));
                                                String mCareer = myUser.substring(myUser.indexOf("*"));

                                                sbGeneral.append("a) ").append("Friends").append(uFriends);
                                                if (uFriends.equals(mFriends))
                                                    sbGeneral.append(" ✅\n");
                                                else
                                                    sbGeneral.append(" ❌\n");

                                                sbGeneral.append("b) ").append("Dating").append(uDating);
                                                if (uDating.equals(mDating))
                                                    sbGeneral.append(" ✅\n");
                                                else
                                                    sbGeneral.append(" ❌\n");

                                                sbGeneral.append("c) ").append("Career").append(uCareer);
                                                if (uCareer.equals(mCareer))
                                                    sbGeneral.append(" ✅\n");
                                                else
                                                    sbGeneral.append(" ❌\n");
                                                break;
                                            case 7:
                                                sbGeneral.append("Sexual preference: ").append(general_details.get(6));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(6).equals(mfd.getMatch_general_details().get(6)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 8:
                                                sbGeneral.append("Political Inclination: ").append(general_details.get(7));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(7).equals(mfd.getMatch_general_details().get(7)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 9:
                                                sbGeneral.append("Zodiac Sign: ").append(general_details.get(8));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(8).equals(mfd.getMatch_general_details().get(8)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                            case 10:
                                                sbGeneral.append("Religion: ").append(general_details.get(9));

                                                if (mfd.getMatch_general_details().size() != 1) {
                                                    if (general_details.get(9).equals(mfd.getMatch_general_details().get(9)))
                                                        sbGeneral.append(" ✅\n");
                                                    else
                                                        sbGeneral.append(" ❌\n");
                                                } else {
                                                    sbGeneral.append(" ❌\n");
                                                }
                                                break;
                                        }
                                        sbGeneral.append("\n");
                                        count++;
                                    }
                                    // Log.d(TAG, "onDataChange: general_details: " + general_details);
                                    filterDetails.setGeneral_details(general_details);
                                }
                            }
                            AddCardDetailsToArrayList(9, "4", filterPrivacyDetails.isGd_p(), "General Details", sbGeneral.toString(), R.drawable.ic_filter_general_details, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());


                            //MY DD 11
                            StringBuilder sbLoc = new StringBuilder();
                            if (!filterPrivacyDetails.isDd_p()) {
                                if (snapshot.hasChild(mContext.getString(R.string.field_my_dd))) {


                                    ArrayList<String> my_dd = new ArrayList<>(5);

                                    sbLoc.append("Dream Destinations\n");
                                    int count = 1;
                                    for (DataSnapshot dataSnapshot : snapshot.child(mContext.getString(R.string.field_my_dd)).getChildren()) {
                                        my_dd.add(String.valueOf(dataSnapshot.getValue()));
                                        sbLoc.append(count).append(". ").append(my_dd.get(count - 1)).append("\n");
                                        count++;

                                    }
                                    if (snapshot.hasChild(mContext.getString(R.string.field_my_loc)))
                                        sbLoc.append("Hometown/location: ").append(snapshot.child(mContext.getString(R.string.field_my_loc)).getValue());
                                    else
                                        sbLoc.append("Hometown/location: ").append(mContext.getString(R.string.not_available));
                                    filterDetails.setMy_dd(my_dd);
                                    AddCardDetailsToArrayList(11, "3", filterPrivacyDetails.isDd_p(), "Dream Destinations", sbLoc.toString(), R.drawable.ic_filter_places, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());

                                }


                            } else {
                                AddCardDetailsToArrayList(11, "3", filterPrivacyDetails.isDd_p(), "Dream Destinations", sbLoc.toString(), R.drawable.ic_filter_places, gson.toJson(filterDetails.getMy_dd()), filterDetails.getMy_loc(), gson.toJson(mfd.getMatch_dd()), mfd.getMatch_loc());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Log.d(TAG, String.format("onDataChange: alsKeySet: %s", alsLeftKeySet));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetBooksFromID(int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
        Log.d(TAG, "GetBooksFromID: query: " + query);
        try {
            Log.d(TAG, "GetBooksFromID: pos: " + pos);
            Log.d(TAG, "GetBooksFromID: pos: " + pos);
            String id = query.get(pos - 1);
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/books/v1/volumes/"
                            + id)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // Do something when request failed
                    //Log.d(TAG, "onFailure: error " + e.getMessage());
                    //Log.d(TAG, "Request Failed.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error : " + response);
                    } else {
                        Log.d(TAG, "Request Successful.");
                    }

                    // Read data in the worker thread
                    final String data = response.body().string();
                    //Log.d(TAG, "onResponse: data: " + data);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        JSONObject volumeInfoObject = new JSONObject(jsonObject.getString("volumeInfo"));
                        //StringBuilder stringBuilder = new StringBuilder();
                        sbFinal.append(pos).append(". ");
                        if (jsonObject.has("id")) {
                            if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
                                sbFinal.append(mContext.getString(R.string.not_available));
                            } else {
                                sbFinal.append(volumeInfoObject.getString("title"));
                            }
                            sbFinal.append(" by ");
                            String authors = null;
                            if (volumeInfoObject.has("authors")) {
                                JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                                authors = authorsArray.getString(0);
                            } else {
                                authors = mContext.getString(R.string.not_available);
                            }
                            if (TextUtils.isEmpty(authors)) {
                                sbFinal.append(mContext.getString(R.string.not_available));
                            } else {
                                sbFinal.append(authors);
                            }
                            sbFinal.append("\n");
                        }
                        Log.d(TAG, "GetBooksFromID: a pos: " + pos);
                        Log.d(TAG, "GetBooksFromID: a pos: " + pos);

                        if (isLast) {
                            AddCardDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
                            rMusic.run();
                        } else {
                            GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
                        }
                    } catch (Exception e) {
//
//                        Handler handler = new Handler(Looper.getMainLooper());
//                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                        sbFinal.append(mContext.getString(R.string.not_available));
                        if (isLast) {
                            AddCardDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
                            rMusic.run();
                        } else {
                            GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
                        }
                        e.printStackTrace();
                    }
                    //Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
        } catch (Exception e) {
//
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
            sbFinal.append(mContext.getString(R.string.not_available));
            if (isLast) {
                AddCardDetailsToArrayList(2, "2", filterPrivacyDetails.isBo_p(), "Fav Books", sbFinal.toString(), R.drawable.ic_filter_book, gson.toJson(filterDetails.getBooks()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_books()), mContext.getString(R.string.not_available));
                rMusic.run();
            } else {
                GetBooksFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
            }
            e.printStackTrace();
        }
    }

    private void GetMusicFromID(int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
        String id = query.get(pos - 1);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://deezerdevs-deezer.p.rapidapi.com/track/" + id)
                .get()
                .addHeader("x-rapidapi-key", mContext.getString(R.string.Rapid_MUSIC_API_DEEZER))
                .addHeader("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Do something when request failed
                Log.d(TAG, "onFailure: error " + e.getMessage());
                Log.d(TAG, "Request Failed.");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Error : " + response);
                } else {
                    Log.d(TAG, "Request Successful.");
                }

                // Read data in the worker thread
                final String data = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(data);
                    sbFinal.append(pos).append(". ");
                    if (!TextUtils.isEmpty(jsonObject.getString("id"))) {
                        if (TextUtils.isEmpty(jsonObject.getString("title"))) {
                            sbFinal.append("N/A");
                        } else {
                            sbFinal.append(jsonObject.getString("title"));
                        }
                        sbFinal.append(" by ");
                        JSONObject jsonObjectNew = new JSONObject(jsonObject.getString("artist"));
                        if (TextUtils.isEmpty(jsonObjectNew.getString("name"))) {
                            sbFinal.append("N/A");
                        } else {
                            sbFinal.append(jsonObjectNew.getString("name"));
                        }
                    }

                    if (isLast) {
                        AddCardDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", sbFinal.toString(), R.drawable.ic_filter_music, gson.toJson(filterDetails.getMusic()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_music()), mContext.getString(R.string.not_available));
                        rMovies.run();
                    } else {
                        GetMusicFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
                    }

                } catch (Exception e) {
                    if (isLast) {
                        AddCardDetailsToArrayList(4, "2", filterPrivacyDetails.isMu_p(), "Music I Love", sbFinal.toString(), R.drawable.ic_filter_music, gson.toJson(filterDetails.getMusic()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_music()), mContext.getString(R.string.not_available));
                        rMovies.run();
                    } else {
                        GetMusicFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
                    }
                    e.printStackTrace();
                }
                // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
            }
        });

    }

    private void GetMovieFromID(final int pos, ArrayList<String> query, StringBuilder sbFinal, boolean isLast) {
        try {
            Log.d(TAG, "GetMovieFromID: query: " + query);
            String type = query.get(pos - 1).substring(query.get(pos - 1).indexOf("$4$") + 3);
            Log.d(TAG, "GetMovieFromID: type" + type);
            String name = query.get(pos - 1).substring(0, query.get(pos - 1).indexOf("$4$"));
            Log.d(TAG, "GetMovieFromID: name" + name);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/"
                            + type + "/"
                            + name
                            + "?api_key="
                            + mContext.getString(R.string.The_Movie_DB_API_KEY)
                            + "&page=1"
                            + "&include_adult=true")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // Do something when request failed
                    Log.d(TAG, "onFailure: error " + e.getMessage());
                    Log.d(TAG, "Request Failed.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error : " + response);
                    } else {
                        Log.d(TAG, "Request Successful.");
                    }

                    // Read data in the worker thread
                    final String data = response.body().string();
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(data);
                        sbFinal.append(pos).append(". ");
                        if (jsonObject.has("id")) {


                            if (type.equals("movie")) {
                                if (!jsonObject.has("title")) {
                                    sbFinal.append(mContext.getString(R.string.not_available));
                                } else {
                                    sbFinal.append(jsonObject.getString("title"));

                                }
                            } else if (type.equals("tv")) {
                                if (!jsonObject.has("name")) {
                                    sbFinal.append(mContext.getString(R.string.not_available));
                                } else {
                                    sbFinal.append(jsonObject.getString("name"));

                                }
                            }

                            sbFinal.append(", [");
                            sbFinal.append(type);
                            sbFinal.append("]\n");

                        }
                        Log.d(TAG, "onResponse: pos: " + pos);
                        if (isLast) {
                            AddCardDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> rFinal.run());
                        } else {
                            GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);
                        }
                        response.close();
                    } catch (Exception e) {
                        if (isLast) {
                            AddCardDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> rFinal.run());
                        } else {
                            GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);

                        }
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            if (isLast) {
                AddCardDetailsToArrayList(10, "2", filterPrivacyDetails.isMo_p(), "Favourite Movies", sbFinal.toString(), R.drawable.ic_filter_movie, gson.toJson(filterDetails.getMovies()), mContext.getString(R.string.not_available), gson.toJson(mfd.getMatch_movie()), mContext.getString(R.string.not_available));
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> rFinal.run());
            } else {
                GetMovieFromID(pos + 1, query, sbFinal, (pos) == query.size() - 1);

            }
            e.printStackTrace();
        }
    }

    private void AddCardDetailsToArrayList(int pos, String TypeOfField, boolean Privacy, String
            PrivacyContent, String Content, int drawable,
                                           String Value1, String Value2, String MatchValue, String MatchValue2) {
        alsDetails.add(TypeOfField);//0 (Type Of Field)
        alsDetails.add(String.valueOf(Privacy));//1 (PRivacy)
        if (Privacy) {
            alsDetails.add(PrivacyContent);//2
        } else {
            alsDetails.add(Content);//2
        }
        alsDetails.add(String.valueOf(drawable));//3
        alsDetails.add(String.valueOf(Value1));//4
        alsDetails.add(Value2);//5
        alsDetails.add(MatchValue);//6
        alsDetails.add(MatchValue2);//7

        alsAllDetails.set(pos, alsDetails);
        alsDetails = new ArrayList<>(10);
        Log.d(TAG, "AddCardDetailsToArrayList: alsAllDetails: " + alsAllDetails);
    }
}
