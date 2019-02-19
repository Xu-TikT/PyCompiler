package com.baidu.pythoncompiler.tset;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.pythoncompiler.R;

public class TouchEventActivity extends AppCompatActivity {

    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_event);

//        scroll = findViewById(R.id.scroll);
//        scroll.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MyRel rel = findViewById(R.id.myrel);
//                rel.rectF = new RectF(scroll.getLeft(), scroll.getTop(), scroll.getRight(), scroll.getBottom());
//            }
//        },200);

//        final TextView viewById = findViewById(R.id.tv_move);
//        viewById.setMovementMethod(ScrollingMovementMethod.getInstance());
//        viewById.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int offset = viewById.getLineCount() * viewById.getLineHeight();
//                if (offset > viewById.getHeight()) {
//                    viewById.scrollTo(0, offset - viewById.getHeight());
//                }
//            }
//        }, 300);

    }

    public void test(View view) {
        Toast.makeText(TouchEventActivity.this, "click", Toast.LENGTH_SHORT).show();
    }
}
