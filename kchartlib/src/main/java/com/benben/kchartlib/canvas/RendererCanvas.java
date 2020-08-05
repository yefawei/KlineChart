package com.benben.kchartlib.canvas;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.CallSuper;

import com.benben.kchartlib.adapter.IAdapter;
import com.benben.kchartlib.drawing.Drawing;
import com.benben.kchartlib.impl.ICanvasPortLayout;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.impl.IViewPort;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/1
 * @描述 : 画板
 */
public class RendererCanvas implements IRendererCanvas, IDrawingPortLayout, IViewPort {

    private ICanvasPortLayout.CanvasLayoutParams mLayoutParams;
    private int mWidth;
    private int mHeight;
    protected Rect mViewPort = new Rect();
    private boolean mInUpdateDrawingPortLayout;
    private ICanvasPortLayout mCanvasPortLayout;

    protected IDataProvider mDataProvider;

    private ArrayList<Drawing> mDrawings = new ArrayList<>();
    private ArrayList<Drawing> mHorizontalLinearDrawings = new ArrayList<>();
    private ArrayList<Drawing> mVerticalLinearDrawings = new ArrayList<>();

    public RendererCanvas() {
    }

    public RendererCanvas(ICanvasPortLayout.CanvasLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("CanvasLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public void setLayoutParams(ICanvasPortLayout.CanvasLayoutParams params) {
        if (params == null) {
            throw new NullPointerException("CanvasLayoutParams cannot be null!");
        }
        mLayoutParams = params;
    }

    public ICanvasPortLayout.CanvasLayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    @Override
    public void setInUpdateDrawingPortLayout(boolean inUpdate) {
        mInUpdateDrawingPortLayout = inUpdate;
    }

    @Override
    public boolean inUpdateDrawingPortLayout() {
        return mInUpdateDrawingPortLayout;
    }

    @Override
    public void updatePortLayout() {
        setInUpdateDrawingPortLayout(true);
        if (mViewPort.isEmpty()) {
            for (Drawing drawing : mDrawings) {
                drawing.setWidth(0);
                drawing.setHeight(0);
                drawing.updateViewPort(0, 0, 0, 0);
            }
            setInUpdateDrawingPortLayout(false);
            return;
        }

        mHorizontalLinearDrawings.clear();
        mVerticalLinearDrawings.clear();
        for (Drawing drawing : mDrawings) {
            if (drawing.getLayoutParams().isHorizontalLinear()) {
                mHorizontalLinearDrawings.add(drawing);
            }
            if (drawing.getLayoutParams().isVerticalLinear()) {
                mVerticalLinearDrawings.add(drawing);
            }
        }

        final int w = this.getWidth();
        final int h = this.getHeight();
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
        DrawingLayoutParams params;
        // 计算出水平方向宽度
        for (Drawing drawing : mHorizontalLinearDrawings) {
            params = drawing.getLayoutParams();
            if (params.getWidth() > 0) {
                tempValue = params.getWidth();
            } else if (params.getPercent() > 0) {
                tempValue = Math.round(w * params.getPercent());
            } else if (params.getWeight() > 0) {
                tempValue = params.getWeight() * weightUnitWidth;
            } else {
                tempValue = 0;
            }
            if (tempValue > 0) {
                if (tempValue > width) {
                    drawing.setWidth(width);
                    width = 0;
                } else {
                    drawing.setWidth(tempValue);
                    width -= tempValue;
                }
            }
        }
        if (width > 0 && weightUnitWidth > 0) {
            int tw = 0;
            // 说明按比例宽度没有填满视图
            for (Drawing drawing : mHorizontalLinearDrawings) {
                params = drawing.getLayoutParams();
                if (params.getWidth() > 0 || params.getPercent() > 0) continue;
                if (params.getWeight() > 0) {
                    tempValue = Math.round(width * 1.0f / horizontalWeightAndWidth[0] * params.getWeight() + 0.5f);
                    if (tempValue + tw > width) {
                        tempValue = width - tw;
                    }
                    tw += tempValue;
                    Log.w("RendererCanvas", "Horizontal layout with gaps. " + drawing.getClass().getSimpleName() + " ext width: " + tempValue);
                    drawing.setWidth(drawing.getWidth() + tempValue);
                }
                if (width == tw) break;
            }
        }
        // 计算出垂直方向宽度
        for (Drawing drawing : mVerticalLinearDrawings) {
            params = drawing.getLayoutParams();
            if (params.getHeight() > 0) {
                tempValue = params.getHeight();
            } else if (params.getPercent() > 0) {
                tempValue = Math.round(h * params.getPercent());
            } else if (params.getWeight() > 0) {
                tempValue = params.getWeight() * weightUnitHeight;
            } else {
                tempValue = 0;
            }
            if (tempValue > 0) {
                if (tempValue > height) {
                    drawing.setHeight(height);
                    height = 0;
                } else {
                    drawing.setHeight(tempValue);
                    height -= tempValue;
                }
            }
        }
        if (height > 0 && weightUnitHeight > 0) {
            int th = 0;
            // 说明按比例高度没有填满视图
            for (Drawing drawing : mVerticalLinearDrawings) {
                params = drawing.getLayoutParams();
                if (params.getHeight() > 0 || params.getPercent() > 0) continue;
                if (params.getWeight() > 0) {
                    tempValue = Math.round(width * 1.0f / horizontalWeightAndWidth[0] * params.getWeight() + 0.5f);
                    if (tempValue + th > height) {
                        tempValue = height - th;
                    }
                    th += tempValue;
                    Log.w("RendererCanvas", "Vertical layout with gaps. " + drawing.getClass().getSimpleName() + " ext height: " + tempValue);
                    drawing.setHeight(drawing.getHeight() + tempValue);
                }
                if (height == th) break;
            }
        }
        for (Drawing drawing : mDrawings) {
            params = drawing.getLayoutParams();
            if (!params.isHorizontalLinear()) {
                if (params.getWidth() > 0) {
                    tempValue = params.getWidth();
                } else if (params.getPercent() > 0) {
                    tempValue = Math.round(w * params.getPercent());
                } else if (params.getWeight() > 0) {
                    tempValue = w;
                } else {
                    tempValue = 0;
                }
                if (tempValue > 0) {
                    drawing.setWidth(Math.min(tempValue, w));
                }
            }
            if (!params.isVerticalLinear()) {
                if (params.getHeight() > 0) {
                    tempValue = params.getHeight();
                } else if (params.getPercent() > 0) {
                    tempValue = Math.round(h * params.getPercent());
                } else if (params.getWeight() > 0) {
                    tempValue = h;
                } else {
                    tempValue = 0;
                }
                if (tempValue > 0) {
                    drawing.setHeight(Math.min(tempValue, h));
                }
            }
        }

        int startX = getLeft();
        int startY = getTop();
        int x;
        int y;
        // 最终确认布局
        for (Drawing drawing : mDrawings) {
            params = drawing.getLayoutParams();
            if (params.isHorizontalLinear()) {
                x = startX;
                startX += drawing.getWidth();
            } else if (params.getHorizontalPosition() == DrawingLayoutParams.POSITION_CENTER) {
                x = getLeft() + (getWidth() - drawing.getWidth()) / 2;
            } else if (params.getHorizontalPosition() == DrawingLayoutParams.POSITION_RIGHT) {
                x = getRight() - drawing.getWidth();
            } else {
                x = getLeft();
            }
            if (params.isVerticalLinear()) {
                y = startY;
                startY += drawing.getHeight();
            } else if (params.getVerticalPosition() == DrawingLayoutParams.POSITION_CENTER) {
                y = getTop() + (getHeight() - drawing.getHeight()) / 2;
            } else if (params.getVerticalPosition() == DrawingLayoutParams.POSITION_BOTTOM) {
                y = getBottom() - drawing.getHeight();
            } else {
                y = getTop();
            }
            drawing.updateViewPort(x, y, x + drawing.getWidth(), y + drawing.getHeight());
        }

        setInUpdateDrawingPortLayout(false);
    }

    /**
     * @return [0]:all weight [1]:all width
     */
    private int[] getHorizontalWeightAndWidth() {
        int[] i = new int[2];
        for (Drawing drawing : mHorizontalLinearDrawings) {
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.getWidth() > 0) {
                i[1] += params.getWidth();
            } else if (params.getPercent() > 0) {
                i[1] += Math.round(this.getWidth() * params.getPercent());
            } else if (params.getWeight() > 0) {
                i[0] += params.getWeight();
            }
        }
        return i;
    }

