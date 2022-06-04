package com.gic.memorableplaces.SignUp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.Adapters.SelectedSocietyRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.SocietiesRecyclerViewAdapter;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.android.material.snackbar.Snackbar;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class SocietiesFragment extends Fragment implements SocietiesRecyclerViewAdapter.OnSocietyClicked {
    private static final String TAG = "SocietiesFragment";
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;

    private ArrayList<String> als, alsSelectedList, alsMatchSelectedList, alsSocietyNames;

    private ImageView IV_PRIVACY, IV_SWITCH_INPUT;
    private ImageView IV_TICK;
    private ImageView IV_BOX;
    private MotionLayout ML;
    private AutofitTextView ATV_VIEW;
    private View view;
    private RecyclerView RV;
    private www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout mLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_societies_in_college, container, false);
        mContext = getActivity();
        als = new ArrayList<>();
        alsSelectedList = new ArrayList<>(5);
        alsMatchSelectedList = new ArrayList<>(5);
        alsSocietyNames = new ArrayList<>(40);

        ML = view.findViewById(R.id.ML_MAIN_SIC);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_SIC);

        ATV_VIEW = view.findViewById(R.id.ATV_MY_SOCIETY);


        if (getArguments() != null) {
            alsSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_society_in_college)));
            alsMatchSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_match_society_in_college)));

            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }


        Log.d(TAG, "SetBackButton: alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: alsMatchSelectList: " + alsMatchSelectedList);
        if (alsSelectedList.contains("N/A")) {
            alsSelectedList.clear();
        }
        if (alsMatchSelectedList.contains("N/A")) {
            alsMatchSelectedList.clear();
        }

        Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);


        SetSocieties(view);
        SetTypeFaces(view);

        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        SameDetailBox(view);
        SwitchInput(view);
        SetBackButton(view);
        ShowSpotlights();
        ATV_VIEW.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            LayoutInflater inflaterDialog;
            builder = new AlertDialog.Builder(getActivity());
            inflaterDialog = requireActivity().getLayoutInflater();

            final View view = inflaterDialog.inflate(R.layout.dialog_layout_music_movies_books_selected, null);  // this line
            builder.setView(view);
            final AlertDialog SelectedItemsAlertDialog = builder.create();
            SelectedItemsAlertDialog.show();
            final TextView Title = view.findViewById(R.id.SelectedOptionTV);
            final SwipeableRecyclerView rSelected = view.findViewById(R.id.FINAL_MOVIES_BOOKS_MUSIC_RV);
            rSelected.setLayoutManager(new LinearLayoutManager(mContext));
            final AppCompatButton ConfirmButton = view.findViewById(R.id.close_dialog);

            Title.setText("Selected Societies:");

            DisplaySelectedSociety(rSelected);
            ConfirmButton.setOnClickListener(v1 -> SelectedItemsAlertDialog.dismiss());

        });


        return view;
    }

    private void ShowSpotlights() {
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


        if (!preferencesManager.isDisplayed("SWITCH")) {

            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {
                        SB_SAME_DETAIL.show();
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

        } else if (!preferencesManager.isDisplayed("SB_SAME_DETAIL")) {
            SB_SAME_DETAIL.show();
        }
    }

    private void DisplaySelectedSociety(SwipeableRecyclerView recyclerView) {
        Log.d(TAG, "DisplaySelectedSociety: alsSelectedList: " + alsSelectedList);
        SelectedSocietyRecyclerViewAdapter mSelectedAdapter;
        if (isSwitched)
            mSelectedAdapter = new SelectedSocietyRecyclerViewAdapter(alsMatchSelectedList, getActivity(), false, false, false, false);
        else
            mSelectedAdapter = new SelectedSocietyRecyclerViewAdapter(alsSelectedList, getActivity(), false, false, false, false);

        recyclerView.setAdapter(mSelectedAdapter);
        mSelectedAdapter.notifyDataSetChanged();
    }

    private void SetSocieties(View view) {

        SetArrayLists(mContext.getString(R.string.enactus_hasnraj), mContext.getString(R.string.enactus_hansraj_desp), R.drawable.enactus, "#000000");

        SetArrayLists(mContext.getString(R.string.nss_hansraj), mContext.getString(R.string.nss_hansraj_desp), R.drawable.nss_hansraj, "#000000");

        SetArrayLists(mContext.getString(R.string.ncc_hansraj), mContext.getString(R.string.ncc_hansraj_desp), R.drawable.ncc_hansraj, "#000000");

        SetArrayLists(mContext.getString(R.string.spic_macay), mContext.getString(R.string.spic_macay_desp), R.drawable.spicmacay, "#000000");

        SetArrayLists(mContext.getString(R.string.haritima), mContext.getString(R.string.haritima_desp), R.drawable.haritima, "#000000");

        SetArrayLists(mContext.getString(R.string.kavyanjali), mContext.getString(R.string.kavyanjali_desp), R.drawable.kavyanjali, "#000000");

        SetArrayLists(mContext.getString(R.string.pixels), mContext.getString(R.string.pixels_desp), R.drawable.pixels, "#000000");

        SetArrayLists(mContext.getString(R.string.swaranjali), mContext.getString(R.string.swaranjali_desp), R.drawable.swaranjali, "#000000");

        SetArrayLists(mContext.getString(R.string.illuminati), mContext.getString(R.string.illuminati_desp), R.drawable.illuminati, "#000000");

        SetArrayLists(mContext.getString(R.string.fic_hansraj), mContext.getString(R.string.fic_hansraj_desp), R.drawable.fic, "#000000");

        SetArrayLists(mContext.getString(R.string.ferc_hansraj), mContext.getString(R.string.ferc_hansraj_desp), R.drawable.ferc, "#000000");

        SetArrayLists(mContext.getString(R.string.osctraca), mContext.getString(R.string.osctraca_desp), R.drawable.ostraca, "#000000");

        SetArrayLists(mContext.getString(R.string.sga_hansraj), mContext.getString(R.string.sga_hansraj_desp), R.drawable.sga, "#000000");

        SetArrayLists(mContext.getString(R.string.eco_soc), mContext.getString(R.string.eco_soc_desp), R.drawable.eco_society, "#000000");

        SetArrayLists(mContext.getString(R.string.internship_cell), mContext.getString(R.string.internship_cell_desp), R.drawable.internship_cell, "#000000");

        SetArrayLists(mContext.getString(R.string.vision), mContext.getString(R.string.vision_desp), R.drawable.vision, "#000000");

        SetArrayLists(mContext.getString(R.string.neenv), mContext.getString(R.string.neenv_desp), R.drawable.neenv, "#000000");

        SetArrayLists(mContext.getString(R.string.kalakriti), mContext.getString(R.string.kalakriti_desp), R.drawable.kalakriti, "#000000");

        SetArrayLists(mContext.getString(R.string.gec), mContext.getString(R.string.gec_desp), R.drawable.gec, "#000000");

        SetArrayLists(mContext.getString(R.string.cdf), mContext.getString(R.string.cdf_desp), R.drawable.cdf, "#000000");

        SetArrayLists(mContext.getString(R.string.fashion_soc), mContext.getString(R.string.fashion_soc_desp), R.drawable.fashion_soc, "#000000");

        SetArrayLists(mContext.getString(R.string.rotaract), mContext.getString(R.string.rotaract_desp), R.drawable.rotract_club, "#000000");

        SetArrayLists(mContext.getString(R.string.culinary), mContext.getString(R.string.culinary_desp), R.drawable.culinary_arts, "#000000");

        SetArrayLists(mContext.getString(R.string.markit), mContext.getString(R.string.markit_desp), R.drawable.mark_it, "#000000");

        SetArrayLists(mContext.getString(R.string.north_east_cell), mContext.getString(R.string.north_east_cell_desp), R.drawable.north_east_cell, "#000000");

        SetArrayLists(mContext.getString(R.string.entrepreneurial_cell), mContext.getString(R.string.entrepreneurial_cell_desp), R.drawable.e_cell, "#000000");

        SetArrayLists(mContext.getString(R.string.placement_cell), mContext.getString(R.string.placement_cell_desp), R.drawable.placement_cell, "#000000");

        SetArrayLists(mContext.getString(R.string.terpsichorean), mContext.getString(R.string.terpsichorean_desp), R.drawable.tepsichorean, "#000000");

        SetArrayLists(mContext.getString(R.string.nishtha), mContext.getString(R.string.nishtha_desp), R.drawable.nishtha, "#000000");

        SetArrayLists(mContext.getString(R.string.sports_soc), mContext.getString(R.string.sports_soc_desp), R.drawable.sports_hansraj, "#000000");

        SetArrayLists(mContext.getString(R.string.sparc), mContext.getString(R.string.sparc_desp), R.drawable.society_sparc, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.tedx_hansraj), mContext.getString(R.string.tedx_hansraj_desp), R.drawable.tedx, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.markus), mContext.getString(R.string.markus_desp), R.drawable.markus, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.wdc), mContext.getString(R.string.wdc_desp), R.drawable.wdc, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.one_eighty_degree), mContext.getString(R.string.one_eighty_degree_desp), R.drawable._80_degree, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.the_dramatics_society), mContext.getString(R.string.the_dramatics_society_desp), R.drawable.society_dramsoc, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.gwc), mContext.getString(R.string.gwc_desp), R.drawable.gwc, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.comm_soc), mContext.getString(R.string.comm_soc_desp), R.drawable.comm_soc, "#FFFFFF");

        SetArrayLists(mContext.getString(R.string.debsoc), mContext.getString(R.string.debsoc_desp), R.drawable.debsoc, "#FFFFFF");

        RV = view.findViewById(R.id.rv_societies);

        mLayoutManager = new ZoomRecyclerLayout(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV.setHasFixedSize(true);
        RV.setLayoutManager(mLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(RV);

        InitiateRecyclerView();

        AutoCompleteTextView ACTV = view.findViewById(R.id.ACTV_SEARCH_SOCIETY);

        ACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_dropdown_item_1line, alsSocietyNames);
                ACTV.setThreshold(1);
                ACTV.setAdapter(adapter);
                ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
                            @Override
                            protected int getHorizontalSnapPreference() {
                                return super.getHorizontalSnapPreference();
                            }
                        };
                        String selection = ACTV.getText().toString();
                        int newPos = alsSocietyNames.indexOf(selection);

                        smoothScroller.setTargetPosition(newPos);
                        mLayoutManager.startSmoothScroll(smoothScroller);
                    }

                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void InitiateRecyclerView() {
        Log.d(TAG, "InitiateRecyclerView: alsMatchSelectedList changed: " + alsMatchSelectedList);
        SocietiesRecyclerViewAdapter srvaAdapter;
        if (isSwitched) {
            srvaAdapter = new SocietiesRecyclerViewAdapter(als, alsMatchSelectedList, this, mContext);
        } else {
            srvaAdapter = new SocietiesRecyclerViewAdapter(als, alsSelectedList, this, mContext);
        }
        RV.setAdapter(srvaAdapter);
        srvaAdapter.notifyDataSetChanged();
    }

    private void SetArrayLists(String name, String desp, int icon, String Color) {
        StringBuilder stringBuilder = new StringBuilder();

        alsSocietyNames.add(name);
        stringBuilder.append(name).append("$1*").append(desp).append("$2*").append(icon).append("$3*").append(Color);
        if (alsSelectedList.contains(name)) {
            alsSelectedList.set(alsSelectedList.indexOf(name), stringBuilder.toString());
        }
        if (alsMatchSelectedList.contains(name)) {
            alsMatchSelectedList.set(alsMatchSelectedList.indexOf(name), stringBuilder.toString());
        }
        als.add(stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
    }

    private void SetTypeFaces(View view) {
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_SIC);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setTypeface(tf);
        ATV_VIEW.setTypeface(cap);
    }

    private void SameDetailBox(View view) {

        IV_BOX = view.findViewById(R.id.IV_BOX_SIC);
        IV_TICK = view.findViewById(R.id.IV_TICK_SIC);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
                alsMatchSelectedList.clear();
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                isSameDetail = true;
                alsMatchSelectedList.clear();
                alsMatchSelectedList.addAll(alsSelectedList);
            }
            InitiateRecyclerView();

        });


    }

    private void SwitchInput(View view) {
        IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_SIC);
        TextView TV_NOTICE = view.findViewById(R.id.TV_SWITCH_NOTICE);
        TextView TV_SAME_DETAIL = view.findViewById(R.id.TV_SWITCH_SAME_DETAIL);
        IV_SWITCH_INPUT.setOnClickListener(v -> {

            if (!isSwitched) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                isSwitched = true;
                TV_NOTICE.setText("Please select up to 5 societies that you are a part of, these will be used to create the profile of your ideal match.");
                IV_BOX.setVisibility(View.VISIBLE);
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.VISIBLE);
                } else {
                    IV_TICK.setVisibility(View.GONE);
                }
                TV_SAME_DETAIL.setVisibility(View.VISIBLE);
                TV_NOTICE.setTextColor(Color.parseColor("#FFFFFF"));
                ATV_VIEW.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                isSwitched = false;
                TV_NOTICE.setText("Please select up to 5 societies that you are a part of, people will be available to filter you based upon these societies.");
                IV_BOX.setVisibility(View.GONE);
                IV_TICK.setVisibility(View.GONE);
                TV_SAME_DETAIL.setVisibility(View.GONE);
                TV_NOTICE.setTextColor(Color.parseColor("#000000"));
                ATV_VIEW.setTextColor(Color.parseColor("#000000"));
            }
            InitiateRecyclerView();

        });


    }

    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_SIC);
        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> temp = new ArrayList<>(5);
            if (alsSelectedList.isEmpty()) {
                temp.add("N/A");
            } else {
                for (String string : alsSelectedList) {
                    temp.add(string.substring(0, string.indexOf("$1*")));
                }
            }
            bundle.putStringArrayList(mContext.getString(R.string.field_society_in_college), temp);
            ArrayList<String> MatchTemp = new ArrayList<>(5);
            if (alsMatchSelectedList.isEmpty()) {
                MatchTemp.add("N/A");
            } else {
                for (String string : alsMatchSelectedList) {
                    MatchTemp.add(string.substring(0, string.indexOf("$1*")));
                }
            }
            bundle.putStringArrayList(mContext.getString(R.string.field_match_society_in_college), MatchTemp);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(SocietiesFragment.this).commit();
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

    @Override
    public void onItemClick(int position, String color, ImageView IV, TextView
            TV, ConstraintLayout CL) {

        Log.d(TAG, "onItemClick: CL Tag: " + CL.getTag());
        if (CL.getTag().equals("unselected")) {
            if (isSwitched) {
                if (alsMatchSelectedList.size() < 5) {
                    IV.setImageResource(R.drawable.ic_correct);
                    TV.setText("Added");
                    CL.setTag("selected");
                    alsMatchSelectedList.add(als.get(position));
                    Snackbar.make(view, "Society added to list.", Snackbar.LENGTH_SHORT).show();
                    if (color.equals("#FFFFFF")) {
                        IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                        TV.setTextColor(Color.parseColor("#000000"));
                        CL.setBackgroundResource(R.drawable.rounded_white_background);
                    } else {
                        IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        TV.setTextColor(Color.parseColor("#FFFFFF"));
                        CL.setBackgroundResource(R.drawable.rounded_white_background);
                        CL.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    }
                } else {
                    Toast.makeText(mContext, "You can add a maximum of 5 societies only.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (alsSelectedList.size() < 5) {
                    IV.setImageResource(R.drawable.ic_correct);
                    TV.setText("Added");
                    CL.setTag("selected");
                    alsSelectedList.add(als.get(position));
                    Snackbar.make(view, "Society added to list.", Snackbar.LENGTH_SHORT).show();
                    if (color.equals("#FFFFFF")) {
                        IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                        TV.setTextColor(Color.parseColor("#000000"));
                        CL.setBackgroundResource(R.drawable.rounded_white_background);
                    } else {
                        IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        TV.setTextColor(Color.parseColor("#FFFFFF"));
                        CL.setBackgroundResource(R.drawable.rounded_white_background);
                        CL.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    }
                } else {
                    Toast.makeText(mContext, "You can add a maximum of 5 societies only.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            IV.setImageResource(R.drawable.ic_plus_sign_white);
            TV.setText("Select");
            CL.setTag("unselected");
            if (isSwitched) {
                alsMatchSelectedList.remove(als.get(position));
            } else {
                alsSelectedList.remove(als.get(position));
            }
            Snackbar.make(view, "Society remove from list.", Snackbar.LENGTH_SHORT).show();
            if (color.equals("#FFFFFF")) {
                IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                TV.setTextColor(Color.parseColor("#FFFFFF"));

                CL.setBackgroundTintList(null);
                CL.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
            } else {
                IV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                TV.setTextColor(Color.parseColor("#000000"));

                CL.setBackgroundTintList(null);
                CL.setBackgroundResource(R.drawable.border_rounded_black_20sdp);
            }
        }
    }


}
