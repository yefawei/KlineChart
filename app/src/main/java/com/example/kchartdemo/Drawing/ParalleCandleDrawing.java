package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.InteractiveKChartView;
import com.yfw.kchartcore.ScrollAndScaleView;
import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.data.AdapterDataObserver;
import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.CandleIndexRange;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.index.range.TransitionIndexRange;
import com.yfw.kchartext.drawing.ParallelTriggerAnimDrawing;
import com.yfw.kchartext.utils.FontCalculateUtils;

/**
 * @日期 : 2020/7/14
 * @描述 : 最大值最小值有变更、新添加、末尾值有变更都以动画的形式过渡蜡烛图
 */
public class ParalleCandleDrawing extends ParallelTriggerAnimDrawing<TransitionIndexRange>
        implements IndexRange.OnCalcValueListener, InteractiveKChartView.OnAdapterChangeListener {

    private static final int mTransitionTagId = 1;      // 最大最小值过渡
    private static final int mLastInsertedTagId = 2;    // 末尾有插入
    private static final int mLastUpdateTagId = 3;      // 末尾有更新

    private final Paint mPaint;

    private float mLastItemClosePrice;
    private float mLastItemTargetPrice;

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
    public void onAttachAdapter(@Nullable BaseKChartAdapter<?> adapter) {
        if (adapter == null) return;
        adapter.registerDataSetObserver(mAdapterDataObserver);
    }

    @Override
    public void onDetachAdapter(@Nullable BaseKChartAdapter<?> adapter) {
        if (adapter == null) return;
        adapter.unregisterDataSetObserver(mAdapterDataObserver);
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
        if (!inAnimTime(mLastUpdateTagId)) {
            mLastItemClosePrice = mLastItemTargetPrice;
        }
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
        IEntity item;
        if (inAnim()) {
            if (inAnimTime(mLastInsertedTagId)) {
                // 插入一条数据动画
                int stopIndex = transformer.getStopIndex();
                item = mDataProvider.getAdapter().getItem(stopIndex);
                int scrollState = mDataProvider.getScrollState();
                if (scrollState == ScrollAndScaleView.SCROLL_STATE_SETTLING) {
                    int padding = mDataProvider.getPaddingHelper().getRightExtPadding(mDataProvider.getScaleX());
                    drawCandle(canvas, item, width, getRight() - padding - width / 2.0f, stopIndex, false);
                } else {
                    float process = getAnimProcess(mLastInsertedTagId);
                    float position = transformer.getPointInScreenXByIndex(stopIndex);
                    drawCandle(canvas, item, width, position - width * (1.0f - process), stopIndex, false);
                }
                for (int i = transformer.getStartIndex(); i < stopIndex; i++) {
                    item = mDataProvider.getAdapter().getItem(i);
                    float positionId = transformer.getPointInScreenXByIndex(i);
                    drawCandle(canvas, item, width, positionId, i, false);
                }
                return;
            } else if (inAnimTime(mLastUpdateTagId)) {
                // 最后一条数据有更新
                int stopIndex = transformer.getStopIndex();
                item = mDataProvider.getAdapter().getItem(stopIndex);
                float position = transformer.getPointInScreenXByIndex(stopIndex);
                drawCandle(canvas, item, width, position, stopIndex, true);
                for (int i = transformer.getStartIndex(); i < stopIndex; i++) {
                    item = mDataProvider.getAdapter().getItem(i);
                    float positionId = transformer.getPointInScreenXByIndex(i);
                    drawCandle(canvas, item, width, positionId, i, false);
                }
                return;
            }
        }
        for (int i = transformer.getStartIndex(); i <= transformer.getStopIndex(); i++) {
            item = mDataProvider.getAdapter().getItem(i);
            float position = transformer.getPointInScreenXByIndex(i);
            drawCandle(canvas, item, width, position, i, false);
        }

    }

    private void drawCandle(Canvas canvas, IEntity entity, float width, float inScreenPosition, int positionId, boolean processLast) {
        float closePrice = entity.getClosePrice();
        float process = getAnimProcess(mLastUpdateTagId);
        if (processLast) {
            closePrice = (mLastItemTargetPrice - mLastItemClosePrice) * process + mLastItemClosePrice;
        }
        if (entity.getOpenPrice() > closePrice) { // 跌
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.GREEN);
        }

        float heighY = getCoordinateY(entity.getHighPrice());
        float lowY = getCoordinateY(entity.getLowPrice());
        float openY = getCoordinateY(entity.getOpenPrice());
        if (entity.getOpenPrice() == closePrice) {
            canvas.drawLine(inScreenPosition - width / 2, openY, inScreenPosition + width / 2, openY, mPaint);
            canvas.drawLine(inScreenPosition, lowY, inScreenPosition, heighY, mPaint);
            return;
        }
        float closeY = getCoordinateY(closePrice);
        // 部分手机top值需要小于bottom值才能正常显示，这里做了个兼容
        if (closeY > openY) {
            canvas.drawRect(inScreenPosition - width / 2, openY, inScreenPosition + width / 2, closeY, mPaint);
        } else {
            canvas.drawRect(inScreenPosition - width / 2, closeY, inScreenPosition + width / 2, openY, mPaint);
        }
        canvas.drawLine(inScreenPosition, lowY, inScreenPosition, heighY, mPaint);

        String format = ((KlineInfo) entity).getFormatTime();
        mPaint.setColor(Color.WHITE);
        float v = mPaint.measureText(format);
        canvas.drawText(format, inScreenPosition - v / 2, heighY, mPaint);

        String pos = String.valueOf(positionId);
        v = mPaint.measureText(pos);
        float offset = FontCalculateUtils.getFontTopYOffset(mPaint);
        canvas.drawText(pos, inScreenPosition - v / 2, lowY + offset, mPaint);
    }

    AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {

        @Override
        public void onChanged(int allCount) {
            if (allCount == 0) return;
            IEntity item = mDataProvider.getAdapter().getItem(allCount - 1);
            mLastItemClosePrice = mLastItemTargetPrice = item.getClosePrice();
            stopAllAnim();
        }

        @Override
        public void onLastUpdated(int index) {
            IEntity item = mDataProvider.getAdapter().getItem(index);
            if (mLastItemTargetPrice != item.getClosePrice()) {
                float process = getAnimProcess(mLastUpdateTagId);
                mLastItemClosePrice = (mLastItemTargetPrice - mLastItemClosePrice) * process + mLastItemClosePrice;
                mLastItemTargetPrice = item.getClosePrice();
                startAnim(mLastUpdateTagId, 200);
            }
        }

        @Override
        public void onLastInserted(int insertedCount) {
            if (insertedCount != 1) return;
            stopAnim(mLastUpdateTagId);
            startAnim(mLastInsertedTagId, 4000);
        }
    };
}
