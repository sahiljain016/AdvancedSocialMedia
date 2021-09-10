package com.gic.memorableplaces.SignUp;

import static android.widget.LinearLayout.GONE;
import static android.widget.LinearLayout.INVISIBLE;
import static android.widget.LinearLayout.OnClickListener;
import static android.widget.LinearLayout.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.ResultMMBRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.SelectedMMBRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicMoviesBooksFragment extends Fragment implements ResultMMBRecyclerViewAdapter.OnMMBResultsClickListener {
    private static final String TAG = "MusicMoviesBooksFragment";
    private Context mContext;
    private long last_text_edit = 0;
    private int MAX_NUMBER_OF_RESULTS = 10;

    private boolean isMusic = true;
    private boolean isMovie = false;
    private boolean isBook = false;
    private boolean isClicked = false;
    private String QUERY_TYPE = "movie";
    private String MOVIE_NAME_KEYWORD = "title", status = "public";

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private ArrayList<String> trackNames;
    private ArrayList<String> trackCoversList;
    private ArrayList<String> trackArtist;
    private ArrayList<String> MovieNameList;
    private ArrayList<String> MovieOverViewList;
    private ArrayList<String> MovieCoverList;
    private ArrayList<String> BookNameList;
    private ArrayList<String> BookCoverList;
    private ArrayList<String> BookAuthorList;
    private ArrayList<String> selDesp;
    private ArrayList<String> selName;
    private ArrayList<String> selCover;

    private ArrayList<String> mSNameList = new ArrayList<>();
    private ArrayList<String> mSCreatorList = new ArrayList<>();
    private ArrayList<String> mSCoverList = new ArrayList<>();

    private HashMap<String, String> keyList;

    private AppCompatButton MUSIC_BUTTON;
    private AppCompatButton MOVIES_BUTTON;
    private AppCompatButton BOOKS_BUTTON;
    private AppCompatButton SET_DETAILS;
    private AppCompatButton ViewItems;
    private AppCompatButton ChangMovieOrTvShow;
    private TextView tTITLE_OPTIONS;
    private TextView NO_SELECTION;
    private TextView Title_Make_Data_Private;
    private EditText eTrackName;
    private EditText eMovieName;
    private EditText eBookName;
    private ImageView privateInfoNotice, bg_icon_mmb;
    private AnimatedRecyclerView rResults;
    private RecyclerView.Adapter mResultAdapter;
    private RecyclerView.Adapter mSelectedAdapter;
    //private LVCircularZoom lvCircularZoom;
    private ImageView tick, UserChecked, UserUnChecked;
    private ResultMMBRecyclerViewAdapter.OnMMBResultsClickListener onMMBResultsClickListener;

    private AlertDialog.Builder builder;
    private LayoutInflater inflaterDialog;
    private AlertDialog Alertdialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_movies_books, container, false);
        Log.d(TAG, "onCreateView: MusicMoviesBooksFragment");

        mContext = getActivity();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        trackNames = new ArrayList<>();
        trackCoversList = new ArrayList<>();
        trackArtist = new ArrayList<>();
        MovieCoverList = new ArrayList<>();
        MovieOverViewList = new ArrayList<>();
        MovieNameList = new ArrayList<>();
        BookAuthorList = new ArrayList<>();
        BookCoverList = new ArrayList<>();
        BookNameList = new ArrayList<>();
        mSNameList = new ArrayList<>();
        mSCreatorList = new ArrayList<>();
        mSCoverList = new ArrayList<>();
        selName = new ArrayList<>();
        selCover = new ArrayList<>();
        selDesp = new ArrayList<>();
        keyList = new HashMap<>();
        MUSIC_BUTTON = view.findViewById(R.id.Button_Music);
        ChangMovieOrTvShow = view.findViewById(R.id.ChangeMovieType);
        MOVIES_BUTTON = view.findViewById(R.id.Button_Movies);
        bg_icon_mmb = view.findViewById(R.id.bg_icon_mmb);
        BOOKS_BUTTON = view.findViewById(R.id.Button_Books);
        SET_DETAILS = view.findViewById(R.id.Set_Details);
        ViewItems = view.findViewById(R.id.ViewSelectedItems);
        tTITLE_OPTIONS = view.findViewById(R.id.Selected_title);
        NO_SELECTION = view.findViewById(R.id.button_No_MMB);
        eTrackName = view.findViewById(R.id.TrackName);
        eMovieName = view.findViewById(R.id.MovieName);
        eBookName = view.findViewById(R.id.BookName);
        rResults = view.findViewById(R.id.Result_MOVIES_BOOKS_MUSIC_RV);
        ///lvCircularZoom = view.findViewById(R.id.loadingdots);
        privateInfoNotice = view.findViewById(R.id.private_notice);
        tick = view.findViewById(R.id.tick_me);
        UserChecked = view.findViewById(R.id.FieldChecked);
        UserUnChecked = view.findViewById(R.id.FieldUnchecked);
        Title_Make_Data_Private = view.findViewById(R.id.heading_change_filter);
        onMMBResultsClickListener = this;

      //  lvCircularZoom.setViewColor(Color.BLACK);

        SetJellyToggleButton(mContext.getString(R.string.field_music));
        SetDetailsAndPrivateInfo(mContext.getString(R.string.field_music));
        setNoSelection(mContext.getString(R.string.field_music), eTrackName);
        NoSelection(mContext.getString(R.string.field_music), "music", eTrackName);

        builder = new AlertDialog.Builder(getActivity());
        inflaterDialog = requireActivity().getLayoutInflater();

        TopButtonsOnClick();
        findMusic();
        findMovies();
        findBooks();
        viewSelectedItems(false);
        return view;
    }


    private void TopButtonsOnClick() {

        MUSIC_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMusic = true;
                isMovie = false;
                isBook = false;
                bg_icon_mmb.setImageResource(R.drawable.ic_music);
                SetButtonAppearance(MUSIC_BUTTON, MOVIES_BUTTON, BOOKS_BUTTON);
                eTrackName.setVisibility(View.VISIBLE);
                eMovieName.setVisibility(View.GONE);
                ChangMovieOrTvShow.setVisibility(INVISIBLE);
                eBookName.setVisibility(View.GONE);
                setNoSelection(mContext.getString(R.string.field_music), eTrackName);
                tTITLE_OPTIONS.setText("Music: ");
                NO_SELECTION.setText("I don't like any kind of Music.");
                NoSelection(mContext.getString(R.string.field_music), "music", eTrackName);
                SetJellyToggleButton(mContext.getString(R.string.field_music));
                SetDetailsAndPrivateInfo(mContext.getString(R.string.field_music));
                Title_Make_Data_Private.setText("Make my music private");
                ClearAllLists();
            }
        });
        MOVIES_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMusic = false;
                isMovie = true;
                isBook = false;
                bg_icon_mmb.setImageResource(R.drawable.ic_movie);

                SetButtonAppearance(MOVIES_BUTTON, MUSIC_BUTTON, BOOKS_BUTTON);
                eTrackName.setVisibility(View.GONE);
                eMovieName.setVisibility(View.VISIBLE);
                eBookName.setVisibility(View.GONE);
                ChangMovieOrTvShow.setVisibility(VISIBLE);
                Title_Make_Data_Private.setText("Make my movies private");