    /**
     * @return [0]:all weight [1]:all height
     */
    private int[] getVerticalWeightAndHeight() {
        int[] i = new int[2];
        for (Drawing drawing : mVerticalLinearDrawings) {
            DrawingLayoutParams params = drawing.getLayoutParams();
            if (params.getHeight() > 0) {
                i[1] += params.getHeight();
            } else if (params.getPercent() > 0) {
                i[1] += Math.round(this.getHeight() * params.getPercent());
            } else if (params.getWeight() > 0) {
                i[0] += params.getWeight();
            }
        }
        return i;
    }

    @CallSuper
    @Override
    public void attachedCanvasPortLayout(ICanvasPortLayout portLayout, IDataProvider dataProvider) {
        mCanvasPortLayout = portLayout;
        mDataProvider = dataProvider;
        for (Drawing drawing : mDrawings) {
            drawing.attachedDrawingPortLayout(this, mDataProvider);
        }
    }

    @CallSuper
    @Override
    public void detachedCanvasPortLayout() {
        mCanvasPortLayout = null;
        mDataProvider = null;
        mWidth = 0;
        mHeight = 0;
        mViewPort.setEmpty();
        for (Drawing drawing : mDrawings) {
            drawing.detachedDrawingPortLayout();
        }
    }

    @Override
    public void setWidth(int width) {
        if (mCanvasPortLayout != null && mCanvasPortLayout.inUpdateCanvasPortLayout()) {
            mWidth = width;
        } else {
            Log.w("RendererCanvas", "Setting width is not allowed.");
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public void setHeight(int height) {
        if (mCanvasPortLayout != null && mCanvasPortLayout.inUpdateCanvasPortLayout()) {
            mHeight = height;
        } else {
            Log.w("RendererCanvas", "Setting width is not allowed.");
        }
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getLeft() {
        return mViewPort.left;
    }

    @Override
    public int getTop() {
        return mViewPort.top;
    }

    @Override
    public int getRight() {
        return mViewPort.right;
    }

    @Override
    public int getBottom() {
        return mViewPort.bottom;
    }

    @Override
    public void updateViewPort(int left, int top, int right, int bottom) {
        if (mCanvasPortLayout != null && mCanvasPortLayout.inUpdateCanvasPortLayout()) {
            mViewPort.set(left, top, right, bottom);
        } else {
            Log.w("RendererCanvas", "Setting port is not allowed.");
        }
    }

    @Override
    public String toViewPortString() {
        StringBuilder vps = new StringBuilder("--------" + this.getClass().getSimpleName() +
                "--ViewPort: " +
                "[width: " + getWidth() + ", height: " + getHeight() + "]" +
                "[left: " + getLeft() + "]" +
                "[top: " + getTop() + "]" +
                "[right: " + getRight() + "]" +
                "[bottom: " + getBottom() + "]");
        vps.append("\n");
        for (Drawing drawing : mDrawings) {
            vps.append(drawing.toViewPortString());
            vps.append("\n");
        }
        return vps.toString();
    }

    @Override
    public boolean isValid() {
        return !mViewPort.isEmpty();
    }

    @Override
    public void addDrawing(Drawing drawing) {
        if (drawing == null) return;

        if (drawing.getLayoutParams() == null) {
            drawing.setLayoutParams(new DrawingLayoutParams());
        }
        mDrawings.add(drawing);
        if (mCanvasPortLayout != null) {
            drawing.attachedDrawingPortLayout(this, mDataProvider);
        }
    }

    @Override
    public void removeDrawing(Drawing drawing) {
        if (drawing == null) return;

        mDrawings.remove(drawing);
        if (mCanvasPortLayout != null) {
            drawing.detachedDrawingPortLayout();
        }
    }

    @Override
    public void render(Canvas canvas) {
        IAdapter adapter = mDataProvider.getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            renderEmpty(canvas);
        } else {
            renderData(canvas);
        }
    }

    private void renderEmpty(Canvas canvas) {
        for (Drawing drawing : mDrawings) {
            if (!drawing.isValid()) continue;
            canvas.save();
            if (drawing.drawInViewPort()) {
                canvas.clipRect(drawing.getLeft(), drawing.getTop(), drawing.getRight(), drawing.getBottom());
            }
            drawing.drawEmpty(canvas);
            canvas.restore();
        }
    }

    private void renderData(Canvas canvas) {
        for (Drawing drawing : mDrawings) {
            if (!drawing.isValid()) continue;
            canvas.save();
            if (drawing.drawInViewPort()) {
                canvas.clipRect(drawing.getLeft(), drawing.getTop(), drawing.getRight(), drawing.getBottom());
            }
            drawing.drawData(canvas);
            canvas.restore();
        }
    }
}
