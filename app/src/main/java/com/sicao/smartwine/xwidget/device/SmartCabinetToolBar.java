package com.sicao.smartwine.xwidget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by techssd on 2017/3/14.
 */

public class SmartCabinetToolBar extends Toolbar {
    public SmartCabinetToolBar(Context context) {
        super(context);
    }

    public SmartCabinetToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartCabinetToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
