package com.example.kchartdemo;

import android.graphics.Canvas;
import android.graphics.Color;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.drawing.Drawing;

/**
 * @日期 : 2020/8/12
 * @描述 :
 */
public class BGDrawing extends Drawing {

    public BGDrawing() {
        RendererCanvas.DrawingLayoutParams params = new RendererCanvas.DrawingLayoutParams();
        params.setWeight(1);
        setLayoutParams(params);
    }

    @Override
    public void drawEmpty(Canvas canvas) {
        drawData(canvas);
    }

    @Override
    public void drawData(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
    }
}

