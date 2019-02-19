package com.baidu.pythoncompiler.tset;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by tianhouchao on 2019/1/27.
 */

public class MyScroll extends ScrollView {
    public MyScroll(Context context) {
        this(context,null);
    }

    public MyScroll(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
//    }
}
