package com.gic.memorableplaces.utils;

import android.os.Build;

/**
 * @author JoongWon Baik
 */
public class Platform {
    public static boolean hasAndroidR() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }
    public static boolean greaterThanP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }
}
