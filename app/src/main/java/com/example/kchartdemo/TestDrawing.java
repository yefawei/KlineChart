package com.example.kchartdemo;

import android.graphics.Canvas;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.Drawing;

/**
 * @日期 : 2020/8/7
 * @描述 :
 */
public class TestDrawing extends Drawing {

    public TestDrawing(int color) {
        this.color = color;
    }

    private int color;

    public TestDrawing(RendererCanvas.DrawingLayoutParams params,int color) {
        super(params);
        this.color = color;
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        canvas.drawColor(color);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(color);
    }
}
