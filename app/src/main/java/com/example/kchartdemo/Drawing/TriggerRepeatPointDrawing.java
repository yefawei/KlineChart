package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.layout.IParentPortLayout;
import com.yfw.kchartext.drawing.RepeatAnimDrawing;
import com.yfw.kchartext.drawing.TriggerRepeatAnimDrawing;
import com.yfw.kchartext.utils.FontCalculateUtils;

/**
 * @日期 : 2020/9/25
 * @描述 : 手动触发重复点动画
 */
public class TriggerRepeatPointDrawing extends TriggerRepeatAnimDrawing<IndexRange, KlineInfo> {

    private boolean isOpen;
    private final Paint mPaint;

    public TriggerRepeatPointDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
        setRepeatMode(RepeatAnimDrawing.REVERSE);
        setInterpolator(new AccelerateDecelerateInterpolator());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(54);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<KlineInfo> dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        Log.e("TriggerRepeatPoint", "attachedParentPortLayout");
    }

    @Override
    public void detachedParentPortLayout() {
        super.detachedParentPortLayout();
        Log.e("TriggerRepeatPoint", "detachedParentPortLayout");
    }

    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        super.updateViewPort(left, top, right, bottom);
    }

    @Override
    public boolean canSingleTap() {
        return true;
    }

    @Override
    public boolean onSingleTap(int x, int y) {
        if (x > getLeft() && x < (getLeft() + 200) && y > getTop() && y < (getTop() + 100)) {
            isOpen = !isOpen;
            if (isOpen) {
                restartCycle();
                startRepeatAnim(Integer.MAX_VALUE, 1_000);
            } else {
                stopRepeatAnim();
            }
            return true;
        }
        if (x < getRight() && x > (getRight() - 200) && y > getTop() && y < (getTop() + 100)) {
            if (isPause()) {
                resume();
            } else {
                pause();
            }
            return true;
        }
        return false;
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        drawData(canvas);
    }

    private String open = "开";
    private String close = "关";
    private String resume = "恢复";
    private String stop = "暂停";

    @Override
    public void drawData(Canvas canvas) {
        mPaint.setAlpha(255);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(getLeft(), getTop(), 200, 100, mPaint);
        canvas.drawRect(getRight() - 200, getTop(), getRight(), 100, mPaint);
        mPaint.setColor(Color.BLACK);
        if (isOpen) {
            float x = getLeft() + 100 - mPaint.measureText(close) / 2;
            float y = FontCalculateUtils.getBaselineFromCenter(mPaint, getTop() + 50);
            canvas.drawText(close, x, y, mPaint);
        } else {
            float x = getLeft() + 100 - mPaint.measureText(open) / 2;
            float y = FontCalculateUtils.getBaselineFromCenter(mPaint, getTop() + 50);
            canvas.drawText(open, x, y, mPaint);
        }

        if (isPause()) {
            float x = getRight() - 100 - mPaint.measureText(resume) / 2;
            float y = FontCalculateUtils.getBaselineFromCenter(mPaint, getTop() + 50);
            canvas.drawText(resume, x, y, mPaint);
        } else {
            float x = getRight() - 100 - mPaint.measureText(stop) / 2;
            float y = FontCalculateUtils.getBaselineFromCenter(mPaint, getTop() + 50);
            canvas.drawText(stop, x, y, mPaint);
        }
        if (inAnim()) {
            float animProcess = getAnimProcess();
            float x = mViewPort.centerX();
            float y = mViewPort.centerY();
            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha(123);
            canvas.drawCircle(x, y, animProcess * mViewPort.height() / 4, mPaint);
        }
    }
}
