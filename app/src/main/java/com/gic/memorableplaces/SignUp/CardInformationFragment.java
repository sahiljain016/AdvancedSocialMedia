

package com.gic.memorableplaces.SignUp;

import static com.gic.memorableplaces.utils.QueryDatabase.SetFieldsInDatabase5Lines;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.SelectedSocietyRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.SocietiesRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.ZodiacArrayAdapter;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.Home.HomeActivity;
import com.gic.memorableplaces.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitHelper;
import me.grantland.widget.AutofitTextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class CardInformationFragment extends Fragment {
    private static final String TAG = "CardInformationFragment";
    private static final int ERROR_DIALOG_REQUEST = 101;
    private static final int MAX_NUMBER_OF_RESULTS = 10;
    private static final int SHAKING_TIME = 100;
    private static final int SHAKING_OFFSET = 10;
    private static final int MAX_SELECTED_ITEMS = 5;
    private long last_text_edit = 0;
    private int i = 1;

    private RelativeLayout rlAge, rlGames, rlZodiacAndPosts, rlCollegeYears,
            rlGender, rlSocieties, rlInterests, rlMuMoBo, rlFavHangoutLoc;
    private AutofitTextView bAge, bGames, bZodiacAndPosts, bMuMoBo, bCollegeYears, bGender,
            bSocieties, bInterests, bFavHangoutPlaces;
    private ImageView AlertAge, AlertGames, AlertZodiacAndPosts, AlertMuMoBo, AlertCollegeYears, AlertGender,
            AlertSocieties, AlertInterests, AlertFavHangoutLoc;
    private AppCompatButton mBackCard, mNextCard;

    private String birthDate = "", sUserDescription;
    private boolean isClickedPrivate = false;
    private boolean isClickedGender = false;
    private boolean isDialogShown = false;
    private boolean isLocked = false;
    private boolean isAgeGiven = false;
    private boolean isBirthDateGiven = false;
    private boolean isGameGiven = false;
    private boolean isCollegeYearGiven = false;
    private boolean isMusicGiven = false;
    private boolean isMovieGiven = false;
    private boolean isBookGiven = false;
    private boolean isHobbyGiven = false;
    private boolean isGenderGiven = false;
    private boolean isOtherPostGiven = false;
    private boolean isPronounGiven = false;
    private boolean isZodiacGiven = false;
    private boolean isCRGiven = false;
    private boolean isSocietyGiven = false;
    private boolean isAllOK = false;
    private Bundle bundle;
    private String UserUID;
    private String sAge, sZodiacAndPosts, sMuMoBo, sGender, sCommonAlert;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    //Lists For all dialogs
    private Context mContext;
    private HashMap<String, String> statusOfFields;
    private HashMap<String, String> keyList;
    private HashMap<String, String> valueOfFields;
    private ArrayList<String> FieldList;
    private ArrayList<String> MissingItems;
    //Lists For Society dialog
    private ArrayList<String> SocietyNames;
    private ArrayList<String> SocietyDesp;
    private ArrayList<String> SocietyCover;
    private ArrayList<String> SelectedSocietyNames;
    private ArrayList<String> SelectedSocietyDesp;
    private ArrayList<String> SelectedSocietyCover;
    private List<String> socNames;
    private List<String> socCover;
    private List<String> socDesp;
    //Lists For Hobbies dialog
    private ArrayList<String> HobbiesList;
    private ArrayList<String> SelectedHobbies;
    //Lists For Zodiac dialog
    private ArrayList<String> ZodiacSigns;
    private ArrayList<Integer> ZodiacImages;
    private boolean is_CR_YES = false;
    private boolean is_CR_SELECTED = false;
    private boolean is_OTHER_SELECTED = false;
    private boolean is_OTHER_YES = false;

    private Animation myAnim;

    private AlertDialog.Builder builder;
    private LayoutInflater inflaterDialog;
    private AlertDialog Alertdialog;
    private AlertDialog PrivacyAlertDialog;
    private View vDialog;

    private AutoCompleteTextView SocietySearchBar;
    private RecyclerView.Adapter mAdapter;
    private ZoomRecyclerLayout mLayoutManager;
    private RecyclerView.Adapter mSelectedAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_pt_2, container, false);
        Log.d(TAG, "onCreateView: CardInformationFragment");

        mContext = getActivity();
        bundle = new Bundle();

        if (getArguments() != null) {
            sUserDescription = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_description)));
        }
        bundle.putString(mContext.getString(R.string.field_description), sUserDescription);
        statusOfFields = new HashMap<>();
        keyList = new HashMap<>();
        valueOfFields = new HashMap<>();
        FieldList = new ArrayList<>();
        MissingItems = new ArrayList<>();

        SocietyNames = new ArrayList<>();
        SocietyCover = new ArrayList<>();
        SocietyDesp = new ArrayList<>();
        socNames = new ArrayList<>();
        socCover = new ArrayList<>();
        socDesp = new ArrayList<>();
        SelectedSocietyNames = new ArrayList<>();
        SelectedSocietyDesp = new ArrayList<>();
        SelectedSocietyCover = new ArrayList<>();

        SelectedHobbies = new ArrayList<>();
        HobbiesList = new ArrayList<>();

        ZodiacImages = new ArrayList<>();
        ZodiacSigns = new ArrayList<>();

        rlAge = view.findViewById(R.id.age_rl);
        rlCollegeYears = view.findViewById(R.id.college_year_rl);
        rlZodiacAndPosts = view.findViewById(R.id.zodiac_posts_rl);
        rlGames = view.findViewById(R.id.games_rl);
        rlGender = view.findViewById(R.id.gender_rl);
        rlInterests = view.findViewById(R.id.interests_rl);
        rlFavHangoutLoc = view.findViewById(R.id.fav_hangout_location_rl);
        rlMuMoBo = view.findViewById(R.id.music_movies_books_rl);
        rlSocieties = view.findViewById(R.id.societies_rl);
        mNextCard = view.findViewById(R.id.next_card);
        mBackCard = view.findViewById(R.id.back_card);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        bAge = view.findViewById(R.id.age_button);
        bCollegeYears = view.findViewById(R.id.college_year_button);
        bZodiacAndPosts = view.findViewById(R.id.zodiac_posts_button);
        bGames = view.findViewById(R.id.games_button);
        bGender = view.findViewById(R.id.gender_button);
        bInterests = view.findViewById(R.id.interests_button);
        bFavHangoutPlaces = view.findViewById(R.id.fav_hangout_location);
        bMuMoBo = view.findViewById(R.id.mus_mov_boo_button);
        bSocieties = view.findViewById(R.id.societies_button);

        AlertAge = view.findViewById(R.id.AlertAge);
        AlertCollegeYears = view.findViewById(R.id.AlertCollegeYears);
        AlertZodiacAndPosts = view.findViewById(R.id.AlertZodiacAndPosts);
        AlertGames = view.findViewById(R.id.AlertGames);
        AlertGender = view.findViewById(R.id.AlertGender);
        AlertInterests = view.findViewById(R.id.AlertInterests);
        AlertFavHangoutLoc = view.findViewById(R.id.AlertFavLocation);
        AlertMuMoBo = view.findViewById(R.id.AlertMuMoBo);
        AlertSocieties = view.findViewById(R.id.AlertSocieties);

        AutofitHelper.create(bGames);
        // AutofitHelper.create(bMuMoBo);
        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        builder = new AlertDialog.Builder(getActivity());
        inflaterDialog = requireActivity().getLayoutInflater();

        UserUID = mAuth.getCurrentUser().getUid();
        boolean isFromFilters = false;
        if (getArguments() != null) {
            if(getArguments().containsKey("fragment_name")) {
                if (getArguments().getString("fragment_name").equals(mContext.getString(R.string.friends_filter_fragment))) {
                    isFromFilters = true;
                    mBackCard.setVisibility(View.GONE);
                    mNextCard.setVisibility(View.GONE);
                }
            }
        }
        if (!isFromFilters) {
            Query query = myRef.child(mContext.getString(R.string.dbname_users))
                    .child(UserUID)
                    .child(mContext.getString(R.string.field_page_number));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.getValue().toString().equals(mContext.getString(R.string.card_completed)) || !snapshot.exists()) {
                        myRef.child(mContext.getString(R.string.dbname_users))
                                .child(UserUID)
                                .child(mContext.getString(R.string.field_page_number))
                                .setValue("2");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //ON CLICK METHODS FOR BUTTONS
        MakeEverythingPublic();
        SetUserAge();
        SetMoMuBo();
        SetGender();
        SetCollegeYear();
        SetSociety();
        SetInterests();
        SetGames();
        SetZodiacAndPosts();
        OnPageChanges();


//        bZodiacAndPosts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlZodiacAndPosts.startAnimation(myAnim);
//
//                if (isServiceOK()) {
//                    FavouriteLocationMapDialog fragment = new FavouriteLocationMapDialog();
//                    FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    Transaction.replace(R.id.FrameLayoutCardInfo, fragment);
//                    Transaction.addToBackStack(mContext.getString(R.string.music_movies_books_fragment));
//                    Transaction.commit();
//                }
//            }
//        });

        bFavHangoutPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shaked");
                rlFavHangoutLoc.startAnimation(myAnim);
            }
        });


        rlAge.animate().alpha(1).setDuration(250).start();
        rlCollegeYears.animate().alpha(1).setDuration(250).setStartDelay(250);
        rlZodiacAndPosts.animate().alpha(1).setDuration(250).setStartDelay(500);
        rlGames.animate().alpha(1).setDuration(250).setStartDelay(750);
        rlGender.animate().alpha(1).setDuration(250).setStartDelay(1000);
        rlSocieties.animate().alpha(1).setDuration(250).setStartDelay(1250);
        rlInterests.animate().alpha(1).setDuration(250).setStartDelay(1500);
        rlMuMoBo.animate().alpha(1).setDuration(250).setStartDelay(1750);
        rlFavHangoutLoc.animate().alpha(1).setDuration(250).setStartDelay(2000);

        return view;
    }

    private void OnPageChanges() {
        mBackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CourseAndFullNameCardFragment fragment = new CourseAndFullNameCardFragment();
                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.course_and_full_name_Card_fragment));
                Transaction.commit();
                requireFragmentManager().beginTransaction().remove(CardInformationFragment.this).commit();
            }
        });

        mNextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogShown = true;
                Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field));
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MissingItems.clear();
                        isGameGiven = CheckDetailsGiven(mContext.getString(R.string.field_games), rlGames, AlertGames);
                        SetAlertOnClicks(AlertGames, sCommonAlert);
                        isCollegeYearGiven = CheckDetailsGiven(mContext.getString(R.string.field_college_year), rlCollegeYears, AlertCollegeYears);
                        SetAlertOnClicks(AlertCollegeYears, sCommonAlert);

                        isSocietyGiven = CheckDetailsGiven(mContext.getString(R.string.field_society), rlSocieties, AlertSocieties);
                        SetAlertOnClicks(AlertSocieties, sCommonAlert);
                        isHobbyGiven = CheckDetailsGiven(mContext.getString(R.string.field_hobbies), rlInterests, AlertInterests);
                        SetAlertOnClicks(AlertInterests, sCommonAlert);
                        CheckMMBGiven();
                        CheckAgeBirthDateGiven(mContext.getString(R.string.field_age), "0", mContext.getString(R.string.field_birth_date), "DD/MM/YYYY"
                                , rlAge, true, AlertAge, isAgeGiven, isBirthDateGiven);
                        CheckAgeBirthDateGiven(mContext.getString(R.string.field_gender), "N/A", mContext.getString(R.string.field_pronouns), "N/A", rlGender
                                , false, AlertGender, isGenderGiven, isPronounGiven);
                        CheckZodiacANDOtherPosts();


                        SetAlertOnClicks(AlertAge, sAge);
                        SetAlertOnClicks(AlertZodiacAndPosts, sZodiacAndPosts);
                        SetAlertOnClicks(AlertGender, sGender);
                        SetAlertOnClicks(AlertMuMoBo, sMuMoBo);
                        SetAlertOnClicks(AlertFavHangoutLoc, sCommonAlert);


                        isAllOK = isGenderGiven && isCollegeYearGiven && isAgeGiven && isGameGiven && isBirthDateGiven && isBookGiven
                                && isHobbyGiven && isMovieGiven && isMusicGiven && isSocietyGiven && isPronounGiven && isZodiacGiven
                                && isCRGiven && isOtherPostGiven;

                        if (isAllOK) {
                            requireFragmentManager().beginTransaction().remove(CardInformationFragment.this).commit();
                            ProfilePhotoFragment fragment = new ProfilePhotoFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
                            Transaction.addToBackStack(mContext.getString(R.string.profile_photo_fragment_card));
                            Transaction.commit();
                        } else {
                            if (i != 1) {
                                if (isGenderGiven && isBirthDateGiven && isCollegeYearGiven && isAgeGiven && isCRGiven && isOtherPostGiven) {
                                    if (isDialogShown) {

                                        InflateDialog(R.layout.dialog_missing_details);

                                        final Button Positive = vDialog.findViewById(R.id.positive);
                                        final Button Negative = vDialog.findViewById(R.id.negative);
                                        final TextView MissingItemsDetails = vDialog.findViewById(R.id.desp_missing_items);

                                        MissingItemsDetails.setText("");
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("The following details are missing: ");
                                        for (int i = 0; i < MissingItems.size(); i++) {
                                            stringBuilder.append("\n" + "⦿ ").append(MissingItems.get(i));
                                        }
                                        MissingItemsDetails.setText(stringBuilder.toString());

                                        Positive.setText("Continue Anyway");
                                        Negative.setText("Fill missing details");

                                        Positive.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alertdialog.dismiss();
                                                requireFragmentManager().beginTransaction().remove(CardInformationFragment.this).commit();
                                                ProfilePhotoFragment fragment = new ProfilePhotoFragment();
                                                fragment.setArguments(bundle);
                                                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
                                                Transaction.addToBackStack(mContext.getString(R.string.profile_photo_fragment_card));
                                                Transaction.commit();
                                                //Move To Next Profile Fragment
                                            }
                                        });

                                        Negative.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alertdialog.dismiss();
                                            }
                                        });

                                    }
                                }

                            }
                            i++;
                            isDialogShown = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void SetDetailForFilter(String Detail, String Value, String OldValue, String State) {


        if (State.equals(mContext.getString(R.string.field_public))) {

            if (!OldValue.equals("N/A")) {
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(Detail)
                        .child(OldValue)
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
            }
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(Value)
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(mContext.getString(R.string.field_user_id));
        } else {
            if (!OldValue.equals("N/A")) {
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(Detail)
                        .child(OldValue)
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
            }
        }
    }

    private void CheckStatus(String Detail, String Value, String State) {


        if (!State.equals(mContext.getString(R.string.field_public))) {


            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(Value)
                    .child(mAuth.getCurrentUser().getUid())
                    .removeValue();

        }
    }

    private void SetAlertOnClicks(ImageView Alert, final String AlertMessage) {

        Alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CheckAgeBirthDateGiven: AlertMessage " + AlertMessage);
                TextView Notice = new TextView(mContext);
                Notice.setPadding(30, 10, 30, 10);
                Notice.setTextColor(Color.WHITE);
                Notice.setTextSize(20);
                Notice.setBackgroundColor(Color.BLACK);
                Notice.setText(Html.fromHtml(AlertMessage, Html.FROM_HTML_MODE_LEGACY));
                builder.setView(Notice);
                AlertDialog noticeDialog = builder.create();
                noticeDialog.show();
            }
        });
    }

    private void CheckAgeBirthDateGiven(String CheckField1, String CheckAgainst1, String CheckField2, String CheckAgainst2,
                                        RelativeLayout Shake, boolean isAge, ImageView Alert, boolean CheckBoolean1, boolean CheckBoolean2) {
//        if (CardChildren.getKey().equals(mContext.getString(R.string.field_age))) {
        if (valueOfFields.get(CheckField1).equals(CheckAgainst1)) {

            makeMeShake(Shake, SHAKING_TIME, SHAKING_OFFSET);
            if (isAge)
                isAgeGiven = false;
            else
                isGenderGiven = false;

            MissingItems.add(CheckField1);
            Alert.setVisibility(View.VISIBLE);
        } else {

            if (isAge)
                isAgeGiven = true;
            else
                isGenderGiven = true;
            MissingItems.remove(CheckField1);
            Alert.setVisibility(View.INVISIBLE);
            //  }
        }

//        if (CardChildren.getKey().equals(mContext.getString(R.string.field_birth_date))) {
        if (valueOfFields.get(CheckField2).equals(CheckAgainst2)) {

            makeMeShake(Shake, SHAKING_TIME, SHAKING_OFFSET);
            if (isAge)
                isBirthDateGiven = false;
            else
                isPronounGiven = false;
            MissingItems.add(CheckField2);
            Alert.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "CheckAgeBirthDateGiven: birthDate Given");
            if (isAge)
                isBirthDateGiven = true;
            else
                isPronounGiven = true;
            MissingItems.remove(CheckField2);
            Alert.setVisibility(View.INVISIBLE);
            // }
        }
        if (isAge) {
            CheckBoolean1 = isAgeGiven;
            CheckBoolean2 = isBirthDateGiven;
        } else {
            CheckBoolean1 = isGenderGiven;
            CheckBoolean2 = isPronounGiven;
        }
        Log.d(TAG, "CheckAgeBirthDateGiven: isAgeGiven " + isAgeGiven);
        Log.d(TAG, "CheckAgeBirthDateGiven: isBirthDateGiven " + isBirthDateGiven);
        if (!CheckBoolean1 && CheckBoolean2) {
            //Set Age Error
            if (isAge)
                sAge = "Age is a required field. You can choose to keep it private, if you do not wish to share your age with other users.";
            else
                sGender = "Gender is a required field. You can choose to keep it private, if you do not wish to share your age with other users.";
            Alert.setVisibility(View.VISIBLE);
        } else if (CheckBoolean1 && !CheckBoolean2) {
            Alert.setVisibility(View.VISIBLE);
            if (isAge)
                sAge = "BirthDate is a required field. You can choose to keep it private, if you do not wish to share your BirthDate with other users.";
            else
                sGender = "Pronouns is an optional field. You can choose to keep it private, if you do not wish to share it with other users.";
            //Set BirthDate Error
        } else if (!CheckBoolean1 && !CheckBoolean2) {
            Alert.setVisibility(View.VISIBLE);
            if (isAge)
                sAge = "BirthDate & Age are required fields. You can choose to keep them private, if you do not wish to share them with other users.";
            else
                sGender = "Gender is a required fields & Pronouns is an optional field. You can choose to keep them private, if you do not wish to share them with other users.";
            //Set both Error
        }
    }

    private void CheckMMBGiven() {
        isMusicGiven = CheckDetailsGiven(mContext.getString(R.string.field_music), null, AlertMuMoBo);
        isMovieGiven = CheckDetailsGiven(mContext.getString(R.string.field_movie), null, AlertMuMoBo);
        isBookGiven = CheckDetailsGiven(mContext.getString(R.string.field_books), null, AlertMuMoBo);

        if (!isMusicGiven && isMovieGiven && isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Music is an optional field. Completing this detail will help you to find people with similar interest & vice versa.";
            //Set Age Error
        } else if (isMusicGiven && !isMovieGiven && isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Movie is an optional field. Completing this detail will help you to find people with similar interest & vice versa.";

            //Set BirthDate Error
        } else if (isMusicGiven && isMovieGiven && !isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Book is an optional field. Completing this detail will help you to find people with similar interest & vice versa.";

            //Set both Error
        } else if (!isMusicGiven && !isMovieGiven && isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Movie & Music are optional fields. Completing these detail will help you to find people with similar interest & vice versa.";

            //Set BirthDate Error
        } else if (isMusicGiven && !isMovieGiven && !isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Movie & Book are optional fields. Completing these detail will help you to find people with similar interest & vice versa.";

            //Set both Error
        } else if (!isMusicGiven && isMovieGiven && !isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "Book & Music are optional fields. Completing these detail will help you to find people with similar interest & vice versa.";

            //Set both Error
        } else if (!isMusicGiven && !isMovieGiven && !isBookGiven) {
            makeMeShake(rlMuMoBo, SHAKING_TIME, SHAKING_OFFSET);
            AlertMuMoBo.setVisibility(View.VISIBLE);
            sMuMoBo = "<B>Movie, Music & Book</B> are optional fields. Completing these detail will help you to find people with similar interest & vice versa.";

            //Set both Error
        }
    }

    private void CheckZodiacANDOtherPosts() {
        if (valueOfFields.get(mContext.getString(R.string.field_zodiac_sign)).equals("N/A")) {
            MissingItems.add("Zodiac Sign");
            isZodiacGiven = false;
        } else {
            MissingItems.remove("Zodiac Sign");
            isZodiacGiven = true;
        }
        if (valueOfFields.get(mContext.getString(R.string.field_class_representative)).equals("N/A")) {
            MissingItems.add("CR Column");
            isCRGiven = false;
        } else {
            MissingItems.remove("CR Column");
            isCRGiven = true;
        }

        if (valueOfFields.get(mContext.getString(R.string.field_other_posts)).equals("N/A")) {
            MissingItems.add("Other Posts Column");
            isOtherPostGiven = false;
        } else {
            MissingItems.remove("Other Posts Column");
            isOtherPostGiven = true;
        }

        if (!isZodiacGiven && isCRGiven && isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "Zodiac Sign missing, it is an optional fields. Completing this detail will help you to find people with similar zodiac signs & vice versa.";
        } else if (isZodiacGiven && !isCRGiven && isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "CR Options not chosen, it is a required fields. Please choose no, if you are not the CR of your class.";
        } else if (isZodiacGiven && isCRGiven && !isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "Other Posts/Positions Options not chosen, it is a required fields. Please choose no, if it is not applicable to you.";
        } else if (isZodiacGiven && !isCRGiven && !isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "Other Posts/Positions & CR Options not chosen, they are required fields. Please choose no, if they are not applicable to you.";
        } else if (!isZodiacGiven && isCRGiven && !isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "*Other Posts/Positions is not selected, it is a required field. Please choose an option\n*Zodiac Sign missing, it is an optional field, you can choose the \"No Zodiac Option\", if it does not apply to you.";
        } else if (!isZodiacGiven && !isCRGiven && isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "*CR Option is not selected, it is a required field. Please choose an option\n*Zodiac Sign missing, it is an optional field, you can choose the \"No Zodiac Option\", if it does not apply to you.";
        } else if (!isZodiacGiven && !isCRGiven && !isOtherPostGiven) {
            makeMeShake(rlZodiacAndPosts, SHAKING_TIME, SHAKING_OFFSET);
            AlertZodiacAndPosts.setVisibility(View.VISIBLE);
            sZodiacAndPosts = "*CR & Other Posts/Positions Options are not selected, they are required fields. Please choose an option for both\n*Zodiac Sign missing, it is an optional field, you can choose the \"No Zodiac Option\", if it does not apply to you.";
        }
    }

    private boolean CheckDetailsGiven(String DetailName, RelativeLayout DetailRL, ImageView alert) {
        Log.d(TAG, "CheckDetailsGiven: Detail Name" + DetailName);
        Log.d(TAG, String.format("CheckDetailsGiven: value of fields %s", valueOfFields));
        if (Objects.equals(valueOfFields.get(DetailName), "N/A")) {
            makeMeShake(DetailRL, SHAKING_TIME, SHAKING_OFFSET);
            // Log.d(TAG, "CheckDetailsGiven: Detail Name");
            MissingItems.add(DetailName);
            if (alert != null) {
                alert.setVisibility(View.VISIBLE);
                if (DetailName.equals(mContext.getString(R.string.field_college_year)))
                    sCommonAlert = "<B>" + DetailName + "</B> is a required field. You can choose to keep it private, if you do not wish to share this detail with other users.";
                else
                    sCommonAlert = "<B>" + DetailName + "</B> is an optional field. Completing this detail will help you to find people with similar interest & vice versa.";

            }
            Log.d(TAG, "CheckDetailsGiven: MissingItems " + MissingItems);
            return false;
        } else {
            alert.setVisibility(View.INVISIBLE);
            // Log.d(TAG, "CheckDetailsGiven: Detail Name");
            MissingItems.remove(DetailName);
            Log.d(TAG, "CheckDetailsGiven: MissingItems " + MissingItems);
            return true;
        }
    }


    private void SetArrayLists(String sSocietyName, String sSocietyDesp, String sSocietyURL) {
        SocietyNames.add(sSocietyName);
        SocietyDesp.add(sSocietyDesp);
        SocietyCover.add(sSocietyURL);
    }

    private void SetFieldInDatabase(String fieldValue, String field) {
        myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(field)
                .setValue(fieldValue);
    }

    private boolean isServiceOK() {

        int Availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if (Availability == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServiceOK: success ");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(Availability)) {
            Log.d(TAG, "isServiceOK: error but user resolvable");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(((HomeActivity) mContext), Availability, ERROR_DIALOG_REQUEST);
            dialog.show();

        } else {
            Toast.makeText(mContext, "Sorry, map is not available.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void InflateDialog(int Layout) {
        vDialog = inflaterDialog.inflate(Layout, null);  // this line
        builder.setView(vDialog);
        Alertdialog = builder.create();
        Alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Alertdialog.show();
    }

    private void SetPublicFields(ArrayList<String> PublicFields) {

        for (int i = 0; i < PublicFields.size(); i++) {
            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field_is_private))
                    .child(PublicFields.get(i))
                    .setValue(mContext.getString(R.string.field_public));

            statusOfFields.put(PublicFields.get(i), mContext.getString(R.string.field_public));
        }
        PublicFields.remove(mContext.getString(R.string.field_age));
        PublicFields.remove(mContext.getString(R.string.field_birth_date));

        for (int i = 0; i < PublicFields.size(); i++) {
            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(PublicFields.get(i))
                    .setValue("N/A");

            valueOfFields.put(PublicFields.get(i), "N/A");
        }
    }

    private void MakeEverythingPublic() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: boolean snapshot " + snapshot.exists());
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_is_private)).getChildren()) {
                        statusOfFields.put(ds.getKey(), ds.getValue().toString());
                    }
                    if (snapshot.child(mContext.getString(R.string.field)).exists()) {
                        for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field)).getChildren()) {
                            valueOfFields.put(ds.getKey(), ds.getValue().toString());
                        }
                    }
                } else {

                    myRef.child(mContext.getString(R.string.dbname_user_card))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field))
                            .child(mContext.getString(R.string.field_birth_date))
                            .setValue("DD/MM/YYYY");
                    myRef.child(mContext.getString(R.string.dbname_user_card))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field))
                            .child(mContext.getString(R.string.field_age))
                            .setValue("0");
                    valueOfFields.put(mContext.getString(R.string.field_age), "0");
                    valueOfFields.put(mContext.getString(R.string.field_birth_date), "DD/MM/YYYY");

                    ArrayList<String> FieldsList = new ArrayList<>();
                    FieldsList.add(mContext.getString(R.string.field_age));
                    FieldsList.add(mContext.getString(R.string.field_birth_date));
                    FieldsList.add(mContext.getString(R.string.field_music));
                    FieldsList.add(mContext.getString(R.string.field_movie));
                    FieldsList.add(mContext.getString(R.string.field_books));
                    FieldsList.add(mContext.getString(R.string.field_gender));
                    FieldsList.add(mContext.getString(R.string.field_zodiac_sign));
                    FieldsList.add(mContext.getString(R.string.field_class_representative));
                    FieldsList.add(mContext.getString(R.string.field_other_posts));
                    FieldsList.add(mContext.getString(R.string.field_college_year));
                    FieldsList.add(mContext.getString(R.string.field_society));
                    FieldsList.add(mContext.getString(R.string.field_hobbies));
                    FieldsList.add(mContext.getString(R.string.field_games));
                    FieldsList.add(mContext.getString(R.string.field_pronouns));
                    SetPublicFields(FieldsList);
                }
                Log.d(TAG, String.format("onDataChange: valueofFields %s", valueOfFields));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SetGames() {
        bGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlGames.startAnimation(myAnim);
                AlertGames.setVisibility(View.INVISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString(mContext.getString(R.string.field_games), statusOfFields.get(mContext.getString(R.string.field_games)));
               /* GamesFragment fragment = new GamesFragment();
                fragment.setArguments(bundle);
                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCardInfo, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.games_fragment));
                Transaction.commit();*/

            }
        });


    }


