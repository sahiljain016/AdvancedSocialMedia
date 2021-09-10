package com.gic.memorableplaces.CustomLibs;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TightTextView extends androidx.appcompat.widget.AppCompatTextView {
    public TightTextView(Context context) {
        super(context);
    }

    public TightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int specModeW = MeasureSpec.getMode(widthMeasureSpec);
        if (specModeW != MeasureSpec.EXACTLY) {
            Layout layout = getLayout();
            if (layout != null) {
                int w = (int) Math.ceil(getMaxLineWidth(layout)) + getCompoundPaddingLeft() + getCompoundPaddingRight();
                if (w < getMeasuredWidth()) {
                    super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                            heightMeasureSpec);
                }
            }
        }
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}
