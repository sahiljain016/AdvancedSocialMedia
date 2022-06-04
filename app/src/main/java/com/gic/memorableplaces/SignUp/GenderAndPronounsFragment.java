

package com.gic.memorableplaces.SignUp;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class GenderAndPronounsFragment extends Fragment {
    private static final String TAG = "GenderAndPronounsFragment";
    private Context mContext = getActivity();
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;

    private String sGender, sMatchedGender, sPronouns;
    private ArrayList<String> alsMatchedGender;
    private Handler handler;

    private ImageView IV_PRIVACY, IV_BOX;
    private MotionLayout ML;
    private AutofitTextView ATV_OTHER_FILL;
    private AutofitTextView ATV_PRONOUNS_TITLE;
    private AutofitTextView ATV_MALE;
    private AutofitTextView ATV_FEMALE;
    private AutofitTextView ATV_OTHER;
    private AutofitTextView ATV_PNTS;
    private EditText ET_OTHER_GENDER, ET_PRONOUNS;
    private RoundedImageView RIV;

    private ConstraintLayout CL_MALE, CL_FEMALE, CL_OTHER, CL_PNTS;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gender_pronouns, container, false);
        mContext = getActivity();
        handler = new Handler(Looper.getMainLooper());

        sGender = mContext.getString(R.string.not_available);
        sMatchedGender = mContext.getString(R.string.not_available);
        sPronouns = mContext.getString(R.string.not_available);

        alsMatchedGender = new ArrayList<>();
        CL_MALE = view.findViewById(R.id.CL_MALE_GP);
        CL_FEMALE = view.findViewById(R.id.CL_FEMALE_GP);
        CL_OTHER = view.findViewById(R.id.CL_OTHER_GP);
        CL_PNTS = view.findViewById(R.id.CL_PNTS_GP);

        ATV_MALE = view.findViewById(R.id.ATV_MALE_GP);
        ATV_FEMALE = view.findViewById(R.id.ATV_FEMALE_GP);
        ATV_OTHER = view.findViewById(R.id.ATV_OTHER_GP);
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_GP);
        ATV_PNTS = view.findViewById(R.id.ATV_PNTS_GP);
        ATV_OTHER_FILL = view.findViewById(R.id.ATV_OTHER_FILL_GP);
        ATV_PRONOUNS_TITLE = view.findViewById(R.id.ATV_PRONOUNS_GP);

        ET_OTHER_GENDER = view.findViewById(R.id.ET_OTHER_GP);
        ET_PRONOUNS = view.findViewById(R.id.ET_PRONOUNS_GP);
        RIV = view.findViewById(R.id.RIV_BACK_FROM_PN_GP);

        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_GP);
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_GP);
        IV_BOX = view.findViewById(R.id.IV_BOX_GP);
        ImageView IV_TICK = view.findViewById(R.id.IV_TICK_GP);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_GP);
        ML = view.findViewById(R.id.ML_MAIN_GP);

        if (getArguments() != null) {
            sGender = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_gender)));
            if (getArguments().getStringArrayList(requireActivity().getString(R.string.field_match_gender)) != null)
                alsMatchedGender.addAll(getArguments().getStringArrayList(requireActivity().getString(R.string.field_match_gender)));
            sPronouns = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_pronouns)));
            // sMatchedGender = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_match_gender)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }
        ShowSpotlights(IV_SWITCH_INPUT);
        //alsMatchedGender.add(mContext.getString(R.string.not_available));
        if (alsMatchedGender.contains(mContext.getString(R.string.not_available))) {
            alsMatchedGender.clear();
        }
        SetSelected(sGender);
        Log.d(TAG, "onCreateView: alsMatchedGender 1: " + alsMatchedGender);
        if (!sPronouns.equals(mContext.getString(R.string.not_available))) {
            ET_PRONOUNS.setText(sPronouns);
        }

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setTypeface(tf);
        ATV_MALE.setTypeface(cap);
        ATV_FEMALE.setTypeface(cap);
        ATV_OTHER.setTypeface(cap);
        ATV_PNTS.setTypeface(cap);
        ATV_OTHER_FILL.setTypeface(cap);

        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);

        IV_BOX.setOnClickListener(v1 -> {
            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                if (!sGender.equals("N/A")) {
                    sMatchedGender = sGender;
                    alsMatchedGender.clear();
                    alsMatchedGender.add(sGender);
                    SetMultiSelected();
                }
                isSameDetail = true;

            }
        });

        IV_SWITCH_INPUT.setOnClickListener(v -> {
            if (CL_FEMALE.getAlpha() == 0.0 || CL_MALE.getAlpha() == 0.0 || CL_OTHER.getAlpha() == 0.0 || CL_PNTS.getAlpha() == 0.0) {
                RIV.performClick();
                handler.postDelayed(() -> {
                    ML.setTransition(R.id.TRANS_SWITCH_GP);
                    if (!isSwitched) {
                        ML.setBackgroundColor(Color.parseColor("#212121"));
                        ML.transitionToEnd();
                        ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                        isSwitched = true;
                        SetMultiSelected();
//
//                        ATV_OTHER.setTextColor(Color.parseColor("#FFFFFF"));
//                        ATV_FEMALE.setTextColor(Color.parseColor("#FFFFFF"));
//                        ATV_MALE.setTextColor(Color.parseColor("#FFFFFF"));
//                        ATV_PNTS.setTextColor(Color.parseColor("#FFFFFF"));

                    } else {
                        ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        ML.transitionToStart();
                        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                        isSwitched = false;
                        SetSelected(sGender);
//
//                        ATV_OTHER.setTextColor(Color.parseColor("#000000"));
//                        ATV_FEMALE.setTextColor(Color.parseColor("#000000"));
//                        ATV_MALE.setTextColor(Color.parseColor("#000000"));
//                        ATV_PNTS.setTextColor(Color.parseColor("#000000"));

                    }
                }, 1000);


            } else {
                ML.setTransition(R.id.TRANS_SWITCH_GP);
                if (!isSwitched) {
                    ML.setBackgroundColor(Color.parseColor("#212121"));
                    ML.transitionToEnd();
                    ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                    isSwitched = true;
                    SetMultiSelected();
//
//                    ATV_OTHER.setTextColor(Color.parseColor("#FFFFFF"));
//                    ATV_FEMALE.setTextColor(Color.parseColor("#FFFFFF"));
//                    ATV_MALE.setTextColor(Color.parseColor("#FFFFFF"));
//                    ATV_PNTS.setTextColor(Color.parseColor("#FFFFFF"));

                } else {
                    ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    ML.transitionToStart();
                    ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                    isSwitched = false;
                    SetSelected(sGender);
//
//                    ATV_OTHER.setTextColor(Color.parseColor("#000000"));
//                    ATV_FEMALE.setTextColor(Color.parseColor("#000000"));
//                    ATV_MALE.setTextColor(Color.parseColor("#000000"));
//                    ATV_PNTS.setTextColor(Color.parseColor("#000000"));
                }
            }

            Log.d(TAG, "SetMultiSelected: alsMatchedGender 3: " + alsMatchedGender);
        });

        ET_OTHER_GENDER.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSwitched) {
                    if (TextUtils.isEmpty(s.toString())) {
                        sGender = mContext.getString(R.string.not_available);
                    } else {
                        sGender = s.toString();
                    }
                } else {
                    if (TextUtils.isEmpty(s.toString())) {
                        sMatchedGender = mContext.getString(R.string.not_available);
                        SetMultiSelectedGender(CL_OTHER, ATV_OTHER, true);
                    } else {
                        sMatchedGender = s.toString();
                        SetMultiSelectedGender(CL_OTHER, ATV_OTHER, false);
                    }
                }
            }
        });
        ET_PRONOUNS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    sPronouns = mContext.getString(R.string.not_available);
                } else {
                    sPronouns = s.toString();
                }

            }
        });

        CL_MALE.setOnClickListener(v -> {
            if (!isSwitched) {
                ML.setTransition(R.id.TRANS_MALE_CENTER);
                ML.transitionToEnd();
                ATV_PRONOUNS_TITLE.setVisibility(View.VISIBLE);
                ET_PRONOUNS.setVisibility(View.VISIBLE);
                SetSelectedGenderProperties(CL_MALE, ATV_MALE);
                sGender = mContext.getString(R.string.field_male);
            } else {
                if (alsMatchedGender.contains(mContext.getString(R.string.field_male))) {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.remove(mContext.getString(R.string.field_male));
                    SetMultiSelectedGender(CL_MALE, ATV_MALE, true);
                } else {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.add(mContext.getString(R.string.field_male));
                    SetMultiSelectedGender(CL_MALE, ATV_MALE, false);
                }
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.GONE);
                }
            }

            Log.d(TAG, "SetMultiSelected: alsMatchedGender: " + alsMatchedGender);
        });
        CL_FEMALE.setOnClickListener(v -> {
            if (!isSwitched) {
                ML.setTransition(R.id.TRANS_FEMALE_CENTER);
                ML.transitionToEnd();
                ATV_PRONOUNS_TITLE.setVisibility(View.VISIBLE);
                ET_PRONOUNS.setVisibility(View.VISIBLE);
                SetSelectedGenderProperties(CL_FEMALE, ATV_FEMALE);
                sGender = mContext.getString(R.string.field_female);
            } else {
                if (alsMatchedGender.contains(mContext.getString(R.string.field_female))) {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.remove(mContext.getString(R.string.field_female));
                    SetMultiSelectedGender(CL_FEMALE, ATV_FEMALE, true);
                } else {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.add(mContext.getString(R.string.field_female));
                    SetMultiSelectedGender(CL_FEMALE, ATV_FEMALE, false);
                }
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.GONE);
                }
            }

        });
        CL_OTHER.setOnClickListener(v -> {


            ATV_OTHER_FILL.setVisibility(View.VISIBLE);
            ET_OTHER_GENDER.setVisibility(View.VISIBLE);
            ATV_PRONOUNS_TITLE.setVisibility(View.VISIBLE);
            ET_PRONOUNS.setVisibility(View.VISIBLE);
            if (!isSwitched) {
                ML.setTransition(R.id.TRANS_OTHER_CENTER);
                ML.transitionToEnd();
                SetSelectedGenderProperties(CL_OTHER, ATV_OTHER);
                ET_OTHER_GENDER.setText(sGender);
                ATV_OTHER_FILL.setTextColor(Color.parseColor("#000000"));
                ATV_PRONOUNS_TITLE.setTextColor(Color.parseColor("#000000"));
                ET_OTHER_GENDER.setTextColor(Color.parseColor("#000000"));
                ET_OTHER_GENDER.setHintTextColor(Color.parseColor("#CC000000"));
                ET_OTHER_GENDER.setHighlightColor(Color.parseColor("#000000"));
                ET_PRONOUNS.setTextColor(Color.parseColor("#000000"));
                ET_PRONOUNS.setHintTextColor(Color.parseColor("#CC000000"));
                ET_PRONOUNS.setHighlightColor(Color.parseColor("#000000"));
            } else {
                ATV_PRONOUNS_TITLE.setVisibility(View.GONE);
                ET_PRONOUNS.setVisibility(View.GONE);
                for (String string : alsMatchedGender) {
                    if (!string.equals(mContext.getString(R.string.field_female)) && !string.equals(mContext.getString(R.string.field_male)) && !string.equals(mContext.getString(R.string.field_prefer_not_to_say))) {
                        ET_OTHER_GENDER.setText(string);
                        break;
                    }
                }
                ATV_OTHER_FILL.setTextColor(Color.parseColor("#FFFFFF"));
                ATV_PRONOUNS_TITLE.setTextColor(Color.parseColor("#FFFFFF"));
                ET_OTHER_GENDER.setTextColor(Color.parseColor("#FFFFFF"));
                ET_OTHER_GENDER.setHintTextColor(Color.parseColor("#CCFFFFFF"));
                ET_OTHER_GENDER.setHighlightColor(Color.parseColor("#FFFFFF"));
                ET_PRONOUNS.setTextColor(Color.parseColor("#FFFFFF"));
                ET_PRONOUNS.setHintTextColor(Color.parseColor("#CCFFFFFF"));
                ET_PRONOUNS.setHighlightColor(Color.parseColor("#FFFFFF"));
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.GONE);
                }

                ML.setTransition(R.id.TRANS_OTHER_CENTER);
//                if (contains) {
                //alsMatchedGender.remove(gender);
                Log.d(TAG, "onCreateView: alsMatchedGender 4: " + alsMatchedGender);
                if (ML.getProgress() == 1.0) {
                    ML.transitionToStart();
                } else {
                    ML.transitionToEnd();
                    // SetMultiSelectedGender(CL_OTHER, ATV_OTHER, false);
                }
            }
        });
        CL_PNTS.setOnClickListener(v -> {
            if (!isSwitched) {
                ML.setTransition(R.id.TRANS_PNTS_CENTER);
                ML.transitionToEnd();
                ATV_PRONOUNS_TITLE.setVisibility(View.VISIBLE);
                ET_PRONOUNS.setVisibility(View.VISIBLE);
                SetSelectedGenderProperties(CL_PNTS, ATV_PNTS);
                sGender = mContext.getString(R.string.field_prefer_not_to_say);

            } else {
                if (alsMatchedGender.contains(mContext.getString(R.string.field_prefer_not_to_say))) {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.remove(mContext.getString(R.string.field_prefer_not_to_say));
                    SetMultiSelectedGender(CL_PNTS, ATV_PNTS, true);
                } else {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.add(mContext.getString(R.string.field_prefer_not_to_say));
                    SetMultiSelectedGender(CL_PNTS, ATV_PNTS, false);
                }
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.GONE);
                }
            }

        });

        RIV.setOnClickListener(v -> {
            ML.transitionToStart();
            if (isSwitched) {
                boolean contains = false;
                String gender = "";
                for (String string : alsMatchedGender) {
                    if (!string.equals(mContext.getString(R.string.field_female)) && !string.equals(mContext.getString(R.string.field_male)) && !string.equals(mContext.getString(R.string.field_prefer_not_to_say))) {
                        contains = true;
                        gender = string;
                        break;
                    }
                }
                Log.d(TAG, "onCreateView: sMatchedGender: " + sMatchedGender);
                Log.d(TAG, "onCreateView: contains: " + contains);
                Log.d(TAG, "onCreateView: alsMatchedGender 2: " + alsMatchedGender);
                Log.d(TAG, "onCreateView: condition: " + !sMatchedGender.equals(mContext.getString(R.string.not_available)));
                if (!sMatchedGender.equals(mContext.getString(R.string.not_available)) && !contains && !alsMatchedGender.contains(sMatchedGender)) {
                    alsMatchedGender.add(sMatchedGender);
                } else if ((sMatchedGender.equals(mContext.getString(R.string.not_available)) || TextUtils.isEmpty(sMatchedGender)) && contains) {
                    alsMatchedGender.remove(gender);
                }
            }
            Log.d(TAG, "onCreateView: alsMatchedGender 2 after: " + alsMatchedGender);
            if (CL_OTHER.getAlpha() == 1.0) {
                ATV_OTHER_FILL.setVisibility(View.GONE);
                ET_OTHER_GENDER.setVisibility(View.GONE);
            }
        });

        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (isSwitched) {
                boolean contains = false;
                String gender = "";
                for (String string : alsMatchedGender) {
                    if (!string.equals(mContext.getString(R.string.field_female)) && !string.equals(mContext.getString(R.string.field_male)) && !string.equals(mContext.getString(R.string.field_prefer_not_to_say))) {
                        contains = true;
                        gender = string;
                        break;
                    }
                }
                Log.d(TAG, "onCreateView: sMatchedGender: " + sMatchedGender);
                Log.d(TAG, "onCreateView: contains: " + contains);
                Log.d(TAG, "onCreateView: alsMatchedGender 2: " + alsMatchedGender);
                Log.d(TAG, "onCreateView: condition: " + !sMatchedGender.equals(mContext.getString(R.string.not_available)));
                if (!sMatchedGender.equals(mContext.getString(R.string.not_available)) && !contains) {
                    alsMatchedGender.add(sMatchedGender);
                } else if ((sMatchedGender.equals(mContext.getString(R.string.not_available)) || TextUtils.isEmpty(sMatchedGender)) && contains) {
                    alsMatchedGender.remove(gender);
                }
            }
            if (alsMatchedGender.size() == 0) {
                alsMatchedGender.add(mContext.getString(R.string.not_available));
            }
            bundle.putString(mContext.getString(R.string.field_gender), sGender);
            bundle.putStringArrayList(mContext.getString(R.string.field_match_gender), alsMatchedGender);
            bundle.putString(mContext.getString(R.string.field_pronouns), sPronouns);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(GenderAndPronounsFragment.this).commit();
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
                });

        SpotlightView.Builder SB_FEMALE = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Select Gender")
                .subHeadingTvText("Click the gender to select it as your gender.")
                .target(CL_FEMALE)
                .usageId("CL_FEMALE")
                .setListener(spotlightViewId -> {
                    SB_PRONOUNS.show();
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        if (!preferencesManager.isDisplayed("SWITCH")) {
            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {

                        CL_FEMALE.performClick();
                        SB_FEMALE.show();
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

        } else if (!preferencesManager.isDisplayed("CL_FEMALE")) {
            if (!isSwitched) {
                ML.setTransition(R.id.TRANS_FEMALE_CENTER);
                ML.transitionToEnd();
                ATV_PRONOUNS_TITLE.setVisibility(View.VISIBLE);
                ET_PRONOUNS.setVisibility(View.VISIBLE);
                SetSelectedGenderProperties(CL_FEMALE, ATV_FEMALE);
                sGender = mContext.getString(R.string.field_female);
            } else {
                if (alsMatchedGender.contains(mContext.getString(R.string.field_female))) {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.remove(mContext.getString(R.string.field_female));
                    SetMultiSelectedGender(CL_FEMALE, ATV_FEMALE, true);
                } else {
                    sMatchedGender = mContext.getString(R.string.not_available);
                    alsMatchedGender.add(mContext.getString(R.string.field_female));
                    SetMultiSelectedGender(CL_FEMALE, ATV_FEMALE, false);
                }
            }
            SB_FEMALE.show();
        }
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

    private void SetMultiSelectedGender(ConstraintLayout CV, AutofitTextView ATV, boolean isSelected) {
        if (isSelected) {
            ATV.setTextColor(Color.parseColor("#FFFFFF"));
            CV.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        } else {
            ATV.setTextColor(Color.parseColor("#efc859"));
            CV.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
        }
    }

    private void SetSelectedGenderProperties(ConstraintLayout CV, AutofitTextView ATV) {
        CL_PNTS.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_OTHER.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_MALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_FEMALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

        if (!isSwitched) {
            ATV_OTHER.setTextColor(Color.parseColor("#000000"));
            ATV_FEMALE.setTextColor(Color.parseColor("#000000"));
            ATV_MALE.setTextColor(Color.parseColor("#000000"));
            ATV_PNTS.setTextColor(Color.parseColor("#000000"));
        } else {
            ATV_OTHER.setTextColor(Color.parseColor("#FFFFFF"));
            ATV_FEMALE.setTextColor(Color.parseColor("#FFFFFF"));
            ATV_MALE.setTextColor(Color.parseColor("#FFFFFF"));
            ATV_PNTS.setTextColor(Color.parseColor("#FFFFFF"));
        }

        ATV.setTextColor(Color.parseColor("#efc859"));
        CV.setBackgroundResource(R.drawable.border_rounded_yellow_20sdp);
    }

    private void SetMultiSelected() {
        CL_PNTS.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_OTHER.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_MALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
        CL_FEMALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

        ATV_OTHER.setTextColor(Color.parseColor("#FFFFFF"));
        ATV_FEMALE.setTextColor(Color.parseColor("#FFFFFF"));
        ATV_MALE.setTextColor(Color.parseColor("#FFFFFF"));
        ATV_PNTS.setTextColor(Color.parseColor("#FFFFFF"));
        Log.d(TAG, "SetMultiSelected: multi alsMatchedGender: " + alsMatchedGender);
        for (String string : alsMatchedGender) {
            if (string.equals(mContext.getString(R.string.field_male))) {
                SetMultiSelectedGender(CL_MALE, ATV_MALE, false);
            } else if (string.equals(mContext.getString(R.string.field_female))) {
                SetMultiSelectedGender(CL_FEMALE, ATV_FEMALE, false);
            } else if (string.equals(mContext.getString(R.string.field_prefer_not_to_say))) {
                SetMultiSelectedGender(CL_PNTS, ATV_PNTS, false);
            } else {
                if (!string.equals(mContext.getString(R.string.not_available))) {
                    SetMultiSelectedGender(CL_OTHER, ATV_OTHER, false);
                }
            }
        }
    }

    private void SetSelected(String Field) {
        if (Field.equals(mContext.getString(R.string.field_male))) {
            SetSelectedGenderProperties(CL_MALE, ATV_MALE);
        } else if (Field.equals(mContext.getString(R.string.field_female))) {
            SetSelectedGenderProperties(CL_FEMALE, ATV_FEMALE);
        } else if (Field.equals(mContext.getString(R.string.field_prefer_not_to_say))) {
            SetSelectedGenderProperties(CL_PNTS, ATV_PNTS);
        } else if (Field.equals(mContext.getString(R.string.not_available)) || TextUtils.isEmpty(sGender)) {
            CL_PNTS.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
            CL_OTHER.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
            CL_MALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
            CL_FEMALE.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

            if (isSwitched) {
                ATV_OTHER.setTextColor(Color.parseColor("#FFFFFF"));
                ATV_FEMALE.setTextColor(Color.parseColor("#FFFFFF"));
                ATV_MALE.setTextColor(Color.parseColor("#FFFFFF"));
                ATV_PNTS.setTextColor(Color.parseColor("#FFFFFF"));
            } else {

                ATV_OTHER.setTextColor(Color.parseColor("#000000"));
                ATV_FEMALE.setTextColor(Color.parseColor("#000000"));
                ATV_MALE.setTextColor(Color.parseColor("#000000"));
                ATV_PNTS.setTextColor(Color.parseColor("#000000"));
            }
        } else {
            SetSelectedGenderProperties(CL_OTHER, ATV_OTHER);
        }
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