//                jellyToggleButtonMusic.setVisibility(View.GONE);
//                jellyToggleButtonMovie.setVisibility(View.VISIBLE);
//                jellyToggleButtonBooks.setVisibility(View.GONE);
                setNoSelection(mContext.getString(R.string.field_movie), eMovieName);
                tTITLE_OPTIONS.setText("Movie: ");
                NO_SELECTION.setText("I don't like any kind of Movies.");
                NoSelection(mContext.getString(R.string.field_movie), "movie", eMovieName);
                SetJellyToggleButton(mContext.getString(R.string.field_movie));
                SetDetailsAndPrivateInfo(mContext.getString(R.string.field_movie));
                ClearAllLists();
            }
        });
        BOOKS_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMusic = false;
                isMovie = false;
                isBook = true;
                bg_icon_mmb.setImageResource(R.drawable.ic_book);

                SetButtonAppearance(BOOKS_BUTTON, MOVIES_BUTTON, MUSIC_BUTTON);
                eTrackName.setVisibility(View.GONE);
                eMovieName.setVisibility(View.GONE);
                eBookName.setVisibility(View.VISIBLE);
                ChangMovieOrTvShow.setVisibility(INVISIBLE);
                setNoSelection(mContext.getString(R.string.field_books), eBookName);
                SetJellyToggleButton(mContext.getString(R.string.field_books));
                SetDetailsAndPrivateInfo(mContext.getString(R.string.field_books));
                Title_Make_Data_Private.setText("Make my books private");
                tTITLE_OPTIONS.setText("Book: ");
                NO_SELECTION.setText("I don't like any kind of BOOKS.");
                NoSelection(mContext.getString(R.string.field_books), "books", eBookName);
                ClearAllLists();

            }
        });
        privateInfoNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView Notice = new TextView(mContext);
                Notice.setPadding(10, 10, 10, 10);
                Notice.setTextColor(Color.WHITE);
                Notice.setTextSize(20);
                Notice.setBackgroundColor(Color.BLACK);
                Notice.setText(mContext.getString(R.string.field_private_data_notice));
                builder.setView(Notice);
                Alertdialog = builder.create();
                Alertdialog.show();
            }
        });

        SET_DETAILS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                SetSelectedInDatabase(mContext.getString(R.string.field_music), "track",
                        mContext.getString(R.string.field_track_name), mContext.getString(R.string.field_singer_name)
                        , mContext.getString(R.string.field_cover_url));

                SetSelectedInDatabase(mContext.getString(R.string.field_movie), "movie",
                        mContext.getString(R.string.field_movie_name), mContext.getString(R.string.field_movie_overview)
                        , mContext.getString(R.string.field_cover_url));

                SetSelectedInDatabase(mContext.getString(R.string.field_books), "books",
                        mContext.getString(R.string.field_book_name), mContext.getString(R.string.field_book_authors)
                        , mContext.getString(R.string.field_cover_url));

                requireFragmentManager().beginTransaction().remove(MusicMoviesBooksFragment.this).commit();
            }
        });

    }

    private void SetDetailForFilter(String Detail, String Value, String Value2, String key) {

        if (key.equals("members")) {
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(MiscTools.ChangeKeyForFirebase(Value))
                    .child(key)
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(mContext.getString(R.string.field_user_id));
        } else {
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(MiscTools.ChangeKeyForFirebase(Value))
                    .child(key)
                    .setValue(Value2);
        }

    }

    private void ClearDetails(String Detail, String Value, String key) {

        myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(Detail)
                .child(MiscTools.ChangeKeyForFirebase(Value))
                .child(key)
                .child(mAuth.getCurrentUser().getUid())
                .removeValue();


    }

    private void ClearAllLists() {
        trackArtist.clear();
        trackCoversList.clear();
        trackNames.clear();
        MovieCoverList.clear();
        MovieNameList.clear();
        MovieOverViewList.clear();
        BookNameList.clear();
        BookCoverList.clear();
        BookAuthorList.clear();
        mSCoverList.clear();
        mSCreatorList.clear();
        mSNameList.clear();
    }

    private void setNoSelection(final String FieldName, final EditText editText) {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(FieldName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue().toString().equals(mContext.getString(R.string.no_selection))) {
                        NO_SELECTION.setBackgroundColor(Color.parseColor("#A37C0202"));
                        tick.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                100.0f
                        );
                        SET_DETAILS.setLayoutParams(param);
                        ViewItems.setVisibility(View.GONE);
                        editText.setEnabled(false);
                    } else {
                        BringBackViewButton();
                        editText.setEnabled(true);
                    }
                } else {
                    BringBackViewButton();
                    editText.setEnabled(true);
                }
                editText.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void NoSelection(final String FieldName, final String heading, final EditText editText) {
        NO_SELECTION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tick.getVisibility() == GONE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("I don't like " + heading + ".")
                            .setMessage("This will delete all the items you have selected in the " + heading + " category.")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                                    .child(mAuth.getCurrentUser().getUid())
                                                    .child(mContext.getString(R.string.field))
                                                    .child(FieldName)
                                                    .removeValue();
                                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                                    .child(mAuth.getCurrentUser().getUid())
                                                    .child(mContext.getString(R.string.field))
                                                    .child(FieldName)
                                                    .setValue(mContext.getString(R.string.no_selection));
                                            mSNameList.clear();
                                            mSCoverList.clear();
                                            mSCreatorList.clear();
                                            Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
                                            NO_SELECTION.setBackgroundColor(Color.parseColor("#A37C0202"));
                                            tick.setVisibility(View.VISIBLE);
                                            dialog.dismiss();
                                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    100.0f
                                            );
                                            SET_DETAILS.setLayoutParams(param);
                                            ViewItems.setVisibility(View.GONE);
                                            editText.setEnabled(false);
                                            editText.setText("");
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
                } else {
                    BringBackViewButton();
                    myRef.child(mContext.getString(R.string.dbname_user_card))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field))
                            .child(FieldName)
                            .removeValue();
                    Toast.makeText(mContext, "Selected Removed", Toast.LENGTH_SHORT).show();
                    editText.setEnabled(true);
                    editText.setText("");
                }

            }
        });
    }

    private void BringBackViewButton() {

        tick.setVisibility(GONE);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50.0f
        );
        //param.setMarginEnd(20);
        param.setMargins(20, 0, 20, 10);
        SET_DETAILS.setLayoutParams(param);
        ViewItems.setLayoutParams(param);
        ViewItems.setVisibility(View.VISIBLE);
        NO_SELECTION.setBackgroundColor(Color.parseColor("#F05093DC"));

    }


    private void viewSelectedItems(boolean isItemClick) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final View view = inflaterDialog.inflate(R.layout.dialog_layout_music_movies_books_selected, null);  // this line
                builder.setView(view);
                Alertdialog = builder.create();
                Alertdialog.show();
                final TextView Title = view.findViewById(R.id.SelectedOptionTV);
                final SwipeableRecyclerView rSelected = view.findViewById(R.id.FINAL_MOVIES_BOOKS_MUSIC_RV);
                rSelected.setLayoutManager(new LinearLayoutManager(mContext));
                final AppCompatButton ConfirmButton = view.findViewById(R.id.close_dialog);
                rSelected.setRightBg(R.color.red);
                rSelected.setRightImage(R.drawable.ic_trash);


                ConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMusic) {
                            if (mSNameList.size() > 0 && status.equals(mContext.getString(R.string.field_public))) {
                                for (int i = 0; i < mSNameList.size(); i++) {
                                    SetDetailForFilter(mContext.getString(R.string.field_music), mSNameList.get(i), "", "members");
                                    SetDetailForFilter(mContext.getString(R.string.field_music), mSNameList.get(i), mSCreatorList.get(i), mContext.getString(R.string.field_singer_name));
                                    SetDetailForFilter(mContext.getString(R.string.field_music), mSNameList.get(i), mSCoverList.get(i), mContext.getString(R.string.field_cover_url));
                                }
                            }
                            SetSelectedInDatabase(mContext.getString(R.string.field_music), "track",
                                    mContext.getString(R.string.field_track_name), mContext.getString(R.string.field_singer_name)
                                    , mContext.getString(R.string.field_cover_url));
                        }
                        if (isMovie) {
                            if (mSNameList.size() > 0 && status.equals(mContext.getString(R.string.field_public))) {
                                for (int i = 0; i < mSNameList.size(); i++) {
                                    SetDetailForFilter(mContext.getString(R.string.field_movie), mSNameList.get(i), "", "members");
                                    SetDetailForFilter(mContext.getString(R.string.field_movie), mSNameList.get(i), mSCreatorList.get(i), mContext.getString(R.string.field_movie_overview));
                                    SetDetailForFilter(mContext.getString(R.string.field_movie), mSNameList.get(i), mSCoverList.get(i), mContext.getString(R.string.field_cover_url));
                                }
                            }
                            SetSelectedInDatabase(mContext.getString(R.string.field_movie), "movie",
                                    mContext.getString(R.string.field_movie_name), mContext.getString(R.string.field_movie_overview)
                                    , mContext.getString(R.string.field_cover_url));
                        }
                        if (isBook) {
                            if (mSNameList.size() > 0 && status.equals(mContext.getString(R.string.field_public))) {
                                for (int i = 0; i < mSNameList.size(); i++) {
                                    SetDetailForFilter(mContext.getString(R.string.field_books), mSNameList.get(i), "", "members");
                                    SetDetailForFilter(mContext.getString(R.string.field_books), mSNameList.get(i), mSCreatorList.get(i), mContext.getString(R.string.field_book_authors));
                                    SetDetailForFilter(mContext.getString(R.string.field_books), mSNameList.get(i), mSCoverList.get(i), mContext.getString(R.string.field_cover_url));
                                }
                            }
                            SetSelectedInDatabase(mContext.getString(R.string.field_books), "books",
                                    mContext.getString(R.string.field_book_name), mContext.getString(R.string.field_book_authors)
                                    , mContext.getString(R.string.field_cover_url));
                        }

                        Alertdialog.dismiss();
                    }
                });

                if (isMusic) {
                    Title.setText("Selected Music: ");
                    GetSelectedFromDatabase(rSelected, mContext.getString(R.string.field_music), mContext.getString(R.string.field_track_name)
                            , mContext.getString(R.string.field_singer_name), mContext.getString(R.string.field_cover_url), "track");
                }
                if (isMovie) {
                    Title.setText("Selected Movies: ");
                    GetSelectedFromDatabase(rSelected, mContext.getString(R.string.field_movie), mContext.getString(R.string.field_movie_name)
                            , mContext.getString(R.string.field_movie_overview), mContext.getString(R.string.field_cover_url), "movie");
                }
                if (isBook) {
                    Title.setText("Selected Books: ");
                    GetSelectedFromDatabase(rSelected, mContext.getString(R.string.field_books), mContext.getString(R.string.field_book_name)
                            , mContext.getString(R.string.field_book_authors), mContext.getString(R.string.field_cover_url), "books");
                }

            }
        };
        if (isItemClick) {
            runnable.run();
        }
        ViewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runnable.run();

            }
        });

    }

    private void GetSelectedFromDatabase(final SwipeableRecyclerView rSelected, final String fieldName,
                                         final String field1, final String field2, final String field3, final String heading) {

        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(fieldName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArrayList<String> keySet = new ArrayList<>();
                    keySet.add(dataSnapshot.getKey());
                    for (DataSnapshot TrackChildren : dataSnapshot.getChildren()) {
                        if (mSNameList.size() < 5) {
                            if (TrackChildren.getKey().equals(field1)
                                    && !mSNameList.contains(TrackChildren.getValue().toString())) {
                                keyList.put(TrackChildren.getValue().toString(), keySet.get(0));
                                Log.d(TAG, String.format("onDataChange: kelist %s", keyList));
                                mSNameList.add(TrackChildren.getValue().toString());
                            }
                            if (TrackChildren.getKey().equals(field2) &&
                                    !mSCreatorList.contains(TrackChildren.getValue().toString())) {
                                mSCreatorList.add(TrackChildren.getValue().toString());
                            }
                            if (TrackChildren.getKey().equals(field3) &&
                                    !mSCoverList.contains(TrackChildren.getValue().toString())) {
                                mSCoverList.add(TrackChildren.getValue().toString());
                            }
                        }
                    }

                }
                mSelectedAdapter = new SelectedMMBRecyclerViewAdapter(mSNameList, mSCreatorList, mSCoverList,
                        getActivity(), isMusic, isMovie, isBook, true);
                rSelected.setAdapter(mSelectedAdapter);
                mSelectedAdapter.notifyDataSetChanged();
                rSelected.setListener(new SwipeLeftRightCallback.Listener() {

                    @Override
                    public void onSwipedLeft(int position) {
                        Log.d(TAG, "onSwipedRight: positon " + position);

                        String Selected = mSNameList.get(position);

                        ClearDetails(fieldName, Selected, "members");
                        if (position != mSNameList.size()
                                && position < mSNameList.size()) {
                            mSNameList.remove(position);
                        }
                        if (position != mSCreatorList.size()
                                && position < mSCreatorList.size()) {
                            mSCreatorList.remove(position);
                        }
                        if (position != mSCoverList.size()
                                && position < mSCoverList.size()) {
                            mSCoverList.remove(position);
                        }
                        Log.d(TAG, "onSwipedLeft: selected " + Selected);
                        //Log.d(TAG, String.format("onSwipedLeft: Dummy %s", DummySelected));
                        String TrackNumber = keyList.get(Selected);
                        Log.d(TAG, "onSwipedLeft: SocietyNumber " + TrackNumber);

                        if (TrackNumber != null) {
                            myRef.child(mContext.getString(R.string.dbname_user_card))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field))
                                    .child(fieldName)
                                    .child(TrackNumber)
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetSelectedInDatabase(String field, String heading, String field1, String field2, String field3) {
        for (int i = 0; i < mSNameList.size(); i++) {
            if (!selName.contains(mSNameList.get(i))) {
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field1)
                        .setValue(mSNameList.get(i));
                selName.add(mSNameList.get(i));
            }
        }
        for (int i = 0; i < mSCreatorList.size(); i++) {
            if (!selDesp.contains(mSCreatorList.get(i))) {
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field2)
                        .setValue(mSCreatorList.get(i));
                selDesp.add(mSCreatorList.get(i));
            }
        }
        for (int i = 0; i < mSCoverList.size(); i++) {
            if (!selCover.contains(mSCoverList.get(i))) {
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field))
                        .child(field)
                        .child(String.valueOf(heading + i))
                        .child(field3)
                        .setValue(mSCoverList.get(i));
                selCover.add(mSCoverList.get(i));
            }
        }
    }

    private void findBooks() {
        Log.d(TAG, "onClick: shaked");
        final long delay = 1000; // 1 seconds after user stops typing
        // TODO: do what you need here   final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(eBookName.getText().toString())) {
                        try {
                            BookAuthorList.clear();
                            BookCoverList.clear();
                            BookNameList.clear();
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://www.googleapis.com/books/v1/volumes"
                                            + "?q="
                                            + eBookName.getText().toString()
                                            + "&key="
                                            + mContext.getString(R.string.The_GOOGLE_BOOKS_API_KEY))
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
                                        String result = jsonObject.getString("items");
                                        JSONArray resultArray = new JSONArray(result);
                                        BookNameList.clear();
                                        BookCoverList.clear();
                                        BookAuthorList.clear();
                                        int limit = 0;
                                        if (resultArray.length() > MAX_NUMBER_OF_RESULTS) {
                                            limit = MAX_NUMBER_OF_RESULTS;
                                        } else {
                                            limit = resultArray.length();
                                        }
                                        for (int i = 0; i < limit; i++) {
                                            JSONObject insideArray = resultArray.getJSONObject(i);
                                            String volumeInfo = insideArray.getString("volumeInfo");

                                            JSONObject volumeInfoObject = new JSONObject(volumeInfo);

                                            if (!BookNameList.contains(volumeInfoObject.getString("title"))) {
                                                if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
                                                    BookNameList.add("N/A");
                                                } else {
                                                    BookNameList.add(volumeInfoObject.getString("title"));
                                                }
                                                // String title = volumeInfoObject.getString("title");
                                                //Log.d(TAG, "onResponse: Book TITLE " + title);
//                                                Log.d(TAG, "onResponse: Volume Info: " + volumeInfoObject.getString("authors"));
                                                String authors = null;
                                                if (volumeInfoObject.has("authors")) {
                                                    JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                                                    authors = authorsArray.getString(0);
                                                } else {
                                                    authors = "N/A";
                                                }
                                                Log.d(TAG, String.format("onResponse: Volume Info object: %s", authors));
//                                                String authors = volumeInfoObject.getString("authors");
                                                Log.d(TAG, "onResponse: authors " + authors);
                                                //String filtered = authors.replaceAll("[\\[\\]\"]", "");
                                                if (TextUtils.isEmpty(authors)) {
                                                    BookAuthorList.add("N/A");
                                                } else {
                                                    BookAuthorList.add(authors);

                                                }

                                                if (volumeInfoObject.has("imageLinks")) {
                                                    String ImageLink = volumeInfoObject.getString("imageLinks");
                                                    JSONObject imageLinksObject = new JSONObject(ImageLink);
                                                    if (TextUtils.isEmpty(imageLinksObject.getString("thumbnail"))) {
                                                        BookCoverList.add("N/A");
                                                    } else {
                                                        BookCoverList.add(imageLinksObject.getString("thumbnail"));

                                                    }
                                                }
                                                else{
                                                    BookCoverList.add("N/A");
                                                }
                                                //Log.d(TAG, "onResponse: image link book " + ImageURL);
                                            }
                                        }
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieNameList));
//                                        Log.d(TAG, new StringBuilder().append("onResponse: trackNames ").append(MovieCoverList).toString());
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieOverViewList));
                                        Handler handler1 = new Handler(Looper.getMainLooper());
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mResultAdapter = new ResultMMBRecyclerViewAdapter(BookNameList, BookAuthorList, BookCoverList,
                                                        getActivity(), isMusic, isMovie, isBook, onMMBResultsClickListener);
                                                rResults.setAdapter(mResultAdapter);
                                                mResultAdapter.notifyDataSetChanged();
                                                rResults.scheduleLayoutAnimation();
                                                //lvCircularZoom.stopAnim();
                                               // lvCircularZoom.setVisibility(View.GONE);

                                            }
                                        }, 1000);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                                }
                            });
                            // }
