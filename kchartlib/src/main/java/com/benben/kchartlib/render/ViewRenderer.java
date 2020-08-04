package com.benben.kchartlib.render;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IMainCanvasPort;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @日期 : 2020/7/1
 * @描述 : 视图层，负责处理[主视图、左视图、顶视图、右视图、底视图]
 * -------------------------
 * ┊   ┊      顶       ┊   ┊
 * ┊   ┊---------------┊   ┊
 * ┊   ┊               ┊   ┊
 * ┊左 ┊      主       ┊ 右 ┊
 * ┊   ┊               ┊   ┊
 * ┊   ┊---------------┊   ┊
 * ┊   ┊      底       ┊   ┊
 * -------------------------
 */
public class ViewRenderer extends Renderer implements IMainCanvasPort {

    static final int LEFT_TOP_VERTICAL_FLAG = 0b00000001;       // 左上角以垂直为主
    static final int LEFT_BOTTOM_VERTICAL_FLAG = 0b00000010;    // 左下角以垂直为主
    static final int RIGHT_TOP_VERTICAL_FLAG = 0b00000100;      // 右上角以垂直为主
    static final int RIGHT_BOTTOM_VERTICAL_FLAG = 0b00001000;   // 右下角以垂直为主

    public static final int POSITION_MAIN = 0;
    public static final int POSITION_LEFT = 1;
    public static final int POSITION_TOP = 2;
    public static final int POSITION_RIGHT = 3;
    public static final int POSITION_BOTTOM = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POSITION_MAIN, POSITION_LEFT, POSITION_TOP, POSITION_RIGHT, POSITION_BOTTOM})
    public @interface Position {

    }

    private int mCornerRuleFlags = 0b00001111;
    private RendererCanvas[] mHorizontalCanvas = new RendererCanvas[3]; // [0]:left [1]:main [2]:right
    private RendererCanvas[] mVerticalCanvas = new RendererCanvas[3]; // [0]:top [1]:main [2]:bottom

    public ViewRenderer(IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public void updatePortLayout() {
        if (mHorizontalCanvas[1] == null) return;

        setInUpdateCanvasPortLayout(true);
        if (mViewPort.isEmpty()) {
            for (RendererCanvas renderCanvas : mHorizontalCanvas) {
                if (renderCanvas == null) continue;
                renderCanvas.setWidth(0);
                renderCanvas.setHeight(0);
                renderCanvas.updateViewPort(0, 0, 0, 0);
            }
            if (mVerticalCanvas[0] != null) {
                mVerticalCanvas[0].setWidth(0);
                mVerticalCanvas[0].setHeight(0);
                mVerticalCanvas[0].updateViewPort(0, 0, 0, 0);
            }
            if (mVerticalCanvas[2] != null) {
                mVerticalCanvas[2].setWidth(0);
                mVerticalCanvas[2].setHeight(0);
                mVerticalCanvas[2].updateViewPort(0, 0, 0, 0);
            }
            setInUpdateCanvasPortLayout(false);
            updateDrawingPortLayout();
            return;
        }

        int width = this.getWidth();
        int height = this.getHeight();
        int weightUnitWidth = 0;
        int[] horizontalWeightAndWidth = getHorizontalWeightAndWidth();
        if (horizontalWeightAndWidth[0] != 0) {
            int remainingWidth = width - horizontalWeightAndWidth[1];
            if (remainingWidth > 0) {
                // 说明固定宽度没有超出总宽度
                weightUnitWidth = (int) (remainingWidth * 1.0f / horizontalWeightAndWidth[0]);
            }
        }
        int weightUnitHeight = 0;
        int[] verticalWeightAndHeight = getVerticalWeightAndHeight();
        if (verticalWeightAndHeight[0] != 0) {
            int remainingHeight = height - verticalWeightAndHeight[1];
            if (remainingHeight > 0) {
                // 说明固定高度没有超出总宽度
                weightUnitHeight = (int) (remainingHeight * 1.0f / verticalWeightAndHeight[0]);
            }
        }

        int tempValue;
        // 计算出水平方向每个画板的宽度
        for (RendererCanvas renderCanvas : mHorizontalCanvas) {
            if (renderCanvas == null) continue;
            CanvasLayoutParams layoutParams = renderCanvas.getLayoutParams();
            if (layoutParams.getWidth() > 0) {
                if (layoutParams.getWidth() > width) {
                    renderCanvas.setWidth(width);
                    width = 0;
                } else {
                    renderCanvas.setWidth(layoutParams.getWidth());
                    width -= layoutParams.getWidth();
                }
            } else if (layoutParams.getWeight() > 0) {
                tempValue = weightUnitWidth * layoutParams.getWeight();
                renderCanvas.setWidth(tempValue);
                width -= tempValue;
            } else {
                renderCanvas.setWidth(0);
            }
        }
        if (width > 0 && weightUnitWidth > 0) {
            int w = 0;
            // 说明按比例宽度没有填满视图
            for (RendererCanvas horizontalCanva : mHorizontalCanvas) {
                if (horizontalCanva == null) continue;
                CanvasLayoutParams layoutParams = horizontalCanva.getLayoutParams();
                if (layoutParams.getWidth() > 0) continue;
                if (layoutParams.getWeight() > 0) {
                    tempValue = Math.round(width * 1.0f / horizontalWeightAndWidth[0] * layoutParams.getWeight() + 0.5f);
                    if (tempValue + w > width) {
                        tempValue = width - w;
                    }
                    w += tempValue;
                    Log.w("ViewRenderer", "Horizontal layout with gaps. " + horizontalCanva.getClass().getSimpleName() + " ext width: " + tempValue);
                    horizontalCanva.setWidth(horizontalCanva.getWidth() + tempValue);
                }
                if (width == w) break;
            }
        }

        // 计算出垂直方向每个画板的高度
        for (RendererCanvas renderCanvas : mVerticalCanvas) {
            if (renderCanvas == null) continue;
            CanvasLayoutParams layoutParams = renderCanvas.getLayoutParams();
            if (layoutParams.getHeight() > 0) {
                if (layoutParams.getHeight() > height) {
                    renderCanvas.setHeight(height);
                    height = 0;
                } else {
                    renderCanvas.setHeight(layoutParams.getHeight());
                    height -= layoutParams.getHeight();
                }
            } else if (layoutParams.getWeight() > 0) {
                tempValue = weightUnitHeight * layoutParams.getWeight();
                renderCanvas.setHeight(tempValue);
                height -= tempValue;
            } else {
                renderCanvas.setHeight(0);
            }
        }
        if (height > 0 && weightUnitHeight > 0) {
            int h = 0;
            // 说明按比例高度没有填满视图
            for (RendererCanvas verticalCanva : mVerticalCanvas) {
                if (verticalCanva == null) continue;
                CanvasLayoutParams layoutParams = verticalCanva.getLayoutParams();
                if (layoutParams.getHeight() > 0) continue;
                if (layoutParams.getWeight() > 0) {
                    tempValue = Math.round(height * 1.0f / verticalWeightAndHeight[0] * layoutParams.getWeight() + 0.5f);
                    if (tempValue + h > height) {
                        tempValue = height - h;
                    }
                    h += tempValue;
                    Log.w("ViewRenderer", "Vertical layout with gaps. " + verticalCanva.getClass().getSimpleName() + " ext height: " + tempValue);
                    verticalCanva.setHeight(verticalCanva.getHeight() + tempValue);
                }
                if (height == h) break;
            }
        }

        //补全水平方向画板的高度，补全垂直方向画板的宽度
        if (mHorizontalCanvas[0] != null) {
            tempValue = mHorizontalCanvas[1].getHeight();
            if (mVerticalCanvas[0] != null && (mCornerRuleFlags & LEFT_TOP_VERTICAL_FLAG) == LEFT_TOP_VERTICAL_FLAG) {
                // 左上角以垂直为主
                tempValue += mVerticalCanvas[0].getHeight();
            }
            if (mVerticalCanvas[2] != null && (mCornerRuleFlags & LEFT_BOTTOM_VERTICAL_FLAG) == LEFT_BOTTOM_VERTICAL_FLAG) {
                // 左下角以垂直为主
                tempValue += mVerticalCanvas[2].getHeight();
            }
            mHorizontalCanvas[0].setHeight(tempValue);
        }
        if (mVerticalCanvas[0] != null) {
            tempValue = mVerticalCanvas[1].getWidth();
            if (mHorizontalCanvas[0] != null && (mCornerRuleFlags & LEFT_TOP_VERTICAL_FLAG) != LEFT_TOP_VERTICAL_FLAG) {
                // 左上角以水平为主
                tempValue += mHorizontalCanvas[0].getWidth();
            }
            if (mHorizontalCanvas[2] != null && (mCornerRuleFlags & RIGHT_TOP_VERTICAL_FLAG) != RIGHT_TOP_VERTICAL_FLAG) {
                // 右上角以水平为主
                tempValue += mHorizontalCanvas[2].getWidth();
            }
            mVerticalCanvas[0].setWidth(tempValue);
        }
        if (mHorizontalCanvas[2] != null) {
            tempValue = mHorizontalCanvas[1].getHeight();
            if (mVerticalCanvas[0] != null && (mCornerRuleFlags & RIGHT_TOP_VERTICAL_FLAG) == RIGHT_TOP_VERTICAL_FLAG) {
                // 右上角以垂直为主
                tempValue += mVerticalCanvas[0].getHeight();
            }
            if (mVerticalCanvas[2] != null && (mCornerRuleFlags & RIGHT_BOTTOM_VERTICAL_FLAG) == RIGHT_BOTTOM_VERTICAL_FLAG) {
                // 右下角以垂直为主
                tempValue += mVerticalCanvas[2].getHeight();
            }
            mHorizontalCanvas[2].setHeight(tempValue);
        }
        if (mVerticalCanvas[2] != null) {
            tempValue = mVerticalCanvas[1].getWidth();
            if (mHorizontalCanvas[0] != null && (mCornerRuleFlags & LEFT_BOTTOM_VERTICAL_FLAG) != LEFT_BOTTOM_VERTICAL_FLAG) {
                // 左下角以水平为主
                tempValue += mHorizontalCanvas[0].getWidth();
            }
            if (mHorizontalCanvas[2] != null && (mCornerRuleFlags & RIGHT_BOTTOM_VERTICAL_FLAG) != RIGHT_BOTTOM_VERTICAL_FLAG) {
                // 右下角以水平为主
                tempValue += mHorizontalCanvas[2].getWidth();
            }
            mVerticalCanvas[2].setWidth(tempValue);
        }

        // 确定最终坐标位置
        if (mHorizontalCanvas[0] != null) {
            if (mVerticalCanvas[0] != null) {
                if ((mCornerRuleFlags & LEFT_TOP_VERTICAL_FLAG) == LEFT_TOP_VERTICAL_FLAG) {
                    mHorizontalCanvas[0].updateViewPort(getLeft(), getTop(),
                            getLeft() + mHorizontalCanvas[0].getWidth(), getTop() + mHorizontalCanvas[0].getHeight());
                    mVerticalCanvas[0].updateViewPort(mHorizontalCanvas[0].getRight(), getTop(),
                            mHorizontalCanvas[0].getRight() + mVerticalCanvas[0].getWidth(), getTop() + mVerticalCanvas[0].getHeight());
                } else {
                    mVerticalCanvas[0].updateViewPort(getLeft(), getTop(),
                            getLeft() + mVerticalCanvas[0].getWidth(), getTop() + mVerticalCanvas[0].getHeight());
                    mHorizontalCanvas[0].updateViewPort(getLeft(), mVerticalCanvas[0].getBottom(),
                            getLeft() + mHorizontalCanvas[0].getWidth(), mVerticalCanvas[0].getBottom() + mHorizontalCanvas[0].getHeight());
                }
                mHorizontalCanvas[1].updateViewPort(mHorizontalCanvas[0].getRight(), mVerticalCanvas[0].getBottom(),
                        mHorizontalCanvas[0].getRight() + mHorizontalCanvas[1].getWidth(), mVerticalCanvas[0].getBottom() + mHorizontalCanvas[1].getHeight());
            } else {
                mHorizontalCanvas[0].updateViewPort(getLeft(), getTop(),
                        getLeft() + mHorizontalCanvas[0].getWidth(), getTop() + mHorizontalCanvas[0].getHeight());
                mHorizontalCanvas[1].updateViewPort(mHorizontalCanvas[0].getRight(), getTop(),
                        mHorizontalCanvas[0].getRight() + mHorizontalCanvas[1].getWidth(), getTop() + mHorizontalCanvas[1].getHeight());
            }
        } else if (mVerticalCanvas[0] != null) {
            mVerticalCanvas[0].updateViewPort(getLeft(), getTop(),
                    getLeft() + mVerticalCanvas[0].getWidth(), getTop() + mVerticalCanvas[0].getHeight());
            mHorizontalCanvas[1].updateViewPort(getLeft(), mVerticalCanvas[0].getBottom(),
                    getLeft() + mHorizontalCanvas[1].getWidth(), mVerticalCanvas[0].getBottom() + mHorizontalCanvas[1].getHeight());
        } else {
            mHorizontalCanvas[1].updateViewPort(getLeft(), getTop(),
                    getLeft() + mHorizontalCanvas[1].getWidth(), getTop() + mHorizontalCanvas[1].getHeight());
        }

        if (mHorizontalCanvas[2] != null) {
            if (mVerticalCanvas[0] != null && (mCornerRuleFlags & RIGHT_TOP_VERTICAL_FLAG) != RIGHT_TOP_VERTICAL_FLAG) {
                mHorizontalCanvas[2].updateViewPort(mHorizontalCanvas[1].getRight(), mVerticalCanvas[0].getBottom(),
                        mHorizontalCanvas[1].getRight() + mHorizontalCanvas[2].getWidth(), mVerticalCanvas[0].getBottom() + mHorizontalCanvas[2].getHeight());
            } else {
                mHorizontalCanvas[2].updateViewPort(mHorizontalCanvas[1].getRight(), getTop(),
                        mHorizontalCanvas[1].getRight() + mHorizontalCanvas[2].getWidth(), getTop() + mHorizontalCanvas[2].getHeight());
            }
        }
        if (mVerticalCanvas[2] != null) {
            if (mHorizontalCanvas[0] != null && (mCornerRuleFlags & LEFT_BOTTOM_VERTICAL_FLAG) != LEFT_BOTTOM_VERTICAL_FLAG) {
                mVerticalCanvas[2].updateViewPort(getLeft(), mHorizontalCanvas[0].getBottom(),
                        getLeft() + mVerticalCanvas[2].getWidth(), mHorizontalCanvas[0].getBottom() + mVerticalCanvas[2].getHeight());
            } else {
                mVerticalCanvas[2].updateViewPort(mHorizontalCanvas[1].getLeft(), mHorizontalCanvas[1].getBottom(),
                        mHorizontalCanvas[1].getLeft() + mVerticalCanvas[2].getWidth(), mHorizontalCanvas[1].getBottom() + mVerticalCanvas[2].getHeight());
            }
        }
        setInUpdateCanvasPortLayout(false);
        updateDrawingPortLayout();
    }

    /**
     * @return [0]:all weight [1]:all width
     */
    private int[] getHorizontalWeightAndWidth() {
        int[] i = new int[2];
        for (RendererCanvas c : mHorizontalCanvas) {
            if (c == null) continue;
            CanvasLayoutParams layoutParams = c.getLayoutParams();
            if (layoutParams.getWidth() > 0) {
                i[1] += layoutParams.getWidth();
            } else {
                i[0] += layoutParams.getWeight();
            }
        }
        return i;
    }

    /**
     * @return [0]:all weight [1]:all height
     */
    private int[] getVerticalWeightAndHeight() {
        int[] i = new int[2];
        for (RendererCanvas c : mVerticalCanvas) {
            if (c == null) continue;
            CanvasLayoutParams layoutParams = c.getLayoutParams();
            if (layoutParams.getHeight() > 0) {
                i[1] += layoutParams.getHeight();
            } else {
                i[0] += layoutParams.getWeight();
            }
        }
        return i;
    }

    private void updateDrawingPortLayout() {
        if (mHorizontalCanvas[0] != null) {
            mHorizontalCanvas[0].updatePortLayout();
        }
        if (mHorizontalCanvas[1] != null) {
            mHorizontalCanvas[1].updatePortLayout();
        }
        if (mHorizontalCanvas[2] != null) {
            mHorizontalCanvas[2].updatePortLayout();
        }
        if (mVerticalCanvas[0] != null) {
            mVerticalCanvas[0].updatePortLayout();
        }
        if (mVerticalCanvas[2] != null) {
            mVerticalCanvas[2].updatePortLayout();
        }
    }

    @Override
    public void preCalcDataValue() {

    }

    @Override
    public void render(Canvas canvas) {
        if (mHorizontalCanvas[0] != null) {
            render(canvas, mHorizontalCanvas[0]);
        }
        if (mHorizontalCanvas[1] != null) {
            render(canvas, mHorizontalCanvas[1]);
        }
        if (mHorizontalCanvas[2] != null) {
            render(canvas, mHorizontalCanvas[2]);
        }
        if (mVerticalCanvas[0] != null) {
            render(canvas, mVerticalCanvas[0]);
        }
        if (mVerticalCanvas[2] != null) {
            render(canvas, mVerticalCanvas[2]);
        }
    }

    public void render(Canvas canvas, RendererCanvas rendererCanvas) {
        if (!rendererCanvas.isValid()) return;
        canvas.save();
        rendererCanvas.render(canvas);
        canvas.restore();
    }

    @Override
    public int getMainCanvasLeft() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getLeft();
        }
        return 0;
    }

    @Override
    public int getMainCanvasTop() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getTop();
        }
        return 0;
    }

    @Override
    public int getMainCanvasRight() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getRight();
        }
        return 0;
    }

    @Override
    public int getMainCanvasBottom() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getBottom();
        }
        return 0;
    }

    @Override
    public int getMainCanvasWidth() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getWidth();
        }
        return 0;
    }

    @Override
    public int getMainCanvasHeight() {
        if (mainCanvasValid()) {
            return mHorizontalCanvas[1].getHeight();
        }
        return 0;
    }

    @Override
    public boolean mainCanvasValid() {
        return mHorizontalCanvas[1] != null && mHorizontalCanvas[1].isValid();
    }

    /**
     * 设置画板各个角的占位规则
     * @param leftTopVertical true:左上垂直为主导 false:左上水平为主导
     * @param rightTopVertical true:右上垂直为主导 false:右上水平为主导
     * @param rightBottomVertical true:右下垂直为主导 false:右下水平为主导
     * @param leftBottomVertical true:左下垂直为主导 false:左下水平为主导
     */
    public void setCornerRule(boolean leftTopVertical, boolean rightTopVertical,
                              boolean rightBottomVertical, boolean leftBottomVertical) {
        if (leftTopVertical) {
            mCornerRuleFlags |= LEFT_TOP_VERTICAL_FLAG;
        } else {
            mCornerRuleFlags &= ~LEFT_TOP_VERTICAL_FLAG;
        }
        if (rightTopVertical) {
            mCornerRuleFlags |= RIGHT_TOP_VERTICAL_FLAG;
        } else {
            mCornerRuleFlags &= ~RIGHT_TOP_VERTICAL_FLAG;
        }
        if (rightBottomVertical) {
            mCornerRuleFlags |= RIGHT_BOTTOM_VERTICAL_FLAG;
        } else {
            mCornerRuleFlags &= ~RIGHT_BOTTOM_VERTICAL_FLAG;
        }
        if (leftBottomVertical) {
            mCornerRuleFlags |= LEFT_BOTTOM_VERTICAL_FLAG;
        } else {
            mCornerRuleFlags &= ~LEFT_BOTTOM_VERTICAL_FLAG;
        }
    }

    @Nullable
    public RendererCanvas getRenderCanvas(@Position int position) {
        if (position == POSITION_LEFT) {
            return mHorizontalCanvas[0];
        }
        if (position == POSITION_TOP) {
            return mVerticalCanvas[0];
        }
        if (position == POSITION_MAIN) {
            return mHorizontalCanvas[1];
        }
        if (position == POSITION_RIGHT) {
            return mHorizontalCanvas[2];
        }
        if (position == POSITION_BOTTOM) {
            return mVerticalCanvas[2];
        }
        return null;
    }

    public void addRenderCanvas(RendererCanvas canvas, @Position int position) {
        if (canvas == null) {
            throw new NullPointerException("RendererCanvas cannot be null!");
        }
        if (position == POSITION_LEFT) {
            mHorizontalCanvas[0] = canvas;
        }
        if (position == POSITION_TOP) {
            mVerticalCanvas[0] = canvas;
        }
        if (position == POSITION_MAIN) {
            mHorizontalCanvas[1] = canvas;
            mVerticalCanvas[1] = canvas;
        }
        if (position == POSITION_RIGHT) {
            mHorizontalCanvas[2] = canvas;
        }
        if (position == POSITION_BOTTOM) {
            mVerticalCanvas[2] = canvas;
        }
        if (canvas.getLayoutParams() == null) {
            canvas.setLayoutParams(new CanvasLayoutParams(0, 0));
        }
        canvas.attachedCanvasPortLayout(this, mDataProvider);
    }

    public void removeRenderCanvas(@Position int position) {
        if (position == POSITION_LEFT) {
            if (mHorizontalCanvas[0] != null) {
                mHorizontalCanvas[0].detachedCanvasPortLayout();
                mHorizontalCanvas[0] = null;
            }
        }
        if (position == POSITION_TOP) {
            if (mVerticalCanvas[0] != null) {
                mVerticalCanvas[0].detachedCanvasPortLayout();
                mVerticalCanvas[0] = null;
            }
        }
        if (position == POSITION_MAIN) {
            if (mHorizontalCanvas[1] != null) {
                mHorizontalCanvas[1].detachedCanvasPortLayout();
                mHorizontalCanvas[1] = null;
                mVerticalCanvas[1] = null;
            }
        }
        if (position == POSITION_RIGHT) {
            if (mHorizontalCanvas[2] != null) {
                mHorizontalCanvas[2].detachedCanvasPortLayout();
                mHorizontalCanvas[2] = null;
            }
        }
        if (position == POSITION_BOTTOM) {
            if (mVerticalCanvas[2] != null) {
                mVerticalCanvas[2].detachedCanvasPortLayout();
                mVerticalCanvas[2] = null;
            }
        }
    }
}
