package com.benben.kchartlib.impl;

import com.benben.kchartlib.canvas.RendererCanvas;

/**
 * @日期 : 2020/7/7
 * @描述 :
 */
public interface ICanvasPortLayout extends IPortLayout {

    /**
     * 设置是否在更新画板绘制范围
     */
    void setInUpdateCanvasPortLayout(boolean inUpdate);

    /**
     * 是否在更新画板绘制范围
     */
    boolean inUpdateCanvasPortLayout();

    /**
     * 画板预期布局参数
     * 提供布局建议，实际结果宽高结果和布局结果会通过
     * {@link RendererCanvas#setWidth(int)}
     * {@link RendererCanvas#setHeight(int)}
     * {@link RendererCanvas#updateViewPort}
     * 传递进去
     */
    class CanvasLayoutParams {
        private int mWidth;
        private int mHeight;
        private int mWeight;

        public CanvasLayoutParams(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public CanvasLayoutParams(int width, int height, int weight) {
            mWidth = width;
            mHeight = height;
            mWeight = weight;
        }

        public void setWidth(int width) {
            if (width < 0) width = 0;
            mWidth = width;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setHeight(int height) {
            if (height < 0) height = 0;
            mHeight = height;
        }

        public int getHeight() {
            return mHeight;
        }

        /**
         * 比例权重
         * 在水平方向如果{@link CanvasLayoutParams#mWidth}不为0则不起作用
         * 在垂直方向如果{@link CanvasLayoutParams#mHeight}不为0则不起作用
         */
        public void setWeight(int weight) {
            if (weight < 0) weight = 0;
            mWeight = weight;
        }

        public int getWeight() {
            return mWeight;
        }
    }
}
