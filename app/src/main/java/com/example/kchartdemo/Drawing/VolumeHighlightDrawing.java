package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.index.range.VolumeIndexRange;
import com.benben.kchartlib.touch.TapMarkerOption;
import com.benben.kchartlib.touch.TouchTapManager;

/**
 * @日期 : 2020/8/31
 * @描述 : 成交量高亮
 */
public class VolumeHighlightDrawing extends Drawing {

    private final Paint mPaint;

    public VolumeHighlightDrawing(VolumeIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setAlpha(127);
        mPaint.setStrokeWidth(6);
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
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
        }
    }

    private void drawFixHighlight(Canvas canvas, TapMarkerOption marker) {
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
        }
    }
}
