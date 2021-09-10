package com.gic.memorableplaces.SignUp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.GamePlatformRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.GameResultsRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.GamesRecyclerViewAdapter;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

import static android.widget.LinearLayout.GONE;
import static android.widget.LinearLayout.OnClickListener;
import static java.util.Objects.requireNonNull;

public class GamesFragment extends Fragment implements GameResultsRecyclerViewAdapter.OnResultsClickListener {
    private static final String TAG = "GamesFragment";
    private static final int MAX_SELECTED_ITEMS = 5;
    private Context mContext;
    private long last_text_edit = 0;
    private int MAX_NUMBER_OF_RESULTS = 10;
    private boolean FirstItemSelected = true;
    private boolean isClickedPrivate = false;
    private String isGamesPrivate, status = "public";

    private GameResultsRecyclerViewAdapter.OnResultsClickListener onResultsClickListener;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private ArrayList<String> GamesName;
    private ArrayList<String> GamesImgUrl;
    private ArrayList<String> GamesPlatform;
    private ArrayList<String> UserFavPlatform;
    private ArrayList<String> GamesRating;

    private ArrayList<String> SelectedGamesName;
    private ArrayList<String> SelectedGamesImgUrl;
    private ArrayList<String> SelectedGamesPlatform;
    private ArrayList<String> SelectedGamesRating;

    private ArrayList<String> Top5Platforms;

    private HashMap<String, String> keyList;
    private StringBuilder stringBuilder;

    private AutoCompleteTextView GamesSearchBar;
    private AppCompatButton closePage;
    private AutofitTextView RemoveGame;
    private ImageView privateDataNoticeImageView;
    private AppCompatButton Private;
    private TextView PRIVATE_DATA_NOTICE;

    private RecyclerView GamesRecyclerView;
    private ZoomRecyclerLayout mLayoutManager;
    private RecyclerView.Adapter mSelectedAdapter;

    private RecyclerView GamesResultsRecyclerView;
    private GameResultsRecyclerViewAdapter mResultsAdapter;

    private AlertDialog.Builder builder;
    private LayoutInflater inflaterDialog;
    private AlertDialog Alertdialog;
    private View vDialog;
    private View MainView;
  //  private LVCircularZoom lvCircularZoom;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainView = inflater.inflate(R.layout.dialog_layout_games, container, false);
        Log.d(TAG, "onCreateView: GamesFragment");

        mContext = getActivity();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        keyList = new HashMap<>();

