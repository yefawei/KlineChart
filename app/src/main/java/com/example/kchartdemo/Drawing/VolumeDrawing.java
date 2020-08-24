package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.index.IVolume;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/8/12
 * @描述 :
 */
public class VolumeDrawing extends Drawing {

    private final Paint mPaint;

    public VolumeDrawing(@Nullable IndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        super.updateViewPort(left, top, right, bottom);
        if (isValid()) {
            mPaint.setShader(new LinearGradient(left, top, left, bottom, new int[]{
                    Color.argb(255, 255, 255, 255),
                    Color.argb(0, 255, 255, 255)
            }, null, Shader.TileMode.CLAMP));
        }
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(Color.argb(48, 0, 255, 255));
        float width = mDataProvider.getScalePointWidth();
        Transformer transformer = mDataProvider.getTransformer();
        for (int i = transformer.getStartIndex(); i <= transformer.getStopIndex(); i++) {
            IVolume item = (IVolume) mDataProvider.getAdapter().getItem(i);
            float limit = transformer.getPointXByIndex(i);

            float y = getCoordinateY(item.getVolume());
            float y2 = getCoordinateY(0);
            if (y < y2) {
                canvas.drawRect(limit - width / 2, y, limit + width / 2, y2, mPaint);
            } else {
                canvas.drawRect(limit - width / 2, y2, limit + width / 2, y, mPaint);
            }
        }
    }
}