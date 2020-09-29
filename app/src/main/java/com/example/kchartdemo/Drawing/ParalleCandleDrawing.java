package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.data.Transformer;
import com.benben.kchartlib.drawing.ParallelTriggerAnimDrawing;
import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.range.CandleIndexRange;
import com.benben.kchartlib.index.range.IndexRange;
import com.benben.kchartlib.index.range.TransitionIndexRange;
import com.benben.kchartlib.utils.FontCalculateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @日期 : 2020/7/14
 * @描述 : 最大值最小值有变更、新添加、末尾值有变更都以动画的形式过渡蜡烛图
 */
public class ParalleCandleDrawing extends ParallelTriggerAnimDrawing<TransitionIndexRange> implements IndexRange.OnCalcValueListener {

    private static final int mTransitionTagId = 1;      // 最大最小值过渡
    private static final int mLastInsertedTagId = 2;    // 末尾有插入
    private static final int mLastUpdateTagId = 3;      // 末尾有更新

    private Date date = new Date();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

    private final Paint mPaint;

    public ParalleCandleDrawing(TransitionIndexRange indexRange, RendererCanvas.DrawingLayoutParams params) {
        super(indexRange, params);
        if (!(indexRange.getRealIndexRange() instanceof CandleIndexRange)) {
            throw new IllegalArgumentException("RealIndexRange is not CandleIndexRange!");
        }
        indexRange.addOnCalcValueListener(this);
        setInterpolator(new DecelerateInterpolator());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(18);
    }

    @Override
    public void onResetValue(boolean isEmptyData) {
        if (isEmptyData) {
            stopAnim(mTransitionTagId);
        }
    }

    @Override
    public void onCalcValueEnd(boolean isEmptyData) {
        if (!mIndexRange.isInTransition() && mIndexRange.valueIsChange()) {
            startAnim(mTransitionTagId, 400);
            mIndexRange.startTransition();
        }
    }

    @Override
    public void preCalcDataValue() {
        super.preCalcDataValue();
    }

    @Override
    public void updateAnimProcessTime(long time) {
        super.updateAnimProcessTime(time);
        if (mIndexRange.isInTransition()) {
            mIndexRange.updateProcess(getAnimProcess());
        }
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

        date.setTime(entity.getDatatime());
        String format = mSimpleDateFormat.format(date);
        mPaint.setColor(Color.WHITE);
        float v = mPaint.measureText(format);
        canvas.drawText(format, center - v / 2, heighY, mPaint);

        String pos = position + "";
        v = mPaint.measureText(pos);
        float offset = FontCalculateUtils.getFontTopYOffset(mPaint);
        canvas.drawText(pos, center - v / 2, lowY + offset, mPaint);
    }
}