        if (getArguments() != null) {
            isGamesPrivate = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_games)));
        }
        onResultsClickListener = this;
        UserFavPlatform = new ArrayList<>();
        SelectedGamesName = new ArrayList<>();
        SelectedGamesPlatform = new ArrayList<>();
        SelectedGamesRating = new ArrayList<>();
        SelectedGamesImgUrl = new ArrayList<>();
        Top5Platforms = new ArrayList<>();

        GamesSearchBar = MainView.findViewById(R.id.ZodiacSearchBar);
        GamesRecyclerView = MainView.findViewById(R.id.GamesRecyclerView);
        GamesResultsRecyclerView = MainView.findViewById(R.id.GamesResultsRecyclerView);
        closePage = MainView.findViewById(R.id.close_dialog);
        RemoveGame = MainView.findViewById(R.id.RemoveGame);
        Private = MainView.findViewById(R.id.PlatformButton);
        //lvCircularZoom = MainView.findViewById(R.id.Loading_dots);

        builder = new AlertDialog.Builder(getActivity());
        inflaterDialog = requireActivity().getLayoutInflater();

        mLayoutManager = new ZoomRecyclerLayout(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setStackFromEnd(true);
        GamesRecyclerView.setLayoutManager(mLayoutManager);
 //       lvCircularZoom.setVisibility(GONE);

        try {


            closePage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetTop5Platforms();
                    if (SelectedGamesName.size() > 0 && status.equals(mContext.getString(R.string.field_public))) {
                        for (int i = 0; i < SelectedGamesName.size(); i++) {
                            SetDetailForFilter(mContext.getString(R.string.field_games), SelectedGamesName.get(i), "", "members");
                            SetDetailForFilter(mContext.getString(R.string.field_games), SelectedGamesName.get(i), SelectedGamesImgUrl.get(i), mContext.getString(R.string.field_game_cover_url));
                            SetDetailForFilter(mContext.getString(R.string.field_games), SelectedGamesName.get(i), SelectedGamesPlatform.get(i), mContext.getString(R.string.field_game_platform));
                            SetDetailForFilter(mContext.getString(R.string.field_games), SelectedGamesName.get(i), SelectedGamesRating.get(i), mContext.getString(R.string.field_game_rating));
                        }
                    }
                    requireFragmentManager().beginTransaction().remove(GamesFragment.this).commit();
                }
            });

            GetGamesListFromAPI();
            PrivacyDialog();
            GetSelectedFromDatabase();
            DeleteSelectedGame();

        } catch (Exception e) {
            Log.d(TAG, "onCreateView: error  " + e.getMessage());
        }
        return MainView;
    }

    private void SetDetailForFilter(String Detail, String Value, String Value2, String key) {

        if (key.equals("members")) {
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(Value)
                    .child(key)
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(mContext.getString(R.string.field_user_id));
        } else {
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(Detail)
                    .child(Value)
                    .child(key)
                    .setValue(Value2);
        }

    }

    private void ClearDetails(String Detail, String Value, String key) {

        myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(Detail)
                .child(Value)
                .child(key)
                .child(mAuth.getCurrentUser().getUid())
                .removeValue();


    }

    private void ClearAllLists() {
        SelectedGamesImgUrl.clear();
        SelectedGamesName.clear();
        SelectedGamesRating.clear();
        SelectedGamesPlatform.clear();
    }

    private void GetSelectedFromDatabase() {

        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(mContext.getString(R.string.field_games));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot TrackChildren : dataSnapshot.getChildren()) {
                        if (SelectedGamesName.size() < MAX_SELECTED_ITEMS) {
                            if (Objects.equals(TrackChildren.getKey(), mContext.getString(R.string.field_game_name))
                                    && !SelectedGamesName.contains(TrackChildren.getValue().toString())) {
                                SelectedGamesName.add(TrackChildren.getValue().toString());
                            }
                            if (Objects.equals(TrackChildren.getKey(), mContext.getString(R.string.field_game_platform))) {
                                SelectedGamesPlatform.add(TrackChildren.getValue().toString());
                                if (!Top5Platforms.contains(TrackChildren.getValue().toString()) && Top5Platforms.size() < MAX_SELECTED_ITEMS) {
                                    Top5Platforms.add(TrackChildren.getValue().toString());
                                    Log.d(TAG, "onDataChange: Top5Platforms " + Top5Platforms);
                                }
                            }
                            if (Objects.equals(TrackChildren.getKey(), mContext.getString(R.string.field_game_rating))) {
                                SelectedGamesRating.add(TrackChildren.getValue().toString());
                            }
                            if (Objects.equals(TrackChildren.getKey(), mContext.getString(R.string.field_game_cover_url)) &&
                                    !SelectedGamesImgUrl.contains(TrackChildren.getValue().toString())) {
                                SelectedGamesImgUrl.add(TrackChildren.getValue().toString());
                            }
                        }
                    }

                }
                if (SelectedGamesName.size() > 0) {
                    for (int i = 0; i < SelectedGamesName.size(); i++) {
                        ClearDetails(mContext.getString(R.string.field_games), SelectedGamesName.get(i), "members");
                    }
                }
                Log.d(TAG, String.format("onDataChange: Selected Games Rating %s", SelectedGamesRating));
                mSelectedAdapter = new GamesRecyclerViewAdapter(SelectedGamesName, SelectedGamesPlatform,
                        SelectedGamesImgUrl, SelectedGamesRating, getActivity());
                GamesRecyclerView.setAdapter(mSelectedAdapter);
                mSelectedAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetTop5Platforms() {
        myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field))
                .child(mContext.getString(R.string.Top5Platform))
                .removeValue();
        for (int i = 0; i < Top5Platforms.size(); i++) {
            Log.d(TAG, String.format("SetTop5Platforms: Top5Platofrms %s", Top5Platforms));
            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(mContext.getString(R.string.Top5Platform))
                    .child(requireNonNull(myRef.push().getKey()))
                    .setValue(Top5Platforms.get(i));

        }
    }

    private void SetSelectedInDatabase(String field) {

        String Key = myRef.push().getKey();
        for (int i = 0; i < SelectedGamesName.size(); i++) {

            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(field)
                    .child(Key)
                    .child(mContext.getString(R.string.field_game_name))
                    .setValue(SelectedGamesName.get(i));
//            selName.add(ResultMMBRecyclerViewAdapter.mSNameList.get(i));

        }
        for (int i = 0; i < SelectedGamesPlatform.size(); i++) {

            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(field)
                    .child(Key)
                    .child(mContext.getString(R.string.field_game_platform))
                    .setValue(SelectedGamesPlatform.get(i));

        }
        for (int i = 0; i < SelectedGamesRating.size(); i++) {

            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(field)
                    .child(Key)
                    .child(mContext.getString(R.string.field_game_rating))
                    .setValue(SelectedGamesRating.get(i));

        }
        for (int i = 0; i < SelectedGamesImgUrl.size(); i++) {

            myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(field)
                    .child(Key)
                    .child(mContext.getString(R.string.field_game_cover_url))
                    .setValue(SelectedGamesImgUrl.get(i));

        }


    }

    private void PrivacyDialog() {

        Private.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field_is_private))
                        .child(mContext.getString(R.string.field_games));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        View vDialog = inflaterDialog.inflate(R.layout.layout_privacy, null);  // this line
                        builder.setView(vDialog);
                        Alertdialog = builder.create();
                        Alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        Alertdialog.show();
                        final JellyToggleButton jellyToggleButton = vDialog.findViewById(R.id.MakeDataPrivateSwitchMusic);
                        PRIVATE_DATA_NOTICE = vDialog.findViewById(R.id.private_warning);

                        jellyToggleButton.setChecked(!mContext.getString(R.string.field_public).equals(snapshot.getValue().toString()));
                        privateDataNoticeImageView = vDialog.findViewById(R.id.private_notice);
                        privateDataNoticeImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "onClick: privateDataNoticeImageView");

                                if (!isClickedPrivate) {
                                    Log.d(TAG, "onClick: privateDataNoticeImageView !isClickedPrivate");
                                    PRIVATE_DATA_NOTICE.setVisibility(View.VISIBLE);
                                    isClickedPrivate = true;
                                } else {
                                    Log.d(TAG, "onClick: privateDataNoticeImageView isClickedPrivate");
                                    PRIVATE_DATA_NOTICE.setVisibility(View.GONE);
                                    isClickedPrivate = false;
                                }
                            }
                        });
                        jellyToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (!isChecked) {
                                    myRef.child(mContext.getString(R.string.dbname_user_card))
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(mContext.getString(R.string.field_is_private))
                                            .child(mContext.getString(R.string.field_games))
                                            .setValue(mContext.getString(R.string.field_public));
                                    status = mContext.getString(R.string.field_public);

                                } else {

                                    myRef.child(mContext.getString(R.string.dbname_user_card))
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(mContext.getString(R.string.field_is_private))
                                            .child(mContext.getString(R.string.field_games))
                                            .setValue(mContext.getString(R.string.field_private));
                                    status = mContext.getString(R.string.field_private);


                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: destored");
        SetTop5Platforms();
    }

    private void GetGamesListFromAPI() {
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(GamesSearchBar.getText().toString())) {
                        try {
                            GamesName = new ArrayList<>();
                            GamesPlatform = new ArrayList<>();
                            GamesImgUrl = new ArrayList<>();
                            GamesRating = new ArrayList<>();
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://api.rawg.io/api/games"
                                            + "?key=" + mContext.getString(R.string.rawg_games_api_key)
                                            + "&search=" + GamesSearchBar.getText().toString()
                                            + "&ordering=-added")
                                    .get()
                                    .build();
                            //   .url("https://api.rawg.io/api/games?key=" + mContext.getString(R.string.rawg_games_api_key) + "&search=" + GamesSearchBar.getText().toString() + "&ordering=-added")


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
                                        final String data = requireNonNull(response.body()).string();
                                        // Log.d(TAG, "onResponse: data: "+ response.body().string());
                                        // Read data in the worker thread
                                        Thread thread = new Thread() {
                                            @Override
                                            public void run() {
                                                try {
                                                    synchronized (this) {
                                                        wait(1000);


                                                        requireActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                JSONObject jsonObject = null;
                                                                try {
                                                                    jsonObject = new JSONObject(data);
                                                                    String result = jsonObject.getString("results");
                                                                    JSONArray resultArray = new JSONArray(result);

                                                                    int limit = 0;
                                                                    if (resultArray.length() > MAX_NUMBER_OF_RESULTS) {
                                                                        limit = MAX_NUMBER_OF_RESULTS;
                                                                    } else {
                                                                        limit = resultArray.length();
                                                                    }
                                                                    for (int i = 0; i < limit; i++) {
                                                                        stringBuilder = new StringBuilder();
                                                                        JSONObject jsonPart = resultArray.getJSONObject(i);
                                                                        if (!GamesName.contains(jsonPart.getString("name"))) {

                                                                            GamesName.add(jsonPart.getString("name"));
                                                                            GamesImgUrl.add(jsonPart.getString("background_image"));
                                                                            GamesRating.add(jsonPart.getString("rating"));
                                                                            //     Log.d(TAG, "onResponse: GamesName " + GamesName);
                                                                            //   Log.d(TAG, "onResponse: GamesImgUrl " + GamesImgUrl);
                                                                            //   Log.d(TAG, "onResponse: GamesRating " + GamesRating);

                                                                            String Platforms = jsonPart.getString("platforms");
                                                                            JSONArray PlatformsArray = new JSONArray(Platforms);

                                                                            for (int j = 0; j < PlatformsArray.length(); j++) {
                                                                                JSONObject PlatformsObject = PlatformsArray.getJSONObject(j);
                                                                                String insidePlatforms = PlatformsObject.getString("platform");
                                                                                JSONObject insidePlatformsObject = new JSONObject(insidePlatforms);
                                                                                stringBuilder.append(insidePlatformsObject.getString("name"));
                                                                                if ((j + 1) != PlatformsArray.length()) {
                                                                                    stringBuilder.append(" ; ");
                                                                                }
                                                                            }
                                                                            GamesPlatform.add(stringBuilder.toString());
                                                                            // Log.d(TAG, "onResponse: GamesPlatform " + GamesPlatform);

                                                                        }

                                                                    }
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieNameList));
//                                        Log.d(TAG, new StringBuilder().append("onResponse: trackNames ").append(MovieCoverList).toString());
//                                        Log.d(TAG, String.format("onResponse: trackNames %s", MovieOverViewList));

                                                                    mResultsAdapter = new GameResultsRecyclerViewAdapter(GamesName, GamesPlatform, GamesImgUrl, GamesRating,
                                                                            mContext, onResultsClickListener);
                                                                    GamesResultsRecyclerView.setAdapter(mResultsAdapter);
                                                                    mResultsAdapter.notifyDataSetChanged();
                                                                    GamesResultsRecyclerView.scheduleLayoutAnimation();
                                                                    CloseKeyBoard();
                                                                    RemoveGame.setText("Show Selected Games");
//                                                GamesAutoCompleteAdapter adapter = new GamesAutoCompleteAdapter(mContext, GamesName, GamesPlatform
//                                                        , GamesImgUrl, GamesRating);
//                                                GamesSearchBar.setThreshold(0);
//                                                GamesSearchBar.setAdapter(adapter);
//                                                GamesSearchBar.showDropDown();
//                                                if (GamesSearchBar.isPopupShowing()) {
//                                                    CloseKeyBoard();

                                                                   // lvCircularZoom.stopAnim();
                                                                 //   lvCircularZoom.setVisibility(View.GONE);
//


                                                                } catch (Exception e) {
                                                                    Log.d(TAG, "onResponse: error " + e.getMessage());
                                                                }
                                                            }
                                                        });

                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        thread.start();

                                        //    Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                                    }
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

        GamesSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
