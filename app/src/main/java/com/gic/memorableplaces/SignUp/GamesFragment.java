package com.gic.memorableplaces.SignUp;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.Adapters.GameResultsRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.GamesRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class GamesFragment extends Fragment implements GameResultsRecyclerViewAdapter.OnGameSelectedListener {
    private static final String TAG = "SocietiesFragment";
    private long last_text_edit = 0;
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;

    private GameResultsRecyclerViewAdapter grrva;
    private GameResultsRecyclerViewAdapter.OnGameSelectedListener ogsl;
    private ArrayList<String> alsPlatforms, alsSelectedList, alsMatchSelectedList, alsGamesList;

    private ImageView IV_PRIVACY, IV_SEARCH_ICON;
    private ImageView IV_TICK;
    private ImageView IV_BOX;
    private ImageView IV_SWITCH_INPUT;

    private MotionLayout ML, ML_2;
    private EditText ET_SEARCH;
    private TextView TV_NO_SELECTION;
    private RecyclerView RV_SELECTED;
    private GifImageView GIV_LOADING;
    private AnimatedRecyclerView RV_ALL;
    private GamesRecyclerViewAdapter mSelectedAdapter;

    private ZoomRecyclerLayout mLayoutManager;
    private Handler handler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_fd, container, false);
        mContext = getActivity();
        handler = new Handler(Looper.getMainLooper());
        alsPlatforms = new ArrayList<>();
        alsSelectedList = new ArrayList<>(5);
        alsMatchSelectedList = new ArrayList<>(5);
        alsGamesList = new ArrayList<>();
        ogsl = this;
        ML = view.findViewById(R.id.ML_MAIN_VG);
        ML_2 = view.findViewById(R.id.ML_2_VG);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_VG);
        RV_ALL = view.findViewById(R.id.RV_ALL_VG);
        RV_SELECTED = view.findViewById(R.id.RV_SELECTED_VG);
        ET_SEARCH = view.findViewById(R.id.ET_SEARCH_VG);
        IV_SEARCH_ICON = view.findViewById(R.id.IV_SEARCH_ICON);
        AppCompatButton ACB_SHOW_SELECTED = view.findViewById(R.id.ACB_SHOW_SELECTED_VG);
        TV_NO_SELECTION = view.findViewById(R.id.TV_NO_SELECTION_VG);
        ImageView IV_QUES = view.findViewById(R.id.IV_QUES_VG);
        GIV_LOADING = view.findViewById(R.id.GIV_LOADING_VG);


        if (getArguments() != null) {
            alsSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_video_games)));
            alsMatchSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_match_video_games)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }


        Log.d(TAG, "SetBackButton: alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: alsMatchSelectList: " + alsMatchSelectedList);
        if (alsSelectedList.contains("N/A")) {
            alsSelectedList.clear();
            TV_NO_SELECTION.setVisibility(View.VISIBLE);
        } else {
            GIV_LOADING.setVisibility(View.VISIBLE);
            TV_NO_SELECTION.setVisibility(View.GONE);
            for (int i = 0; i < alsSelectedList.size(); i++)
                GetDetailsFromName(alsSelectedList.get(i), true, (i + 1) == alsSelectedList.size(), i);
        }
        if (alsMatchSelectedList.contains("N/A")) {
            alsMatchSelectedList.clear();
        } else {
            for (int i = 0; i < alsMatchSelectedList.size(); i++)
                GetDetailsFromName(alsMatchSelectedList.get(i), false, (i + 1) == alsMatchSelectedList.size(), i);
        }

        SetTypeFaces(view);
        GetGamesListFromAPI();
        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        SameDetailBox(view);
        SwitchInput(view);
        SetBackButton(view);

        ShowSpotlights(IV_SWITCH_INPUT);


        ACB_SHOW_SELECTED.setOnClickListener(v -> {
            ML_2.setTransition(R.id.TRANS_VG);
            if (ML_2.getProgress() == 1.0)
                ML_2.transitionToStart();

            if (isSwitched) {
                if (alsMatchSelectedList.size() == 1 && alsMatchSelectedList.get(0).equals("N/A")) {
                    TV_NO_SELECTION.setVisibility(View.INVISIBLE);
                } else {
                    TV_NO_SELECTION.setVisibility(View.VISIBLE);
                }
            } else {
                if (alsSelectedList.size() == 1 && alsSelectedList.get(0).equals("N/A")) {
                    TV_NO_SELECTION.setVisibility(View.INVISIBLE);
                } else {
                    TV_NO_SELECTION.setVisibility(View.VISIBLE);
                }
            }
        });

        IV_QUES.setOnClickListener(v -> {
            if (isSwitched) {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 video games, they will be added to the profile of your ideal match.", 0, v);

            } else {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 video games that you like, so that other users can use them as a filter.", 0, v);

            }
        });


        return view;
    }

    private void ShowSpotlights(ImageView IV_SWITCH_INPUT) {
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
        spotlightConfig.setLineAndArcColor(Color.parseColor("#FFFFD6A5"));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);

        PreferencesManager preferencesManager = new PreferencesManager(mContext);


        SpotlightView.Builder SB_SAME_DETAIL = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Copy Detail")
                .subHeadingTvText("Click this box to copy the detail selected on your page.")
                .target(IV_BOX)
                .usageId("IV_BOX")
                .setListener(spotlightViewId -> {
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        SpotlightView.Builder SB_SEARCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Search Games")
                .subHeadingTvText("Enter the name of the game her to make a search & select option from the list that appears.")
                .target(IV_SEARCH_ICON)
                .usageId("SB_SEARCH")
                .setListener(spotlightViewId -> {
                    SB_SAME_DETAIL.show();
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });


        if (!preferencesManager.isDisplayed("SWITCH")) {

            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {
                        SB_SEARCH.show();
                        ET_SEARCH.setText("Grand theft auto");
                        Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                    });

            new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Privacy Button")
                    .subHeadingTvText("This button can help you lock this detail on your profile. If Locked, it will only be visible to those who you swipe right.")
                    .target(IV_PRIVACY)
                    .usageId("PRIVACY")
                    .setListener(spotlightViewId -> {
                        SB_SWITCH.show();
                        Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                    }).show();

        } else if (!preferencesManager.isDisplayed("SB_SEARCH")) {
            ET_SEARCH.setText("Grand theft auto");
            SB_SEARCH.show();
        }
    }

    private void GetGamesListFromAPI() {
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(ET_SEARCH.getText().toString())) {
                        try {
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://api.rawg.io/api/games"
                                            + "?key=" + mContext.getString(R.string.rawg_games_api_key)
                                            + "&search=" + ET_SEARCH.getText().toString()
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
                                        requireActivity().runOnUiThread(() -> {
                                            alsGamesList.clear();
                                            alsPlatforms.clear();
                                            ML_2.setTransition(R.id.TRANS_VG);
                                            if (ML_2.getProgress() == 0.0)
                                                ML_2.transitionToEnd();
                                            TV_NO_SELECTION.setVisibility(View.INVISIBLE);

                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(data);
                                                String result = jsonObject.getString("results");
                                                JSONArray resultArray = new JSONArray(result);

                                                int limit = 0;
                                                limit = Math.min(resultArray.length(), 10);
                                                for (int i = 0; i < limit; i++) {
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    JSONObject jsonPart = resultArray.getJSONObject(i);

                                                    if (!TextUtils.isEmpty(jsonPart.getString("slug"))) {
                                                        stringBuilder.append(jsonPart.getString("name"));
                                                        stringBuilder.append("$1$");
                                                        if (!TextUtils.isEmpty(jsonPart.getString("background_image")))
                                                            stringBuilder.append(jsonPart.getString("background_image"));
                                                        else
                                                            stringBuilder.append("N/A");
                                                        stringBuilder.append("$2$");
                                                        if (!TextUtils.isEmpty(jsonPart.getString("background_image")))
                                                            stringBuilder.append(jsonPart.getString("rating"));
                                                        else
                                                            stringBuilder.append("N/A");

                                                        stringBuilder.append("$3$");
                                                        String Platforms = jsonPart.getString("platforms");
                                                        JSONArray PlatformsArray = new JSONArray(Platforms);
                                                        StringBuilder platforms = new StringBuilder();
                                                        if (PlatformsArray.length() > 0) {
                                                            for (int j = 0; j < PlatformsArray.length(); j++) {
                                                                JSONObject PlatformsObject = PlatformsArray.getJSONObject(j);
                                                                String insidePlatforms = PlatformsObject.getString("platform");
                                                                JSONObject insidePlatformsObject = new JSONObject(insidePlatforms);
                                                                platforms.append(insidePlatformsObject.getString("name"));
                                                                if ((j + 1) != PlatformsArray.length()) {
                                                                    platforms.append(" , ");
                                                                }
                                                            }
                                                        } else {
                                                            platforms.append("N/A");
                                                        }

                                                        stringBuilder.append(platforms.toString());
                                                        stringBuilder.append("$4$");
                                                        stringBuilder.append(jsonPart.getString("slug"));
                                                        alsGamesList.add(stringBuilder.toString());
                                                    }

                                                }

                                                Log.d(TAG, "run: alsGamesList: " + alsGamesList);
                                                Log.d(TAG, "run: alsPlatforms: " + alsPlatforms);

                                                GIV_LOADING.setVisibility(View.GONE);
                                                if (isSwitched) {

                                                    grrva = new GameResultsRecyclerViewAdapter(alsGamesList, "#212121", true,
                                                            mContext, ogsl);
                                                } else {

                                                    grrva = new GameResultsRecyclerViewAdapter(alsGamesList, "#FFFFFF", true,
                                                            mContext, ogsl);
                                                }
                                                RV_ALL.setItemAnimator(new DefaultItemAnimator());
                                                RV_ALL.setAdapter(grrva);
                                                grrva.notifyDataSetChanged();
                                                RV_ALL.scheduleLayoutAnimation();

                                            } catch (Exception e) {
                                                Log.d(TAG, "onResponse: error " + e.getMessage());
                                            }
                                        });
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

        ET_SEARCH.addTextChangedListener(new TextWatcher() {
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
                Log.d(TAG, "afterTextChanged: ");
                last_text_edit = System.currentTimeMillis();
                GIV_LOADING.setVisibility(View.VISIBLE);
                handler.postDelayed(input_finish_checker, delay);

            }
        });

    }


    private void GetDetailsFromName(String query, boolean isSelected, boolean isLast, int pos) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.rawg.io/api/games"
                        + "?key=" + mContext.getString(R.string.rawg_games_api_key)
                        + "&search=" + query
                        + "&search_exact=true"
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


                    try {
                        JSONObject jsonObject = null;

                        jsonObject = new JSONObject(data);
                        String result = jsonObject.getString("results");
                        JSONArray resultArray = new JSONArray(result);
                        Log.d(TAG, String.format("onResponse: resultArray: %s", resultArray));
                        for (int i = 0; i < 1; i++) {
                            StringBuilder stringBuilder = new StringBuilder();
                            JSONObject jsonPart = resultArray.getJSONObject(i);

                            if (!TextUtils.isEmpty(jsonPart.getString("name"))) {
                                stringBuilder.append(jsonPart.getString("name"));
                                stringBuilder.append("$1$");
                                if (!TextUtils.isEmpty(jsonPart.getString("background_image")))
                                    stringBuilder.append(jsonPart.getString("background_image"));
                                else
                                    stringBuilder.append("N/A");
                                stringBuilder.append("$2$");
                                if (!TextUtils.isEmpty(jsonPart.getString("background_image")))
                                    stringBuilder.append(jsonPart.getString("rating"));
                                else
                                    stringBuilder.append("N/A");

                                stringBuilder.append("$3$");
                                String Platforms = jsonPart.getString("platforms");
                                JSONArray PlatformsArray = new JSONArray(Platforms);
                                StringBuilder platforms = new StringBuilder();
                                if (PlatformsArray.length() > 0) {
                                    for (int j = 0; j < PlatformsArray.length(); j++) {
                                        JSONObject PlatformsObject = PlatformsArray.getJSONObject(j);
                                        String insidePlatforms = PlatformsObject.getString("platform");
                                        JSONObject insidePlatformsObject = new JSONObject(insidePlatforms);
                                        platforms.append(insidePlatformsObject.getString("name"));
                                        if ((j + 1) != PlatformsArray.length()) {
                                            platforms.append(" , ");
                                        }
                                    }
                                } else {
                                    platforms.append("N/A");
                                }
                                stringBuilder.append(platforms.toString());
                                stringBuilder.append("$4$");
                                stringBuilder.append(jsonPart.getString("slug"));
                                if (isSelected) {
                                    alsSelectedList.set(pos, stringBuilder.toString());
                                } else {
                                    alsMatchSelectedList.set(pos, stringBuilder.toString());
                                }
                            }

                        }

                        Log.d(TAG, "run: alsGamesList: " + alsGamesList);
                        Log.d(TAG, "run: alsPlatforms: " + alsPlatforms);


                        if (isSelected && isLast) {
                            handler.post(() -> {

                                Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
                                Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);

                                GIV_LOADING.setVisibility(View.GONE);
                                TV_NO_SELECTION.setVisibility(View.VISIBLE);
                                mLayoutManager = new ZoomRecyclerLayout(mContext);
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                mLayoutManager.setStackFromEnd(true);

                                RV_SELECTED.setLayoutManager(mLayoutManager);
                                mSelectedAdapter = new GamesRecyclerViewAdapter(alsSelectedList,true, getActivity(), position -> {
                                    Log.d(TAG, "onItemClick: position: " + position);
                                    alsSelectedList.remove(position);
                                    mSelectedAdapter.notifyItemRemoved(position);
                                });
                                RV_SELECTED.setAdapter(mSelectedAdapter);
                                mSelectedAdapter.notifyDataSetChanged();
                            });
                        }
                    } catch (Exception e) {
                        if (isLast && isSelected) {
                            GIV_LOADING.setVisibility(View.GONE);
                            TV_NO_SELECTION.setVisibility(View.VISIBLE);
                            mLayoutManager = new ZoomRecyclerLayout(mContext);
                            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            mLayoutManager.setStackFromEnd(true);

                            RV_SELECTED.setLayoutManager(mLayoutManager);
                            mSelectedAdapter = new GamesRecyclerViewAdapter(alsSelectedList, true,getActivity(), position -> {
                                Log.d(TAG, "onItemClick: position: " + position);
                                alsSelectedList.remove(position);
                                mSelectedAdapter.notifyItemRemoved(position);
                            });
                            RV_SELECTED.setAdapter(mSelectedAdapter);
                            mSelectedAdapter.notifyDataSetChanged();

                        }
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void SetTypeFaces(View view) {
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_VG);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");

        ATV_TITLE.setTypeface(tf);
    }

    private void SameDetailBox(View view) {

        IV_BOX = view.findViewById(R.id.IV_BOX_VG);
        IV_TICK = view.findViewById(R.id.IV_TICK_VG);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
                int count = alsMatchSelectedList.size();
                alsMatchSelectedList.clear();
                mSelectedAdapter.notifyItemRangeRemoved(0, count);
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                isSameDetail = true;
                alsMatchSelectedList.clear();
                alsMatchSelectedList.addAll(alsSelectedList);
                ML_2.setTransition(R.id.TRANS_VG);
                ML_2.transitionToStart();
                mLayoutManager = new ZoomRecyclerLayout(mContext);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mLayoutManager.setStackFromEnd(true);

                RV_SELECTED.setLayoutManager(mLayoutManager);
                mSelectedAdapter = new GamesRecyclerViewAdapter(alsMatchSelectedList, true,getActivity(), position -> {
                    Log.d(TAG, "onItemClick: position: " + position);
                    alsMatchSelectedList.remove(position);
                    mSelectedAdapter.notifyItemRemoved(position);
                });

                RV_SELECTED.setAdapter(mSelectedAdapter);
                mSelectedAdapter.notifyDataSetChanged();
            }

        });


    }

    private void SwitchInput(View view) {
        IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_VG);
        IV_SWITCH_INPUT.setOnClickListener(v -> {

            ML.setTransition(R.id.TRANS_SWITCH_VG);
            if (!isSwitched) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                isSwitched = true;
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.VISIBLE);
                } else {
                    IV_TICK.setVisibility(View.GONE);
                }

                ML_2.setTransition(R.id.TRANS_VG);
                ML_2.transitionToStart();
                mLayoutManager = new ZoomRecyclerLayout(mContext);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mLayoutManager.setStackFromEnd(true);

                RV_SELECTED.setLayoutManager(mLayoutManager);
                mSelectedAdapter = new GamesRecyclerViewAdapter(alsMatchSelectedList, true,getActivity(), position -> {
                    Log.d(TAG, "onItemClick: position: " + position);
                    alsMatchSelectedList.remove(position);
                    mSelectedAdapter.notifyItemRemoved(position);
                });

            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                isSwitched = false;
                ML_2.setTransition(R.id.TRANS_VG);
                ML_2.transitionToStart();
                mLayoutManager = new ZoomRecyclerLayout(mContext);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mLayoutManager.setStackFromEnd(true);

                RV_SELECTED.setLayoutManager(mLayoutManager);
                mSelectedAdapter = new GamesRecyclerViewAdapter(alsSelectedList,true, getActivity(), position -> {
                    Log.d(TAG, "onItemClick: position: " + position);
                    alsSelectedList.remove(position);
                    mSelectedAdapter.notifyItemRemoved(position);
                });

            }
            RV_SELECTED.setAdapter(mSelectedAdapter);
            mSelectedAdapter.notifyDataSetChanged();

        });


    }

    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_VG);
        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> temp = new ArrayList<>(5);
            if (alsSelectedList.isEmpty()) {
                temp.add("N/A");
            } else {
                for (String string : alsSelectedList) {
                    temp.add(string.substring(string.indexOf("$4$") + 3));
                }
            }
            bundle.putStringArrayList(mContext.getString(R.string.field_video_games), temp);
            ArrayList<String> MatchTemp = new ArrayList<>(5);
            if (alsMatchSelectedList.isEmpty()) {
                MatchTemp.add("N/A");
            } else {
                for (String string : alsMatchSelectedList) {
                    MatchTemp.add(string.substring(string.indexOf("$4$") + 3));
                }
            }
            bundle.putStringArrayList(mContext.getString(R.string.field_match_video_games), MatchTemp);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(GamesFragment.this).commit();
        });
    }

    private void SetPrivacyDialog() {
        Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.dialog_privacy_dialog);
        AppCompatButton ACB_LOCK = dialog.findViewById(R.id.ACB_LOCK_PD);
        AppCompatButton ACB_UNLOCK = dialog.findViewById(R.id.ACB_UNLOCK_PD);
        ImageView IV_PRIVACY_ICON = dialog.findViewById(R.id.IV_PRIVACY_ICON_PD);
        ChangeTheme(R.style.MyDetail, IV_PRIVACY_ICON);

        if (ML.getProgress() == 0.0)
            ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        else
            ChangeTheme(R.style.OtherDetail, IV_PRIVACY);

        ACB_LOCK.setOnClickListener(v -> {
            isLocked = true;
            if (ML.getProgress() == 0.0)
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
            else
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
            dialog.cancel();
        });
        ACB_UNLOCK.setOnClickListener(v -> {
            isLocked = false;
            if (ML.getProgress() == 0.0)
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
            else
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
            dialog.cancel();
        });

        IV_PRIVACY.setOnClickListener(v -> {
            dialog.show();
            if (isLocked) {
                ACB_LOCK.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7CB342")));
            } else {
                ACB_LOCK.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E65955")));
            }
            ChangeTheme(R.style.MyDetail, IV_PRIVACY_ICON);

        });

    }

    private void ChangeTheme(int Style, ImageView IV) {
        final ContextThemeWrapper wrapper = new ContextThemeWrapper(mContext, Style);

        Drawable drawable;
        if (isLocked)
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_locked_privacy, wrapper.getTheme());
        else
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_unlocked_privacy, wrapper.getTheme());


        IV.setImageDrawable(drawable);
    }

    private void ShowSelected(ArrayList<String> als, int position) {
        if (als.size() < 6) {
            if (!als.contains(alsGamesList.get(position))) {

                mLayoutManager = new ZoomRecyclerLayout(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mLayoutManager.setStackFromEnd(true);

                RV_SELECTED.setLayoutManager(mLayoutManager);
                if (isSwitched) {
                    alsMatchSelectedList.add(alsGamesList.get(position));
                    mSelectedAdapter = new GamesRecyclerViewAdapter(alsMatchSelectedList, true,getActivity(), new GamesRecyclerViewAdapter.OnGameRemovedListener() {
                        @Override
                        public void onItemClick(int position) {
                            Log.d(TAG, "onItemClick: position: " + position);
                            alsMatchSelectedList.remove(position);
                            mSelectedAdapter.notifyItemRemoved(position);
                        }
                    });
                } else {
                    alsSelectedList.add(alsGamesList.get(position));
                    mSelectedAdapter = new GamesRecyclerViewAdapter(alsSelectedList, true,getActivity(), new GamesRecyclerViewAdapter.OnGameRemovedListener() {
                        @Override
                        public void onItemClick(int position) {
                            alsSelectedList.remove(position);
                            mSelectedAdapter.notifyItemRemoved(position);
                        }
                    });

                }
                RV_SELECTED.setAdapter(mSelectedAdapter);
                mSelectedAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "You have already selected this game", Toast.LENGTH_SHORT).show();
            }
        } else {
//                    Snackbar.make(MainView, "You can only select a maximum of 5 Games!", Snackbar.LENGTH_SHORT);
            Toast.makeText(mContext, "You can only select a maximum of 5 Games!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onItemClick(int position) {
        ET_SEARCH.setText("");

        ML_2.setTransition(R.id.TRANS_VG);
        ML_2.transitionToStart();
        GIV_LOADING.setVisibility(View.GONE);
        if (isSwitched) {
            ShowSelected(alsMatchSelectedList, position);
        } else {
            ShowSelected(alsSelectedList, position);
        }

    }
}
