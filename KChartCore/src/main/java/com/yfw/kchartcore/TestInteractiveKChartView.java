package com.yfw.kchartcore;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @日期 : 2020/6/30
 * @描述 : 测试可交互K线,会打印绘制所耗时间，通过Drawtime过滤日志
 */
public class TestInteractiveKChartView extends InteractiveKChartView {
    public TestInteractiveKChartView(@NonNull Context context) {
        this(context, null);
    }

    public TestInteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestInteractiveKChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.nanoTime();
        super.onDraw(canvas);
        long drawtime = System.nanoTime() - startTime;
        totalTime += drawtime;
        drawCycles++;
        long average = totalTime / drawCycles;
        if (drawtime > 10_000_000) {
            Log.e("Drawtime", "Drawtime error: " + drawtime + " ms, average: " + average + " ms, cycles: " + drawCycles);
        } else if (drawtime > 5_000_000) {
            Log.w("Drawtime", "Drawtime warn: " + drawtime + " ms, average: " + average + " ms, cycles: " + drawCycles);
        } else {
            Log.i("Drawtime", "Drawtime: " + drawtime + " ms, average: " + average + " ms, cycles: " + drawCycles);
        }
    }
}

