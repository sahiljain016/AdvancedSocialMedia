package com.gic.memorableplaces.utils;

import android.util.Log;
import android.widget.EditText;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringManipulation {
    private static final String TAG = "StringManipulation";

    public static String expandUsername(String username) {
        return username.replace(".", " ");
    }

    public static String comdenseUsername(String username) {
        return username.replace(" ", ".");
    }

    public static String findEmptyLines(String Caption) {
        return Caption.replace("\n", "<Br>");
    }

    public static String getTags(String tag) {
        Log.d(TAG, "getTags: tags  " + tag);
        if(tag.contains("SXC#")){
        Pattern MY_PATTERN = Pattern.compile("SXC#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(tag);
        StringBuilder stringBuilder = new StringBuilder();
        while (mat.find()) {
           // Log.d(TAG, "getTags: tags " + "SXC#"+mat.group(1));
            stringBuilder.append("SXC#").append(mat.group(1));

        }
        String s = stringBuilder.toString().replace(" ", "").replace("SXC#", ",SXC#");
        return s.substring(1, s.length());}
        return tag;
    }
    public static int ordinalIndexOf(String str, char substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }
}
