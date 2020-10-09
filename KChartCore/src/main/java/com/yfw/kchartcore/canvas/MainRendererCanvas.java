package com.yfw.kchartcore.canvas;


import com.yfw.kchartcore.render.MainRenderer;

/**
 * @日期 : 2020/8/6
 * @描述 : 主渲染器画板
 */
public class MainRendererCanvas extends RendererCanvas{

    private MainRenderer.CanvasLayoutParams mLayoutParams;

    public MainRendererCanvas(MainRenderer.CanvasLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("CanvasLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public void setLayoutParams(MainRenderer.CanvasLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("CanvasLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public MainRenderer.CanvasLayoutParams getLayoutParams() {
        return mLayoutParams;
    }
}
