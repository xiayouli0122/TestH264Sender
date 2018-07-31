package com.harsom.baselib.glide.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by ZouWei on 2017/6/29.
 */

public class GlideLoadingProgressBar extends View {

    //第一层颜色
    private int mFirstColor = 0xffdadada;
    //第二层颜色
    private int mSecondColor = 0xf2f2f2;
    //圆的宽度
    private int mCircleWidth = 10;
    //画笔
    private Paint mPaint;
    //进度
    private int mProgress;

    /**
     * 是否应该开始下一个
     */
    private boolean isNext = true;

    public GlideLoadingProgressBar(Context context) {
        this(context, null);
    }

    public GlideLoadingProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GlideLoadingProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
    }

    /**
     * 进度颜色
     */
    public void setFirstColor(int color) {
        this.mFirstColor = color;
    }

    /**
     * 圆环底色
     */
    public void setSecondColor(int color) {
        this.mSecondColor = color;
    }

    /**
     * 圆环宽度
     */
    public void setCircleWidth(int width) {
        this.mCircleWidth = width;
    }

    public void setProgress(int progress) {
        mProgress = (int) (360 * progress * 0.01);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = centre - mCircleWidth / 2;// 半径
        mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
        if (!isNext) {// 第一颜色的圈完整，第二颜色跑
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
        } else {
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
        }
    }


}
