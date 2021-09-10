package com.gic.memorableplaces.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;



public class FollowButtonAnimation {
    private static final String TAG = "FollowButtonAnimation";
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    public void animateFollow(Button Follow, final Button UnFollow) {
        Log.d(TAG, "Toggling Like");

        AnimatorSet animatorSet = new AnimatorSet();
        if (UnFollow.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "Toggling red heart off");

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(UnFollow, "scaleY", 0.5f, 0.1f);
            scaleDownY.setDuration(1500);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(UnFollow, "scaleX", 0.5f, 0.1f);
            scaleDownY.setDuration(1500);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            UnFollow.setVisibility(View.INVISIBLE);
            Follow.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY, scaleDownX);

        } else if (UnFollow.getVisibility() == View.INVISIBLE) {
            Log.d(TAG, "Toggling red heart On");

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(UnFollow, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(UnFollow, "scaleX", 0.1f, 1f);
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            UnFollow.setVisibility(View.VISIBLE);
            Follow.setVisibility(View.INVISIBLE);
            animatorSet.playTogether(scaleDownY, scaleDownX);

//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    heartRed.showAnim();
//                    heartRed.setChecked(true);
//                }
//            }, 350);
        }
        animatorSet.start();
    }
}
