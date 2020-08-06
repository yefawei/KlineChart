package com.benben.kchartlib.canvas;

import android.graphics.Canvas;

import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;

/**
 * @日期 : 2020/7/1
 * @描述 :
 */
public interface IRendererCanvas {

    void setWidth(int width);

    void setHeight(int height);

    void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider);

    void detachedParentPortLayout();

    /**
     * 画板是否有效
     */
    boolean isValid();

    void addDrawing(Drawing drawing);

    void removeDrawing(Drawing drawing);

    /**
     * 绘制
     */
    void render(Canvas canvas);


}
