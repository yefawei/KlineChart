package com.yfw.kchartcore.render;

import android.graphics.Canvas;

/**
 * @日期 : 2020/7/1
 * @描述 : 渲染器
 */
public interface IRenderer {

    /**
     * 更新数据值
     */
    void preCalcDataValue();

    /**
     * 绘制
     */
    void render(Canvas canvas);
}
