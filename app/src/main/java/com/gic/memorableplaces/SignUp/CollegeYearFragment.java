package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class CollegeYearFragment extends Fragment {
    private static final String TAG = "CollegeYearFragment";
    private int StrokeSize = 10;
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;

    private String sCollegeYear = "N/A";
    private ArrayList<String> alsMatchCollegeYear;
    private Handler handler;

    private ImageView IV_PRIVACY;
    private MotionLayout ML;
    private CardView CV_1, CV_2, CV_3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_college_years, container, false);
        mContext = getActivity();
        handler = new Handler(Looper.getMainLooper());
        alsMatchCollegeYear = new ArrayList<>();

        ML = view.findViewById(R.id.ML_MAIN_CY);
        CV_1 = view.findViewById(R.id.CV_1_CY);
        CV_2 = view.findViewById(R.id.CV_2_CY);
        CV_3 = view.findViewById(R.id.CV_3_CY);
        ImageView IV_GO_BACK = view.findViewById(R.id.IV_GO_BACK_CY);
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_CY);
        AutofitTextView ATV_1 = view.findViewById(R.id.ATV_1_CY);
        AutofitTextView ATV_2 = view.findViewById(R.id.ATV_2_CY);
        AutofitTextView ATV_3 = view.findViewById(R.id.ATV_3_CY);
        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_CY);
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_CY);
        ImageView IV_BOX = view.findViewById(R.id.IV_BOX_CY);
        ImageView IV_TICK = view.findViewById(R.id.IV_TICK_CY);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_CY);

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setTypeface(tf);
        ATV_1.setTypeface(cap);
        ATV_2.setTypeface(cap);
        ATV_3.setTypeface(cap);

        if (getArguments() != null) {
            alsMatchCollegeYear = getArguments().getStringArrayList(requireActivity().getString(R.string.field_match_college_year));
            sCollegeYear = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_college_year)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }

        ShowSpotlights(IV_SWITCH_INPUT, IV_BOX);
        while (alsMatchCollegeYear.contains(mContext.getString(R.string.not_available)))
            alsMatchCollegeYear.remove(mContext.getString(R.string.not_available));

        SetInitialSelection(sCollegeYear);

        CV_1.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_1_CENTER);
            ML.transitionToEnd();
            if (isSwitched) {
                if (alsMatchCollegeYear.contains("1")) {
                    alsMatchCollegeYear.remove("1");
                    CV_1.setForeground(null);
                } else {
                    alsMatchCollegeYear.add("1");
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(100);
                    drawable.setStroke(StrokeSize, Color.GREEN);
                    CV_1.setForeground(drawable);
                }
            } else {
                CV_1.setForeground(null);
                CV_2.setForeground(null);
                CV_3.setForeground(null);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.GREEN);
                CV_1.setForeground(drawable);
                sCollegeYear = "1";
            }
        });

        CV_2.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_2_CENTER);
            ML.transitionToEnd();
            if (isSwitched) {
                if (alsMatchCollegeYear.contains("2")) {
                    alsMatchCollegeYear.remove("2");
                    CV_2.setForeground(null);
                } else {
                    alsMatchCollegeYear.add("2");
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(100);
                    drawable.setStroke(StrokeSize, Color.parseColor("#FB8C00"));
                    CV_2.setForeground(drawable);
                }
            } else {
                sCollegeYear = "2";
                CV_1.setForeground(null);
                CV_2.setForeground(null);
                CV_3.setForeground(null);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.parseColor("#FB8C00"));
                CV_2.setForeground(drawable);
            }
        });

        CV_3.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_3_CENTER);
            ML.transitionToEnd();

            if (isSwitched) {
                if (alsMatchCollegeYear.contains("3")) {
                    alsMatchCollegeYear.remove("3");
                    CV_3.setForeground(null);
                } else {
                    alsMatchCollegeYear.add("3");
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(100);
                    drawable.setStroke(StrokeSize, Color.parseColor("#8800FF"));
                    CV_3.setForeground(drawable);
                }
            } else {
                sCollegeYear = "3";
                CV_1.setForeground(null);
                CV_2.setForeground(null);
                CV_3.setForeground(null);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.parseColor("#8800FF"));
                CV_3.setForeground(drawable);
            }
        });

        IV_GO_BACK.setOnClickListener(v -> ML.transitionToStart());
        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                SetUnselectedCardView(CV_1);
                SetUnselectedCardView(CV_2);
                SetUnselectedCardView(CV_3);
                isSameDetail = false;
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                alsMatchCollegeYear.clear();
                alsMatchCollegeYear.add(sCollegeYear);
                SetInitialSelection(sCollegeYear);
                isSameDetail = true;
            }

        });

        IV_SWITCH_INPUT.setOnClickListener(v -> {

            if (CV_1.getAlpha() == 0.0 || CV_2.getAlpha() == 0.0 || CV_3.getAlpha() == 0.0) {
                IV_GO_BACK.performClick();
            }
            handler.postDelayed(() -> {

                ML.setTransition(R.id.TRANS_SWITCH_CY);
                if (!isSwitched) {
                    ML.setBackgroundColor(Color.parseColor("#212121"));
                    ML.transitionToEnd();
                    ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                    IV_GO_BACK.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    isSwitched = true;
                    for (String string : alsMatchCollegeYear)
                        SetInitialSelection(string);

                } else {
                    ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    ML.transitionToStart();
                    ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                    IV_GO_BACK.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    isSwitched = false;
                    CV_1.setForeground(null);
                    CV_2.setForeground(null);
                    CV_3.setForeground(null);
                    SetInitialSelection(sCollegeYear);
                }
            }, 1000);

        });

        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(mContext.getString(R.string.field_college_year), sCollegeYear);
            bundle.putStringArrayList(mContext.getString(R.string.field_match_college_year), alsMatchCollegeYear);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(CollegeYearFragment.this).commit();
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

        SpotlightView.Builder SB_COLLEGE_YEAR = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Selecting College Year")
                .subHeadingTvText("Click on the number to select your college year.")
                .target(CV_1)
                .usageId("SB_COLLEGE_YEAR")
                .setListener(spotlightViewId -> {
                    if (!preferencesManager.isDisplayed("IV_BOX")) {
                        IV_SWITCH_INPUT.performClick();
                        SB_SAME_DETAIL.show();
                    }
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });

        if (!preferencesManager.isDisplayed("SWITCH")) {

            SpotlightView.Builder SB_SWITCH = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Switch Profiles")
                    .subHeadingTvText("Click to enter data in the profile of your ideal match.")
                    .target(IV_SWITCH_INPUT)
                    .usageId("SWITCH")
                    .setListener(spotlightViewId -> {
                        SB_COLLEGE_YEAR.show();
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

        } else if (!preferencesManager.isDisplayed("SB_COLLEGE_YEAR")) {
            SB_COLLEGE_YEAR.show();
        }
    }

    private void SetUnselectedCardView(CardView cardView) {
        cardView.setForeground(null);
    }

    private void SetInitialSelection(String college_year) {
        switch (college_year) {
            case "1": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.GREEN);
                CV_1.setForeground(drawable);
                break;
            }
            case "2": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.parseColor("#FB8C00"));
                CV_2.setForeground(drawable);
                break;
            }
            case "3": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(StrokeSize, Color.parseColor("#8800FF"));
                CV_3.setForeground(drawable);
                break;
            }
            default: {
                CV_1.setForeground(null);
                CV_2.setForeground(null);
                CV_3.setForeground(null);
            }
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
