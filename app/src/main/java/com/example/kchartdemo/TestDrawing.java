package com.example.kchartdemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import androidx.annotation.ColorInt;

import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.utils.FontCalculateUtils;

/**
 * @日期 : 2020/7/14
 * @描述 :
 */
public class TestDrawing extends Drawing {

    private final Paint mPaint;
    private final TextPaint mTextPaint;
    private final String mMsg;

    public TestDrawing(@ColorInt int bgColor, @ColorInt int strColor, String msg, IDrawingPortLayout.DrawingLayoutParams params) {
        super(null, params);
        mMsg = msg;

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(strColor);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bgColor);

    }

    @Override
    public void drawing(Canvas canvas) {
        canvas.drawRect(mViewPort, mPaint);
        float w = mTextPaint.measureText(mMsg);
        float center = FontCalculateUtils.getBaselineFromCenter(mTextPaint, mViewPort.height() * 1.0f / 2 + mViewPort.top);
        canvas.drawText(mMsg, (mViewPort.width() - w) / 2 + mViewPort.left, center, mTextPaint);
    }
}