//                        lvCircularZoom.setVisibility(View.VISIBLE);
//                        lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                GamesImgUrl.clear();
//                GamesName.clear();
//                GamesPlatform.clear();


//                lvCircularZoom.setVisibility(View.VISIBLE);
//                lvCircularZoom.startAnim(500);
//                GamesRating.clear();
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
//                            lvCircularZoom.stopAnim();
//                            lvCircularZoom.setVisibility(GONE);
                }
                if (!TextUtils.isEmpty(GamesSearchBar.getText().toString())) {
                    GamesRecyclerView.setVisibility(GONE);
                    GamesResultsRecyclerView.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "afterTextChanged: ");
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);

            }
        });

    }

    private void DeleteSelectedGame() {
        RemoveGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RemoveGame.getText().toString().equals(mContext.getString(R.string.remove_this_game))) {
                    if (SelectedGamesName.isEmpty()) {
                        Toast.makeText(mContext, "You have not selected any game.", Toast.LENGTH_SHORT).show();
                    } else {

                        int position = mLayoutManager.findFirstVisibleItemPosition();
                        Log.d(TAG, "onClick: position " + position);


                        final String NameToBeDeleted = SelectedGamesName.get(position);
                        Log.d(TAG, "onClick: Name To be deleted " + NameToBeDeleted);
                        //   ClearAllLists();

                        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                                .child(mAuth.getCurrentUser().getUid())
                                .child(mContext.getString(R.string.field))
                                .child(mContext.getString(R.string.field_games));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    ArrayList<String> keySet = new ArrayList<>();
                                    keySet.add(dataSnapshot.getKey());
                                    for (DataSnapshot GameDetails : dataSnapshot.getChildren()) {
                                        if (GameDetails.getKey().equals(mContext.getString(R.string.field_game_name))) {
                                            keyList.put(GameDetails.getValue().toString(), keySet.get(0));
                                            Log.d(TAG, String.format("onDataChange: kelist %s", keyList));
                                        }
                                    }
                                }
                                String HeadingGame = keyList.get(NameToBeDeleted);

                                Log.d(TAG, "onDataChange: HeadingGame " + HeadingGame);
                                for (DataSnapshot GameNumber : snapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: GameNumber Key : " + GameNumber.getKey());
                                    if (GameNumber.getKey().equals(HeadingGame)) {
                                        for (DataSnapshot SelectedGameChildren : GameNumber.getChildren()) {
                                            if (SelectedGameChildren.getKey().equals(mContext.getString(R.string.field_game_name))) {
                                                SelectedGamesName.remove(SelectedGameChildren.getValue().toString());
                                            }
                                            if (SelectedGameChildren.getKey().equals(mContext.getString(R.string.field_game_rating))) {
                                                SelectedGamesRating.remove(SelectedGameChildren.getValue().toString());
                                            }
                                            if (SelectedGameChildren.getKey().equals(mContext.getString(R.string.field_game_platform))) {
                                                SelectedGamesPlatform.remove(SelectedGameChildren.getValue().toString());
                                            }
                                            if (SelectedGameChildren.getKey().equals(mContext.getString(R.string.field_game_cover_url))) {
                                                SelectedGamesImgUrl.remove(SelectedGameChildren.getValue().toString());
                                            }
                                            Log.d(TAG, "onDataChange: S name " + SelectedGamesName);
                                            Log.d(TAG, "onDataChange: S platform " + SelectedGamesPlatform);
                                            Log.d(TAG, "onDataChange: S rating " + SelectedGamesRating);
                                            Log.d(TAG, "onDataChange: S url " + SelectedGamesImgUrl);
                                        }
                                    }
                                }
                                assert HeadingGame != null;
                                myRef.child(mContext.getString(R.string.dbname_user_card))
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.field))
                                        .child(mContext.getString(R.string.field_games))
                                        .child(HeadingGame)
                                        .removeValue();
                                if (snapshot.getChildrenCount() == 1) {
                                    myRef.child(mContext.getString(R.string.dbname_user_card))
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(mContext.getString(R.string.field))
                                            .child(mContext.getString(R.string.field_games))
                                            .setValue("N/A");
                                    myRef.child(mContext.getString(R.string.dbname_user_card))
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(mContext.getString(R.string.field))
                                            .child(mContext.getString(R.string.Top5Platform))
                                            .setValue("N/A");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ClearAllLists();
                        mSelectedAdapter.notifyDataSetChanged();
                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GetSelectedFromDatabase();
                            }
                        }, 1000);

                    }
                } else {
                    RemoveGame.setText(mContext.getString(R.string.remove_this_game));
                    ClearAllLists();
                    GetSelectedFromDatabase();
                    GamesSearchBar.setText("");
                    //lvCircularZoom.setVisibility(GONE);

                    GamesRecyclerView.setVisibility(View.VISIBLE);
                    GamesResultsRecyclerView.setVisibility(GONE);

                }
            }
        });
    }

    private void CloseKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    @Override
    public void onItemClick(final int position) {
        GamesSearchBar.setText("");
        Log.d(TAG, String.format("onItemClick: SelectedGamesName %s", SelectedGamesName));
        Log.d(TAG, String.format("onItemClick: SelectedGamesName size %s", SelectedGamesName.size()));
        if (SelectedGamesName.size() < MAX_SELECTED_ITEMS) {
            if (!SelectedGamesName.contains(GamesName.get(position))) {
                View vDialog = inflaterDialog.inflate(R.layout.dialog_layout_music_movies_books_selected, null);  // this line
                builder.setView(vDialog);
                Alertdialog = builder.create();
                Alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                Alertdialog.show();
                final TextView Title = vDialog.findViewById(R.id.SelectedOptionTV);
                final SwipeableRecyclerView rSelected = vDialog.findViewById(R.id.FINAL_MOVIES_BOOKS_MUSIC_RV);
                final AppCompatButton CloseDialog = vDialog.findViewById(R.id.close_dialog);

                CloseDialog.setVisibility(GONE);
                Alertdialog.setCanceledOnTouchOutside(false);
                Alertdialog.setCancelable(false);
                rSelected.setLayoutManager(new LinearLayoutManager(mContext));
                Title.setText("Select your preferred platform");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(GamesPlatform.get(position));
                GamesPlatform.clear();
                String[] PlatformsArray = stringBuilder.toString().split(";");
                Collections.addAll(GamesPlatform, PlatformsArray);
                GamesPlatform.add("None");
                mSelectedAdapter = new GamePlatformRecyclerViewAdapter(GamesPlatform,
                        getActivity(), Alertdialog);
                rSelected.setAdapter(mSelectedAdapter);

                mSelectedAdapter.notifyDataSetChanged();

                Alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                                Log.d(TAG, "onDismiss: S before platform " + SelectedGamesPlatform);
//                                Log.d(TAG, "onDismiss: S before rating " + SelectedGamesRating);
//                                Log.d(TAG, "onDismiss: name " + GamesName.get(position));
//                                Log.d(TAG, "onDismiss: platform " + GamePlatformRecyclerViewAdapter.SelectedPlatform);
//                                Log.d(TAG, "onDismiss: rating " + GamesRating.get(position));
//                                Log.d(TAG, "onDismiss: url " + GamesImgUrl.get(position));
                        SelectedGamesName.add(GamesName.get(position));
                        SelectedGamesImgUrl.add(GamesImgUrl.get(position));
                        SelectedGamesRating.add(GamesRating.get(position));
                        SelectedGamesPlatform.add(GamePlatformRecyclerViewAdapter.SelectedPlatform);
                        if (!Top5Platforms.contains(GamePlatformRecyclerViewAdapter.SelectedPlatform) && Top5Platforms.size() < MAX_SELECTED_ITEMS) {
                            Top5Platforms.add(GamePlatformRecyclerViewAdapter.SelectedPlatform);
                        }
                        SelectedGamesPlatform.add(GamePlatformRecyclerViewAdapter.SelectedPlatform);

                        Log.d(TAG, String.format("onDismiss: Top5Plat %s", Top5Platforms));
                        Log.d(TAG, "onDismiss: S name " + SelectedGamesName);
                        Log.d(TAG, "onDismiss: S platform " + SelectedGamesPlatform);
                        Log.d(TAG, "onDismiss: S rating " + SelectedGamesRating);
                        Log.d(TAG, "onDismiss: S url " + SelectedGamesImgUrl);
                        mSelectedAdapter = new GamesRecyclerViewAdapter(SelectedGamesName, SelectedGamesPlatform,
                                SelectedGamesImgUrl, SelectedGamesRating, getActivity());
                        GamesRecyclerView.setAdapter(mSelectedAdapter);
                        mSelectedAdapter.notifyDataSetChanged();
                        SetSelectedInDatabase(mContext.getString(R.string.field_games));


                    }
                });
            } else {
                Toast.makeText(mContext, "You have already selected this game", Toast.LENGTH_SHORT).show();
            }
        } else {
//                    Snackbar.make(MainView, "You can only select a maximum of 5 Games!", Snackbar.LENGTH_SHORT);
            Toast.makeText(mContext, "You can only select a maximum of 5 Games!", Toast.LENGTH_SHORT).show();

        }


    }
}
