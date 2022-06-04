package com.gic.memorableplaces.SignUp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.Adapters.GeneralDetailsRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.jem.rubberpicker.RubberSeekBar;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class GeneralDetailsFragment extends Fragment implements GeneralDetailsRecyclerViewAdapter.OnGeneralDetailsListener {
    private static final String TAG = "GeneralDetailsFragment";
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false, isFirst = true;

    private String sFriend = "N/A", sDate = "N/A", sOther = "N/A";

    private ArrayList<Dialog> ald;
    private ArrayList<String> alsDetails, alsMatchDetails;

    private ImageView IV_PRIVACY;
    private ConstraintLayout CL_SWITCH;
    private TextView TV_SWITCH_NOTICE;
    private MotionLayout ML;
    private View V_WHITE_BLUR;
    private AnimatedRecyclerView arv;
    private RelativeLayout RL;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_details, container, false);
        mContext = getActivity();

        arv = view.findViewById(R.id.ARV_GD);
        RL = view.findViewById(R.id.RL_GD);
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_GD);
        ArrayList<StringBuilder> alsb = new ArrayList<>(11);
        alsDetails = new ArrayList<>(11);
        alsMatchDetails = new ArrayList<>(11);
        ald = new ArrayList<>(11);

        if (getArguments() != null) {
            if (getArguments().getStringArrayList(requireActivity().getString(R.string.field_general_details)) != null)
                alsDetails.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_general_details)));
            if (getArguments().getStringArrayList(requireActivity().getString(R.string.field_match_general_details)) != null)
                alsMatchDetails.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_match_general_details)));

            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        ATV_TITLE.setTypeface(tf);

        boolean isDetailsBreak = false, isMatchDetailsBreak = false;
        for (int i = 0; i < alsDetails.size(); i++) {

            if (!alsDetails.get(i).equals("N/A")) {

                Log.d(TAG, "onCreateView: aslDetails 0: " + alsDetails.get(i));
                isDetailsBreak = true;
                break;
            }

        }
        for (int i = 0; i < alsMatchDetails.size(); i++) {
            if (!alsMatchDetails.get(i).equals("N/A")) {
                isMatchDetailsBreak = true;
                break;
            }
        }
        if (!isDetailsBreak) {
            alsDetails.clear();
        }
        if (!isMatchDetailsBreak) {
            alsMatchDetails.clear();
        }
        for (int i = 0; i < 10; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            BuildStringBuilder(i, stringBuilder);
            alsb.add(stringBuilder);
            ald.add(null);
            if (!isMatchDetailsBreak) {
                alsMatchDetails.add("N/A");
            }
            if (!isDetailsBreak) {
                alsDetails.add("N/A");
            }
        }
        Log.d(TAG, "onCreateView: alsDetails: " + alsDetails);
        Log.d(TAG, "onCreateView: alsMatchDetails: " + alsMatchDetails);
        GeneralDetailsRecyclerViewAdapter gdra = new GeneralDetailsRecyclerViewAdapter(this, mContext, alsb);
        arv.setItemAnimator(new DefaultItemAnimator());
        arv.setAdapter(gdra);
        gdra.notifyDataSetChanged();
        arv.scheduleLayoutAnimation();

        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_GD);
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_GD);
        ImageView IV_BOX = view.findViewById(R.id.IV_BOX_GD);
        ImageView IV_TICK = view.findViewById(R.id.IV_TICK_GD);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_GD);
        ML = view.findViewById(R.id.ML_MAIN_GD);
        CL_SWITCH = view.findViewById(R.id.CL_SWITCH_GD);
        TV_SWITCH_NOTICE = view.findViewById(R.id.TV_SWITCH_NOTICE);
        V_WHITE_BLUR = view.findViewById(R.id.V_WHITE_BLUR);

        ShowSpotlights(IV_SWITCH_INPUT, IV_BOX);
        Log.d(TAG, "onCreateView: alsDetails: " + alsDetails);

        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                for (int i = 0; i < alsMatchDetails.size(); i++) {
                    alsMatchDetails.set(i, "N/A");
                }
                isSameDetail = false;
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                for (int i = 0; i < alsDetails.size(); i++) {
                    alsMatchDetails.set(i, alsDetails.get(i));
                }
                isSameDetail = true;
            }

        });

        IV_SWITCH_INPUT.setOnClickListener(v -> {
            RL.setVisibility(View.GONE);
            if (!isSwitched) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                CL_SWITCH.setVisibility(View.VISIBLE);
                V_WHITE_BLUR.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#212121")));
                TV_SWITCH_NOTICE.setText("These details will be added to the profile of your ideal mate which will be used to find your desired match & show you best matches!");
                TV_SWITCH_NOTICE.setTextColor(Color.parseColor("#FFFFFF"));
                isSwitched = true;

            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                CL_SWITCH.setVisibility(View.GONE);
                TV_SWITCH_NOTICE.setText("Fill these details and help the finding the right visitors to you profile!");
                V_WHITE_BLUR.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                TV_SWITCH_NOTICE.setTextColor(Color.parseColor("#000000"));
                isSwitched = false;

            }
        });
        IV_BACK.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(mContext.getString(R.string.field_general_details), alsDetails);
            bundle.putStringArrayList(mContext.getString(R.string.field_match_general_details), alsMatchDetails);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(GeneralDetailsFragment.this).commit();
        });

        return view;
    }

    private void ShowSpotlights(ImageView IV_SWITCH_INPUT, ImageView IV_BOX) {
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
        /*

        SpotlightView.Builder SB_PRONOUNS = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Enter Pronouns")
                .subHeadingTvText("Enter your pronouns here, it is optional.")
                .target(ET_PRONOUNS)
                .usageId("ET_PRONOUNS")
                .setListener(spotlightViewId -> {
                    if (!preferencesManager.isDisplayed("IV_BOX")) {
                        IV_SWITCH_INPUT.performClick();
                        SB_SAME_DETAIL.show();
                    }
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });*/

        SpotlightView.Builder SB_SAME_DETAIL = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Copy Detail")
                .subHeadingTvText("Click this box to copy the detail selected on your page.")
                .target(IV_BOX)
                .usageId("IV_BOX")
                .setListener(spotlightViewId -> {
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        SpotlightView.Builder SB_RECYCLER_VIEW = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("General Details")
                .subHeadingTvText("Click Individual items to fill the respective details.")
                .target(RL)
                .usageId("SB_RECYCLER_VIEW")
                .setListener(spotlightViewId -> {
                    if (!preferencesManager.isDisplayed("IV_BOX")) {
                        IV_SWITCH_INPUT.performClick();
                        SB_SAME_DETAIL.show();
                    }
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        if (!preferencesManager.isDisplayed("SWITCH")) {
            RL.setVisibility(View.VISIBLE);
            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {
                        SB_RECYCLER_VIEW.show();
                        RL.setVisibility(View.GONE);
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

        } else if (!preferencesManager.isDisplayed("SB_RECYCLER_VIEW")) {
            RL.setVisibility(View.VISIBLE);
            SB_RECYCLER_VIEW.show();
        }
    }

    private void Height(Dialog dialog) {

        RubberSeekBar rsb = dialog.findViewById(R.id.RSB_HEIGHT_GD);
        TextView TV_Height = dialog.findViewById(R.id.TV_HEIGHT_GD);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);

        if (isSwitched) {
            if (!alsMatchDetails.get(0).equals("N/A") && !TextUtils.isEmpty(alsMatchDetails.get(0))) {
                Log.d(TAG, "Height: sHeight: " + alsMatchDetails.get(0));
                rsb.setCurrentValue(Integer.parseInt(alsMatchDetails.get(0)));
                TV_Height.setText(alsMatchDetails.get(0) + " cm");
            } else {
                rsb.setCurrentValue(150);
                TV_Height.setText("150 cm");
            }

        } else {

            if (!alsDetails.get(0).equals("N/A") && !TextUtils.isEmpty(alsDetails.get(0))) {
                Log.d(TAG, "Height: sHeight: " + alsDetails.get(0));
                rsb.setCurrentValue(Integer.parseInt(alsDetails.get(0)));
                TV_Height.setText(alsDetails.get(0) + " cm");
            } else {
                rsb.setCurrentValue(150);
                TV_Height.setText("150 cm");
            }
        }
        dialog.setOnShowListener(dialog1 -> {
            if (isSwitched) {
                if (!alsMatchDetails.get(0).equals("N/A") && !TextUtils.isEmpty(alsMatchDetails.get(0))) {
                    Log.d(TAG, "Height: sHeight: " + alsMatchDetails.get(0));
                    rsb.setCurrentValue(Integer.parseInt(alsMatchDetails.get(0)));
                    TV_Height.setText(alsMatchDetails.get(0) + " cm");
                } else {
                    rsb.setCurrentValue(150);
                    TV_Height.setText("150 cm");
                }

            } else {

                if (!alsDetails.get(0).equals("N/A") && !TextUtils.isEmpty(alsDetails.get(0))) {
                    Log.d(TAG, "Height: sHeight: " + alsDetails.get(0));
                    rsb.setCurrentValue(Integer.parseInt(alsDetails.get(0)));
                    TV_Height.setText(alsDetails.get(0) + " cm");
                } else {
                    rsb.setCurrentValue(150);
                    TV_Height.setText("150 cm");
                }
            }
        });
        rsb.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NonNull RubberSeekBar rubberSeekBar, int i, boolean b) {


                TV_Height.setText(i + " cm");


            }

            @Override
            public void onStartTrackingTouch(@NonNull RubberSeekBar rubberSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RubberSeekBar rubberSeekBar) {
                if (isSwitched) {
                    alsMatchDetails.set(0, String.valueOf(rubberSeekBar.getCurrentValue()));
                } else {
                    alsDetails.set(0, String.valueOf(rubberSeekBar.getCurrentValue()));
                }
            }
        });

    }

    private void EducationLevel(Dialog dialog) {

        AppCompatButton acbHS = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acbVS = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acbIC = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acbUD = dialog.findViewById(R.id.ACB_4_GD);
        AppCompatButton acbGS = dialog.findViewById(R.id.ACB_5_GD);
        AppCompatButton acbPG = dialog.findViewById(R.id.ACB_6_GD);
        AppCompatButton acbPH = dialog.findViewById(R.id.ACB_7_GD);

        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_education_level);
        TV.setText("What's your education?");

        acbHS.setText("High School");
        acbVS.setText("Vocational Studies");
        acbIC.setText("In College");
        acbUD.setText("Undergraduate Degree");
        acbGS.setText("In Grad School");
        acbPG.setText("Post Graduate");
        acbPH.setText("Ph.d/ Doctoral Studies");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(10);
        appCompatButtons.add(acbHS);
        appCompatButtons.add(acbVS);
        appCompatButtons.add(acbIC);
        appCompatButtons.add(acbUD);
        appCompatButtons.add(acbGS);
        appCompatButtons.add(acbPG);
        appCompatButtons.add(acbPH);


        if (isSwitched)
            InitiateButtons(appCompatButtons, alsMatchDetails.get(1), "el");
        else
            InitiateButtons(appCompatButtons, alsDetails.get(1), "el");

        dialog.setOnShowListener(dialog1 -> {
            if (isSwitched)
                InitiateButtons(appCompatButtons, alsMatchDetails.get(1), "el");
            else
                InitiateButtons(appCompatButtons, alsDetails.get(1), "el");
        });


    }

    private void Exercise(Dialog dialog) {

        AppCompatButton acbN = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acbR = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acbS = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acbGF = dialog.findViewById(R.id.ACB_4_GD);
        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_exercise);
        TV.setText("How often do you work out?");

        acbN.setText("Never");
        acbR.setText("Rarely");
        acbS.setText("Sometimes");
        acbGF.setText("Gym Freak");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(10);
        appCompatButtons.add(acbN);
        appCompatButtons.add(acbR);
        appCompatButtons.add(acbS);
        appCompatButtons.add(acbGF);

        if (isSwitched)
            InitiateButtons(appCompatButtons, alsMatchDetails.get(2), "ex");
        else
            InitiateButtons(appCompatButtons, alsDetails.get(2), "ex");
        dialog.setOnShowListener(dialog1 -> {

            if (isSwitched)
                InitiateButtons(appCompatButtons, alsMatchDetails.get(2), "ex");
            else
                InitiateButtons(appCompatButtons, alsDetails.get(2), "ex");

        });

    }


    private void Drinking(Dialog dialog) {

        AppCompatButton acbN = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acbR = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acbS = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acbA = dialog.findViewById(R.id.ACB_4_GD);
        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_drinking);
        TV.setText("Do you Drink?");

        acbN.setText("Never");
        acbR.setText("Rarely");
        acbS.setText("Sometimes");
        acbA.setText("Actively");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(10);
        appCompatButtons.add(acbN);
        appCompatButtons.add(acbR);
        appCompatButtons.add(acbS);
        appCompatButtons.add(acbA);

        if (isSwitched)
            InitiateButtons(appCompatButtons, alsMatchDetails.get(3), "dr");
        else
            InitiateButtons(appCompatButtons, alsDetails.get(3), "dr");
        dialog.setOnShowListener(dialog1 -> {
            if (isSwitched)
                InitiateButtons(appCompatButtons, alsMatchDetails.get(3), "dr");
            else
                InitiateButtons(appCompatButtons, alsDetails.get(3), "dr");

        });

    }

    private void Smoking(Dialog dialog) {

        AppCompatButton acbN = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acbR = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acbS = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acbA = dialog.findViewById(R.id.ACB_4_GD);
        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_smoking);
        TV.setText("Do you smoke?");

        acbN.setText("Never");
        acbR.setText("Rarely");
        acbS.setText("Sometimes");
        acbA.setText("Actively");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(10);
        appCompatButtons.add(acbN);
        appCompatButtons.add(acbR);
        appCompatButtons.add(acbS);
        appCompatButtons.add(acbA);

        if (isSwitched)
            InitiateButtons(appCompatButtons, alsMatchDetails.get(4), "sm");
        else
            InitiateButtons(appCompatButtons, alsDetails.get(4), "sm");
        dialog.setOnShowListener(dialog1 -> {

            if (isSwitched)
                InitiateButtons(appCompatButtons, alsMatchDetails.get(4), "sm");
            else
                InitiateButtons(appCompatButtons, alsDetails.get(4), "sm");

        });

    }

    private void LookingFor(Dialog dialog) {

        AppCompatButton acb1 = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acb2 = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acb3 = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acb4 = dialog.findViewById(R.id.ACB_4_GD);
        AppCompatButton acb5 = dialog.findViewById(R.id.ACB_5_GD);
        AppCompatButton acb6 = dialog.findViewById(R.id.ACB_6_GD);
        AppCompatButton acb7 = dialog.findViewById(R.id.ACB_7_GD);
        AppCompatButton acb8 = dialog.findViewById(R.id.ACB_8_GD);
        AppCompatButton acb9 = dialog.findViewById(R.id.ACB_9_GD);
        AppCompatButton acb10 = dialog.findViewById(R.id.ACB_10_GD);
        AppCompatButton acb11 = dialog.findViewById(R.id.ACB_11_GD);
        AppCompatButton acb12 = dialog.findViewById(R.id.ACB_12_GD);
        AppCompatButton acb13 = dialog.findViewById(R.id.ACB_13_GD);
        AppCompatButton acb14 = dialog.findViewById(R.id.ACB_14_GD);
        AutofitTextView atvDate = dialog.findViewById(R.id.ATV_RL_TITLE_GD);
        AutofitTextView atvFriendship = dialog.findViewById(R.id.ATV_FR_TITLE_GD);
        AutofitTextView atvOther = dialog.findViewById(R.id.ATV_OTHER_TITLE_GD);
        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);


        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_search_grey);
        TV.setText("What are you looking for?");

        acb1.setText("Gamer");
        acb2.setText("Study group");
        acb3.setText("Volunteering");
        acb4.setText("Gym\\Sports partner");
        acb5.setText("Travel partner");
        acb6.setText("Clubbing");
        acb7.setText("Debating");
        acb8.setText("Foodie");
        acb9.setText("Music Lover");
        acb10.setText("Roommate");
        acb11.setText("Pet playdate");
        acb12.setText("Cooking partner");
        acb13.setText("Start a course together");
        acb14.setText("Anything");


        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(15);
        appCompatButtons.add(acb1);
        appCompatButtons.add(acb2);
        appCompatButtons.add(acb3);
        appCompatButtons.add(acb4);
        appCompatButtons.add(acb5);
        appCompatButtons.add(acb6);
        appCompatButtons.add(acb7);
        appCompatButtons.add(acb8);
        appCompatButtons.add(acb9);
        appCompatButtons.add(acb10);
        appCompatButtons.add(acb11);
        appCompatButtons.add(acb12);
        appCompatButtons.add(acb13);
        appCompatButtons.add(acb14);
        Log.d(TAG, "LookingFor: looking for: " + alsDetails.get(5));

        String LookingFor;
        if (isSwitched) {
            LookingFor = alsMatchDetails.get(5);

        } else {
            LookingFor = alsDetails.get(5);
        }
        if (!TextUtils.isEmpty(LookingFor) && !LookingFor.equals("N/A")) {
            String substring = LookingFor.substring((LookingFor.indexOf(",") + 1), LookingFor.indexOf("*"));

            sFriend = LookingFor.substring(0, LookingFor.indexOf(","));
            sDate = substring;
            sOther = LookingFor.substring(LookingFor.indexOf("*") + 1);
        }
        InitiateButtons(appCompatButtons, sFriend, "fr");
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow: alsMatchDetails.get(5)" + alsDetails);
                if (isSwitched) {
                    if (!TextUtils.isEmpty(alsMatchDetails.get(5)) && !alsMatchDetails.get(5).equals("N/A")) {
                        sFriend = alsMatchDetails.get(5).substring(0, alsMatchDetails.get(5).indexOf(","));
                        sDate = alsMatchDetails.get(5).substring((alsMatchDetails.get(5).indexOf(",") + 1), alsMatchDetails.get(5).indexOf("*"));
                        sOther = alsMatchDetails.get(5).substring(alsMatchDetails.get(5).indexOf("*") + 1);
                    }

                } else {
                    if (!TextUtils.isEmpty(alsDetails.get(5)) && !alsDetails.get(5).equals("N/A")) {
                        sFriend = alsDetails.get(5).substring(0, alsDetails.get(5).indexOf(","));
                        sDate = alsDetails.get(5).substring((alsDetails.get(5).indexOf(",") + 1), alsDetails.get(5).indexOf("*"));
                        sOther = alsDetails.get(5).substring(alsDetails.get(5).indexOf("*") + 1);
                    }
                }
                Log.d(TAG, "onShow: sOther: " + sOther);
                Log.d(TAG, "onShow: sDate: " + sDate);
                Log.d(TAG, "onShow: sFriend: " + sFriend);

                atvFriendship.performClick();
            }
        });

        atvFriendship.setOnClickListener(v -> {
            if (atvFriendship.getCurrentTextColor() != Color.parseColor("#000000")) {
                atvDate.setTextColor(Color.parseColor("#939393"));
                atvFriendship.setTextColor(Color.parseColor("#000000"));
                atvOther.setTextColor(Color.parseColor("#939393"));
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.animate().alpha(0).setDuration(500).start();
                }
                acb1.setText("Gamer");
                acb2.setText("Study group");
                acb3.setText("Volunteering");
                acb4.setText("Gym/Sports partner");
                acb5.setText("Travel partner");
                acb6.setText("Clubbing");
                acb7.setText("Debating");
                acb8.setText("Foodie");
                acb9.setText("Music Lover");
                acb10.setText("Roommate");
                acb11.setText("Pet playdate");
                acb12.setText("Cooking partner");
                acb13.setText("Start a course together");
                acb14.setText("Anything");

                appCompatButtons.clear();

                appCompatButtons.add(acb1);
                appCompatButtons.add(acb2);
                appCompatButtons.add(acb3);
                appCompatButtons.add(acb4);
                appCompatButtons.add(acb5);
                appCompatButtons.add(acb6);
                appCompatButtons.add(acb7);
                appCompatButtons.add(acb8);
                appCompatButtons.add(acb9);
                appCompatButtons.add(acb10);
                appCompatButtons.add(acb11);
                appCompatButtons.add(acb12);
                appCompatButtons.add(acb13);
                appCompatButtons.add(acb14);
                InitiateButtons(appCompatButtons, sFriend, "fr");
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.setVisibility(View.VISIBLE);
                    appCompatButton.animate().alpha(1).setDuration(500).start();
                }

            }
        });

        atvDate.setOnClickListener(v -> {
            if (atvDate.getCurrentTextColor() != Color.parseColor("#000000")) {
                atvDate.setTextColor(Color.parseColor("#000000"));
                atvFriendship.setTextColor(Color.parseColor("#939393"));
                atvOther.setTextColor(Color.parseColor("#939393"));
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.animate().alpha(0).setDuration(500).start();
                }
                acb1.setText("Don't Know");
                acb2.setText("Casual Dating");
                acb3.setText("Party Date");
                acb4.setText("Long Distance Relationship");
                acb6.setText("Relationship");
                acb7.setText("Long term Relationship");
                acb8.setText("Marriage");
                acb9.setVisibility(View.GONE);
                acb10.setVisibility(View.GONE);
                acb11.setVisibility(View.GONE);
                acb12.setVisibility(View.GONE);
                acb13.setVisibility(View.GONE);
                acb14.setVisibility(View.GONE);

                appCompatButtons.clear();

                appCompatButtons.add(acb1);
                appCompatButtons.add(acb2);
                appCompatButtons.add(acb3);
                appCompatButtons.add(acb4);
                appCompatButtons.add(acb5);
                appCompatButtons.add(acb6);
                appCompatButtons.add(acb7);
                appCompatButtons.add(acb8);
                InitiateButtons(appCompatButtons, sDate, "da");
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.setVisibility(View.VISIBLE);
                    appCompatButton.animate().alpha(1).setDuration(500).start();
                }
            }
        });

        atvOther.setOnClickListener(v -> {
            if (atvOther.getCurrentTextColor() != Color.parseColor("#000000")) {
                atvDate.setTextColor(Color.parseColor("#939393"));
                atvFriendship.setTextColor(Color.parseColor("#939393"));
                atvOther.setTextColor(Color.parseColor("#000000"));
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.animate().alpha(0).setDuration(500).start();
                }
                acb1.setText("Co worker");
                acb2.setText("Competition Partner");
                acb3.setText("Project Partner");
                acb4.setText("Mentor");
                acb6.setText("Investor");
                acb7.setText("Freelancer");
                acb8.setText("Part time Job");
                acb9.setText("Full Time Hire");
                acb10.setText("Co Founder");
                acb11.setVisibility(View.GONE);
                acb12.setVisibility(View.GONE);
                acb13.setVisibility(View.GONE);
                acb14.setVisibility(View.GONE);

                appCompatButtons.clear();

                appCompatButtons.add(acb1);
                appCompatButtons.add(acb2);
                appCompatButtons.add(acb3);
                appCompatButtons.add(acb4);
                appCompatButtons.add(acb5);
                appCompatButtons.add(acb6);
                appCompatButtons.add(acb7);
                appCompatButtons.add(acb8);
                appCompatButtons.add(acb9);
                appCompatButtons.add(acb10);
                InitiateButtons(appCompatButtons, sOther, "ot");
                for (AppCompatButton appCompatButton : appCompatButtons) {
                    appCompatButton.setVisibility(View.VISIBLE);
                    appCompatButton.animate().alpha(1).setDuration(500).start();
                }
            }
        });


    }

    private void SexualPreference(Dialog dialog) {

        AppCompatButton acb1 = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acb2 = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acb3 = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acb4 = dialog.findViewById(R.id.ACB_4_GD);
        AppCompatButton acb5 = dialog.findViewById(R.id.ACB_5_GD);
        AppCompatButton acb6 = dialog.findViewById(R.id.ACB_6_GD);
        AppCompatButton acb7 = dialog.findViewById(R.id.ACB_7_GD);
        AppCompatButton acb8 = dialog.findViewById(R.id.ACB_8_GD);
        AppCompatButton acb9 = dialog.findViewById(R.id.ACB_9_GD);
        AppCompatButton acb10 = dialog.findViewById(R.id.ACB_10_GD);

        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_heart_like);
        IV.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        TV.setText("What's you sexuality?");

        acb1.setText("Straight");
        acb2.setText("Gay");
        acb3.setText("Lesbian");
        acb4.setText("Queer");
        acb5.setText("Bisexual");
        acb6.setText("Pansexual");
        acb7.setText("Polysexual");
        acb8.setText("Questioning");
        acb9.setText("Sapiosexual");
        acb10.setText("Other");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(15);
        appCompatButtons.add(acb1);
        appCompatButtons.add(acb2);
        appCompatButtons.add(acb3);
        appCompatButtons.add(acb4);
        appCompatButtons.add(acb5);
        appCompatButtons.add(acb6);
        appCompatButtons.add(acb7);
        appCompatButtons.add(acb8);
        appCompatButtons.add(acb9);
        appCompatButtons.add(acb10);
        if (isSwitched) {
            InitiateButtons(appCompatButtons, alsMatchDetails.get(6), "se");
        } else {
            InitiateButtons(appCompatButtons, alsDetails.get(6), "se");
        }
        dialog.setOnShowListener(dialog1 -> {
            if (isSwitched) {
                InitiateButtons(appCompatButtons, alsMatchDetails.get(6), "se");
            } else {
                InitiateButtons(appCompatButtons, alsDetails.get(6), "se");
            }
        });

    }

    private void ZodiacSign(Dialog dialog) {

        AppCompatButton acb1 = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acb2 = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acb3 = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acb4 = dialog.findViewById(R.id.ACB_4_GD);
        AppCompatButton acb5 = dialog.findViewById(R.id.ACB_5_GD);
        AppCompatButton acb6 = dialog.findViewById(R.id.ACB_6_GD);
        AppCompatButton acb7 = dialog.findViewById(R.id.ACB_7_GD);
        AppCompatButton acb8 = dialog.findViewById(R.id.ACB_8_GD);
        AppCompatButton acb9 = dialog.findViewById(R.id.ACB_9_GD);
        AppCompatButton acb10 = dialog.findViewById(R.id.ACB_10_GD);
        AppCompatButton acb11 = dialog.findViewById(R.id.ACB_11_GD);

        ImageView iv1 = dialog.findViewById(R.id.IV_1_GD);
        ImageView iv2 = dialog.findViewById(R.id.IV_2_GD);
        ImageView iv3 = dialog.findViewById(R.id.IV_3_GD);
        ImageView iv4 = dialog.findViewById(R.id.IV_4_GD);
        ImageView iv5 = dialog.findViewById(R.id.IV_5_GD);
        ImageView iv6 = dialog.findViewById(R.id.IV_6_GD);
        ImageView iv7 = dialog.findViewById(R.id.IV_7_GD);
        ImageView iv8 = dialog.findViewById(R.id.IV_8_GD);
        ImageView iv9 = dialog.findViewById(R.id.IV_9_GD);
        ImageView iv10 = dialog.findViewById(R.id.IV_10_GD);

        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
        iv3.setVisibility(View.VISIBLE);
        iv4.setVisibility(View.VISIBLE);
        iv5.setVisibility(View.VISIBLE);
        iv6.setVisibility(View.VISIBLE);
        iv7.setVisibility(View.VISIBLE);
        iv8.setVisibility(View.VISIBLE);
        iv9.setVisibility(View.VISIBLE);
        iv10.setVisibility(View.VISIBLE);

        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_filter_zodiac_scorpion);
        TV.setText("What's your zodiac Sign?");

        acb1.setText("Aquarius");
        acb2.setText("Aries");
        acb3.setText("Cancer");
        acb4.setText("Gemini");
        acb5.setText("Leo");
        acb6.setText("Libra");
        acb7.setText("Pisces");
        acb8.setText("Sagittarius");
        acb9.setText("Taurus");
        acb10.setText("Virgo");
        acb11.setText("None");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(15);
        appCompatButtons.add(acb1);
        appCompatButtons.add(acb2);
        appCompatButtons.add(acb3);
        appCompatButtons.add(acb4);
        appCompatButtons.add(acb5);
        appCompatButtons.add(acb6);
        appCompatButtons.add(acb7);
        appCompatButtons.add(acb8);
        appCompatButtons.add(acb9);
        appCompatButtons.add(acb10);
        appCompatButtons.add(acb11);

        if (isSwitched) {
            InitiateButtons(appCompatButtons, alsMatchDetails.get(8), "zs");
        } else {
            InitiateButtons(appCompatButtons, alsDetails.get(8), "zs");
        }
        dialog.setOnShowListener(dialog1 -> {

            if (isSwitched) {
                InitiateButtons(appCompatButtons, alsMatchDetails.get(8), "zs");
            } else {
                InitiateButtons(appCompatButtons, alsDetails.get(8), "zs");
            }
        });
    }


    private void Religion(Dialog dialog) {

        AppCompatButton acb1 = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acb2 = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acb3 = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acb4 = dialog.findViewById(R.id.ACB_4_GD);
        AppCompatButton acb5 = dialog.findViewById(R.id.ACB_5_GD);
        AppCompatButton acb6 = dialog.findViewById(R.id.ACB_6_GD);
        AppCompatButton acb7 = dialog.findViewById(R.id.ACB_7_GD);
        AppCompatButton acb8 = dialog.findViewById(R.id.ACB_8_GD);
        AppCompatButton acb9 = dialog.findViewById(R.id.ACB_9_GD);
        AppCompatButton acb10 = dialog.findViewById(R.id.ACB_10_GD);
        AppCompatButton acb11 = dialog.findViewById(R.id.ACB_11_GD);
        AppCompatButton acb12 = dialog.findViewById(R.id.ACB_12_GD);
        AppCompatButton acb13 = dialog.findViewById(R.id.ACB_13_GD);


        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_religion);
        TV.setText("Do you follow a religion?");

        acb1.setText("Atheist");
        acb2.setText("Agnostic");
        acb3.setText("Buddhism");
        acb4.setText("Christianity");
        acb5.setText("Sikhism");
        acb6.setText("Hinduism");
        acb7.setText("Jainism");
        acb8.setText("Judaism");
        acb9.setText("Zoroastrianism");
        acb10.setText("Islam");
        acb11.setText("Mormonism");
        acb12.setText("Spiritual");
        acb13.setText("Other");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(15);
        appCompatButtons.add(acb1);
        appCompatButtons.add(acb2);
        appCompatButtons.add(acb3);
        appCompatButtons.add(acb4);
        appCompatButtons.add(acb5);
        appCompatButtons.add(acb6);
        appCompatButtons.add(acb7);
        appCompatButtons.add(acb8);
        appCompatButtons.add(acb9);
        appCompatButtons.add(acb10);
        appCompatButtons.add(acb11);
        appCompatButtons.add(acb12);
        appCompatButtons.add(acb13);
        if (isSwitched) {
            InitiateButtons(appCompatButtons, alsMatchDetails.get(9), "re");
        } else {
            InitiateButtons(appCompatButtons, alsDetails.get(9), "re");
        }
        dialog.setOnShowListener(dialog1 -> {

            if (isSwitched) {
                InitiateButtons(appCompatButtons, alsMatchDetails.get(9), "re");
            } else {
                InitiateButtons(appCompatButtons, alsDetails.get(9), "re");
            }
        });
    }

    private void Politics(Dialog dialog) {

        AppCompatButton acb1 = dialog.findViewById(R.id.ACB_1_GD);
        AppCompatButton acb2 = dialog.findViewById(R.id.ACB_2_GD);
        AppCompatButton acb3 = dialog.findViewById(R.id.ACB_3_GD);
        AppCompatButton acb4 = dialog.findViewById(R.id.ACB_4_GD);

        ImageView IV = dialog.findViewById(R.id.IV_BUTTONS_DIALOG);
        TextView TV = dialog.findViewById(R.id.TV_BUTTONS_DIALOG);
        TextView TV_SKIP = dialog.findViewById(R.id.TV_SKIP_DIALOG_GD);

        SetSkipDialog(TV_SKIP, dialog);
        IV.setImageResource(R.drawable.ic_religion);
        TV.setText("Do you follow a religion?");

        acb1.setText("Not Interested");
        acb2.setText("Moderate");
        acb3.setText("Left");
        acb4.setText("Right");

        ArrayList<AppCompatButton> appCompatButtons = new ArrayList<>(15);
        appCompatButtons.add(acb1);
        appCompatButtons.add(acb2);
        appCompatButtons.add(acb3);
        appCompatButtons.add(acb4);
        if (isSwitched) {
            InitiateButtons(appCompatButtons, alsMatchDetails.get(7), "po");
        } else {
            InitiateButtons(appCompatButtons, alsDetails.get(7), "po");
        }
        dialog.setOnShowListener(dialog1 -> {

            if (isSwitched) {
                InitiateButtons(appCompatButtons, alsMatchDetails.get(7), "po");
            } else {
                InitiateButtons(appCompatButtons, alsDetails.get(7), "po");
            }
        });
    }

    private void InitiateButtons(ArrayList<AppCompatButton> acb, String field, String type) {

        for (AppCompatButton appCompatButton : acb) {
            appCompatButton.setVisibility(View.VISIBLE);
            if (!field.equals("N/A") && !TextUtils.isEmpty(field)) {
                if (field.equals(appCompatButton.getText().toString())) {
                    appCompatButton.setTextColor(Color.parseColor("#FFFFFF"));
                    appCompatButton.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                    appCompatButton.setTag("selected");
                } else {
                    appCompatButton.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
                    appCompatButton.setTextColor(Color.parseColor("#000000"));
                    appCompatButton.setTag("unselected");
                }
            } else {
                appCompatButton.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
                appCompatButton.setTextColor(Color.parseColor("#000000"));
                appCompatButton.setTag("unselected");
            }
            SetSelectionOnButton(appCompatButton, type, acb);
        }

    }

    private void SetSelectionOnButton(AppCompatButton acb, String field, ArrayList<AppCompatButton> alsACB) {

        acb.setOnClickListener(v -> {
            for (AppCompatButton acb1 : alsACB) {
                acb1.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
                acb1.setTextColor(Color.parseColor("#000000"));
                acb1.setTag("unselected");
            }

            if (acb.getTag().equals("unselected")) {
                acb.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                acb.setTextColor(Color.parseColor("#FFFFFF"));
                acb.setTag("selected");
                SetStringValue(field, acb.getText().toString());
            } else {
                acb.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
                acb.setTextColor(Color.parseColor("#000000 "));
                acb.setTag("unselected");
                SetStringValue(field, "N/A");
            }
        });
    }

    private void BuildStringBuilder(int pos, StringBuilder stringBuilder) {

        if (pos == 0) {
            stringBuilder.append("Height").append(",").append(R.drawable.ic_height);
        } else if (pos == 1) {
            stringBuilder.append("Education Level").append(",").append(R.drawable.ic_education_level);

        } else if (pos == 2) {
            stringBuilder.append("Exercise").append(",").append(R.drawable.ic_exercise);

        } else if (pos == 3) {
            stringBuilder.append("Drinking").append(",").append(R.drawable.ic_drinking);

        } else if (pos == 4) {
            stringBuilder.append("Smoking").append(",").append(R.drawable.ic_smoking);

        } else if (pos == 5) {
            stringBuilder.append("Looking For").append(",").append(R.drawable.ic_search_grey);

        } else if (pos == 6) {
            stringBuilder.append("Sexual Preference").append(",").append(R.drawable.ic_white_heart_outline);
        } else if (pos == 7) {
            stringBuilder.append("Politics").append(",").append(R.drawable.ic_politics);
        } else if (pos == 8) {
            stringBuilder.append("Zodiac Sign").append(",").append(R.drawable.ic_filter_zodiac_scorpion);
        } else if (pos == 9) {
            stringBuilder.append("Religion").append(",").append(R.drawable.ic_religion);
        }

    }

    private void SetStringValue(String Field, String Value) {
        int pos;
        switch (Field) {
            case "el":
                pos = 1;
                break;
            case "ex":
                pos = 2;
                break;
            case "dr":
                pos = 3;
                break;
            case "sm":
                pos = 4;
                break;
            case "fr":
                pos = 51;
                sFriend = Value;
                break;
            case "ot":
                pos = 52;
                sOther = Value;
                break;
            case "da":
                pos = 5;
                sDate = Value;
                break;
            case "se":
                pos = 6;
                break;
            case "po":
                pos = 7;
                break;
            case "zs":
                pos = 8;
                break;
            case "re":
                pos = 9;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Field);
        }

        Log.d(TAG, "SetStringValue: Value: " + Value + " pos: " + pos);

        if (pos == 5) {
            if (isSwitched) {
                alsMatchDetails.set(5, sFriend + "," + Value + "*" + sOther);
            } else {
                alsDetails.set(5, sFriend + "," + Value + "*" + sOther);
            }
        } else if (pos == 51) {
            if (isSwitched) {
                alsMatchDetails.set(5, Value + "," + sDate + "*" + sOther);
            } else {
                alsDetails.set(5, Value + "," + sDate + "*" + sOther);
            }
        } else if (pos == 52) {
            if (isSwitched) {
                alsMatchDetails.set(5, sFriend + "," + sDate + "*" + Value);
            } else {
                alsDetails.set(5, sFriend + "," + sDate + "*" + Value);
            }
        } else {
            if (isSwitched) {
                alsMatchDetails.set(pos, Value);
            } else {
                alsDetails.set(pos, Value);
            }
        }
        Log.d(TAG, "SetStringValue: alsDetails: " + alsDetails);
        Log.d(TAG, "SetStringValue: alsMatchDetails: " + alsMatchDetails);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void SetSkipDialog(View view, Dialog dialog) {
        view.setOnClickListener(v -> dialog.cancel());
    }

    @Override
    public void onItemClick(int position, ConstraintLayout CL) {
        Log.d(TAG, "onItemClick: position: " + position);
        if (CL.getTag().equals("not_init")) {
            if (position == 0) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_height_gd);
                ald.set(0, dialog);
                dialog.show();
                Height(dialog);
            } else if (position == 1) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(1, dialog);
                dialog.show();
                EducationLevel(dialog);
            } else if (position == 2) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(2, dialog);
                dialog.show();
                Exercise(dialog);
            } else if (position == 3) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(3, dialog);
                dialog.show();
                Drinking(dialog);
            } else if (position == 4) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(4, dialog);
                dialog.show();
                Smoking(dialog);
            } else if (position == 5) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_looking_for_gd);
                ald.set(5, dialog);
                dialog.show();
                LookingFor(dialog);
            } else if (position == 6) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(6, dialog);
                dialog.show();
                SexualPreference(dialog);
            } else if (position == 7) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(7, dialog);
                dialog.show();
                Politics(dialog);
            } else if (position == 8) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(8, dialog);
                dialog.show();
                ZodiacSign(dialog);
            } else if (position == 9) {
                Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.layout_buttons_gd);
                ald.set(9, dialog);
                dialog.show();
                Religion(dialog);
            }

            CL.setTag("init");
        } else {
            ald.get(position).show();
        }


        // inflate 1st layout


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
}
