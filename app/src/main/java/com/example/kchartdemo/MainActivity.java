package com.example.kchartdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benben.kchartlib.InteractiveKChartView;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.ICanvasPortLayout;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.render.ViewRenderer;

public class MainActivity extends AppCompatActivity {

    private InteractiveKChartView mKChart;
    private TextView mTvLeftWidth;
    private TextView mTvLeftWeight;
    private TextView mTvTopHeight;
    private TextView mTvTopWeight;
    private TextView mTvMainWidth;
    private TextView mTvMainHeight;
    private TextView mTvMaintWeight;
    private TextView mTvRightWidth;
    private TextView mTvRightWeight;
    private TextView mTvBottomHeight;
    private TextView mTvBottomWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKChart = findViewById(R.id.k_chart);
        mTvLeftWidth = findViewById(R.id.tv_left_width);
        mTvLeftWeight = findViewById(R.id.tv_left_weight);
        mTvTopHeight = findViewById(R.id.tv_top_height);
        mTvTopWeight = findViewById(R.id.tv_top_weight);
        mTvMainWidth = findViewById(R.id.tv_main_width);
        mTvMainHeight = findViewById(R.id.tv_main_height);
        mTvMaintWeight = findViewById(R.id.tv_main_weight);
        mTvRightWidth = findViewById(R.id.tv_right_width);
        mTvRightWeight = findViewById(R.id.tv_right_weight);
        mTvBottomHeight = findViewById(R.id.tv_bottom_height);
        mTvBottomWeight = findViewById(R.id.tv_bottom_weight);
    }


    public void updateLayout(View view) {
        mKChart.updateRenderPortLayout();
        showToast("更新布局");
    }

    public void toViewPort(View view) {
        Log.e("ScrollAndScaleView", mKChart.getViewRender().toViewPortString());
    }

    private boolean changeLeftPadding;

    public void changeLeftPadding(View view) {
        changeLeftPadding = !changeLeftPadding;
        if (changeLeftPadding) {
            showToast("30 左 padding");
            mKChart.setPadding(30, mKChart.getPaddingTop(), mKChart.getPaddingRight(), mKChart.getPaddingBottom());
        } else {
            showToast("0 左 padding");
            mKChart.setPadding(0, mKChart.getPaddingTop(), mKChart.getPaddingRight(), mKChart.getPaddingBottom());
        }
    }

    private boolean changeTopPadding;

    public void changeTopPadding(View view) {
        changeTopPadding = !changeTopPadding;
        if (changeTopPadding) {
            showToast("30 顶 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), 30, mKChart.getPaddingRight(), mKChart.getPaddingBottom());
        } else {
            showToast("0 顶 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), 0, mKChart.getPaddingRight(), mKChart.getPaddingBottom());
        }
    }

    private boolean changeRightPadding;

    public void changeRightPadding(View view) {
        changeRightPadding = !changeRightPadding;
        if (changeRightPadding) {
            showToast("30 右 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), mKChart.getPaddingTop(), 30, mKChart.getPaddingBottom());
        } else {
            showToast("0 右 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), mKChart.getPaddingTop(), 0, mKChart.getPaddingBottom());
        }
    }

    private boolean changeBottomPadding;

    public void changeBottomPadding(View view) {
        changeBottomPadding = !changeBottomPadding;
        if (changeBottomPadding) {
            showToast("30 底 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), mKChart.getPaddingTop(), mKChart.getPaddingRight(), 30);
        } else {
            showToast("0 底 padding");
            mKChart.setPadding(mKChart.getPaddingLeft(), mKChart.getPaddingTop(), mKChart.getPaddingRight(), 0);
        }
    }

    private boolean changeLeftCanvas;
    private RendererCanvas mLeftRenderCanvas = new RendererCanvas(new ICanvasPortLayout.CanvasLayoutParams(0, 0, 0));
    private boolean initLeft;
    public void changeLeftCanvas(View view) {
        if (!initLeft) {
            initLeft = true;
            IDrawingPortLayout.DrawingLayoutParams params;
            TestDrawing draw;

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            params.setWeight(1);
            params.setPercent(0.2f);
            draw = new TestDrawing(Color.RED, Color.BLACK, "中间", params);
            mLeftRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_LEFT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(2);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.DKGRAY, Color.BLACK, "left", params);
            mLeftRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_RIGHT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(3);
            params.setPercent(0.3f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GRAY, Color.BLACK, "right", params);
            mLeftRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_TOP);
            params.setWeight(4);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.LTGRAY, Color.BLACK, "top", params);
            mLeftRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
            params.setWeight(5);
            params.setPercent(0.1f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GREEN, Color.BLACK, "bottom", params);
            mLeftRenderCanvas.addDrawing(draw);
        }
        changeLeftCanvas = !changeLeftCanvas;
        if (changeLeftCanvas) {
            showToast("添加左画板");
            mKChart.getViewRender().addRenderCanvas(mLeftRenderCanvas, ViewRenderer.POSITION_LEFT);
        } else {
            showToast("移除左画板");
            mKChart.getViewRender().removeRenderCanvas(ViewRenderer.POSITION_LEFT);
        }
        mKChart.updateRenderPortLayout();
    }

    private boolean changeTopCanvas;
    private RendererCanvas mTopRenderCanvas = new RendererCanvas(new ICanvasPortLayout.CanvasLayoutParams(0, 0, 0));
    private boolean initTop;
    public void changeTopCanvas(View view) {
        if (!initTop) {
            initTop = true;
            IDrawingPortLayout.DrawingLayoutParams params;
            TestDrawing draw;

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            params.setWeight(1);
            params.setPercent(0.2f);
            draw = new TestDrawing(Color.RED, Color.BLACK, "中间", params);
            mTopRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_LEFT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(2);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.DKGRAY, Color.BLACK, "left", params);
            mTopRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_RIGHT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(3);
            params.setPercent(0.3f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GRAY, Color.BLACK, "right", params);
            mTopRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_TOP);
            params.setWeight(4);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.LTGRAY, Color.BLACK, "top", params);
            mTopRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
            params.setWeight(5);
            params.setPercent(0.1f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GREEN, Color.BLACK, "bottom", params);
            mTopRenderCanvas.addDrawing(draw);
        }
        changeTopCanvas = !changeTopCanvas;
        if (changeTopCanvas) {
            showToast("添加顶画板");
            mKChart.getViewRender().addRenderCanvas(mTopRenderCanvas, ViewRenderer.POSITION_TOP);
        } else {
            showToast("移除顶画板");
            mKChart.getViewRender().removeRenderCanvas(ViewRenderer.POSITION_TOP);
        }
        mKChart.updateRenderPortLayout();
    }

    private boolean changeMainCanvas;
    private RendererCanvas mMainRenderCanvas = new RendererCanvas(new ICanvasPortLayout.CanvasLayoutParams(0, 0, 0));
    private boolean initMain;

    public void changeMainCanvas(View view) {
        if (!initMain) {
            initMain = true;
            IDrawingPortLayout.DrawingLayoutParams params;
            TestDrawing draw;

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            params.setWeight(1);
            params.setPercent(0.2f);
            draw = new TestDrawing(Color.RED, Color.BLACK, "中间", params);
            mMainRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_LEFT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(2);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.DKGRAY, Color.BLACK, "left", params);
            mMainRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_RIGHT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(3);
            params.setPercent(0.3f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GRAY, Color.BLACK, "right", params);
            mMainRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_TOP);
            params.setWeight(4);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.LTGRAY, Color.BLACK, "top", params);
            mMainRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
            params.setWeight(5);
            params.setPercent(0.1f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GREEN, Color.BLACK, "bottom", params);
            mMainRenderCanvas.addDrawing(draw);
        }
        changeMainCanvas = !changeMainCanvas;
        if (changeMainCanvas) {
            showToast("添加主画板");
            mKChart.getViewRender().addRenderCanvas(mMainRenderCanvas, ViewRenderer.POSITION_MAIN);
        } else {
            showToast("移除主画板");
            mKChart.getViewRender().removeRenderCanvas(ViewRenderer.POSITION_MAIN);
        }
        mKChart.updateRenderPortLayout();
    }

    private boolean changeRightCanvas;
    private RendererCanvas mRightRenderCanvas = new RendererCanvas(new ICanvasPortLayout.CanvasLayoutParams(0, 0, 0));
    private boolean initRight;
    public void changeRightCanvas(View view) {
        if (!initRight) {
            initRight = true;
            IDrawingPortLayout.DrawingLayoutParams params;
            TestDrawing draw;

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            params.setWeight(1);
            params.setPercent(0.2f);
            draw = new TestDrawing(Color.RED, Color.BLACK, "中间", params);
            mRightRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_LEFT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(2);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.DKGRAY, Color.BLACK, "left", params);
            mRightRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_RIGHT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(3);
            params.setPercent(0.3f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GRAY, Color.BLACK, "right", params);
            mRightRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_TOP);
            params.setWeight(4);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.LTGRAY, Color.BLACK, "top", params);
            mRightRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
            params.setWeight(5);
            params.setPercent(0.1f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GREEN, Color.BLACK, "bottom", params);
            mRightRenderCanvas.addDrawing(draw);
        }
        changeRightCanvas = !changeRightCanvas;
        if (changeRightCanvas) {
            showToast("添加右画板");
            mKChart.getViewRender().addRenderCanvas(mRightRenderCanvas, ViewRenderer.POSITION_RIGHT);
        } else {
            showToast("移除右画板");
            mKChart.getViewRender().removeRenderCanvas(ViewRenderer.POSITION_RIGHT);
        }
        mKChart.updateRenderPortLayout();
    }

    private boolean changeBottomCanvas;
    private RendererCanvas mBottomRenderCanvas = new RendererCanvas(new ICanvasPortLayout.CanvasLayoutParams(0, 0, 0));
    private boolean initBottom;
    public void changeBottomCanvas(View view) {
        if (!initBottom) {
            initBottom = true;
            IDrawingPortLayout.DrawingLayoutParams params;
            TestDrawing draw;

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            params.setWeight(1);
            params.setPercent(0.2f);
            draw = new TestDrawing(Color.RED, Color.BLACK, "中间", params);
            mBottomRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_LEFT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(2);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.DKGRAY, Color.BLACK, "left", params);
            mBottomRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_RIGHT);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setWeight(3);
            params.setPercent(0.3f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GRAY, Color.BLACK, "right", params);
            mBottomRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_TOP);
            params.setWeight(4);
            params.setPercent(0.2f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.LTGRAY, Color.BLACK, "top", params);
            mBottomRenderCanvas.addDrawing(draw);

            params = new IDrawingPortLayout.DrawingLayoutParams();
            params.setHorizontalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_CENTER);
            params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
            params.setWeight(5);
            params.setPercent(0.1f);
            params.setVerticalLinear(true);
            params.setHorizontalLinear(true);
            draw = new TestDrawing(Color.GREEN, Color.BLACK, "bottom", params);
            mBottomRenderCanvas.addDrawing(draw);
        }
        changeBottomCanvas = !changeBottomCanvas;
        if (changeBottomCanvas) {
            showToast("添加底画板");
            mKChart.getViewRender().addRenderCanvas(mBottomRenderCanvas, ViewRenderer.POSITION_BOTTOM);
        } else {
            showToast("移除底画板");
            mKChart.getViewRender().removeRenderCanvas(ViewRenderer.POSITION_BOTTOM);
        }
        mKChart.updateRenderPortLayout();
    }

    private int leftWidth;

    public void addLeftW(View view) {
        leftWidth += 100;
        mTvLeftWidth.setText("" + leftWidth);
        mLeftRenderCanvas.getLayoutParams().setWidth(leftWidth);
        mKChart.updateRenderPortLayout();
        showToast("左布局增加宽度");
    }

    public void subLeftW(View view) {
        leftWidth -= 100;
        mTvLeftWidth.setText("" + leftWidth);
        mLeftRenderCanvas.getLayoutParams().setWidth(leftWidth);
        mKChart.updateRenderPortLayout();
        showToast("左布局减少宽度");
    }

    private int leftWeight;

    public void addLeftWeight(View view) {
        leftWeight += 1;
        mTvLeftWeight.setText("" + leftWeight);
        mLeftRenderCanvas.getLayoutParams().setWeight(leftWeight);
        mKChart.updateRenderPortLayout();
        showToast("左布局增加百分比");
    }

    public void subLeftWeight(View view) {
        leftWeight -= 1;
        mTvLeftWeight.setText("" + leftWeight);
        mLeftRenderCanvas.getLayoutParams().setWeight(leftWeight);
        mKChart.updateRenderPortLayout();
        showToast("左布局减少百分比");
    }

    private int topHeight;

    public void addTopH(View view) {
        topHeight += 100;
        mTvTopHeight.setText("" + topHeight);
        mTopRenderCanvas.getLayoutParams().setHeight(topHeight);
        mKChart.updateRenderPortLayout();
        showToast("顶布局增加高度");
    }

    public void subTopH(View view) {
        topHeight -= 100;
        mTvTopHeight.setText("" + topHeight);
        mTopRenderCanvas.getLayoutParams().setHeight(topHeight);
        mKChart.updateRenderPortLayout();
        showToast("顶布局减少高度");
    }

    private int topWeight;

    public void addTopWeight(View view) {
        topWeight += 1;
        mTvTopWeight.setText("" + topWeight);
        mTopRenderCanvas.getLayoutParams().setWeight(topWeight);
        mKChart.updateRenderPortLayout();
        showToast("顶布局增加百分比");
    }

    public void subTopWeight(View view) {
        topWeight -= 1;
        mTvTopWeight.setText("" + topWeight);
        mTopRenderCanvas.getLayoutParams().setWeight(topWeight);
        mKChart.updateRenderPortLayout();
        showToast("顶布局减少百分比");
    }

    private int mainWidth;

    public void addMainWidth(View view) {
        mainWidth += 100;
        mTvMainWidth.setText("" + mainWidth);
        mMainRenderCanvas.getLayoutParams().setWidth(mainWidth);
        mKChart.updateRenderPortLayout();
        showToast("主布局增加宽度");
    }

    public void subMainWidth(View view) {
        mainWidth -= 100;
        mTvMainWidth.setText("" + mainWidth);
        mMainRenderCanvas.getLayoutParams().setWidth(mainWidth);
        mKChart.updateRenderPortLayout();
        showToast("主布局减少宽度");
    }

    private int mainHeight;

    public void addMainHeight(View view) {
        mainHeight += 100;
        mTvMainHeight.setText("" + mainHeight);
        mMainRenderCanvas.getLayoutParams().setHeight(mainHeight);
        mKChart.updateRenderPortLayout();
        showToast("主布局增加高度");
    }

    public void subMainHeight(View view) {
        mainHeight -= 100;
        mTvMainHeight.setText("" + mainHeight);
        mMainRenderCanvas.getLayoutParams().setHeight(mainHeight);
        mKChart.updateRenderPortLayout();
        showToast("主布局减少高度");
    }

    private int mainWeight;

    public void addMainWeight(View view) {
        mainWeight += 1;
        mTvMaintWeight.setText("" + mainWeight);
        mMainRenderCanvas.getLayoutParams().setWeight(mainWeight);
        mKChart.updateRenderPortLayout();
        showToast("主布局增加百分比");
    }

    public void subMainWeight(View view) {
        mainWeight -= 1;
        mTvMaintWeight.setText("" + mainWeight);
        mMainRenderCanvas.getLayoutParams().setWeight(mainWeight);
        mKChart.updateRenderPortLayout();
        showToast("主布局减少百分比");
    }

    private int rightWidth;

    public void addRightW(View view) {
        rightWidth += 100;
        mTvRightWidth.setText("" + rightWidth);
        mRightRenderCanvas.getLayoutParams().setWidth(rightWidth);
        mKChart.updateRenderPortLayout();
        showToast("右布局增加宽度");
    }

    public void subRightW(View view) {
        rightWidth -= 100;
        mTvRightWidth.setText("" + rightWidth);
        mRightRenderCanvas.getLayoutParams().setWidth(rightWidth);
        mKChart.updateRenderPortLayout();
        showToast("右布局减少宽度");
    }

    private int rightWeight;

    public void addRightWeight(View view) {
        rightWeight += 1;
        mTvRightWeight.setText("" + rightWeight);
        mRightRenderCanvas.getLayoutParams().setWeight(rightWeight);
        mKChart.updateRenderPortLayout();
        showToast("右布局增加百分比");
    }

    public void subRightWeight(View view) {
        rightWeight -= 1;
        mTvRightWeight.setText("" + rightWeight);
        mRightRenderCanvas.getLayoutParams().setWeight(rightWeight);
        mKChart.updateRenderPortLayout();
        showToast("右布局减少百分比");
    }

    private int bottomHeight;

    public void addBottomH(View view) {
        bottomHeight += 100;
        mTvBottomHeight.setText("" + bottomHeight);
        mBottomRenderCanvas.getLayoutParams().setHeight(bottomHeight);
        mKChart.updateRenderPortLayout();
        showToast("底布局增加高度");
    }

    public void subBottomH(View view) {
        bottomHeight -= 100;
        mTvBottomHeight.setText("" + bottomHeight);
        mBottomRenderCanvas.getLayoutParams().setHeight(bottomHeight);
        mKChart.updateRenderPortLayout();
        showToast("底布局减少高度");
    }

    private int bottomWeight;

    public void addBottomWeight(View view) {
        bottomWeight += 1;
        mTvBottomWeight.setText("" + bottomWeight);
        mBottomRenderCanvas.getLayoutParams().setWeight(bottomWeight);
        mKChart.updateRenderPortLayout();
        showToast("底布局增加百分比");
    }

    public void subBottomWeight(View view) {
        bottomWeight -= 1;
        mTvBottomWeight.setText("" + bottomWeight);
        mBottomRenderCanvas.getLayoutParams().setWeight(bottomWeight);
        mKChart.updateRenderPortLayout();
        showToast("底布局减少百分比");
    }

    private boolean leftTopCorner = true;
    private boolean rightTopCorner = true;
    private boolean rightBottomCorner = true;
    private boolean leftBottomCorner = true;

    public void changeLeftTopCorner(View view) {
        leftTopCorner = !leftTopCorner;
        mKChart.getViewRender().setCornerRule(leftTopCorner, rightTopCorner, rightBottomCorner, leftBottomCorner);
        mKChart.updateRenderPortLayout();
        showToast("左上角：" + leftTopCorner);
    }

    public void changeRightTopCorner(View view) {
        rightTopCorner = !rightTopCorner;
        mKChart.getViewRender().setCornerRule(leftTopCorner, rightTopCorner, rightBottomCorner, leftBottomCorner);
        mKChart.updateRenderPortLayout();
        showToast("右上角：" + rightTopCorner);
    }

    public void changeRightBottomCorner(View view) {
        rightBottomCorner = !rightBottomCorner;
        mKChart.getViewRender().setCornerRule(leftTopCorner, rightTopCorner, rightBottomCorner, leftBottomCorner);
        mKChart.updateRenderPortLayout();
        showToast("右下角：" + rightBottomCorner);
    }

    public void changeLeftBottomCorner(View view) {
        leftBottomCorner = !leftBottomCorner;
        mKChart.getViewRender().setCornerRule(leftTopCorner, rightTopCorner, rightBottomCorner, leftBottomCorner);
        mKChart.updateRenderPortLayout();
        showToast("左下角：" + leftBottomCorner);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}