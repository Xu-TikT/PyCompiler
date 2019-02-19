package com.baidu.pythoncompiler.tset;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.pythoncompiler.R;


/**
 * Created by tianhouchao on 2019/1/19.
 */

public class EmotionView extends RelativeLayout {

    private ImageView ivEmotion;
    private ImageView ivLoading;
    private ImageView ivTextOutput;
    private ImageView ivSpeaking;
    public ScrollView mScrollOcrTextResultOutput;
    private TextView mTvTextResultOutput;
    private Button btnMenuController;
    private OnCoverShowListener coverShowListener;
    private boolean isCanShow;
    private boolean isShowing;
    private CountDownTimer countDownTimer;

    public class MenuControllerClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isCanShow && coverShowListener != null) {
                if (isShowing) {
                    isShowing = false;
                    coverShowListener.onCoverDismiss();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                } else {
                    isShowing = true;
                    coverShowListener.onCoverShow();
                    hide();
                }
            }
        }
    }

    public EmotionView(Context context) {
        this(context, null);
    }

    public EmotionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = LayoutInflater.from(context).
                inflate(R.layout.ea_plugin_codeonline_emotion_view_layout, this, true);
        ivEmotion = inflate.findViewById(R.id.iv_emotion);
        ivLoading = inflate.findViewById(R.id.iv_loading);
        ivSpeaking = inflate.findViewById(R.id.iv_speaking);
        ivTextOutput = inflate.findViewById(R.id.iv_text_output);
        mScrollOcrTextResultOutput = inflate.findViewById(R.id.scroll_ocr_result_output);
        mTvTextResultOutput = inflate.findViewById(R.id.tv_ocr_text_result_output);
        mScrollOcrTextResultOutput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"草，什么几把玩意", Toast.LENGTH_SHORT).show();
            }
        });
        btnMenuController = inflate.findViewById(R.id.menu_controller_btn);
        btnMenuController.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"吗的 ，草，什么几把玩意", Toast.LENGTH_SHORT).show();
//                if (isCanShow && coverShowListener != null) {
//                    if (isShowing) {
//                        isShowing = false;
//                        coverShowListener.onCoverDismiss();
//                        if (countDownTimer != null) {
//                            countDownTimer.cancel();
//                            countDownTimer = null;
//                        }
//                    } else {
//                        isShowing = true;
//                        coverShowListener.onCoverShow();
//                        hide();
//                    }
//                }
            }
        });
    }

    public void playEmotion(int status) {
        hideChild();
        ivEmotion.setVisibility(View.VISIBLE);
    }

    public void playLoading() {
        hideChild();
        ivLoading.setVisibility(View.VISIBLE);
    }

    public void playDefault() {
        hideChild();
        ivLoading.setVisibility(View.VISIBLE);
    }

    public void playTextOutput(String outputMessage) {
        hideChild();
        ivTextOutput.setVisibility(View.VISIBLE);
        mScrollOcrTextResultOutput.setVisibility(View.VISIBLE);
        mTvTextResultOutput.setText(outputMessage);
//        mTvTextResultOutput.setMovementMethod(ScrollingMovementMethod.getInstance());
        mScrollOcrTextResultOutput.fullScroll(ScrollView.FOCUS_DOWN);


//        mTvTextResultOutput.postDelayed(() -> {
//            int offset = mTvTextResultOutput.getLineCount() * mTvTextResultOutput.getLineHeight();
//            if (offset > mTvTextResultOutput.getHeight()) {
//                mTvTextResultOutput.scrollTo(0, offset - mTvTextResultOutput.getHeight());
//            }
//        }, 100);
    }

    public void playSpeaking() {
        hideChild();
        ivSpeaking.setVisibility(View.VISIBLE);
    }

    public void stopTextOutput() {
        ivTextOutput.setVisibility(View.GONE);
        mScrollOcrTextResultOutput.setVisibility(View.GONE);
        mTvTextResultOutput.setText("");
    }

    public void hideChild() {
        mScrollOcrTextResultOutput.setVisibility(View.GONE);
        ivTextOutput.setVisibility(View.GONE);
        ivEmotion.setVisibility(View.GONE);
        ivLoading.setVisibility(View.GONE);
        ivSpeaking.setVisibility(View.GONE);
    }

    public void stopLoading() {
        ivLoading.setVisibility(View.GONE);
    }


    public void setCoverListener(OnCoverShowListener listener) {
        this.coverShowListener = listener;
    }

    public interface OnCoverShowListener {
        void onCoverShow();

        void onCoverDismiss();
    }

    public void setCanShow(boolean canShow) {
        isCanShow = canShow;
    }

    public void dissmissCover() {
        if (coverShowListener != null) {
            coverShowListener.onCoverDismiss();
        }
    }

    private void hide() {
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                isShowing = false;
                if (coverShowListener != null) {
                    coverShowListener.onCoverDismiss();
                }
            }
        };
//        countDownTimer.start();
    }
}
