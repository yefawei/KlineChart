package com.example.kchartdemo.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.drawing.Drawing;

/**
 * @日期 : 2020/8/27
 * @描述 :
 */
public class LeftPaddingDrawing extends Drawing<KlineInfo> {

    public LeftPaddingDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(Color.argb(48, 255, 255, 0));
    }
}
