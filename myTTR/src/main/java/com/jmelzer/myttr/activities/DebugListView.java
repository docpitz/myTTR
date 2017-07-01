package com.jmelzer.myttr.activities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import static com.jmelzer.myttr.Constants.LOG_TAG;

public class DebugListView extends ListView {
    private static final int ROW_HEIGHT = 200;
    private int rows;

    public DebugListView(Context context) {
        super(context);
    }

    public void setRows(int rows) {
        this.rows = rows;
        Log.d(LOG_TAG, "rows set: " + rows);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), rows * ROW_HEIGHT);
        Log.d(LOG_TAG, "onMeasure " +
                ": width: " + decodeMeasureSpec(widthMeasureSpec) +
                "; height: " + decodeMeasureSpec(heightMeasureSpec) +
                "; measuredHeight: " + getMeasuredHeight() +
                "; measuredWidth: " + getMeasuredWidth());
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(LOG_TAG, "onLayout " + ": changed: " + changed + "; left: " + left + "; top: " + top + "; right: " + right + "; bottom: " + bottom);
    }

    private String decodeMeasureSpec(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        String modeString = "<> ";
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                modeString = "UNSPECIFIED ";
                break;

            case MeasureSpec.EXACTLY:
                modeString = "EXACTLY ";
                break;

            case MeasureSpec.AT_MOST:
                modeString = "AT_MOST ";
                break;
        }
        return modeString + Integer.toString(MeasureSpec.getSize(measureSpec));
    }


}

