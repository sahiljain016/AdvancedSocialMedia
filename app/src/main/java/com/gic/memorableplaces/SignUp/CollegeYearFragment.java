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

import me.grantland.widget.AutofitTextView;

public class CollegeYearFragment extends Fragment {
    private static final String TAG = "CollegeYearFragment";
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;

    private String sCollegeYear = "N/A", sMatchCollegeYear = "N/A";
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
            sMatchCollegeYear = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_match_college_year)));
            sCollegeYear = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_college_year)));

            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }

        SetInitialSelection(sCollegeYear);

        CV_1.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_1_CENTER);
            ML.transitionToEnd();
            CV_1.setForeground(null);
            CV_2.setForeground(null);
            CV_3.setForeground(null);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(100);
            drawable.setStroke(5, Color.GREEN);
            CV_1.setForeground(drawable);
            if (isSwitched) {
                sMatchCollegeYear = "1";
            } else {
                sCollegeYear = "1";
            }
        });

        CV_2.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_2_CENTER);
            ML.transitionToEnd();
            CV_1.setForeground(null);
            CV_2.setForeground(null);
            CV_3.setForeground(null);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(100);
            drawable.setStroke(5, Color.parseColor("#FB8C00"));
            CV_2.setForeground(drawable);
            if (isSwitched) {
                sMatchCollegeYear = "2";
            } else {
                sCollegeYear = "2";
            }
        });

        CV_3.setOnClickListener(v -> {
            ML.setTransition(R.id.TRANS_3_CENTER);
            ML.transitionToEnd();
            CV_1.setForeground(null);
            CV_2.setForeground(null);
            CV_3.setForeground(null);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(100);
            drawable.setStroke(5, Color.parseColor("#8800FF"));
            CV_3.setForeground(drawable);
            if (isSwitched) {
                sMatchCollegeYear = "3";
            } else {
                sCollegeYear = "3";
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
                sMatchCollegeYear = sCollegeYear;
                SetInitialSelection(sMatchCollegeYear);
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
                    SetInitialSelection(sMatchCollegeYear);

                } else {
                    ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    ML.transitionToStart();
                    ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                    IV_GO_BACK.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    isSwitched = false;
                    SetInitialSelection(sCollegeYear);
                }
            }, 1000);

        });

        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(mContext.getString(R.string.field_college_year), sCollegeYear);
            bundle.putString(mContext.getString(R.string.field_match_college_year), sMatchCollegeYear);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(CollegeYearFragment.this).commit();
        });
        return view;
    }

    private void SetUnselectedCardView(CardView cardView) {
        cardView.setForeground(null);
    }

    private void SetInitialSelection(String college_year) {
        switch (college_year) {
            case "1": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(5, Color.GREEN);
                CV_1.setForeground(drawable);
                break;
            }
            case "2": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(5, Color.parseColor("#FB8C00"));
                CV_2.setForeground(drawable);
                break;
            }
            case "3": {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(100);
                drawable.setStroke(5, Color.parseColor("#8800FF"));
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
