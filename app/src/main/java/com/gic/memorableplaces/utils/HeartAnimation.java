package com.gic.memorableplaces.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.gic.memorableplaces.CustomLibs.shinebuttonlib.ShineButton;


public class HeartAnimation {
    private static final String TAG = "HeartAnimation";
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    public void toggleLike(ImageView heartOutline, final ShineButton heartRed) {
        Log.d(TAG, "Toggling Like");

        AnimatorSet animatorSet = new AnimatorSet();
        if (heartRed.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "Toggling red heart off");

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f, 0.1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f, 0.1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.INVISIBLE);
            heartOutline.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY, scaleDownX);


        } else if (heartRed.getVisibility() == View.INVISIBLE) {
            Log.d(TAG, "Toggling red heart On");

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.VISIBLE);
            heartOutline.setVisibility(View.INVISIBLE);
            animatorSet.playTogether(scaleDownY, scaleDownX);

            android.os.Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    heartRed.showAnim();
                    heartRed.setChecked(true);
                }
            }, 350);
        }
        animatorSet.start();
    }
}
