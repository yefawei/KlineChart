package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.drawing.Drawing;
import com.yfw.kchartcore.index.range.IndexRange;


/**
 * @日期 : 2020/8/28
 * @描述 :
 */
public class ClickDrawing extends Drawing<IndexRange, KlineInfo> {

    private final TextPaint mPaint;
    private final String mTag;
    private final OnClickListener mListener;

    public ClickDrawing(String tag, OnClickListener listener) {
        mTag = tag;
        mListener = listener;
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(18);
        mPaint.setColor(Color.RED);
    }

    @Override
    public boolean canSingleTap() {
        return true;
    }

    @Override
    public boolean onSingleTap(int x, int y) {
        if (mListener != null) {
            mListener.onClick();
        }
        return true;
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        this.drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(Color.GRAY);
        float w = mPaint.measureText(mTag);
        canvas.drawText(mTag, mViewPort.left + mViewPort.width() / 2.0f - w / 2.0F,
                mViewPort.top + mViewPort.height() / 2.0f, mPaint);
    }

    public interface OnClickListener {
        void onClick();
    }
}
