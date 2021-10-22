

package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.jem.rubberpicker.RubberRangePicker;

import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

public class AgeAndBirthdateFragment extends Fragment {
    private static final String TAG = "AgeAndBirthdateFragment";
    private Context mContext;
    private boolean isLocked = false, isCalendarDate = true;

    private long lMyAge;
    private String sBirthdate = "N/A";

    private AutofitTextView ATV_AGE;
    private EditText ET_DATE, ET_MONTH, ET_YEAR;
    private ImageView IV_PRIVACY;
    private DatePicker DP_AB;
    private MotionLayout ML;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_age_and_birthdate, container, false);
        mContext = getActivity();
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_AB);
        AutofitTextView ATV_BIRTHDATE = view.findViewById(R.id.ATV_BIRTHDATE);
        ATV_AGE = view.findViewById(R.id.ATV_AGE);
        ET_DATE = view.findViewById(R.id.ET_DATE_AB);
        ET_MONTH = view.findViewById(R.id.ET_MONTH_AB);
        ET_YEAR = view.findViewById(R.id.ET_YEAR_AB);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_AB);
        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_AB);
        DP_AB = view.findViewById(R.id.DP_AB);
        ML = view.findViewById(R.id.ML_MAIN_AB);
        RubberRangePicker RSB = view.findViewById(R.id.RSB_AGE_AB);
        ImageFilterView IFV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_AB);
        AutofitTextView ATV_AGE_RANGE_TITLE = view.findViewById(R.id.ATV_AGE_SELECTION_AB);





        String sOtherAgeRange = "N/A";
        if (getArguments() != null) {
            lMyAge = getArguments().getLong(mContext.getString(R.string.field_my_age));
            sOtherAgeRange = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_match_age_range)));
            sBirthdate = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_birth_date)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        Log.d(TAG, "onCreateView: isLocked: " + isLocked);


        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setTypeface(tf);

        ATV_AGE.setTypeface(cap);
        ATV_BIRTHDATE.setTypeface(cap);

        SetAgeAndBirthdate();
        SetPrivacyDialog();
        SetEditTextListeners();

        IFV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(mContext.getString(R.string.field_match_age_range), RSB.getCurrentStartValue() + "-" + RSB.getCurrentEndValue());
            bundle.putString(mContext.getString(R.string.field_birth_date), sBirthdate);
            bundle.putLong(mContext.getString(R.string.field_age), lMyAge);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(AgeAndBirthdateFragment.this).commit();
        });
        IV_SWITCH_INPUT.setOnClickListener(v -> {
            if (ML.getProgress() == 0.0) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
            }
        });


        if (sOtherAgeRange.equals("N/A")) {

            RSB.setCurrentStartValue(18);
            RSB.setCurrentEndValue(25);

            ATV_AGE_RANGE_TITLE.setText("Age between " + 18 + " and " + 25);
        } else {

            int StartValue = Integer.parseInt(sOtherAgeRange.substring(0, sOtherAgeRange.indexOf("-")));
            RSB.setCurrentStartValue(StartValue);
            int EndValue = Integer.parseInt(sOtherAgeRange.substring(sOtherAgeRange.indexOf("-") + 1));
            RSB.setCurrentEndValue(EndValue);

            ATV_AGE_RANGE_TITLE.setText("Age between " + StartValue + " and " + EndValue);
        }
        RSB.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NonNull RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                ATV_AGE_RANGE_TITLE.setText("Age between " + i + " and " + i1);
            }

            @Override
            public void onStartTrackingTouch(@NonNull RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

        return view;
    }


    private void SetAgeAndBirthdate() {
        DP_AB.setOnDateChangedListener((view, year1, monthOfYear, dayOfMonth) -> {
            if (isCalendarDate) {
                sBirthdate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                int mYear = c.get(Calendar.YEAR);
                ATV_AGE.setText("Age: " + ((mYear - year1) - 1) + " years old");
                lMyAge = ((mYear - year1) - 1);

                //  Log.d(TAG, "onDateChanged: month: " + monthOfYear);
                if (dayOfMonth < 10)
                    ET_DATE.setText("0" + dayOfMonth);
                else
                    ET_DATE.setText(String.valueOf(dayOfMonth));

                if ((monthOfYear + 1) < 10)
                    ET_MONTH.setText("0" + (monthOfYear + 1));
                else
                    ET_MONTH.setText(String.valueOf(monthOfYear + 1));
                isCalendarDate = true;
                //isTyped = false;

                Log.d(TAG, "SetAgeAndBirthdate: day: " + dayOfMonth + " month: " + monthOfYear + " year: " + year1);
                ET_YEAR.setText(String.valueOf(year1));
            }
            isCalendarDate = true;
        });

        int date, month, year;

        if (!TextUtils.isEmpty(sBirthdate) && !sBirthdate.equals("N/A")) {
            Log.d(TAG, "SetAgeAndBirthdate: date: " + Integer.parseInt(sBirthdate.substring(0, 2)));
            date = Integer.parseInt(sBirthdate.substring(0, 2));
            Log.d(TAG, "SetAgeAndBirthdate: date: " + Integer.parseInt(sBirthdate.substring(3, 5)));
            month = Integer.parseInt(sBirthdate.substring(3, 5));
            Log.d(TAG, "SetAgeAndBirthdate: date: " + Integer.parseInt(sBirthdate.substring(6)));
            year = Integer.parseInt(sBirthdate.substring(6));

            DP_AB.updateDate(year, month - 1, date);
        } else {
            long estimatedServerTimeMs = System.currentTimeMillis();
            DP_AB.setMaxDate(estimatedServerTimeMs);
        }

        DP_AB.setSoundEffectsEnabled(true);


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

    private void SetEditTextListeners() {
        ET_DATE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s.toString()) && s.length() == 2) {
                    if (Integer.parseInt(s.toString()) > 31 || Integer.parseInt(s.toString()) < 1) {

                        MiscTools.InflateBalloonTooltip(mContext, "Dates are not greater than 31!", 0, ET_DATE);
                    }
                }
            }
        });

        ET_MONTH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s.toString()) && s.length() == 2) {
                    if (Integer.parseInt(s.toString()) > 12 && Integer.parseInt(s.toString()) < 1) {

                        MiscTools.InflateBalloonTooltip(mContext, "There are only 12 Months!", 0, ET_MONTH);
                    }
                }
            }
        });

        ET_YEAR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4 && !isCalendarDate) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(DP_AB.getMaxDate());
                    int mYear = c.get(Calendar.YEAR);
                    if (Integer.parseInt(s.toString()) <= mYear) {


                        DP_AB.updateDate(Integer.parseInt(ET_YEAR.getText().toString()), Integer.parseInt(ET_MONTH.getText().toString()) - 1, Integer.parseInt(ET_DATE.getText().toString()));

                        sBirthdate = DP_AB.getDayOfMonth() + "/" + (DP_AB.getMonth() + 1) + "/" + DP_AB.getYear();
                        ATV_AGE.setText("Age: " + ((mYear - DP_AB.getYear()) - 1) + " years old");
                        lMyAge = ((mYear - DP_AB.getYear()) - 1);

                        // Log.d(TAG, "afterTextChanged: day: " + DP_AB.getDayOfMonth());
                        //Log.d(TAG, "afterTextChanged: month: " + DP_AB.getMonth());
                        //Log.d(TAG, "afterTextChanged: years: " + DP_AB.getYear());
                        isCalendarDate = true;
                        return;
                    } else {
                        MiscTools.InflateBalloonTooltip(mContext, "What are you a time traveller?", 0, ET_YEAR);

                    }
                }

                isCalendarDate = false;
            }
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
