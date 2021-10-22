package com.gic.memorableplaces.CustomLibs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anshul on 24/06/15.
 */
public class ZoomRecyclerLayout extends LinearLayoutManager {
    private static final String TAG = "ZoomRecyclerLayout";
    private float mShrinkAmount = 0.15f;
    private float mShrinkDistance = 0.9f;

    public ZoomRecyclerLayout(Context context) {
        super(context);
    }

    public ZoomRecyclerLayout(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ZoomRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            float midpoint = getHeight() / 2f;
            float d0 = 0f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1f;
            float s1 = 1f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                assert child != null;
                float childMid = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2f;
                float d = Math.min(d1, (Math.abs(midpoint - childMid)));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleX(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "scrollHorizontallyBy: called");
        int orientation = getOrientation();

        if (orientation == RecyclerView.HORIZONTAL) {
            Log.d(TAG, "scrollHorizontallyBy: orientation: " + RecyclerView.HORIZONTAL);
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            float midpoint = getWidth() / 2f;
            float d0 = 0f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1f;
            float s1 = 1f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                assert child != null;
                float childMid = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f;
                float d = Math.min(d1, (Math.abs(midpoint - childMid)));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }


    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new ForceHorizontalLinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

}

class ForceHorizontalLinearSmoothScroller extends LinearSmoothScroller {

    public ForceHorizontalLinearSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDxToMakeVisible(View view, int snapPreference) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return 0;
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int left = layoutManager.getDecoratedLeft(view) - params.leftMargin;
        int right = layoutManager.getDecoratedRight(view) + params.rightMargin;
        int start = layoutManager.getPaddingLeft();
        int end = layoutManager.getWidth() - layoutManager.getPaddingRight();
        return calculateDtToFit(left, right, start, end, snapPreference);
    }
}