//<----------------------------------------------START OF METHODS OT SET USER's HOBBIES------------------------------------------------------------------>

    /**
     * Main Method to inflate the interests dialog box by on click
     * sets close button
     * sets on dismiss listener which sets all the selected hobbies into firebase
     * contains all initialization for widgets
     */

    private void SetInterests() {
        bInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shaked");
                rlInterests.startAnimation(myAnim);
                AlertInterests.setVisibility(View.INVISIBLE);
                InflateDialog(R.layout.dialog_layout_interest);
                final AutoCompleteTextView HobbiesSearchBar = vDialog.findViewById(R.id.hobbies_search_bar);
                final RelativeLayout RL_HEADING = vDialog.findViewById(R.id.RL_heading);
                final RelativeLayout RL_Main = vDialog.findViewById(R.id.RL_CENTER);
                final RelativeLayout RL_Private = vDialog.findViewById(R.id.RL_CHANGE_FILTER_TYPE);
                final ImageButton close = vDialog.findViewById(R.id.close_dialog);
                final ImageView back_icon = vDialog.findViewById(R.id.Image_heading);

                final AppCompatButton CustomHobby = vDialog.findViewById(R.id.custom_hobby);

                final TextView FirstHobby = vDialog.findViewById(R.id.first_hobby);
                final TextView SecondHobby = vDialog.findViewById(R.id.second_hobby);
                final TextView ThirdHobby = vDialog.findViewById(R.id.third_hobby);
                final TextView FourthHobby = vDialog.findViewById(R.id.fourth_hobby);
                final TextView FifthHobby = vDialog.findViewById(R.id.fifth_hobby);

                RL_HEADING.animate().alpha(1).setStartDelay(1500).setDuration(1000);
                RL_Main.animate().alpha(1).setStartDelay(1500).setDuration(1000);
                RL_Private.animate().alpha(1).setStartDelay(1500).setDuration(1000);
                close.animate().alpha(1).setStartDelay(1500).setDuration(1000);
                back_icon.animate().alpha(0.7f).setStartDelay(1500).setDuration(1000);

                FieldList.add(mContext.getString(R.string.field_hobbies));

                SetDetailsAndPrivateInfo(vDialog, FieldList, true, false);
                SetJellyToggleButton(vDialog, mContext.getString(R.string.field_hobbies));
                GetHobbiesFromAPI(HobbiesSearchBar, CustomHobby, FirstHobby, SecondHobby, ThirdHobby, FourthHobby, FifthHobby);
                GetSelectedHobbiesFromDatabase(FirstHobby, SecondHobby, ThirdHobby, FourthHobby, FifthHobby);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alertdialog.dismiss();
                    }
                });
                Log.d(TAG, "onClick: hobbies value  " + valueOfFields.get(mContext.getString(R.string.field_hobbies)));

                Alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d(TAG, String.format("onCancel: Selected Hobbies 1 %s", SelectedHobbies));
                        for (int i = 0; i < SelectedHobbies.size(); i++) {
                            SetDetailForFilter(mContext.getString(R.string.field_hobbies), SelectedHobbies.get(i), "", statusOfFields.get(mContext.getString(R.string.field_hobbies)));
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field))
                                    .child(mContext.getString(R.string.field_hobbies))
                                    .child("hobby" + "_" + i)
                                    .setValue(SelectedHobbies.get(i));


                        }
                        SelectedHobbies.clear();
                    }
                });
            }
        });

    }

    /**
     * Getting Selected Hobbies from Firebase & displaying into appropriate text View
     * Settings in Selected list
     * Based upon the children count in the Hobbies node
     *
     * @param FirstHobby
     * @param SecondHobby
     * @param ThirdHobby
     * @param FourthHobby
     * @param FifthHobby
     */
    private void GetSelectedHobbiesFromDatabase(final TextView FirstHobby, final TextView SecondHobby,
                                                final TextView ThirdHobby, final TextView FourthHobby, final TextView FifthHobby) {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(mContext.getString(R.string.field_hobbies));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        keyList.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                        //  Log.d(TAG, String.format("onDataChange: Selected Hobbies 1 %s", SelectedHobbies));
                        if (!TextUtils.isEmpty(dataSnapshot.getValue().toString()) && SelectedHobbies.size() < MAX_SELECTED_ITEMS) {
                            SelectedHobbies.add(dataSnapshot.getValue().toString());
                            // Log.d(TAG, String.format("onDataChange: Selected Hobbies 2 %s", SelectedHobbies));

                        }
                    }
                    Log.d(TAG, String.format("onDataChange: Selected Hobbies 3 %s", SelectedHobbies));
                    if (!SelectedHobbies.isEmpty()) {
                        for (int i = 0; i < SelectedHobbies.size(); i++) {
                            CheckStatus(mContext.getString(R.string.field_hobbies), SelectedHobbies.get(i), mContext.getString(R.string.field_private));
                        }
                    }
                    Log.d(TAG, "onDataChange: snapshot children count " + snapshot.getChildrenCount());

                    if (snapshot.getChildrenCount() == 1) {
                        FirstHobby.setText(SelectedHobbies.get(0));
                        FirstHobby.setVisibility(View.VISIBLE);
                        FirstHobby.animate().alpha(1).setDuration(1000);

                    } else if (snapshot.getChildrenCount() == 2) {
                        FirstHobby.setText(SelectedHobbies.get(0));
                        SecondHobby.setText(SelectedHobbies.get(1));

                        FirstHobby.setVisibility(View.VISIBLE);
                        SecondHobby.setVisibility(View.VISIBLE);

                        FirstHobby.animate().alpha(1).setDuration(1000);
                        SecondHobby.animate().alpha(1).setDuration(1000);
                    } else if (snapshot.getChildrenCount() == 3) {
                        FirstHobby.setText(SelectedHobbies.get(0));
                        SecondHobby.setText(SelectedHobbies.get(1));
                        ThirdHobby.setText(SelectedHobbies.get(2));

                        FirstHobby.setVisibility(View.VISIBLE);
                        SecondHobby.setVisibility(View.VISIBLE);
                        ThirdHobby.setVisibility(View.VISIBLE);

                        FirstHobby.animate().alpha(1).setDuration(1000);
                        SecondHobby.animate().alpha(1).setDuration(1000);
                        ThirdHobby.animate().alpha(1).setDuration(1000);
                    } else if (snapshot.getChildrenCount() == 4) {
                        FirstHobby.setText(SelectedHobbies.get(0));
                        SecondHobby.setText(SelectedHobbies.get(1));
                        ThirdHobby.setText(SelectedHobbies.get(2));
                        FourthHobby.setText(SelectedHobbies.get(3));

                        FirstHobby.setVisibility(View.VISIBLE);
                        SecondHobby.setVisibility(View.VISIBLE);
                        ThirdHobby.setVisibility(View.VISIBLE);
                        FourthHobby.setVisibility(View.VISIBLE);

                        FirstHobby.animate().alpha(1).setDuration(1000);
                        SecondHobby.animate().alpha(1).setDuration(1000);
                        ThirdHobby.animate().alpha(1).setDuration(1000);
                        FourthHobby.animate().alpha(1).setDuration(1000);
                    } else if (snapshot.getChildrenCount() == 5) {
                        FirstHobby.setText(SelectedHobbies.get(0));
                        SecondHobby.setText(SelectedHobbies.get(1));
                        ThirdHobby.setText(SelectedHobbies.get(2));
                        FourthHobby.setText(SelectedHobbies.get(3));
                        FifthHobby.setText(SelectedHobbies.get(4));

                        FirstHobby.setVisibility(View.VISIBLE);
                        SecondHobby.setVisibility(View.VISIBLE);
                        ThirdHobby.setVisibility(View.VISIBLE);
                        FourthHobby.setVisibility(View.VISIBLE);
                        FifthHobby.setVisibility(View.VISIBLE);

                        FirstHobby.animate().alpha(1).setDuration(1000);
                        SecondHobby.animate().alpha(1).setDuration(1000);
                        ThirdHobby.animate().alpha(1).setDuration(1000);
                        FourthHobby.animate().alpha(1).setDuration(1000);
                        FifthHobby.animate().alpha(1).setDuration(1000);
                    }
                    FirstHobby.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DeleteSelectedHobby(keyList.get("hobby_0"), FirstHobby);
                            return false;
                        }
                    });
                    SecondHobby.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DeleteSelectedHobby(keyList.get("hobby_1"), SecondHobby);
                            return false;
                        }
                    });
                    ThirdHobby.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DeleteSelectedHobby(keyList.get("hobby_2"), ThirdHobby);
                            return false;
                        }
                    });
                    FourthHobby.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DeleteSelectedHobby(keyList.get("hobby_3"), FourthHobby);
                            return false;
                        }
                    });
                    FifthHobby.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DeleteSelectedHobby(keyList.get("hobby_4"), FifthHobby);
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Gets All the hobbies from the wikipedia hobby API list
     * Using OkHTTPResult  Library
     * autocomplete textview filters the hobby list
     * selected hobby gets set text into respective text view,database & selected hobbies list
     * Sets custom hobby for user if there typed word is no in the list
     * MAX 5 HOBBIES
     *
     * @param HobbiesSearchBar The Autocomplete text view
     * @param CustomHobby      Custom Hobby Button
     * @param FirstHobby
     * @param SecondHobby
     * @param ThirdHobby
     * @param FourthHobby
     * @param FifthHobby
     */
    private void GetHobbiesFromAPI(final AutoCompleteTextView HobbiesSearchBar, final AppCompatButton CustomHobby, final TextView FirstHobby, final TextView SecondHobby,
                                   final TextView ThirdHobby, final TextView FourthHobby, final TextView FifthHobby) {

        try {
            HobbiesList.clear();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .addHeader("accept","application/json")
                    .addHeader("Authorization","Bearer 9Snr7WOOkzFwyepB848K9NFRPZeRDlVomiypoiuOuHgSyLYgSyDAIhIOpXArdh43")
                    .url("https://en.wikipedia.org/w/api.php?"
                            + "format=json"
                            + "&action=query"
                            + "&prop=links"
                            + "&titles=List%20of%20hobbies"
                            + "&pllimit=500")
                    .get()
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the selected hobby in database
     * sets into respective text view number
     * sets alpha of respective text view to 1 by animation
     * makes views visible according to the size of Selected Hobbies list
     *
     * @param selection   String of selected hobby
     * @param FirstHobby  Text view for First Hobby
     * @param SecondHobby Text view for Second Hobby
     * @param ThirdHobby  Text view for Third Hobby
     * @param FourthHobby Text view for Fourth Hobby
     * @param FifthHobby  Text view for Fifth Hobby
     */

    private void SetSelectedHobby(String selection, TextView FirstHobby, TextView SecondHobby,
                                  TextView ThirdHobby, TextView FourthHobby, TextView FifthHobby) {
        Log.d(TAG, String.format("SetSelectedHobby: SelectedHobbies %s", SelectedHobbies));
        if (SelectedHobbies.size() == 1) {
            FirstHobby.setText(selection);

            FirstHobby.setVisibility(View.VISIBLE);

            FirstHobby.animate().alpha(1).setDuration(1000);


        } else if (SelectedHobbies.size() == 2) {
            SecondHobby.setText(selection);
            SecondHobby.animate().alpha(1).setDuration(1000);

            FirstHobby.setVisibility(View.VISIBLE);
            SecondHobby.setVisibility(View.VISIBLE);


        } else if (SelectedHobbies.size() == 3) {
            ThirdHobby.setText(selection);
            ThirdHobby.animate().alpha(1).setDuration(1000);

            FirstHobby.setVisibility(View.VISIBLE);
            SecondHobby.setVisibility(View.VISIBLE);
            ThirdHobby.setVisibility(View.VISIBLE);


        } else if (SelectedHobbies.size() == 4) {
            FourthHobby.setText(selection);
            FourthHobby.animate().alpha(1).setDuration(1000);

            FirstHobby.setVisibility(View.VISIBLE);
            SecondHobby.setVisibility(View.VISIBLE);
            ThirdHobby.setVisibility(View.VISIBLE);
            FourthHobby.setVisibility(View.VISIBLE);


        } else if (SelectedHobbies.size() == 5) {
            FifthHobby.setText(selection);
            FifthHobby.animate().alpha(1).setDuration(1000);

            FirstHobby.setVisibility(View.VISIBLE);
            SecondHobby.setVisibility(View.VISIBLE);
            ThirdHobby.setVisibility(View.VISIBLE);
            FourthHobby.setVisibility(View.VISIBLE);
            FifthHobby.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to delete the long particular hobby
     * displays selected hobby in dialog box
     * removes selected hobby from selected list
     * deletes the whole hobby node from the database
     * closes the dialog to triger on dismiss listener
     * which in turn sets the updated list in database
     *
     * @param hobby         Selected hobby
     * @param HobbyNumberTV TextView from Which hobby has to be deleted
     */
    private void DeleteSelectedHobby(final String hobby, final TextView HobbyNumberTV) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("I don't like " + hobby + ".")
                .setMessage("This will delete " + hobby + " from your selected hobbies.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                myRef.child(mContext.getString(R.string.dbname_user_card))
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.field))
                                        .child(mContext.getString(R.string.field_hobbies))
                                        .removeValue();
                                HobbyNumberTV.setText("");
                                HobbyNumberTV.animate().alpha(0).setDuration(1000);
                                SelectedHobbies.remove(hobby);
                                Alertdialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
        builder.show();
    }

    private void SetZodiacAndPosts() {
        bZodiacAndPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlZodiacAndPosts.startAnimation(myAnim);
                AlertZodiacAndPosts.setVisibility(View.INVISIBLE);

                InflateDialog(R.layout.dialog_layout_zodiac_positions);
                final EditText Sections_CR_ET = vDialog.findViewById(R.id.Sections_CR_ET);
                final EditText OT_POSITION_ET = vDialog.findViewById(R.id.PositionET);
                final EditText OT_ORGANIZATION_ET = vDialog.findViewById(R.id.OrganizationET);
                final EditText OT_REMARK_ET = vDialog.findViewById(R.id.RemarkET);
                final AppCompatButton Privacy = vDialog.findViewById(R.id.PrivacyButton);
                final AppCompatButton CR_YES = vDialog.findViewById(R.id.CR_YES);
                final AppCompatButton CR_NO = vDialog.findViewById(R.id.CR_NO);
                final AppCompatButton OT_POSITION_YES = vDialog.findViewById(R.id.OT_POS_YES);
                final AppCompatButton OT_POSITION_NO = vDialog.findViewById(R.id.OT_POS_NO);
                final AutofitTextView NO_TO_ALL = vDialog.findViewById(R.id.Set_NO_All);
                final AppCompatButton close_dialog = vDialog.findViewById(R.id.close_dialog);
                final LinearLayout LL_CR_SECTIONS = vDialog.findViewById(R.id.LL_CR_QUES2);
                final LinearLayout LL_OTHER_HEADINGS = vDialog.findViewById(R.id.LL_OTHER_HEADINGS);
                final Spinner ZodiacSearchBar = vDialog.findViewById(R.id.ZodiacSpinner);

                Log.d(TAG, String.format("onClick: ZodiacSigns %s", ZodiacSigns));
                ZodiacSigns.clear();
                ZodiacImages.clear();
                ZodiacSigns.add("Select A Zodiac Sign");
                ZodiacSigns.add("Aquarius");
                ZodiacSigns.add("Pisces");
                ZodiacSigns.add("Aries");
                ZodiacSigns.add("Taurus");
                ZodiacSigns.add("Gemini");
                ZodiacSigns.add("Cancer");
                ZodiacSigns.add("Leo");
                ZodiacSigns.add("Virgo");
                ZodiacSigns.add("Libra");
                ZodiacSigns.add("Scorpio");
                ZodiacSigns.add("Sagittarius");
                ZodiacSigns.add("Capricorn");

                ZodiacImages.add(0);
                ZodiacImages.add(R.drawable.ic_zodiac_aquarius);
                ZodiacImages.add(R.drawable.ic_zodiac_pisces);
                ZodiacImages.add(R.drawable.ic_zodiac_aries);
                ZodiacImages.add(R.drawable.ic_zodiac_taurus);
                ZodiacImages.add(R.drawable.ic_zodiac_gemini);
                ZodiacImages.add(R.drawable.ic_zodiac_cancer);
                ZodiacImages.add(R.drawable.ic_zodiac_leo);
                ZodiacImages.add(R.drawable.ic_zodiac_virgo);
                ZodiacImages.add(R.drawable.ic_zodiac_libra);
                ZodiacImages.add(R.drawable.ic_filter_zodiac_scorpion);
                ZodiacImages.add(R.drawable.ic_zodiac_sagittarius);
                ZodiacImages.add(R.drawable.ic_zodiac_sagittarius);


                ZodiacArrayAdapter adapter = new ZodiacArrayAdapter(mContext, ZodiacSigns, ZodiacImages);
                ZodiacSearchBar.setAdapter(adapter);


                Sections_CR_ET.setEnabled(false);
                Alertdialog.setCancelable(false);
                Alertdialog.setCanceledOnTouchOutside(false);
                PrivacyDialog(Privacy, mContext.getString(R.string.field_zodiac_sign));

                ZodiacSearchBar.setSelection(ZodiacSigns.indexOf(valueOfFields.get(mContext.getString(R.string.field_zodiac_sign))));

                NO_TO_ALL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myRef.child(mContext.getString(R.string.dbname_user_card))
                                .child(mAuth.getCurrentUser().getUid())
                                .child(mContext.getString(R.string.field))
                                .child(mContext.getString(R.string.field_zodiac_sign))
                                .setValue(mContext.getString(R.string.no_selection));
                        Toast.makeText(mContext, "Zodiac removed!", Toast.LENGTH_SHORT).show();
                    }
                });

                if (valueOfFields.get(mContext.getString(R.string.field_class_representative)).equals("true")) {
                    CR_YES.setBackgroundColor(Color.parseColor("#FF6200EE"));
                    CR_NO.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    LL_CR_SECTIONS.animate().alpha(1).setDuration(1000);

                    Sections_CR_ET.setEnabled(true);
                    is_CR_YES = true;
                    is_CR_SELECTED = true;

                    Sections_CR_ET.setText(valueOfFields.get(mContext.getString(R.string.field_sections_in_course)));
                } else if (valueOfFields.get(mContext.getString(R.string.field_class_representative)).equals("false")) {
                    CR_YES.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    CR_NO.setBackgroundColor(Color.parseColor("#FF6200EE"));

                    LL_CR_SECTIONS.animate().alpha(0.5f);

                    Sections_CR_ET.setEnabled(false);
                    is_CR_YES = false;
                    is_CR_SELECTED = true;
                }
                if (valueOfFields.get(mContext.getString(R.string.field_other_posts)).equals("false")) {
                    OT_POSITION_YES.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    OT_POSITION_NO.setBackgroundColor(Color.parseColor("#FF6200EE"));

                    LL_OTHER_HEADINGS.animate().alpha(0.5f);
                    OT_POSITION_ET.setEnabled(false);
                    OT_ORGANIZATION_ET.setEnabled(false);
                    OT_REMARK_ET.setEnabled(false);
                    is_OTHER_YES = false;
                    is_OTHER_SELECTED = true;
                } else if (!valueOfFields.get(mContext.getString(R.string.field_other_posts)).equals("N/A")) {
                    OT_POSITION_YES.setBackgroundColor(Color.parseColor("#FF6200EE"));
                    OT_POSITION_NO.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    LL_OTHER_HEADINGS.animate().alpha(1).setDuration(1000);
                    OT_POSITION_ET.setEnabled(true);
                    OT_ORGANIZATION_ET.setEnabled(true);
                    OT_REMARK_ET.setEnabled(true);
                    is_OTHER_YES = true;
                    is_OTHER_SELECTED = true;

                    Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                            .child(UserUID)
                            .child(mContext.getString(R.string.field))
                            .child(mContext.getString(R.string.field_other_posts));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot OtherPostChildren : snapshot.getChildren()) {
                                if (OtherPostChildren.getKey().equals(mContext.getString(R.string.field_position_name_in_other_post))) {
                                    OT_POSITION_ET.setText(OtherPostChildren.getValue().toString());
                                }
                                if (OtherPostChildren.getKey().equals(mContext.getString(R.string.field_organization_name_in_other_post))) {
                                    OT_ORGANIZATION_ET.setText(OtherPostChildren.getValue().toString());
                                }
                                if (OtherPostChildren.getKey().equals(mContext.getString(R.string.field_remark_in_other_post))) {
                                    OT_REMARK_ET.setText(OtherPostChildren.getValue().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                CR_YES.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CR_YES.setBackgroundColor(Color.parseColor("#FF6200EE"));
                        CR_NO.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        LL_CR_SECTIONS.animate().alpha(1).setDuration(1000);

                        Sections_CR_ET.setEnabled(true);
                        is_CR_YES = true;
                        is_CR_SELECTED = true;


                    }
                });
                CR_NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CR_YES.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        CR_NO.setBackgroundColor(Color.parseColor("#FF6200EE"));

                        LL_CR_SECTIONS.animate().alpha(0.5f);

                        Sections_CR_ET.setEnabled(false);
                        is_CR_YES = false;
                        is_CR_SELECTED = true;
                    }
                });

                OT_POSITION_YES.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OT_POSITION_YES.setBackgroundColor(Color.parseColor("#FF6200EE"));
                        OT_POSITION_NO.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        LL_OTHER_HEADINGS.animate().alpha(1).setDuration(1000);
                        OT_POSITION_ET.setEnabled(true);
                        OT_ORGANIZATION_ET.setEnabled(true);
                        OT_REMARK_ET.setEnabled(true);
                        is_OTHER_YES = true;
                        is_OTHER_SELECTED = true;

                    }
                });
                OT_POSITION_NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OT_POSITION_YES.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        OT_POSITION_NO.setBackgroundColor(Color.parseColor("#FF6200EE"));

                        LL_OTHER_HEADINGS.animate().alpha(0.5f);
                        OT_POSITION_ET.setEnabled(false);
                        OT_ORGANIZATION_ET.setEnabled(false);
                        OT_REMARK_ET.setEnabled(false);
                        is_OTHER_YES = false;
                        is_OTHER_SELECTED = true;
                    }
                });

                close_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (is_CR_SELECTED && is_OTHER_SELECTED) {
                            if (is_CR_YES && is_OTHER_YES) {
                                if (TextUtils.isEmpty(Sections_CR_ET.getText().toString())) {
                                    Sections_CR_ET.setError("Please type the No. of sections in your course or type 1.");
                                }
                                if (TextUtils.isEmpty(OT_POSITION_ET.getText().toString())) {
                                    OT_POSITION_ET.setError("Please type the name of your post.");

                                }
                                if (TextUtils.isEmpty(OT_ORGANIZATION_ET.getText().toString())) {
                                    OT_ORGANIZATION_ET.setError("Please type the name of the organization in which you hold this position.");
                                }
                                if (!TextUtils.isEmpty(Sections_CR_ET.getText().toString()) && !TextUtils.isEmpty(OT_POSITION_ET.getText().toString())
                                        && !TextUtils.isEmpty(OT_ORGANIZATION_ET.getText().toString())) {
                                    if (ZodiacSearchBar.getSelectedItem().toString().equals("Select A Zodiac Sign")) {
                                        SaveZodiacANDPostsToDatabase(mContext.getString(R.string.no_selection), true, true, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    } else {
                                        SaveZodiacANDPostsToDatabase(ZodiacSearchBar.getSelectedItem().toString(), true, true, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    }
                                    Alertdialog.dismiss();
                                }
                            } else if (is_CR_YES) {
                                if (TextUtils.isEmpty(Sections_CR_ET.getText().toString())) {
                                    Sections_CR_ET.setError("Please type the No. of sections in your course or type 1.");
                                } else {
                                    if (ZodiacSearchBar.getSelectedItem().toString().equals("Select A Zodiac Sign")) {
                                        SaveZodiacANDPostsToDatabase(mContext.getString(R.string.no_selection), true, false, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    } else {
                                        SaveZodiacANDPostsToDatabase(ZodiacSearchBar.getSelectedItem().toString(), true, false, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    }
                                    Alertdialog.dismiss();
                                }
                            } else if (is_OTHER_YES) {
                                if (TextUtils.isEmpty(OT_POSITION_ET.getText().toString()) && TextUtils.isEmpty(OT_ORGANIZATION_ET.getText().toString())) {
                                    OT_POSITION_ET.setError("Please type the name of your post.");
                                    OT_ORGANIZATION_ET.setError("Please type the name of the organization in which you hold this position.");
                                } else if (TextUtils.isEmpty(OT_POSITION_ET.getText().toString())) {
                                    OT_POSITION_ET.setError("Please type the name of your post.");

                                } else if (TextUtils.isEmpty(OT_ORGANIZATION_ET.getText().toString())) {
                                    OT_ORGANIZATION_ET.setError("Please type the name of the organization in which you hold this position.");
                                } else {
                                    if (ZodiacSearchBar.getSelectedItem().toString().equals("Select A Zodiac Sign")) {
                                        SaveZodiacANDPostsToDatabase(mContext.getString(R.string.no_selection), false, true, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    } else {
                                        SaveZodiacANDPostsToDatabase(ZodiacSearchBar.getSelectedItem().toString(), false, true, OT_POSITION_ET.getText().toString()
                                                , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                    }
                                    Alertdialog.dismiss();
                                }
                            } else {
                                if (ZodiacSearchBar.getSelectedItem().toString().equals("Select A Zodiac Sign")) {
                                    SaveZodiacANDPostsToDatabase(mContext.getString(R.string.no_selection), false, false, OT_POSITION_ET.getText().toString()
                                            , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                } else {
                                    SaveZodiacANDPostsToDatabase(ZodiacSearchBar.getSelectedItem().toString(), false, false, OT_POSITION_ET.getText().toString()
                                            , OT_ORGANIZATION_ET.getText().toString(), OT_REMARK_ET.getText().toString(), Sections_CR_ET.getText().toString());
                                }
                                Alertdialog.dismiss();
                            }
                        } else if (is_CR_SELECTED) {
                            Toast.makeText(mContext, "Please select an option for Other Posts", Toast.LENGTH_SHORT).show();
                        } else if (is_OTHER_SELECTED) {
                            Toast.makeText(mContext, "Please select an option for CR", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Please select an option for the given questions!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void SaveZodiacANDPostsToDatabase(String ZodiacSign, boolean isCR, boolean isOtherPost, String PositionName,
                                              String OrganizationName, String Remark, final String Section) {

        SetDetailForFilter(mContext.getString(R.string.field_zodiac_sign), ZodiacSign, valueOfFields.get(mContext.getString(R.string.field_zodiac_sign)), statusOfFields.get(mContext.getString(R.string.field_zodiac_sign)));
        if (isCR) {
            SetDetailForFilter(mContext.getString(R.string.field_class_representative), "", "", mContext.getString(R.string.field_public));
        } else {
            CheckStatus(mContext.getString(R.string.field_class_representative), "", mContext.getString(R.string.field_private));
        }
        SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                mContext.getString(R.string.field_zodiac_sign), ZodiacSign, "", "");

        SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                mContext.getString(R.string.field_class_representative), String.valueOf(isCR), "", "");

        if (!TextUtils.isEmpty(Section)) {
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                    mContext.getString(R.string.field_sections_in_course), Section, "", "");
        }

        if (isOtherPost) {
            SetFieldsInDatabase5Lines(6, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                    mContext.getString(R.string.field_other_posts), mContext.getString(R.string.field_position_name_in_other_post), PositionName, "");
            SetFieldsInDatabase5Lines(6, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                    mContext.getString(R.string.field_other_posts), mContext.getString(R.string.field_organization_name_in_other_post), OrganizationName, "");
            SetFieldsInDatabase5Lines(6, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                    mContext.getString(R.string.field_other_posts), mContext.getString(R.string.field_remark_in_other_post), Remark, "");
        } else {
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_user_card), UserUID, mContext.getString(R.string.field),
                    mContext.getString(R.string.field_other_posts), String.valueOf(false), "", "");
//            myRef.child(mContext.getString(R.string.field_to_be_checked_other_pos_list))
//                    .child()
        }
        if (isCR && isOtherPost) {
            FetchDetailsQuery(Section, true, "", "", "");
            FetchDetailsQuery("", false, PositionName, OrganizationName, Remark);
        } else if (isCR) {
            FetchDetailsQuery(Section, true, "", "", "");
        } else if (isOtherPost) {
            FetchDetailsQuery("", false, PositionName, OrganizationName, Remark);
        }


    }

    private void FetchDetailsQuery(final String Section, boolean isCR, String PositionName, String OrganizationName, String Remark) {
        final String Field3;
        if (isCR) {
            Field3 = mContext.getString(R.string.field_to_be_checked_cr_list);
        } else {
            Field3 = mContext.getString(R.string.field_to_be_checked_other_pos_list);
        }
        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                        mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_course), snapshot.getValue(User.class).getCourse(), "", "");
                SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                        mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_email), snapshot.getValue(User.class).getEmail(), "", "");
                SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                        mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_username), snapshot.getValue(User.class).getUsername(), "", "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query NameQuery = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid());

        NameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(mContext.getString(R.string.field_display_name))) {
                        SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                                mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_display_name), dataSnapshot.getValue().toString(), "", "");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (isCR) {
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                    mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_sections_in_course), Section, "", "");
        } else {
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                    mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_position_name_in_other_post), PositionName, "", "");
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                    mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_organization_name_in_other_post), OrganizationName, "", "");
            SetFieldsInDatabase5Lines(5, mContext.getString(R.string.dbname_misc), Field3,
                    mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_remark_in_other_post), Remark, "", "");
        }

    }

    private void CloseKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    // <-----------------------------------------------------------SET SOCIETIES METHODS (START)--------------------------------------------------->
    private void SetSociety() {

        bSocieties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shaked");
                rlSocieties.startAnimation(myAnim);
                AlertSocieties.setVisibility(View.INVISIBLE);

                InflateDialog(R.layout.dialog_layout_societies);

                final RecyclerView recyclerView = vDialog.findViewById(R.id.RecyclerViewSocieties);
                final ImageView background = vDialog.findViewById(R.id.background_society_dialog);
                final ImageView Tick = vDialog.findViewById(R.id.correct_society);
                final ImageView Lock = vDialog.findViewById(R.id.private_lock);
                final ImageView Cross = vDialog.findViewById(R.id.cross_society);
                final AppCompatButton ViewItems = vDialog.findViewById(R.id.view_Items);
                SocietySearchBar = vDialog.findViewById(R.id.society_search_bar);

                mLayoutManager = new ZoomRecyclerLayout(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(mLayoutManager);


                if (Objects.equals(statusOfFields.get(mContext.getString(R.string.field_society)), mContext.getString(R.string.field_public))) {
                    Lock.setImageResource(R.drawable.ic_unlocked);
                    isLocked = false;
                } else {
                    Lock.setImageResource(R.drawable.ic_locked);
                    isLocked = true;
                }
                Cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alertdialog.dismiss();
                        SetSelectedInDatabase(mContext.getString(R.string.field_society), "society_"
                                , mContext.getString(R.string.society_name)
                                , mContext.getString(R.string.society_cover_url)
                                , mContext.getString(R.string.society_desp), SelectedSocietyNames
                                , SelectedSocietyCover, SelectedSocietyDesp);
                    }
                });

                SetDetailsAndPrivateInfo(vDialog, null, true, true);
                QueryToGetSocietiesFromDatabase(recyclerView, Tick, ViewItems);
                GetSelectedFromDatabase(null, mContext.getString(R.string.field_society)
                        , mContext.getString(R.string.society_name)
                        , mContext.getString(R.string.society_desp)
                        , mContext.getString(R.string.society_cover_url), "society_");
                Lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isLocked) {
                            Lock.setImageResource(R.drawable.ic_locked);
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_is_private))
                                    .child(mContext.getString(R.string.field_society))
                                    .setValue(mContext.getString(R.string.field_private));
                            isLocked = true;
                        } else {
                            Lock.setImageResource(R.drawable.ic_unlocked);
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_is_private))
                                    .child(mContext.getString(R.string.field_society))
                                    .setValue(mContext.getString(R.string.field_public));
                            isLocked = false;
                        }

                    }
                });


                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        background.animate().alpha(0).setDuration(500);
                        background.setImageResource(R.drawable.blue_gradient_background);
                        background.animate().alpha(1).setDuration(500).setStartDelay(500);
                    }
                });
            }
        });
    }

    private void QueryToGetSocietiesFromDatabase(final RecyclerView recyclerView, final ImageView Tick, final AppCompatButton ViewItems) {
        Query query = myRef.child(mContext.getString(R.string.dbname_misc))
                .child(mContext.getString(R.string.society_hansraj));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    SetArrayLists(mContext.getString(R.string.enactus_hasnraj), mContext.getString(R.string.enactus_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fenactus.PNG?alt=media&token=9304d3e9-d042-4696-a35f-a32459bba63b");

                    SetArrayLists(mContext.getString(R.string.the_dramatics_society), mContext.getString(R.string.the_dramatics_society_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fdramsoc.jpg?alt=media&token=4f5e79eb-ee2c-478d-a8ff-f8e88c101496");

                    SetArrayLists(mContext.getString(R.string.nss_hansraj), mContext.getString(R.string.nss_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fnss_hansraj.jpg?alt=media&token=190e39d9-af31-4f13-9bf4-b715548a8271");

                    SetArrayLists(mContext.getString(R.string.ncc_hansraj), mContext.getString(R.string.ncc_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fncc_hansraj.jpg?alt=media&token=32942009-2a33-4e4a-9388-7c8bf28cb002");

                    SetArrayLists(mContext.getString(R.string.spic_macay), mContext.getString(R.string.spic_macay_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fspicmacay.jpg?alt=media&token=3fe4ba34-c046-418d-8ba6-1ecdb532505b");

                    SetArrayLists(mContext.getString(R.string.haritima), mContext.getString(R.string.haritima_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fharitima.jpg?alt=media&token=4a15e605-b9a3-455f-a955-96abc32ac511");

                    SetArrayLists(mContext.getString(R.string.kavyanjali), mContext.getString(R.string.kavyanjali_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fkavyanjali.jpg?alt=media&token=9c354092-c234-4f7c-8f5f-bc1fada91c32");

                    SetArrayLists(mContext.getString(R.string.pixels), mContext.getString(R.string.pixels_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fpixels.jpg?alt=media&token=4d19de4c-d805-44e2-9d70-7a2cf40f94ab");

                    SetArrayLists(mContext.getString(R.string.swaranjali), mContext.getString(R.string.swaranjali_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fswaranjali.jpg?alt=media&token=1b1c7b8d-64b8-421b-a97d-614e1aeba0a4");

                    SetArrayLists(mContext.getString(R.string.entrepreneurial_cell), mContext.getString(R.string.entrepreneurial_cell_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fentrepeneurial%20cell.jpg?alt=media&token=ffff254c-df1b-4fdd-aec4-82cc46c62a90");

                    SetArrayLists(mContext.getString(R.string.illuminati), mContext.getString(R.string.illuminati_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Filluminati.jpg?alt=media&token=f07d6385-698e-42c8-9866-552509e39543");

                    SetArrayLists(mContext.getString(R.string.fic_hansraj), mContext.getString(R.string.fic_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Ffic.jpg?alt=media&token=bb0a0456-ee3e-46ca-a424-c7bc21802fca");

                    SetArrayLists(mContext.getString(R.string.ferc_hansraj), mContext.getString(R.string.ferc_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fferc.jpg?alt=media&token=18a4fa67-9103-4171-9f71-0427fa584cb1");

                    SetArrayLists(mContext.getString(R.string.osctraca), mContext.getString(R.string.osctraca_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fostraca.jpg?alt=media&token=00b8c1f3-0d36-4ee5-87d6-175af6127eaf");

                    SetArrayLists(mContext.getString(R.string.sga_hansraj), mContext.getString(R.string.sga_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fsga.jpg?alt=media&token=97f69e8c-e3bf-40d8-9476-94219d4054c7");

                    SetArrayLists(mContext.getString(R.string.tedx_hansraj), mContext.getString(R.string.tedx_hansraj_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2FTEDx.jpg?alt=media&token=6dc57a1c-5c07-4516-82cb-0ed98cc2c3d6");

                    SetArrayLists(mContext.getString(R.string.eco_soc), mContext.getString(R.string.eco_soc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Feco_society.jpg?alt=media&token=02029210-7a52-4431-a592-4a20989f6ea4");

                    SetArrayLists(mContext.getString(R.string.internship_cell), mContext.getString(R.string.internship_cell_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Finternship_cell.jpg?alt=media&token=a4c4684d-e398-44c8-ade0-2d28f07d9fab");

                    SetArrayLists(mContext.getString(R.string.vision), mContext.getString(R.string.vision_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fvision.jpg?alt=media&token=f4280739-3ced-4617-b4bc-7180e23749df");

                    SetArrayLists(mContext.getString(R.string.neenv), mContext.getString(R.string.neenv_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fneenv.jpg?alt=media&token=de7eceeb-4e01-4970-a3b2-24340d710722");

                    SetArrayLists(mContext.getString(R.string.markus), mContext.getString(R.string.markus_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fmarkus.jpg?alt=media&token=9b12fd7c-972e-4775-9c5f-4ca530d4b638");

                    SetArrayLists(mContext.getString(R.string.sparc), mContext.getString(R.string.sparc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fsparc.jpg?alt=media&token=8216d560-60c0-4cf4-9053-23dff0b1ba57");

                    SetArrayLists(mContext.getString(R.string.kalakriti), mContext.getString(R.string.kalakriti_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fkalakriti.jpg?alt=media&token=28dbb783-3432-4cfa-b1fb-def5f1c3093d");

                    SetArrayLists(mContext.getString(R.string.gec), mContext.getString(R.string.gec_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fgec.jpg?alt=media&token=dd98c96a-edf4-4bbe-a855-657c21a242e7");

                    SetArrayLists(mContext.getString(R.string.wdc), mContext.getString(R.string.wdc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fwdc.jpg?alt=media&token=4cc8eb69-555b-4064-96ab-a0d88799d6de");

                    SetArrayLists(mContext.getString(R.string.cdf), mContext.getString(R.string.cdf_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fcdf.jpg?alt=media&token=52881085-87e6-4b45-8330-b455084993b8");

                    SetArrayLists(mContext.getString(R.string.fashion_soc), mContext.getString(R.string.fashion_soc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Ffashion_soc.jpg?alt=media&token=9151a35c-ffe7-4886-8325-91e160559470");

                    SetArrayLists(mContext.getString(R.string.one_eighty_degree), mContext.getString(R.string.one_eighty_degree_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2F180_degree.jpg?alt=media&token=1dfc37a1-17a3-4611-b284-ed98f6656b3d");

                    SetArrayLists(mContext.getString(R.string.rotaract), mContext.getString(R.string.rotaract_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Frotract_club.jpg?alt=media&token=3a253652-95e4-499d-a3d6-b4fcb9079fad");

                    SetArrayLists(mContext.getString(R.string.gwc), mContext.getString(R.string.gwc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fgwc.jpg?alt=media&token=ddc40e7f-3a3a-479f-8c5d-4c74856bcbf5");

                    SetArrayLists(mContext.getString(R.string.culinary), mContext.getString(R.string.culinary_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fculinary_arts.jpg?alt=media&token=e28489cf-3695-4c33-966e-664975ac5607");

                    SetArrayLists(mContext.getString(R.string.markit), mContext.getString(R.string.markit_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fmark_it.jpg?alt=media&token=5b6ec599-e802-452e-a105-433bc300a287");

                    SetArrayLists(mContext.getString(R.string.north_east_cell), mContext.getString(R.string.north_east_cell_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fnorth_east_cell.jpg?alt=media&token=b0e44cb8-f11b-4d9f-9c9f-bdcb80d71064");

                    SetArrayLists(mContext.getString(R.string.placement_cell), mContext.getString(R.string.placement_cell_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fplacement_cell.jpg?alt=media&token=70d998ea-23a7-4ff7-a12e-a7469bfd8d66");

                    SetArrayLists(mContext.getString(R.string.comm_soc), mContext.getString(R.string.comm_soc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fcomm_soc.jpg?alt=media&token=31cfcc26-ca67-40ce-a119-22c748dd5a57");

                    SetArrayLists(mContext.getString(R.string.nishtha), mContext.getString(R.string.nishtha_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fnishtha.jpg?alt=media&token=c3c3f3da-0ba3-4393-95ce-33ebad482297");

                    SetArrayLists(mContext.getString(R.string.debsoc), mContext.getString(R.string.debsoc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fdebsoc.jpg?alt=media&token=7ce21139-9164-4da4-b910-2541d108f898");

                    SetArrayLists(mContext.getString(R.string.sports_soc), mContext.getString(R.string.sports_soc_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Fsports_hansraj.jpg?alt=media&token=d7208a58-8c9a-4678-bdaf-b0dbdfe54b41");

                    SetArrayLists(mContext.getString(R.string.terpsichorean), mContext.getString(R.string.terpsichorean_desp), "https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/miscellaneous%2Fsociety_cover_photos%2Ftepsichorean.jpg?alt=media&token=f2254528-be67-4222-8230-24ff80288a55");

                    for (int i = 0; i < SocietyNames.size(); i++) {
                        myRef.child(mContext.getString(R.string.dbname_misc))
                                .child(mContext.getString(R.string.society_hansraj))
                                .child("society_" + i)
                                .child(mContext.getString(R.string.society_name))
                                .setValue(SocietyNames.get(i));

//                        myRef.child(mContext.getString(R.string.dbname_filter_data))
//                                .child(mContext.getString(R.string.field_society))
//                                .child(SocietyNames.get(i))
//                                .child(mContext.getString(R.string.society_cover_url))
//                                .setValue(SocietyCover.get(i));
//                        myRef.child(mContext.getString(R.string.dbname_filter_data))
//                                .child(mContext.getString(R.string.field_society))
//                                .child(SocietyNames.get(i))
//                                .child(mContext.getString(R.string.society_desp))
//                                .setValue(SocietyDesp.get(i));
                    }
                    for (int i = 0; i < SocietyCover.size(); i++) {
                        myRef.child(mContext.getString(R.string.dbname_misc))
                                .child(mContext.getString(R.string.society_hansraj))
                                .child("society_" + i)
                                .child(mContext.getString(R.string.society_cover_url))
                                .setValue(SocietyCover.get(i));
                    }
                    for (int i = 0; i < SocietyDesp.size(); i++) {
                        myRef.child(mContext.getString(R.string.dbname_misc))
                                .child(mContext.getString(R.string.society_hansraj))
                                .child("society_" + i)
                                .child(mContext.getString(R.string.society_desp))
                                .setValue(SocietyDesp.get(i));
                    }
                } else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        for (DataSnapshot societyChild : snapshot1.getChildren()) {
                            if (societyChild.getKey().equals(mContext.getString(R.string.society_name))) {
                                SocietyNames.add(societyChild.getValue().toString());
                            }
                            if (societyChild.getKey().equals(mContext.getString(R.string.society_cover_url))) {
                                SocietyCover.add(societyChild.getValue().toString());
                            }
                            if (societyChild.getKey().equals(mContext.getString(R.string.society_desp))) {
                                SocietyDesp.add(societyChild.getValue().toString());
                            }
                        }
                    }
                }
                mAdapter = new SocietiesRecyclerViewAdapter(null,null,null,mContext);

                recyclerView.setAdapter(mAdapter);
                recyclerView.setNestedScrollingEnabled(false);
                SocietySearchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                android.R.layout.simple_dropdown_item_1line, SocietyNames);
                        SocietySearchBar.setThreshold(1);
                        SocietySearchBar.setAdapter(adapter);
                        SocietySearchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
                                    @Override
                                    protected int getHorizontalSnapPreference() {
                                        return super.getHorizontalSnapPreference();
                                    }
                                };
                                String selection = SocietySearchBar.getText().toString();
                                int newPos = SocietyNames.indexOf(selection);

                                smoothScroller.setTargetPosition(newPos);
                                mLayoutManager.startSmoothScroll(smoothScroller);
                            }

                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                OnClickTickButton(Tick);
                SetSelectedItems(ViewItems, SelectedSocietyNames, SelectedSocietyCover, SelectedSocietyDesp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OnClickTickButton(ImageView Tick) {
        Tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mLayoutManager.findFirstVisibleItemPosition();
                Log.d(TAG, "onClick: SocietNames " + SocietyNames.size());
                if (SelectedSocietyNames.size() < MAX_SELECTED_ITEMS) {
                    if (position == (SocietyNames.size() - 1) || position == 0) {
                        if (!SelectedSocietyNames.contains(SocietyNames.get(position))) {
                            SelectedSocietyNames.add(SocietyNames.get(position));
                            Snackbar.make(vDialog, "Society Added To My List!", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(vDialog, "Society Already Added To My List!", Snackbar.LENGTH_LONG).show();
                        }
                        if (!SelectedSocietyDesp.contains(SocietyDesp.get(position))) {
                            SelectedSocietyDesp.add(SocietyDesp.get(position));
                        }
                        if (!SelectedSocietyCover.contains(SocietyCover.get(position))) {
                            SelectedSocietyCover.add(SocietyCover.get(position));
                        }
                    } else {
                        // Log.d(TAG, "onClick: society Names in else" + SocietyNames.get(position+1));
                        if (!SelectedSocietyNames.contains(SocietyNames.get(position + 1))) {
                            SelectedSocietyNames.add(SocietyNames.get(position + 1));
                            Snackbar.make(vDialog, "Society Added To My List!", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(vDialog, "Society Already Added To My List!", Snackbar.LENGTH_LONG).show();
                        }
                        if (!SelectedSocietyDesp.contains(SocietyDesp.get(position + 1))) {
                            SelectedSocietyDesp.add(SocietyDesp.get(position + 1));
                        }
                        if (!SelectedSocietyCover.contains(SocietyCover.get(position + 1))) {
                            SelectedSocietyCover.add(SocietyCover.get(position + 1));
                        }
                    }
                } else {
                    Snackbar.make(vDialog, "You have already added a maximum of 5 societies!", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void SetSelectedItems(AppCompatButton ViewItems,
                                  final ArrayList<String> mSocietyName,
                                  final ArrayList<String> mSocietyCover, final ArrayList<String> mSocietyDesp) {
        ViewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = inflaterDialog.inflate(R.layout.dialog_layout_music_movies_books_selected, null);  // this line
                builder.setView(view);
                final AlertDialog SelectedItemsAlertDialog = builder.create();
                SelectedItemsAlertDialog.show();
                final TextView Title = view.findViewById(R.id.SelectedOptionTV);
                final SwipeableRecyclerView rSelected = view.findViewById(R.id.FINAL_MOVIES_BOOKS_MUSIC_RV);
                rSelected.setLayoutManager(new LinearLayoutManager(mContext));
                final AppCompatButton ConfirmButton = view.findViewById(R.id.close_dialog);
                rSelected.setRightBg(R.color.red);
                rSelected.setRightImage(R.drawable.ic_trash);

                Title.setText("Selected Societies:");
                GetSelectedFromDatabase(rSelected, mContext.getString(R.string.field_society)
                        , mContext.getString(R.string.society_name)
                        , mContext.getString(R.string.society_desp)
                        , mContext.getString(R.string.society_cover_url), "society_");
                ConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SetSelectedInDatabase(mContext.getString(R.string.field_society), "society_"
                                , mContext.getString(R.string.society_name)
                                , mContext.getString(R.string.society_cover_url)
                                , mContext.getString(R.string.society_desp), mSocietyName
                                , mSocietyCover, mSocietyDesp);

                        SelectedItemsAlertDialog.dismiss();
                    }
                });

            }
        });


    }

    private void GetSelectedFromDatabase(final SwipeableRecyclerView rSelected,
                                         final String fieldName,
                                         final String field1, final String field2, final String field3, final String heading) {

        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(fieldName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        ArrayList<String> keySet = new ArrayList<>();
                        keySet.add(dataSnapshot.getKey());
                        Log.d(TAG, "onDataChange: keySet " + keySet);
                        //int i =0;
                        for (DataSnapshot TrackChildren : dataSnapshot.getChildren()) {
                            if (SelectedSocietyNames.size() < MAX_SELECTED_ITEMS) {
                                if (TrackChildren.getKey().equals(field1)
                                        && !SelectedSocietyNames.contains(TrackChildren.getValue().toString())) {

                                    keyList.put(TrackChildren.getValue().toString(), keySet.get(0));
                                    SelectedSocietyNames.add(TrackChildren.getValue().toString());
                                    Log.d(TAG, String.format("onDataChange: keylist %s", keyList));

                                }
                                if (TrackChildren.getKey().equals(field2) &&
                                        !SelectedSocietyDesp.contains(TrackChildren.getValue().toString())) {
                                    SelectedSocietyDesp.add(TrackChildren.getValue().toString());
                                }
                                if (TrackChildren.getKey().equals(field3) &&
                                        !SelectedSocietyCover.contains(TrackChildren.getValue().toString())) {
                                    SelectedSocietyCover.add(TrackChildren.getValue().toString());
                                }
                            }
                        }

                        if (SelectedSocietyNames.size() > 0) {
                            for (int i = 0; i < SelectedSocietyNames.size(); i++) {

                                myRef.child(mContext.getString(R.string.dbname_filter_data))
                                        .child(mContext.getString(R.string.field_society))
                                        .child(SelectedSocietyNames.get(i))
                                        .child(mAuth.getCurrentUser().getUid())
                                        .removeValue();
                            }
                        }

                    }
                }
                if (rSelected != null) {
                    mSelectedAdapter = new SelectedSocietyRecyclerViewAdapter(SelectedSocietyNames, getActivity(), false, false, false, false);
                    rSelected.setAdapter(mSelectedAdapter);
                    mSelectedAdapter.notifyDataSetChanged();

                    rSelected.setListener(new SwipeLeftRightCallback.Listener() {
                        @Override
                        public void onSwipedLeft(int position) {

                            Log.d(TAG, "onSwipedRight: positon " + position);

                            String Selected = SelectedSocietyNames.get(position);

                            if (position != SelectedSocietyNames.size()
                                    && position < SelectedSocietyNames.size()) {
                                SelectedSocietyNames.remove(position);
                            }

                            if (position != SelectedSocietyDesp.size()
                                    && position < SelectedSocietyDesp.size()) {
                                SelectedSocietyDesp.remove(position);
                            }

                            if (position != SelectedSocietyCover.size()
                                    && position < SelectedSocietyCover.size()) {
                                SelectedSocietyCover.remove(position);
                            }
                            Log.d(TAG, "onSwipedLeft: selected " + Selected);
                            //Log.d(TAG, String.format("onSwipedLeft: Dummy %s", DummySelected));
                            String SocietyNumber = keyList.get(Selected);
                            Log.d(TAG, "onSwipedLeft: SocietyNumber " + SocietyNumber);

                            if (SocietyNumber != null) {
                                myRef.child(mContext.getString(R.string.dbname_user_card))
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.field))
                                        .child(fieldName)
                                        .child(SocietyNumber)
                                        .removeValue();
                            }

                            mSelectedAdapter.notifyDataSetChanged();

                            Snackbar.make(rSelected,
                                    Selected + " Removed",
                                    Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSwipedRight(int position) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetSelectedInDatabase(String field, String heading, String field1, String
            field2, String field3, ArrayList<String> mSocietyName, ArrayList<String> mSocietyCover,
                                       ArrayList<String> mSocietyDesp) {


        for (int i = 0; i < mSocietyName.size(); i++) {
            if (!socNames.contains(mSocietyName.get(i))) {
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field1)
                        .setValue(mSocietyName.get(i));
                socNames.add(mSocietyName.get(i));

            }
        }
        if (statusOfFields.get(mContext.getString(R.string.field_society)).equals(mContext.getString(R.string.field_public))) {
            for (int i = 0; i < mSocietyName.size(); i++) {
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(mContext.getString(R.string.field_society))
                        .child(mSocietyName.get(i))
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mContext.getString(R.string.field_user_id));
            }
        }
        for (int i = 0; i < mSocietyCover.size(); i++) {
            if (!socCover.contains(mSocietyCover.get(i))) {
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field2)
                        .setValue(mSocietyCover.get(i));
                socCover.add(mSocietyCover.get(i));
            }
        }
        for (int i = 0; i < mSocietyDesp.size(); i++) {
            if (!socDesp.contains(mSocietyDesp.get(i))) {

                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field3)
                        .setValue(mSocietyDesp.get(i));
                socDesp.add(mSocietyDesp.get(i));
            }
        }
    }

    private void SetCollegeYear() {
        bCollegeYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shaked");
                rlCollegeYears.startAnimation(myAnim);
                AlertCollegeYears.setVisibility(View.INVISIBLE);

                InflateDialog(R.layout.dialog_layout_college_year);

                //final LineTextView lineTextView = vDialog.findViewById(R.id.FYText);
                final RelativeLayout RL_FY = vDialog.findViewById(R.id.FY_RL);
                final RelativeLayout RL_SY = vDialog.findViewById(R.id.SY_RL);
                final RelativeLayout RL_TY = vDialog.findViewById(R.id.TY_RL);
                final ImageView FYBadge = vDialog.findViewById(R.id.FYBadge);
                final ImageView TYBadge = vDialog.findViewById(R.id.TYBadge);
                final ImageView SYBadge = vDialog.findViewById(R.id.SYBadge);
                final ImageButton CloseDialog = vDialog.findViewById(R.id.close_dialog);

                SetJellyToggleButton(vDialog, mContext.getString(R.string.field_college_year));
                ArrayList<String> fieldsList = new ArrayList<>();
                fieldsList.add(mContext.getString(R.string.field_college_year));
                SetDetailsAndPrivateInfo(vDialog, fieldsList, true, false);

                Animation animation = ReturnAnimation(R.anim.slide_out);
                RL_FY.startAnimation(animation);
                RL_SY.startAnimation(animation);
                RL_TY.startAnimation(animation);

                final android.os.Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    RL_FY.setVisibility(View.VISIBLE);
                    //Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
                    RL_FY.startAnimation(ReturnAnimation(R.anim.slide_in));
                }, 600);
                android.os.Handler handlerSY = new Handler(Looper.getMainLooper());
                handlerSY.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RL_SY.setVisibility(View.VISIBLE);
                        //Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
                        RL_SY.startAnimation(ReturnAnimation(R.anim.slide_in));

                    }
                }, 1200);
                android.os.Handler handlerTY = new Handler(Looper.getMainLooper());
                handlerTY.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RL_TY.setVisibility(View.VISIBLE);
                        //Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
                        RL_TY.startAnimation(ReturnAnimation(R.anim.slide_in));

                    }
                }, 1800);

                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_selected_year);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                final ImageView SelectedBadge = dialog.findViewById(R.id.Selected_Badge);
                final TextView BadgeText = dialog.findViewById(R.id.badgeText);
                FYBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedBadge.setImageResource(R.drawable.ic_badge);
                        BadgeText.setText("Welcome to HRC! Don't be overwhelmed, enjoy your year!");
                        dialog.show();
                        SetFieldInDatabase(mContext.getString(R.string.field_first_year), mContext.getString(R.string.field_college_year));
                        SetDetailForFilter(mContext.getString(R.string.field_college_year), mContext.getString(R.string.field_first_year), valueOfFields.get(mContext.getString(R.string.field_college_year)), statusOfFields.get(mContext.getString(R.string.field_college_year)));

                        android.os.Handler handler1 = new Handler(Looper.getMainLooper());
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 2000);
                    }
                });
                SYBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedBadge.setImageResource(R.drawable.ic_badge2);
                        BadgeText.setText("We am sure your journey till now has been amazing. Keep working hard!");
                        dialog.show();
                        SetFieldInDatabase(mContext.getString(R.string.field_second_year), mContext.getString(R.string.field_college_year));
                        SetDetailForFilter(mContext.getString(R.string.field_college_year), mContext.getString(R.string.field_second_year), valueOfFields.get(mContext.getString(R.string.field_college_year)), statusOfFields.get(mContext.getString(R.string.field_college_year)));

                        android.os.Handler handler1 = new Handler(Looper.getMainLooper());
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 2000);
//                        shineButton.setClickAnimDuration(3000);
//                        shineButton.animate();
                    }
                });
                TYBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedBadge.setImageResource(R.drawable.ic_badge3);
                        BadgeText.setText("We wish you all the best for your final year!");
                        dialog.show();
                        SetFieldInDatabase(mContext.getString(R.string.field_third_year), mContext.getString(R.string.field_college_year));
                        SetDetailForFilter(mContext.getString(R.string.field_college_year), mContext.getString(R.string.field_third_year), valueOfFields.get(mContext.getString(R.string.field_college_year)), statusOfFields.get(mContext.getString(R.string.field_college_year)));

                        android.os.Handler handler1 = new Handler(Looper.getMainLooper());
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 2000);
                    }
                });
                CloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckStatus(mContext.getString(R.string.field_college_year), valueOfFields.get(mContext.getString(R.string.field_college_year)), statusOfFields.get(mContext.getString(R.string.field_college_year)));
                        Alertdialog.dismiss();
                    }
                });
                //lineTextView.animateText("In the beginning ");
            }
        });
    }

    private void SetGender() {
//        bGender.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: shaked");
//                rlGender.startAnimation(myAnim);
//                AlertGender.setVisibility(View.INVISIBLE);
//                InflateDialog(R.layout.layout_auto_desp);
//                Alertdialog.setCanceledOnTouchOutside(false);
//                Alertdialog.setCancelable(false);
//                //INITIALIZING THE WIDGETS INSIDE THE DIALOG
////                final CardView cv_Male = vDialog.findViewById(R.id.CardView_Male);
////                final CardView cv_Female = vDialog.findViewById(R.id.CardView_Female);
////                final CardView cv_other = vDialog.findViewById(R.id.CardView_Other);
////                final EditText TypeGender = vDialog.findViewById(R.id.TypeGender);
////                final EditText TypePronouns = vDialog.findViewById(R.id.TypePronouns);
////                final ImageView Back = vDialog.findViewById(R.id.BackButton);
////                final ImageView Set_Details = vDialog.findViewById(R.id.close_dialog);
////                final ImageView MAKE_PRIVATE = vDialog.findViewById(R.id.GENDER_MAKE_PRIVATE_BUTTON);
////                final AutofitTextView NotShareGender = vDialog.findViewById(R.id.PreferNotToSay);
//
//                PrivacyDialog(MAKE_PRIVATE, mContext.getString(R.string.field_gender));
//                if (!valueOfFields.get(mContext.getString(R.string.field_pronouns)).equals("N/A"))
//                    TypePronouns.setText(valueOfFields.get(mContext.getString(R.string.field_pronouns)));
//                if (!valueOfFields.get(mContext.getString(R.string.field_gender)).equals(mContext.getString(R.string.field_no_gender)) && !valueOfFields.get(mContext.getString(R.string.field_gender)).equals(mContext.getString(R.string.field_male)) && !valueOfFields.get(mContext.getString(R.string.field_gender)).equals(mContext.getString(R.string.field_female)))
//                    TypeGender.setText(valueOfFields.get(mContext.getString(R.string.field_gender)));
//
//                Back.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (cv_other.getVisibility() == View.VISIBLE) {
//                            cv_Male.animate().alpha(1).setDuration(1500);
//                            cv_Female.animate().alpha(1).setDuration(1500);
//                            RelativeLayout.LayoutParams layoutParams =
//                                    (RelativeLayout.LayoutParams) cv_other.getLayoutParams();
//                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//
//                            cv_other.setLayoutParams(layoutParams);
//                            cv_Female.setVisibility(View.VISIBLE);
//                            cv_Male.setVisibility(View.VISIBLE);
//                            TypeGender.setVisibility(View.GONE);
//                            Back.setVisibility(View.GONE);
//                            //   Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
//                            TypeGender.startAnimation(ReturnAnimation(R.anim.slide_out));
//                            isClickedGender = false;
//                        } else if (cv_Male.getVisibility() == View.VISIBLE) {
//                            RelativeLayout.LayoutParams layoutParams =
//                                    (RelativeLayout.LayoutParams) cv_Male.getLayoutParams();
//                            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
//                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
//                            cv_Male.setLayoutParams(layoutParams);
//                            cv_other.animate().alpha(1).setDuration(1500);
//                            cv_Female.animate().alpha(1).setDuration(1500);
//                            cv_Female.setVisibility(View.VISIBLE);
//                            TypeGender.setVisibility(View.GONE);
//                            cv_other.setVisibility(View.VISIBLE);
//                            Back.setVisibility(View.GONE);
//                            //  Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
//                            TypeGender.startAnimation(ReturnAnimation(R.anim.slide_out));
//                            isClickedGender = false;
//                        } else if (cv_Female.getVisibility() == View.VISIBLE) {
//                            RelativeLayout.LayoutParams layoutParams =
//                                    (RelativeLayout.LayoutParams) cv_Female.getLayoutParams();
//                            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
//                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
//                            cv_Female.setLayoutParams(layoutParams);
//                            cv_other.animate().alpha(1).setDuration(1500);
//                            cv_Male.animate().alpha(1).setDuration(1500);
//                            cv_Male.setVisibility(View.VISIBLE);
//                            TypeGender.setVisibility(View.GONE);
//                            cv_other.setVisibility(View.VISIBLE);
//                            Back.setVisibility(View.GONE);
//                            // Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
//                            TypeGender.startAnimation(ReturnAnimation(R.anim.slide_out));
//                            isClickedGender = false;
//                        }
////                        TypePronouns.setVisibility(GONE);
////                        TypePronouns.startAnimation(ReturnAnimation(R.anim.slide_out));
//                    }
//                });
//
//                Set_Details.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String Pronouns = TypePronouns.getText().toString().replace("/", "\\");
//                        if (!TextUtils.isEmpty(TypeGender.getText().toString()) && !TypeGender.getText().toString().equals("N/A")) {
//                            SetDetailForFilter(mContext.getString(R.string.field_gender), TypeGender.getText().toString().toLowerCase(), valueOfFields.get(mContext.getString(R.string.field_gender)), statusOfFields.get(mContext.getString(R.string.field_gender)));
//                            SetFieldInDatabase(TypeGender.getText().toString(), mContext.getString(R.string.field_gender));
//                        }
//
//                        if (!TextUtils.isEmpty(TypePronouns.getText().toString())) {
//                            SetFieldInDatabase(TypePronouns.getText().toString(), mContext.getString(R.string.field_pronouns));
//                            Log.d(TAG, "onClick: Pronouns " + Pronouns);
//                            SetDetailForFilter(mContext.getString(R.string.field_pronouns), Pronouns, valueOfFields.get(mContext.getString(R.string.field_pronouns)), statusOfFields.get(mContext.getString(R.string.field_pronouns)));
//                        } else {
//                            SetFieldInDatabase("N/A", mContext.getString(R.string.field_pronouns));
//                            SetDetailForFilter(mContext.getString(R.string.field_pronouns), "", "N/A", mContext.getString(R.string.field_private));
//
//                        }
//
//                        CheckStatus(mContext.getString(R.string.field_gender), valueOfFields.get(mContext.getString(R.string.field_gender)), statusOfFields.get(mContext.getString(R.string.field_gender)));
//                        CheckStatus(mContext.getString(R.string.field_pronouns), Pronouns, statusOfFields.get(mContext.getString(R.string.field_pronouns)));
//
//                        isClickedGender = false;
//                        Alertdialog.dismiss();
//                    }
//                });
//                NotShareGender.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        myRef.child(mContext.getString(R.string.dbname_user_card))
//                                .child(mAuth.getCurrentUser().getUid())
//                                .child(mContext.getString(R.string.field))
//                                .child(mContext.getString(R.string.field_gender))
//                                .setValue(mContext.getString(R.string.field_no_gender));
//                        Alertdialog.dismiss();
//
//                    }
//                });
//                //  TypePronouns.startAnimation(ReturnAnimation(R.anim.slide_out));
//                cv_Male.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (!isClickedGender) {
//                            SetFieldInDatabase(mContext.getString(R.string.field_male), mContext.getString(R.string.field_gender));
//                            SetDetailForFilter(mContext.getString(R.string.field_gender), mContext.getString(R.string.field_male), valueOfFields.get(mContext.getString(R.string.field_gender)), statusOfFields.get(mContext.getString(R.string.field_gender)));
//
//                            cv_other.animate().alpha(0).setDuration(1500);
//                            cv_Female.animate().alpha(0).setDuration(1500);
//                            //final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
//                            final android.os.Handler handler = new Handler(Looper.getMainLooper());
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cv_other.setVisibility(View.GONE);
//                                    cv_Female.setVisibility(View.GONE);
//                                    RelativeLayout.LayoutParams layoutParams =
//                                            (RelativeLayout.LayoutParams) cv_Male.getLayoutParams();
//                                    layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
//                                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                                    cv_Male.setLayoutParams(layoutParams);
//                                    RelativeLayout.LayoutParams layoutParamsBack =
//                                            (RelativeLayout.LayoutParams) Back.getLayoutParams();
//                                    layoutParamsBack.removeRule(RelativeLayout.ALIGN_BOTTOM);
//                                    layoutParamsBack.removeRule(RelativeLayout.START_OF);
//                                    layoutParamsBack.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                                    layoutParamsBack.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                                    Back.setLayoutParams(layoutParamsBack);
//                                    Back.setVisibility(View.VISIBLE);
//                                    //TypeGender.setText("Male");
////                                    TypeGender.setVisibility(View.VISIBLE);
////                                    TypeGender.startAnimation(animation);
//                                    Snackbar.make(vDialog,
//                                            "Your gender has been set to Male!",
//                                            Snackbar.LENGTH_SHORT).show();
//
//                                }
//                            }, 1500);
//
////                            TypePronouns.setText(valueOfFields.get(mContext.getString(R.string.field_pronouns)));
////                            TypePronouns.setVisibility(View.VISIBLE);
////                            TypePronouns.startAnimation(ReturnAnimation(R.anim.slide_in));
//
//
//                            isClickedGender = true;
//                        }
//                    }
//                });
//                cv_Female.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (!isClickedGender) {
//                            SetFieldInDatabase(mContext.getString(R.string.field_female), mContext.getString(R.string.field_gender));
//                            SetDetailForFilter(mContext.getString(R.string.field_gender), mContext.getString(R.string.field_female), valueOfFields.get(mContext.getString(R.string.field_gender)), statusOfFields.get(mContext.getString(R.string.field_gender)));
//
//                            cv_other.animate().alpha(0).setDuration(1500);
//                            cv_Male.animate().alpha(0).setDuration(1500);
//                            //final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
//                            final android.os.Handler handler = new Handler(Looper.getMainLooper());
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cv_other.setVisibility(View.GONE);
//                                    cv_Male.setVisibility(View.GONE);
//                                    RelativeLayout.LayoutParams layoutParams =
//                                            (RelativeLayout.LayoutParams) cv_Female.getLayoutParams();
//                                    layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
//                                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                                    cv_Female.setLayoutParams(layoutParams);
//                                    RelativeLayout.LayoutParams layoutParamsBack =
//                                            (RelativeLayout.LayoutParams) Back.getLayoutParams();
//                                    layoutParamsBack.removeRule(RelativeLayout.ALIGN_BOTTOM);
//                                    layoutParamsBack.removeRule(RelativeLayout.START_OF);
//                                    layoutParamsBack.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                                    layoutParamsBack.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                                    Back.setLayoutParams(layoutParamsBack);
//                                    Back.setVisibility(View.VISIBLE);
//                                    // TypeGender.setText("Female");
//                                    Snackbar.make(vDialog,
//                                            "Your gender has been set to Female!",
//                                            Snackbar.LENGTH_SHORT).show();
//
//                                }
//                            }, 1500);
//
////                            TypePronouns.setText(valueOfFields.get(mContext.getString(R.string.field_pronouns)));
////                            TypePronouns.setVisibility(View.VISIBLE);
////                            TypePronouns.startAnimation(ReturnAnimation(R.anim.slide_in));
//
//                            isClickedGender = true;
//                        }
//                    }
//                });
//
////                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
//                TypeGender.startAnimation(ReturnAnimation(R.anim.slide_out));
//
//                cv_other.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!isClickedGender) {
//
//                            cv_Male.animate().alpha(0).setDuration(1500);
//                            cv_Female.animate().alpha(0).setDuration(1500);
//                            //  final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
//                            final android.os.Handler handler = new Handler(Looper.getMainLooper());
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cv_Male.setVisibility(View.GONE);
//                                    cv_Female.setVisibility(View.GONE);
//                                    RelativeLayout.LayoutParams layoutParams =
//                                            (RelativeLayout.LayoutParams) cv_other.getLayoutParams();
//                                    layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                                    cv_other.setLayoutParams(layoutParams);
//                                    RelativeLayout.LayoutParams layoutParamsBack =
//                                            (RelativeLayout.LayoutParams) Back.getLayoutParams();
//                                    layoutParamsBack.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                                    layoutParamsBack.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                                    layoutParamsBack.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.TypeGender);
//                                    layoutParamsBack.addRule(RelativeLayout.START_OF, R.id.TypeGender);
//                                    Back.setLayoutParams(layoutParamsBack);
//                                    Back.setVisibility(View.VISIBLE);
////                                    TypePronouns.setText(valueOfFields.get(mContext.getString(R.string.field_pronouns)));
////                                    TypePronouns.setVisibility(View.VISIBLE);
////                                    TypePronouns.startAnimation(ReturnAnimation(R.anim.slide_in));
//
//                                    //TypeGender.setText(valueOfFields.get(mContext.getString(R.string.field_gender)));
//                                    TypeGender.setVisibility(View.VISIBLE);
//                                    TypeGender.startAnimation(ReturnAnimation(R.anim.slide_in));
////                                TypeGender.setVisibility(View.VISIBLE);
////                                TypeGender.startAnimation(animation);
//
//                                }
//                            }, 1500);
//                            isClickedGender = true;
//                        }
//                    }
//                });
//            }
//        });
    }

    private void PrivacyDialog(View Private, final String Field) {

        Private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vDialog = inflaterDialog.inflate(R.layout.layout_privacy, null);  // this line
                builder.setView(vDialog);
                PrivacyAlertDialog = builder.create();
                PrivacyAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                PrivacyAlertDialog.show();

                SetJellyToggleButton(vDialog, Field);
                FieldList.add(mContext.getString(R.string.field_gender));
                FieldList.add(mContext.getString(R.string.field_pronouns));
                SetDetailsAndPrivateInfo(vDialog, FieldList, false, false);
            }
        });


    }

    private Animation ReturnAnimation(int AnimationName) {
        return AnimationUtils.loadAnimation(getActivity(), AnimationName);
    }

    private void SetMoMuBo() {
        bMuMoBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shaked");
                rlMuMoBo.startAnimation(myAnim);
                AlertMuMoBo.setVisibility(View.INVISIBLE);
//                MusicMoviesBooksFragment fragment = new MusicMoviesBooksFragment();
                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.FrameLayoutCardInfo, fragment);
//                Transaction.addToBackStack(mContext.getString(R.string.music_movies_books_fragment));
//                Transaction.commit();
            }
        });
    }

    private void SetUserAge() {
        bAge.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: shaked");
                rlAge.startAnimation(myAnim);
                AlertAge.setVisibility(View.INVISIBLE);
                //INFLATING THE VIEW INSIDE THE DIALOG
                InflateDialog(R.layout.dialog_prov_loc);
                //INITIALIZING THE WIDGETS INSIDE THE DIALOG
                final DatePicker datePicker = vDialog.findViewById(R.id.fire_grid);
                final TextView BirthDate = vDialog.findViewById(R.id.fragment_container_view_tag);
                final Button setDetails = vDialog.findViewById(R.id.close_dialog);
                final EditText Age = vDialog.findViewById(R.id.games_button);

                FieldList.add(mContext.getString(R.string.field_age));
                FieldList.add(mContext.getString(R.string.field_birth_date));

                SetJellyToggleButton(vDialog, mContext.getString(R.string.field_age));
                SetDetailsAndPrivateInfo(vDialog, FieldList, false, false);
                BirthDate.setText(valueOfFields.get(mContext.getString(R.string.field_birth_date)));
                birthDate = valueOfFields.get(mContext.getString(R.string.field_birth_date));
                Log.d(TAG, "onClick: birthdate  " + birthDate);
                if (!birthDate.equals("DD/MM/YYYY") && !birthDate.equals("N/A") && !TextUtils.isEmpty(birthDate)) {
                    int slash1 = birthDate.indexOf("/");
                    Log.d(TAG, "onClick: slash1 " + slash1);
                    int slash2 = birthDate.lastIndexOf("/");
                    Log.d(TAG, "onClick: slash2 " + slash2);
                    int year = Integer.parseInt(birthDate.substring(slash2 + 1));
                    int month = Integer.parseInt(birthDate.substring(slash1 + 1, slash2));
                    int day = Integer.parseInt(birthDate.substring(0, slash1));

                    Log.d(TAG, "onClick: year " + year);
                    Log.d(TAG, "onClick: month " + month);
                    Log.d(TAG, "onClick: day " + day);


                    datePicker.updateDate(year, month - 1, day);
                }
                Age.setText(valueOfFields.get(mContext.getString(R.string.field_age)));
                //SETTING MAXIMUM DATE LIMIT TO TODAY'S DATE () RETRIEVING CURRENT DATE FROM FIREBASE
                DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                offsetRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        long offset = snapshot.getValue(Long.class);
                        long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                        datePicker.setMaxDate(estimatedServerTimeMs);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.err.println("Listener was cancelled");
                    }
                });
                datePicker.setSoundEffectsEnabled(true);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        int day = datePicker.getDayOfMonth();
//                        int month = datePicker.getMonth() + 1;
//                        int year = datePicker.getYear();
                        birthDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(datePicker.getMaxDate());
                        int mYear = c.get(Calendar.YEAR);
                        Age.setText(String.valueOf((mYear - year) - 1));
                        BirthDate.setText(birthDate);
                    }
                });

                setDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (BirthDate.getText().toString().equals("DD/MM/YYYY")) {
                            Toast.makeText(mContext, "Please select your BirthDate.", Toast.LENGTH_SHORT).show();
                        }
                        if (TextUtils.isEmpty(Age.getText().toString())) {
                            Age.setError("Please type in your current age.");
                        }
                        if (!TextUtils.isEmpty(Age.getText().toString()) && !BirthDate.getText().toString().equals("DD/MM/YYYY")) {
                            SetDetailForFilter(mContext.getString(R.string.field_age), Age.getText().toString(), valueOfFields.get(mContext.getString(R.string.field_age)), statusOfFields.get(mContext.getString(R.string.field_age)));
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field))
                                    .child(mContext.getString(R.string.field_age))
                                    .setValue(Age.getText().toString());

                            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_age))
                                    .setValue(Age.getText().toString());

                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field))
                                    .child(mContext.getString(R.string.field_birth_date))
                                    .setValue(birthDate);

                            Alertdialog.dismiss();
                        }

                    }
                });
            }
        });
    }

    private void SetJellyToggleButton(View vDialog, String field) {
        JellyToggleButton jellyToggleButton = vDialog.findViewById(R.id.MakeDataPrivateSwitchMusic);
        Log.d(TAG, "SetJellyToggleButton: field " + field);
        jellyToggleButton.setChecked(!mContext.getString(R.string.field_public).equals(statusOfFields.get(field)));

    }

    private void SetDetailsAndPrivateInfo(View vDialog, final ArrayList<String> FieldsList,
                                          final boolean inflate, final boolean isButton) {
        final ImageView privateDataNoticeImageView = vDialog.findViewById(R.id.private_notice);
        TextView PRIVATE_DATA_NOTICE = null;
        if (!isButton) {
            PRIVATE_DATA_NOTICE = vDialog.findViewById(R.id.private_warning);
            JellyToggleButton jellyToggleButton = vDialog.findViewById(R.id.MakeDataPrivateSwitchMusic);
            jellyToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, String.format("onCheckedChanged: status of fields %s", statusOfFields));
                    if (!isChecked) {
                        for (int i = 0; i < FieldsList.size(); i++) {
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_is_private))
                                    .child(FieldsList.get(i))
                                    .setValue(mContext.getString(R.string.field_public));

                        }
                    } else {
                        for (int i = -0; i < FieldsList.size(); i++) {
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_is_private))
                                    .child(FieldsList.get(i))
                                    .setValue(mContext.getString(R.string.field_private));

                        }
                    }
                }
            });
        }
        final TextView finalPRIVATE_DATA_NOTICE = PRIVATE_DATA_NOTICE;
        privateDataNoticeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: privateDataNoticeImageView");
                if (inflate) {
                    TextView Notice = new TextView(mContext);
                    Notice.setPadding(10, 10, 10, 10);
                    Notice.setTextColor(Color.WHITE);
                    Notice.setTextSize(20);
                    Notice.setBackgroundColor(Color.BLACK);
                    Notice.setText(mContext.getString(R.string.field_private_data_notice));
                    builder.setView(Notice);
                    PrivacyAlertDialog = builder.create();
                    PrivacyAlertDialog.show();
                } else {
                    if (!isClickedPrivate) {
                        Log.d(TAG, "onClick: privateDataNoticeImageView !isClickedPrivate");
                        finalPRIVATE_DATA_NOTICE.setVisibility(View.VISIBLE);
                        isClickedPrivate = true;

                    } else {
                        Log.d(TAG, "onClick: privateDataNoticeImageView isClickedPrivate");
                        finalPRIVATE_DATA_NOTICE.setVisibility(View.GONE);
                        isClickedPrivate = false;
                    }
                }
            }
        });
    }

    public static View makeMeShake(View view, int duration, int offset) {
        if (view != null) {
            Animation anim = new TranslateAnimation(-offset, offset, 0, 0);
            anim.setDuration(duration);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(5);
            view.startAnimation(anim);
        }
        return view;

    }
}
