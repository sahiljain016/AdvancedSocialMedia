package com.gic.memorableplaces.CustomLibs.CardStack;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;



public class SwipeableTouchHelperCallback extends ItemTouchHelper.Callback {

    private final OnItemSwiped onItemSwiped;

    public SwipeableTouchHelperCallback(OnItemSwiped onItemSwiped) {
        super();
        this.onItemSwiped = onItemSwiped;
    }

    public int getAllowedSwipeDirectionsMovementFlags() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
        return getAllowedSwipeDirectionsMovementFlags();
    }

    public int getAllowedDirectionsMovementFlags(RecyclerView.ViewHolder holder) {
        return getAllowedDirectionsMovementFlags();
    }

    public int getAllowedDirectionsMovementFlags() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ;
    }

    @Override
    public final int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0,
                viewHolder.getAdapterPosition() != 0 ? 0 : getAllowedDirectionsMovementFlags(viewHolder));
    }

    public final float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getWidth() * 0.9f;
    }

    @Override
    public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                RecyclerView.ViewHolder target) {
        return false;
    }

    @Override public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int allowedSwipeDirections = getAllowedSwipeDirectionsMovementFlags(viewHolder);
        if (direction == ItemTouchHelper.LEFT && (allowedSwipeDirections & ItemTouchHelper.LEFT) != 0) {
            onItemSwiped.onItemSwipedLeft();
            onItemSwiped.onItemSwiped();
        } else if (direction == ItemTouchHelper.RIGHT
                && (allowedSwipeDirections & ItemTouchHelper.RIGHT) != 0) {
            onItemSwiped.onItemSwipedRight();
            onItemSwiped.onItemSwiped();
        }
        viewHolder.itemView.invalidate();
    }

    @Override public final float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }

    @Override public final long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                                     float animateDx, float animateDy) {
        return ((SwipeableLayoutManager) recyclerView.getLayoutManager()).getAnimationDuratuion();
    }

    @Override public final void onChildDraw(Canvas c, RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                            boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipValue / getThreshold(viewHolder);
        fraction = Math.min(1, fraction);

        if (viewHolder instanceof OnItemSwipePercentageListener) {
            ((OnItemSwipePercentageListener) viewHolder).onItemSwipePercentage(
                    Math.max(-1, Math.min(1, dX / recyclerView.getMeasuredWidth())));
        }

        SwipeableLayoutManager swipeableLayoutManager =
                (SwipeableLayoutManager) recyclerView.getLayoutManager();

        int childCount = recyclerView.getChildCount();
        if (viewHolder.getAdapterPosition() == 0) {
            viewHolder.itemView.setRotation(
                    swipeableLayoutManager.getAngle() * (dX / recyclerView.getMeasuredWidth()));
            viewHolder.itemView.setScaleX(1);
            viewHolder.itemView.setScaleY(1);
        }

        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            int level = childCount - i - 1;

            if (level > 0) {
                float scale = Math.max(0, Math.min(1,
                        (float) (1 - swipeableLayoutManager.getScaleGap() * level
                                + fraction * swipeableLayoutManager.getScaleGap())));
                child.setScaleX(scale);

                if (level < swipeableLayoutManager.getMaxShowCount() - 1) {
                    child.setScaleY(scale);
                    child.setTranslationY(Math.max(0, (float) (swipeableLayoutManager.getTransYGap() * level
                            - fraction * swipeableLayoutManager.getTransYGap())));
                }
            }
        }
    }

    @Override
    public final void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0);
        viewHolder.itemView.setScaleX(1);
        viewHolder.itemView.setScaleY(1);
    }
}