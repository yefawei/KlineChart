package com.example.kchartdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.index.range.CandleIndexRange;
import com.benben.kchartlib.utils.FontCalculateUtils;

/**
 * @日期 : 2020/7/14
 * @描述 :
 */
public class CandleDrawing extends Drawing {

    private final Paint mPaint;

    public CandleDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(new CandleIndexRange(), params);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawEmpty(Canvas canvas) {

    }

    @Override
    public void drawData(Canvas canvas) {
        Transformer transformer = mDataProvider.getTransformer();


        String indexTag = mIndexRange.getIndexTag();
        int maxIndex = mIndexRange.getMaxIndex();
        int minIndex = mIndexRange.getMinIndex();
        float maxValue = mIndexRange.getMaxValue();
        float minValue = mIndexRange.getMinValue();
//        Log.e("CandleDrawing", indexTag + " maxIndex: " + maxIndex + " minIndex: " + minIndex + " maxValue: " + maxValue + " minValue: " + minValue);
//        Log.e("CandleDrawing", indexTag + " StartIndex: " + transformer.getStartIndex() + " StopIndex: " + transformer.getStopIndex() + " getStartPointX: " + transformer.getStartPointX());

        canvas.drawColor(Color.BLACK);

        float width = mDataProvider.getScalePointWidth();
        for (int i = transformer.getStartIndex(); i <= transformer.getStopIndex(); i++) {
            if (i % 2 == 0) {
                mPaint.setColor(Color.RED);
            } else {
                mPaint.setColor(Color.GREEN);
            }
            float limit = (i - transformer.getStartIndex()) * width + transformer.getStartPointX();
            canvas.drawRect(limit - width / 2, 100, limit + width / 2, 200, mPaint);
            float center = FontCalculateUtils.getBaselineFromCenter(mPaint, 150);
            mPaint.setColor(Color.BLUE);
            mPaint.setTextSize(28);
            canvas.drawText(i + "", limit, center, mPaint);
        }
    }
}
