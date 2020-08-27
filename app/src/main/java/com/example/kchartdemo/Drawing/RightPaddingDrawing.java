package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.Drawing;

/**
 * @日期 : 2020/8/27
 * @描述 :
 */
public class RightPaddingDrawing extends Drawing {

    public RightPaddingDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(Color.argb(48, 255, 0, 255));
    }
}
