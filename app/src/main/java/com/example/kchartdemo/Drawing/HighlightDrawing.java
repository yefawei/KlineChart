package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.AbstractAnimDrawing;
import com.benben.kchartlib.drawing.TriggerRepeatAnimDrawing;
import com.benben.kchartlib.index.range.CandleIndexRange;
import com.benben.kchartlib.touch.TapMarkerOption;
import com.benben.kchartlib.touch.TouchTapManager;

/**
 * @日期 : 2020/8/31
 * @描述 : k线高亮
 */
public class HighlightDrawing extends TriggerRepeatAnimDrawing<CandleIndexRange> {

    private final Paint mPaint;

    public HighlightDrawing(CandleIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setRepeatMode(AbstractAnimDrawing.REVERSE);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(6);
    }

    @Override
    public boolean drawInViewPort() {
        return false;
    }

    @Override
    public void preCalcDataValue() {
        super.preCalcDataValue();
        if (mDataProvider.getTouchTapManager().hasSingleTap()
                || mDataProvider.getTouchTapManager().hasLongTap()) {
            if (!inAnimTime()) {
                startRepeatAnim(Integer.MAX_VALUE, 1_000);
            }
        } else {
            if (inAnimTime()) {
                stopRepeatAnim();
            }
        }
    }

    @Override
    public void drawData(Canvas canvas) {
        TouchTapManager touchTapManager = mDataProvider.getTouchTapManager();
        TapMarkerOption singleTapMarker = touchTapManager.getSingleTapMarker();
        if (singleTapMarker != null) {
            drawHighlight(canvas, singleTapMarker);
            return;
        }
        TapMarkerOption longTapMarker = touchTapManager.getLongTapMarker();
        if (longTapMarker == null) return;
        drawFixHighlight(canvas, longTapMarker);
    }

    private void drawHighlight(Canvas canvas, TapMarkerOption marker) {
        mPaint.setColor(0xBBFFFF00);
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
            if (inAnim()) {
                mPaint.setColor(0xBBFFFFFF);
                canvas.drawCircle(x, y, getAnimProcess() * 40, mPaint);
            }
        }
    }

    private void drawFixHighlight(Canvas canvas, TapMarkerOption marker) {
        mPaint.setColor(0xBBFFFF00);
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            y = getCoordinateY(marker.getEntity().getClosePrice());
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
            if (inAnim()) {
                mPaint.setColor(0xBBFFFFFF);
                canvas.drawCircle(x, y, getAnimProcess() * 40, mPaint);
            }
        }
    }
}
