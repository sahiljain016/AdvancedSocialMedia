package com.gic.memorableplaces.utils;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "GestureDetector";
    private final RelativeLayout rl;
    private final GridView gv;
    private boolean isTopOrBottom = false;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private final LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 15f);
    private final LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 85f);
    private final LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 50f);
    private final LinearLayout.LayoutParams params_4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 50f);

    public GestureDetector(RelativeLayout rl, GridView gv, boolean bIsTopOrBottom) {
        this.rl = rl;
        this.gv = gv;
        this.isTopOrBottom = bIsTopOrBottom;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (isTopOrBottom) {
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

                rl.setLayoutParams(params_1);
                gv.setLayoutParams(params_2);
                gv.setPadding(0, 5, 0, 0);
                return true; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

                rl.setLayoutParams(params_3);
                gv.setLayoutParams(params_4);
                gv.setPadding(0, 5, 0, 0);
                return true; // Top to bottom
            }
        }else{
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                Log.d(TAG, "onFling: left to right ");
            }else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                Log.d(TAG, "onFling: right to left");
            }
        }
        return false;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
