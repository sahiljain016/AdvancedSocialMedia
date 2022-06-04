package com.gic.memorableplaces.SignUp;

import static java.util.Objects.requireNonNull;

import android.app.AlertDialog;
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
import android.widget.RelativeLayout;
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
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class DreamDestinationFragment extends Fragment implements GameResultsRecyclerViewAdapter.OnGameSelectedListener {
    private static final String TAG = "DreamDestinationFragment";
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;
    private long last_text_edit = 0;

    private String sMyLoc, sMatchLoc, sMyFlagUrl, sMatchFlagUrl;
    private ArrayList<String> alsMatchDDLoc, alsDDLoc, alsPlacesList;
    private Handler handler;
    private GameResultsRecyclerViewAdapter grrva;
    private GamesRecyclerViewAdapter grva;
    private GameResultsRecyclerViewAdapter.OnGameSelectedListener ogsl;

    private ImageView IV_PRIVACY, IV_SWITCH_INPUT, IV_BOX, IV_MY_FLAG;
    private ImageFilterView IFV_TYPE;
    private AnimatedRecyclerView ARV;
    private GifImageView GIV_LOADING_MY;
    private EditText ET_SEARCH;
    private RecyclerView RV_SELECTED;
    private TextView TV_MY_CITY_COUNTRY;
    private MotionLayout ML, ML_MY_LOC;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd, container, false);
        mContext = getActivity();
        handler = new Handler(Looper.getMainLooper());

        sMyLoc = mContext.getString(R.string.not_available);
        sMatchLoc = mContext.getString(R.string.not_available);
        sMatchFlagUrl = mContext.getString(R.string.not_available);
        sMyFlagUrl = mContext.getString(R.string.not_available);

        alsMatchDDLoc = new ArrayList<>(5);
        alsDDLoc = new ArrayList<>(5);
        alsPlacesList = new ArrayList<>(10);
        ogsl = this;

        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_DD);

        IV_MY_FLAG = view.findViewById(R.id.IV_FLAG_DD);
        TV_MY_CITY_COUNTRY = view.findViewById(R.id.TV_CITY_COUNTRY_DD);

        IFV_TYPE = view.findViewById(R.id.IFV_TYPE_DD);
        ET_SEARCH = view.findViewById(R.id.ET_SEARCH_DD);
        GIV_LOADING_MY = view.findViewById(R.id.GIV_LOADING_MY_DD);
        ARV = view.findViewById(R.id.ARV_DD);
        RV_SELECTED = view.findViewById(R.id.RV_DD);

        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_DD);
        ML = view.findViewById(R.id.ML_MAIN_DD);
        ML_MY_LOC = view.findViewById(R.id.ML_MY_LOC_DD);

        if (getArguments() != null) {
            sMyLoc = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_my_loc)));
            sMatchLoc = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_match_loc)));
            alsMatchDDLoc.addAll(getArguments().getStringArrayList(requireActivity().getString(R.string.field_match_dd)));
            alsDDLoc.addAll(getArguments().getStringArrayList(requireActivity().getString(R.string.field_my_dd)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }

        if (!sMyLoc.equals(mContext.getString(R.string.not_available))) {
            ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
            ML_MY_LOC.transitionToEnd();
            String url = "https://flagcdn.com/256x192/" + (sMyLoc.substring((sMyLoc.length() - 3), (sMyLoc.length() - 1))).toLowerCase(Locale.ROOT) + ".png";
            sMyFlagUrl = url;
            TV_MY_CITY_COUNTRY.setText(sMyLoc);
            GlideImageLoader.loadImageWithOutTransition(mContext, url, IV_MY_FLAG);
        }
        if (!sMatchLoc.equals(mContext.getString(R.string.not_available))) {
            sMatchFlagUrl = "https://flagcdn.com/256x192/" + (sMatchLoc.substring((sMatchLoc.length() - 3), (sMatchLoc.length() - 1))).toLowerCase(Locale.ROOT) + ".png";
        }

        if (alsMatchDDLoc.size() < 5) {
            for (int i = alsMatchDDLoc.size(); i < 5; i++) {
                alsMatchDDLoc.add(mContext.getString(R.string.not_available));
            }
        }
        if (alsDDLoc.size() < 5) {
            for (int i = alsDDLoc.size(); i < 5; i++) {
                alsDDLoc.add(mContext.getString(R.string.not_available));
            }
        }

        SetSearchFromApi();
        SetBackButton(view);
        SetSameAndSwitch(view);
        ShowSpotlights(IV_SWITCH_INPUT, view);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        ATV_TITLE.setTypeface(tf);
        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);

        IFV_TYPE.setOnClickListener(v -> {
            if (IFV_TYPE.getTag().equals("home")) {
                IFV_TYPE.setImageResource(R.drawable.ic_filter_places);
                IFV_TYPE.setTag("dd");
            } else {
                IFV_TYPE.setImageResource(R.drawable.ic_home_anim);
                IFV_TYPE.setTag("home");
            }
        });

        RelativeLayout RL_MY_LOC = view.findViewById(R.id.RL_MY_LOC_DD);
        ImageView IV_QUES = view.findViewById(R.id.IV_QUES_DD);

        RL_MY_LOC.setOnClickListener(v -> {
            DeleteSelectedHobby(TV_MY_CITY_COUNTRY.getText().toString());
        });

        IV_QUES.setOnClickListener(v -> {
            if (isSwitched) {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 dream cities & the hometown/location that you want your ideal match to visit, they will be added to the profile of your ideal match.", 0, v);

            } else {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 dream cities & your hometown/location, so that other users can use them as a filter.", 0, v);

            }
        });

        ZoomRecyclerLayout mLayoutManager = new ZoomRecyclerLayout(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        RV_SELECTED.setLayoutManager(mLayoutManager);
        SetRecyclerView();

        return view;
    }

    private void DeleteSelectedHobby(String Loc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove " + Loc)
                .setMessage("This will delete " + Loc + " as your selected hometown/location.")
                .setPositiveButton("OK",
                        (dialog, whichButton) -> {
                            if (isSwitched) {
                                sMatchLoc = mContext.getString(R.string.not_available);
                                sMatchFlagUrl = mContext.getString(R.string.not_available);
                            } else {
                                sMyLoc = mContext.getString(R.string.not_available);
                                sMyFlagUrl = mContext.getString(R.string.not_available);
                            }
                            ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
                            ML_MY_LOC.transitionToStart();
                        }
                )
                .setNegativeButton("Cancel",
                        (dialog, whichButton) -> dialog.dismiss()
                )
                .create();
        builder.show();
    }

    private void SetSearchFromApi() {
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                if (!TextUtils.isEmpty(ET_SEARCH.getText().toString())) {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("http://geodb-free-service.wirefreethought.com/v1/geo/cities?limit=10&offset=0"
                                        + "&namePrefix=" + ET_SEARCH.getText().toString())
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
                                    final String data = requireNonNull(response.body()).string();
                                    requireActivity().runOnUiThread(() -> {
                                        alsPlacesList.clear();
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(data);
                                            String result = jsonObject.getString("data");
                                            JSONArray resultArray = new JSONArray(result);

                                            int limit = 0;
                                            limit = Math.min(resultArray.length(), 10);
                                            for (int i = 0; i < limit; i++) {
                                                StringBuilder stringBuilder = new StringBuilder();
                                                JSONObject jsonPart = resultArray.getJSONObject(i);

                                                if (!TextUtils.isEmpty(jsonPart.getString("city"))) {
                                                    stringBuilder.append(jsonPart.getString("city"));
                                                    stringBuilder.append(", ");
                                                    if (!TextUtils.isEmpty(jsonPart.getString("country")))
                                                        stringBuilder.append(jsonPart.getString("country"));
                                                    else
                                                        stringBuilder.append("N/A");
                                                    stringBuilder.append(" (");
                                                    if (!TextUtils.isEmpty(jsonPart.getString("countryCode"))) {
                                                        stringBuilder.append(jsonPart.getString("countryCode"));
                                                        stringBuilder.append(")");
                                                    } else {
                                                        stringBuilder.append("N/A");
                                                    }
                                                    alsPlacesList.add(stringBuilder.toString());
                                                }

                                            }
                                            Log.d(TAG, "run: alsPlacesList: " + alsPlacesList);

                                            SetLoading("gone");
                                            if (isSwitched) {
                                                grrva = new GameResultsRecyclerViewAdapter(alsPlacesList, "#212121", false,
                                                        mContext, ogsl);
                                            } else {

                                                grrva = new GameResultsRecyclerViewAdapter(alsPlacesList, "#FFFFFF", false,
                                                        mContext, ogsl);
                                            }
                                            ARV.setItemAnimator(new DefaultItemAnimator());
                                            ARV.setAdapter(grrva);
                                            grrva.notifyDataSetChanged();
                                            ARV.scheduleLayoutAnimation();

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
        };

        ET_SEARCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetLoading("visible");
                ARV.setVisibility(View.VISIBLE);
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Log.d(TAG, "afterTextChanged: ");
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);

            }
        });
    }

    private void SetLoading(String action) {

        if (action.equals("gone")) {
            GIV_LOADING_MY.setVisibility(View.GONE);
        } else {
            GIV_LOADING_MY.setVisibility(View.VISIBLE);
        }
    }

    private void SetRecyclerView() {
        if (isSwitched) {
            grva = new GamesRecyclerViewAdapter(alsMatchDDLoc, false, getActivity(), position -> {
                Log.d(TAG, "onItemClick: position: " + position);
                if (!alsMatchDDLoc.get(position).equals(mContext.getString(R.string.not_available))) {
                    alsMatchDDLoc.set(position, mContext.getString(R.string.not_available));
                    grva.notifyItemChanged(position);
                } else {
                    Toast.makeText(mContext, "Place already empty!", Toast.LENGTH_SHORT).show();
                }
            });
            RV_SELECTED.setAdapter(grva);
            grva.notifyItemRangeInserted(0, alsMatchDDLoc.size());
        } else {
            grva = new GamesRecyclerViewAdapter(alsDDLoc, false, getActivity(), position -> {
                Log.d(TAG, "onItemClick: alsDDLoc before: " + alsDDLoc);
                if (!alsDDLoc.get(position).equals(mContext.getString(R.string.not_available))) {
                    alsDDLoc.set(position, mContext.getString(R.string.not_available));
                    Log.d(TAG, "onItemClick: alsDDLoc after: " + alsDDLoc);
                    grva.notifyItemRangeChanged(0, alsDDLoc.size());
                } else {
                    Toast.makeText(mContext, "Place already empty!", Toast.LENGTH_SHORT).show();
                }
            });
            RV_SELECTED.setAdapter(grva);
            grva.notifyItemRangeInserted(0, alsDDLoc.size());
        }

    }


    private void SetSameAndSwitch(View view) {
        IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_DD);
        IV_BOX = view.findViewById(R.id.IV_BOX_DD);
        ImageView IV_TICK = view.findViewById(R.id.IV_TICK_DD);
        IV_BOX.setOnClickListener(v1 -> {
            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                if (!sMyLoc.equals(mContext.getString(R.string.not_available))) {
                    sMatchLoc = sMyLoc;
                    sMatchFlagUrl = sMyFlagUrl;
                    ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
                    if (ML_MY_LOC.getProgress() != 1.0) {
                        ML_MY_LOC.transitionToEnd();
                    }
                    TV_MY_CITY_COUNTRY.setText(sMatchLoc);
                    GlideImageLoader.loadImageWithOutTransition(mContext, sMatchFlagUrl, IV_MY_FLAG);
                }else{
                    sMatchFlagUrl = mContext.getString(R.string.not_available);
                    sMatchLoc = mContext.getString(R.string.not_available);
                    ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
                    if (ML_MY_LOC.getProgress() != 0.0) {
                        ML_MY_LOC.transitionToStart();
                    }
                }
                alsMatchDDLoc.clear();
                alsMatchDDLoc.addAll(alsDDLoc);
                grva.notifyItemRangeChanged(0, alsMatchDDLoc.size());

                isSameDetail = true;

            }
        });

        IV_SWITCH_INPUT.setOnClickListener(v -> {

            ML.setTransition(R.id.TRANS_SWITCH_DD);

            if (!isSwitched) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                ARV.setBackgroundColor(Color.parseColor("#212121"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                isSwitched = true;

                ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
                if (sMatchLoc.equals(mContext.getString(R.string.not_available))) {
                    if (ML_MY_LOC.getProgress() != 0.0) {
                        ML_MY_LOC.transitionToStart();
                    }
                } else {
                    if (ML_MY_LOC.getProgress() != 1.0) {
                        ML_MY_LOC.transitionToEnd();
                    }
                    TV_MY_CITY_COUNTRY.setText(sMatchLoc);
                    GlideImageLoader.loadImageWithOutTransition(mContext, sMatchFlagUrl, IV_MY_FLAG);
                }

            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ARV.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                isSwitched = false;
                ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);
                if (sMyLoc.equals(mContext.getString(R.string.not_available))) {
                    if (ML_MY_LOC.getProgress() != 0.0) {
                        ML_MY_LOC.transitionToStart();
                    }
                } else {
                    if (ML_MY_LOC.getProgress() != 1.0) {
                        ML_MY_LOC.transitionToEnd();
                        TV_MY_CITY_COUNTRY.setText(sMyLoc);
                        GlideImageLoader.loadImageWithOutTransition(mContext, sMyFlagUrl, IV_MY_FLAG);
                    }
                }
            }

            SetRecyclerView();
            Log.d(TAG, "SetMultiSelected: alsMatchedGender 3: " + alsMatchDDLoc);
        });


    }

    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_DD);

        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> Match_dd_temp = new ArrayList<>(5);
            ArrayList<String> My_dd_temp = new ArrayList<>(5);
            for (String string : alsMatchDDLoc) {
                if (!string.equals(mContext.getString(R.string.not_available))) {
                    Match_dd_temp.add(string);
                }
            }
            for (String string : alsDDLoc) {
                if (!string.equals(mContext.getString(R.string.not_available))) {
                    My_dd_temp.add(string);
                }
            }
            if (Match_dd_temp.size() == 0) {
                Match_dd_temp.add(mContext.getString(R.string.not_available));
            }
            if (My_dd_temp.size() == 0) {
                My_dd_temp.add(mContext.getString(R.string.not_available));
            }

            bundle.putString(mContext.getString(R.string.field_my_loc), sMyLoc);
            bundle.putString(mContext.getString(R.string.field_match_loc), sMatchLoc);
            bundle.putStringArrayList(mContext.getString(R.string.field_match_dd), Match_dd_temp);
            bundle.putStringArrayList(mContext.getString(R.string.field_my_dd), My_dd_temp);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(DreamDestinationFragment.this).commit();
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

    private void ShowSpotlights(ImageView IV_SWITCH_INPUT, View view) {
        ImageView IV_SEARCH_ICON = view.findViewById(R.id.IV_SEARCH_ICON);
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
                .headingTvText("Search Cities")
                .subHeadingTvText("Enter the name of a city & select the best option from the list that appears.")
                .target(IV_SEARCH_ICON)
                .usageId("SB_SEARCH")
                .setListener(spotlightViewId -> {
                    SB_SAME_DETAIL.show();
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        SpotlightView.Builder SB_TYPE = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Select Type")
                .subHeadingTvText("The house means that you are entering your location/hometown. Click on it to change to dream destinations!")
                .target(IFV_TYPE)
                .usageId("SB_TYPE")
                .setListener(spotlightViewId -> {
                    SB_SEARCH.show();
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        if (!preferencesManager.isDisplayed("SWITCH")) {
            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {

                        SB_TYPE.show();
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

        } else if (!preferencesManager.isDisplayed("SB_TYPE")) {

            SB_TYPE.show();
        }
    }

    @Override
    public void onItemClick(int position) {

        ARV.setVisibility(View.GONE);
        if (IFV_TYPE.getTag().equals("home")) {
            ML_MY_LOC.setTransition(R.id.TRANS_MY_LOC_DD);


            String name = alsPlacesList.get(position);
            String url = "https://flagcdn.com/256x192/" + (name.substring((name.length() - 3), (name.length() - 1))).toLowerCase(Locale.ROOT) + ".png";
            Log.d(TAG, "onItemClick: url  " + url);
            if (ML_MY_LOC.getProgress() != 1.0) {
                ML_MY_LOC.transitionToEnd();
            }
            if (isSwitched) {
                sMatchLoc = name;
                sMatchFlagUrl = url;
            } else {
                sMyLoc = name;
                sMyFlagUrl = url;
            }
            TV_MY_CITY_COUNTRY.setText(name);
            if (!url.equals(mContext.getString(R.string.not_available))) {
                GlideImageLoader.loadImageWithOutTransition(mContext, url, IV_MY_FLAG);
            }
        } else {
            int index;
            if (isSwitched) {
                index = alsMatchDDLoc.indexOf(mContext.getString(R.string.not_available));
            } else {
                index = alsDDLoc.indexOf(mContext.getString(R.string.not_available));
            }


            Log.d(TAG, "onItemClick: alsDDLoc: " + alsDDLoc);

            if (index == -1) {
                Toast.makeText(mContext, "You can only enter a max of 5 dream destinations!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onItemClick: position: " + position);
                if (isSwitched) {
                    alsMatchDDLoc.set(index, alsPlacesList.get(position));
                } else {
                    alsDDLoc.set(index, alsPlacesList.get(position));
                }
                grva.notifyItemChanged(index);
                RV_SELECTED.smoothScrollToPosition(index);

            }
        }
    }
}