package com.baidu.pythoncompiler.tset;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.pythoncompiler.LogUtil;

/**
 * Created by tianhouchao on 2019/1/27.
 */

public class MyRel extends RelativeLayout {

    public RectF rectF;

    public MyRel(Context context) {
        this(context, null);
    }

    public MyRel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean result;


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.loge("onTouchEvent");
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.loge(" ACTION_DOWN 抬起手指：" + ev.getX() + "\t" + ev.getY());
                downX = ev.getX();
                downY = ev.getY();

                if (rectF.contains(downX, downY)) {
                    result = false;
                } else {
                    result = true;
                }
                LogUtil.loge("按下手指 result:" + result);
                LogUtil.loge("按下手指 rectF:" + rectF.toString());
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.loge("ACTION_MOVE 抬起手指：" + ev.getX() + "\t" + ev.getY());
                if (Math.abs(ev.getX() - downX) > 20 || Math.abs(ev.getY() - downY) > 20) {
                    result = false;
                } else {
                    result = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtil.loge("抬起手指：" + ev.getX() + "\t" + ev.getY());
                if (Math.abs(ev.getX() - downX) > 20 || Math.abs(ev.getY() - downY) > 20) {
                    result = false;
                } else {
                    result = true;
                    Toast.makeText(getContext(), "onTouchevent 点击了", Toast.LENGTH_SHORT).show();
                }
                LogUtil.loge("抬起手指时候的 result:" + result);
        }
        LogUtil.loge("返回的 result:" + result);
        return result;

    }

    float downX, downY;
}