//                            }, 1000);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        eBookName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
                //lvCircularZoom.setVisibility(View.VISIBLE);
                //lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BookAuthorList.clear();
                BookCoverList.clear();
                BookNameList.clear();
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
                    //lvCircularZoom.stopAnim();
                    ///lvCircularZoom.setVisibility(GONE);
                }
                Log.d(TAG, "afterTextChanged: ");
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void findMovies() {
        ChangMovieOrTvShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    QUERY_TYPE = "tv";
                    MOVIE_NAME_KEYWORD = "name";
                    isClicked = true;
                    ChangMovieOrTvShow.setText("Search Movies");
                } else {
                    QUERY_TYPE = "movie";
                    MOVIE_NAME_KEYWORD = "title";
                    isClicked = false;
                    ChangMovieOrTvShow.setText("Search TV Show");
                }
            }
        });
        Log.d(TAG, "onClick: shaked");
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(eMovieName.getText().toString())) {
                        try {
                            MovieCoverList.clear();
                            MovieNameList.clear();
                            MovieOverViewList.clear();
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://api.themoviedb.org/3/search/"
                                            + QUERY_TYPE
                                            + "?api_key="
                                            + mContext.getString(R.string.The_Movie_DB_API_KEY)
                                            + "&query="
                                            + eMovieName.getText().toString()
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
                                        String result = jsonObject.getString("results");
                                        JSONArray resultArray = new JSONArray(result);
                                        MovieCoverList.clear();
                                        MovieNameList.clear();
                                        MovieOverViewList.clear();
                                        int limit = 0;
                                        if (resultArray.length() > MAX_NUMBER_OF_RESULTS) {
                                            limit = MAX_NUMBER_OF_RESULTS;
                                        } else {
                                            limit = resultArray.length();
                                        }
                                        for (int i = 0; i < limit; i++) {
                                            JSONObject insideArray = resultArray.getJSONObject(i);
                                            if (!MovieNameList.contains(insideArray.getString(MOVIE_NAME_KEYWORD))) {

                                                if (TextUtils.isEmpty(insideArray.getString(MOVIE_NAME_KEYWORD))) {
                                                    MovieNameList.add("N/A");
                                                } else {
                                                    MovieNameList.add(insideArray.getString(MOVIE_NAME_KEYWORD));

                                                }
                                                if (TextUtils.isEmpty(mContext.getString(R.string.MOVIE_COVER_CONSTANT_URL)
                                                        + insideArray.getString("poster_path"))) {
                                                    MovieCoverList.add("N/A");
                                                } else {
                                                    MovieCoverList.add(mContext.getString(R.string.MOVIE_COVER_CONSTANT_URL)
                                                            + insideArray.getString("poster_path"));
                                                }
//                                                Log.d(TAG, "onResponse: poster url "
//                                                        + mContext.getString(R.string.MOVIE_COVER_CONSTANT_URL)
//                                                        + insideArray.getString("poster_path"));
                                                if (TextUtils.isEmpty(insideArray.getString("overview"))) {
                                                    MovieOverViewList.add("N/A");
                                                } else {
                                                    MovieOverViewList.add(insideArray.getString("overview"));
                                                }
                                                // Log.d(TAG, "onResponse: overview " + insideArray.getString("overview"));
                                            }
                                        }
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieNameList));
//                                        Log.d(TAG, new StringBuilder().append("onResponse: trackNames ").append(MovieCoverList).toString());
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieOverViewList));
                                        Handler handler1 = new Handler(Looper.getMainLooper());
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mResultAdapter = new ResultMMBRecyclerViewAdapter(MovieNameList, MovieOverViewList, MovieCoverList,
                                                        getActivity(), isMusic, isMovie, isBook, onMMBResultsClickListener);
                                                rResults.setAdapter(mResultAdapter);
                                                mResultAdapter.notifyDataSetChanged();
                                                rResults.scheduleLayoutAnimation();
                                               // lvCircularZoom.stopAnim();
                                             //   lvCircularZoom.setVisibility(View.GONE);

                                            }
                                        }, 1000);
