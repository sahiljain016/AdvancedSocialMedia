package com.gic.memorableplaces.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.gic.memorableplaces.R;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MiscTools {
    private static final String TAG = "DatePostedLogic";
    private boolean Days = false;
    private boolean Hours = false;
    private boolean Minutes = false;
    private boolean Seconds = false;


    public String SetUpDateWidget(String ClassName, boolean comment, String TimeStamp) {
        String TimeDifference = "";
        String TimePassed = null;
        //Log.d(TAG, String.format("SetUpDateWidget: ClassName %s", ClassName));
        TimeDifference = getTimeStampDifference(ClassName, TimeStamp);
        //Log.d(TAG, String.format("SetUpDateWidget: TimeDifference %s", TimeDifference));
        // Log.d(TAG, String.format("getTimeStampDifference: photo %s", PostTimeStamp));

        if (!comment) {
            if (Days) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s day ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s days ago", TimeDifference));
                }
            } else if (Hours) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s hour ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s hours ago", TimeDifference));
                }
            } else if (Minutes) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s minute ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s minutes ago", TimeDifference));
                }
            } else if (Seconds) {
                if (TimeDifference.equals("1"))
                    TimePassed = (String.format("%s second ago", TimeDifference));
                else if (!TimeDifference.equals("0")) {
                    TimePassed = (String.format("%s seconds ago", TimeDifference));
                }
            }
        } else {
            if (Days) {
                TimePassed = (String.format("%sd", TimeDifference));
            } else if (Hours) {

                TimePassed = (String.format("%sh", TimeDifference));

            } else if (Minutes) {
                TimePassed = (String.format("%sm", TimeDifference));

            } else if (Seconds) {

                TimePassed = (String.format("%ss", TimeDifference));

            }
        }
        return TimePassed;
    }

    public String getTimeStampDifference(String Date_added, String TimeStamp) {

        String difference = "";
        int differenceD = 0, differenceH = 0, differenceM = 0, differenceS = 0;
        // Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        Date today = null;
        try {
            today = sdf.parse(TimeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //sdf.format(today);
        Date timeStamp;


        try {
            timeStamp = sdf.parse(Date_added);
            assert timeStamp != null;
            // Log.d(TAG, "getTimeStampDifference: timeStamp " + timeStamp.getTime());
            // Log.d(TAG, "getTimeStampDifference: today " + today.getTime());
            differenceD = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60 / 60 / 24);
            // Log.d(TAG, "getTimeStampDifference: differenceD " + differenceD);
            difference = String.valueOf(differenceD);
            Days = true;
            Hours = false;
            Minutes = false;
            Seconds = false;
            // Log.d(TAG, String.format("getTimeStampDifference: difference %s", difference));
            if (differenceD == 0) {

                differenceH = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60 / 60);
                difference = String.valueOf(differenceH);
                // Log.d(TAG, "getTimeStampDifference: differenceH " + differenceH);
                Days = false;
                Hours = true;
                Minutes = false;
                Seconds = false;
            }
            if (differenceH > 24 || differenceH == 0 && differenceD == 0) {
                differenceM = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000 / 60);
                difference = String.valueOf(differenceM);
                //  Log.d(TAG, "getTimeStampDifference: differenceH " + differenceM);
                Days = false;
                Hours = false;
                Minutes = true;
                Seconds = false;
            }
            if (differenceM > 60 || differenceM == 0 && differenceD == 0 && differenceH == 0) {
                differenceS = Math.round((today.getTime() - Objects.requireNonNull(timeStamp).getTime()) / 1000);
                difference = String.valueOf(differenceS);
                //  Log.d(TAG, "getTimeStampDifference: differenceH " + differenceS);
                Days = false;
                Hours = false;
                Minutes = false;
                Seconds = true;
            }

        } catch (ParseException e) {
            // Log.d(TAG, "getTimeStampDifference: ParseException " + e.getMessage());
            difference = "0";
        }
        //Log.d(TAG, String.format("getTimeStampDifference: difference %s", difference));
        return difference;
    }

    public static void InflateBalloonTooltip(Context mContext, String Title, int icon, View v) {
        final Balloon balloon;
        if (icon == 0) {
            balloon = new Balloon.Builder(mContext)
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(BalloonSizeSpec.WRAP)
                    .setTextSize(18f)
                    .setPadding(10)
                    .setCornerRadius(4f)
                    .setAlpha(0.9f)
                    .setText(Title)
                    .setTextColor(Color.WHITE)
                    .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                    .build();
        } else {
            balloon = new Balloon.Builder(mContext)
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(BalloonSizeSpec.WRAP)
                    .setTextSize(18f)
                    .setPadding(10)
                    .setCornerRadius(4f)
                    .setAlpha(0.9f)
                    .setText(Title)
                    .setTextColor(Color.WHITE)
                    .setIconDrawable(ContextCompat.getDrawable(mContext, icon))
                    .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                    .build();
        }
        balloon.showAlignTop(v);
    }

    public static int convertDpToPixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int GetFilterIcon(String DetailName, Context mContext) {
        int icon = 0;
        if (DetailName.equals(mContext.getString(R.string.field_age))) {
            icon = R.drawable.ic_age_filter;
        } else if (DetailName.equals(mContext.getString(R.string.field_course))) {
            icon = R.drawable.ic_course_icon;
        } else if (DetailName.equals(mContext.getString(R.string.field_gender))) {
            icon = R.drawable.ic_gender_symbol;
        } else if (DetailName.equals(mContext.getString(R.string.field_college_year))) {
            icon = R.drawable.ic_university;
        } else if (DetailName.equals(mContext.getString(R.string.field_zodiac_sign))) {
            icon = R.drawable.ic_zodiac_filter;
        } else if (DetailName.equals(mContext.getString(R.string.field_hobbies))) {
            icon = R.drawable.ic_hobbies;
        } else if (DetailName.equals(mContext.getString(R.string.field_games))) {
            icon = R.drawable.ic_controller;
        } else if (DetailName.equals(mContext.getString(R.string.field_music))) {
            icon = R.drawable.ic_music;
        } else if (DetailName.equals(mContext.getString(R.string.field_movie))) {
            icon = R.drawable.ic_movie;
        } else if (DetailName.equals(mContext.getString(R.string.field_books))) {
            icon = R.drawable.ic_book;
        } else if (DetailName.equals(mContext.getString(R.string.field_society))) {
            icon = R.drawable.ic_society_filter;
        } else if (DetailName.equals(mContext.getString(R.string.field_pronouns))) {
            icon = R.drawable.ic_gender_symbol;
        }
        return icon;
    }

    public static String GetTimeStamp(long milliseconds){

        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return formatter.format(calendar.getTime());
    }

    public static String GetKeyForFilter(String DetailName, Context mContext) {
        String key = "";
        if (DetailName.equals(mContext.getString(R.string.field_games))) {
            key = mContext.getString(R.string.field_game_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_music))) {
            key = mContext.getString(R.string.field_track_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_movie))) {
            key = mContext.getString(R.string.field_movie_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_books))) {
            key = mContext.getString(R.string.field_book_name);
        } else if (DetailName.equals(mContext.getString(R.string.field_society))) {
            key = mContext.getString(R.string.society_name);
        }
        return key;
    }

    public static String ChangeKeyForFirebase(String key) {
        String newkey = key.replaceAll("\\.", " ");
        newkey = newkey.replaceAll("#", " ");
        newkey = newkey.replaceAll("\\$", " ");
        newkey = newkey.replaceAll("\\[", " ");
        newkey = newkey.replaceAll("]", " ");

        return newkey;
    }
}
