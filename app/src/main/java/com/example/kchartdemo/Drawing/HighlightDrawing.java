package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.index.range.CandleIndexRange;
import com.benben.kchartlib.overlay.TouchTapManager;
import com.benben.kchartlib.overlay.TapMarkerOptions;

/**
 * @日期 : 2020/8/31
 * @描述 : k线高亮
 */
public class HighlightDrawing extends Drawing {

    private final Paint mPaint;

    private boolean mLastTapIsLongTap;

    public HighlightDrawing(CandleIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setAlpha(127);
        mPaint.setStrokeWidth(6);
    }

    @Override
    public void preCalcDataValue() {
        super.preCalcDataValue();
        if (mLastTapIsLongTap && !mDataProvider.getTouchTapManager().hasLongTap()) {
            // 如果上一次是长按，那么此次非长按则清空点击信息，模仿OK交互
            mLastTapIsLongTap = false;
            mDataProvider.getTouchTapManager().removeAllTapInfo();
            return;
        }
        mLastTapIsLongTap = mDataProvider.getTouchTapManager().hasLongTap();
    }

    @Override
    public void drawData(Canvas canvas) {
        TouchTapManager touchTapManager = mDataProvider.getTouchTapManager();
        TapMarkerOptions singleTapMarker = touchTapManager.getSingleTapMarker();
        if (singleTapMarker != null) {
            Log.e("TapMarkerOptions", "singleTapMarker: " + singleTapMarker.getIndex());
            drawHighlight(canvas, singleTapMarker);
            return;
        }
        TapMarkerOptions longTapMarker = touchTapManager.getLongTapMarker();
        if (longTapMarker == null) return;
        Log.e("TapMarkerOptions", "longTapMarker: " + longTapMarker.getIndex());
        drawFixHighlight(canvas, longTapMarker);
    }

    private void drawHighlight(Canvas canvas, TapMarkerOptions marker) {
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
        }
    }

    private void drawFixHighlight(Canvas canvas, TapMarkerOptions marker) {
        float x = mDataProvider.getTransformer().getPointInScreenXByIndex(marker.getIndex());
        canvas.drawLine(x, mViewPort.top, x, mViewPort.bottom, mPaint);
        float y = marker.getY();
        if (mViewPort.top < y && mViewPort.bottom > y) {
            y = getCoordinateY(marker.getEntity().getClosePrice());
            canvas.drawLine(mViewPort.left, y, mViewPort.right, y, mPaint);
        }
    }
}
