package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.drawing.Drawing;


/**
 * @日期 : 2020/8/28
 * @描述 :
 */
public class PositionDrawing extends Drawing<KlineInfo> {

    private final TextPaint mPaint;
    private final String mTag;
    private final int mBackgroundColor;

    public PositionDrawing(String tag, int textColor, int backgroundColor, RendererCanvas.DrawingLayoutParams params) {
        super(params);
        mTag = tag;
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(36);
        mPaint.setColor(textColor);
        mBackgroundColor = backgroundColor;
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        this.drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);
        float w = mPaint.measureText(mTag);
        canvas.drawText(mTag, mViewPort.left + mViewPort.width() / 2.0f - w / 2.0F,
                mViewPort.top + mViewPort.height() / 2.0f, mPaint);
    }

    public interface OnClickListener {
        void onClick();
    }
}
