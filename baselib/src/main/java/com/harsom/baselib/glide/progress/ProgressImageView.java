package com.harsom.baselib.glide.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by chenpengfei on 2016/11/9.
 */
public class ProgressImageView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mProgressTextView;
    private GlideLoadingProgressBar mProgressBar;
    private int mProgress;

    public ProgressImageView(Context context) {
        super(context);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mImageView = new ImageView(context);
        LayoutParams ivLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ivLp.addRule(CENTER_IN_PARENT);
        mImageView.setLayoutParams(ivLp);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mImageView);

        mProgressBar = new GlideLoadingProgressBar(getContext());
        LayoutParams barLp = new LayoutParams(100, 100);
        barLp.addRule(CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(barLp);
        mProgressBar.setVisibility(GONE);
//        mProgressBar.setFirstColor(R.color.colorPrimary);
//        mProgressBar.setSecondColor(R.color.video_riddle_bg_color);
        addView(mProgressBar);

        mProgressTextView = new TextView(context);
//        mProgressTextView.setTextSize(10);
//        mProgressTextView.setTextColor(Color.RED);
//        LayoutParams tvLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        tvLp.addRule(CENTER_IN_PARENT);
//        mProgressTextView.setLayoutParams(tvLp);
//        mProgressTextView.setVisibility(GONE);
//        addView(mProgressTextView);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(int progress) {
        if (mProgressBar.getVisibility() == GONE || mProgressTextView.getVisibility() == GONE) {
            mProgressBar.setVisibility(VISIBLE);
            mProgressTextView.setVisibility(VISIBLE);
        }
        if (progress == 100) {
            mProgressBar.setVisibility(GONE);
            mProgressTextView.setVisibility(GONE);
            return;
        }
        this.mProgress = progress;
        mProgressTextView.setText(mProgress + "%");
        mProgressBar.setProgress(mProgress);
    }

    public ImageView getImageView() {
        return mImageView;
    }
}
