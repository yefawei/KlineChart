package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.RepeatAnimDrawing;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/9/27
 * @描述 : 自动触发重复点动画
 */
public class RepeatPointDrawing extends RepeatAnimDrawing<IndexRange> {

    private final Paint mPaint;

    public RepeatPointDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
        setRepeatMode(RepeatAnimDrawing.REVERSE);
        setInterpolator(new AccelerateDecelerateInterpolator());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(54);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        Log.e("RepeatPointDrawing", "attachedParentPortLayout");
    }

    @Override
    public void detachedParentPortLayout() {
        super.detachedParentPortLayout();
        Log.e("RepeatPointDrawing", "detachedParentPortLayout");
    }

    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        super.updateViewPort(left, top, right, bottom);
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        if (inAnim()) {
            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha(123);
            float animProcess = getAnimProcess();

            int radius = mViewPort.height() / 8;
            float x = mViewPort.width() / 4.0f;
            float y = mViewPort.centerY();
            canvas.drawCircle(x, y, animProcess * radius, mPaint);

            x = mViewPort.width() / 4.0f * 3.0f;
            y = mViewPort.centerY();
            canvas.drawCircle(x, y, animProcess * radius, mPaint);
        }
    }
}