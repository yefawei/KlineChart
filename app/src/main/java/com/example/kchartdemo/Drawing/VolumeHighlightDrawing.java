package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.touch.TapMarkerOption;
import com.yfw.kchartcore.touch.TouchTapManager;
import com.yfw.kchartext.drawing.TriggerRepeatAnimDrawing;
import com.yfw.kchartext.index.range.VolumeIndexRange;

/**
 * @日期 : 2020/8/31
 * @描述 : 成交量高亮
 */
public class VolumeHighlightDrawing extends TriggerRepeatAnimDrawing<KlineInfo> {

    private final Paint mPaint;
    private final VolumeIndexRange mIndexRange;
    public VolumeHighlightDrawing(VolumeIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(params);
        mIndexRange = indexRange;
        setInterpolator(new AccelerateDecelerateInterpolator());
        setRepeatMode(TriggerRepeatAnimDrawing.REVERSE);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(6);
    }

    @Override
    public IndexRange getIndexRange() {
        return mIndexRange;
    }

    @Override
    public boolean drawInViewPort() {
        return false;
    }

    @Override
    public void preCalcDataValue(boolean emptyBounds) {
        super.preCalcDataValue(emptyBounds);
        if (!emptyBounds && (mDataProvider.getTouchTapManager().hasSingleTap()
                || mDataProvider.getTouchTapManager().hasLongTap())) {
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
        TouchTapManager<KlineInfo> touchTapManager = mDataProvider.getTouchTapManager();
        TapMarkerOption<KlineInfo> singleTapMarker = touchTapManager.getSingleTapMarker();
        if (singleTapMarker != null) {
            drawHighlight(canvas, singleTapMarker);
            return;
        }
        TapMarkerOption<KlineInfo> longTapMarker = touchTapManager.getLongTapMarker();
        if (longTapMarker == null) return;
        drawFixHighlight(canvas, longTapMarker);
    }

    private void drawHighlight(Canvas canvas, TapMarkerOption<KlineInfo> marker) {
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

    private void drawFixHighlight(Canvas canvas, TapMarkerOption<KlineInfo> marker) {
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
}
