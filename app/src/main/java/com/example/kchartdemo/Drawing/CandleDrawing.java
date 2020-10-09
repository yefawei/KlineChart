package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.drawing.Drawing;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.CandleIndexRange;
import com.yfw.kchartext.utils.FontCalculateUtils;

/**
 * @日期 : 2020/7/14
 * @描述 : 蜡烛图
 */
public class CandleDrawing extends Drawing<CandleIndexRange> {

    private final Paint mPaint;

    public CandleDrawing(CandleIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(18);
    }

    @Override
    public void drawData(Canvas canvas) {
        float width = mDataProvider.getScalePointWidth();
        Transformer transformer = mDataProvider.getTransformer();
        for (int i = transformer.getStartIndex(); i <= transformer.getStopIndex(); i++) {
            IEntity item = mDataProvider.getAdapter().getItem(i);
            float limit = transformer.getPointInScreenXByIndex(i);
            drawCandle(canvas, item, width, limit, i);
        }
    }

    private void drawCandle(Canvas canvas, IEntity entity, float width, float center, int position) {
        if (entity.getOpenPrice() > entity.getClosePrice()) { // 跌
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.GREEN);
        }
        float heighY = getCoordinateY(entity.getHighPrice());
        float lowY = getCoordinateY(entity.getLowPrice());
        float openY = getCoordinateY(entity.getOpenPrice());
        if (entity.getOpenPrice() == entity.getClosePrice()) {
            canvas.drawLine(center - width / 2, openY, center + width / 2, openY, mPaint);
            canvas.drawLine(center, lowY, center, heighY, mPaint);
            return;
        }
        float closeY = getCoordinateY(entity.getClosePrice());
        // 部分手机top值需要小于bottom值才能正常显示，这里做了个兼容
        if (closeY > openY) {
            canvas.drawRect(center - width / 2, openY, center + width / 2, closeY, mPaint);
        } else {
            canvas.drawRect(center - width / 2, closeY, center + width / 2, openY, mPaint);
        }
        canvas.drawLine(center, lowY, center, heighY, mPaint);

        String format = ((KlineInfo)entity).getFormatTime();
        mPaint.setColor(Color.WHITE);
        float v = mPaint.measureText(format);
        canvas.drawText(format, center - v / 2, heighY, mPaint);

        String pos = position + "";
        v = mPaint.measureText(pos);
        float offset = FontCalculateUtils.getFontTopYOffset(mPaint);
        canvas.drawText(pos, center - v / 2, lowY + offset, mPaint);
    }
}