//
//
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                                }
                            });
                            // }
//                            }, 1000);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        eMovieName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged: ");
                //lvCircularZoom.setVisibility(View.VISIBLE);
               // lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MovieNameList.clear();
                MovieOverViewList.clear();
                MovieCoverList.clear();
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {

                   // lvCircularZoom.stopAnim();
                    //lvCircularZoom.setVisibility(GONE);
                }
                //Log.d(TAG, "afterTextChanged: ");
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void findMusic() {
        //Log.d(TAG, "onClick: shaked");
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(eTrackName.getText().toString())) {
                        try {
                            trackNames.clear();
                            trackArtist.clear();
                            trackCoversList.clear();
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://deezerdevs-deezer.p.rapidapi.com/search?q=" + eTrackName.getText().toString())
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
                                    final String data = response.body().string();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(data);
                                        String se = jsonObject.getString("data");
                                        JSONArray arr = new JSONArray(se);
                                        trackNames.clear();
                                        trackArtist.clear();
                                        trackCoversList.clear();
                                        int limit = 0;
                                        if (arr.length() > MAX_NUMBER_OF_RESULTS) {
                                            limit = MAX_NUMBER_OF_RESULTS;
                                        } else {
                                            limit = arr.length();
                                        }
                                        for (int i = 0; i < limit; i++) {
                                            JSONObject jsonPart = arr.getJSONObject(i);
                                            if (!trackNames.contains(jsonPart.getString("title"))) {
                                                if (TextUtils.isEmpty(jsonPart.getString("title"))) {
                                                    trackNames.add("N/A");
                                                } else {
                                                    trackNames.add(jsonPart.getString("title"));
                                                }
                                                JSONObject jsonObjectNew = new JSONObject(jsonPart.getString("artist"));
                                                if (TextUtils.isEmpty(jsonObjectNew.getString("name"))) {
                                                    trackArtist.add("N/A");
                                                } else {
                                                    trackArtist.add(jsonObjectNew.getString("name"));
                                                }
                                                JSONObject jsonPart3 = new JSONObject(jsonPart.getString("album"));
                                                if (TextUtils.isEmpty(jsonPart3.getString("cover_medium"))) {
                                                    trackCoversList.add("N/A");
                                                } else {
                                                    trackCoversList.add(jsonPart3.getString("cover_medium"));
                                                }
                                            }
                                            // Log.d(TAG, "onResponse: Track Name " + jsonPart.getString("title"));
                                        }
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", trackNames));
//                                        Log.d(TAG, new StringBuilder().append("onResponse: trackNames ").append(trackArtist).toString());
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", trackCoversList));
                                        Handler handler1 = new Handler(Looper.getMainLooper());
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mResultAdapter = new ResultMMBRecyclerViewAdapter(trackNames, trackArtist, trackCoversList, getActivity(),
                                                        isMusic, isMovie, isBook, onMMBResultsClickListener);
                                                rResults.setAdapter(mResultAdapter);
                                                mResultAdapter.notifyDataSetChanged();
                                                rResults.scheduleLayoutAnimation();
                                                //lvCircularZoom.stopAnim();
                                                //lvCircularZoom.setVisibility(View.GONE);

                                            }
                                        }, 1000);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        eTrackName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
                //lvCircularZoom.setVisibility(View.VISIBLE);
                //lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                trackNames.clear();
                trackArtist.clear();
                trackCoversList.clear();
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {

                    //lvCircularZoom.stopAnim();
                   // lvCircularZoom.setVisibility(GONE);
                }
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void SetJellyToggleButton(final String check) {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_is_private))
                .child(check);

        Log.d(TAG, "SetJellyToggleButton: check " + check);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "SetJellyToggleButton: snapshot value  " + snapshot.getValue().toString());
                if (!snapshot.getValue().toString().equals(mContext.getString(R.string.field_public))) {
                    UserChecked.setVisibility(VISIBLE);
                    UserUnChecked.setVisibility(INVISIBLE);
                } else {
                    UserChecked.setVisibility(INVISIBLE);
                    UserUnChecked.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SetDetailsAndPrivateInfo(final String fieldName) {

        UserUnChecked.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserChecked.setVisibility(VISIBLE);
                UserUnChecked.setVisibility(INVISIBLE);
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field_is_private))
                        .child(fieldName)
                        .setValue(mContext.getString(R.string.field_private));

                status = "private";
            }
        });
        UserChecked.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserChecked.setVisibility(INVISIBLE);
                UserUnChecked.setVisibility(VISIBLE);
                myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field_is_private))
                        .child(fieldName)
                        .setValue(mContext.getString(R.string.field_public));

                status = "public";

            }
        });

    }

    private void SetButtonAppearance(AppCompatButton Button1, AppCompatButton Button2, AppCompatButton Button3) {
        Button1.setTextColor(Color.BLACK);
        Button1.setBackgroundResource(R.drawable.custom_button_login);
        Button2.setTextColor(Color.WHITE);
        Button2.setBackgroundColor(Color.TRANSPARENT);
        Button3.setTextColor(Color.WHITE);
        Button3.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: clicked " + position);
        ArrayList<String> NameList = null;
        ArrayList<String> CreatorList = null;
        ArrayList<String> CoverList = null;
        if (isMovie) {
            NameList = MovieNameList;
            CreatorList = MovieOverViewList;
            CoverList = MovieCoverList;
        } else if (isBook) {
            NameList = BookNameList;
            CreatorList = BookAuthorList;
            CoverList = BookCoverList;
        } else if (isMusic) {
            NameList = trackNames;
            CreatorList = trackArtist;
            CoverList = trackCoversList;
        }

        if (mSNameList.size() < 5) {
            Log.d(TAG, String.format("onItemClick: mSNamlist%s", mSNameList));
            if (NameList != null) {
                Log.d(TAG, "onItemClick: 2");

                if (!mSNameList.contains(NameList.get(position))) {
                    Log.d(TAG, "onItemClick: 3");

                    mSNameList.add(NameList.get(position));
                    viewSelectedItems(true);
                } else {

                    Toast.makeText(mContext, "Item Already Added!", Toast.LENGTH_SHORT).show();
                }
            }
            if (CoverList != null && !mSCoverList.contains(CoverList.get(position))) {
                mSCoverList.add(CoverList.get(position));
            }
            if (CreatorList != null && !mSCreatorList.contains(CreatorList.get(position))) {
                mSCreatorList.add(CreatorList.get(position));
            }
        } else {
            Toast.makeText(mContext, "You can only select a maximum of 5 items.", Toast.LENGTH_SHORT).show();
        }
    }
}
