package com.example.kchartdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.kchartdemo.Drawing.CandleDrawing;
import com.example.kchartdemo.Drawing.ClickDrawing;
import com.example.kchartdemo.Drawing.HighlightDrawing;
import com.example.kchartdemo.Drawing.LeftPaddingDrawing;
import com.example.kchartdemo.Drawing.ParalleCandleDrawing;
import com.example.kchartdemo.Drawing.PositionDrawing;
import com.example.kchartdemo.Drawing.RepeatPointDrawing;
import com.example.kchartdemo.Drawing.RightPaddingDrawing;
import com.example.kchartdemo.Drawing.TransitionCandleDrawing;
import com.example.kchartdemo.Drawing.TransitionValumeDrawing;
import com.example.kchartdemo.Drawing.VolumeDrawing;
import com.example.kchartdemo.Drawing.VolumeHighlightDrawing;
import com.example.kchartdemo.adapter.KChartAdapter;
import com.example.kchartdemo.data.AnimDataSizeChangeHandler;
import com.example.kchartdemo.data.DragonKLineDataProvider;
import com.example.kchartdemo.data.KlineInfo;
import com.example.kchartdemo.utils.ConvertUtils;
import com.yfw.kchartcore.InteractiveKChartView;
import com.yfw.kchartcore.ScrollAndScaleView;
import com.yfw.kchartcore.canvas.MainRendererCanvas;
import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.data.Transformer;
import com.yfw.kchartcore.index.range.CandleIndexRange;
import com.yfw.kchartcore.index.range.ReverseIndexRange;
import com.yfw.kchartcore.index.range.TransitionIndexRange;
import com.yfw.kchartcore.render.MainRenderer;
import com.yfw.kchartcore.touch.TouchTapManager;
import com.yfw.kchartext.index.range.VolumeIndexRange;

import java.util.ArrayList;

public class Test2Activity extends AppCompatActivity {

    private DragonKLineDataProvider mDataProvider;
    private InteractiveKChartView<KlineInfo> mKChart;
    private ScrollView mScrollView;
    private KChartAdapter mAdapter;
    private CandleIndexRange mCandleIndexRange;
    private VolumeIndexRange mVolumeIndexRange;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        mKChart = findViewById(R.id.k_chart);
        mScrollView = findViewById(R.id.scroll_view);
        mDataProvider = new DragonKLineDataProvider();
        mKChart.setPointWidth(ConvertUtils.dp2px(this, 8));
        mKChart.setDataSizeChangeHandler(new AnimDataSizeChangeHandler());
        mKChart.setMaxScaleX(6);
        mKChart.getPaddingHelper().setRightExtPadding(100, false);
        mKChart.getPaddingHelper().setLeftExtPadding(100, false);
        mKChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Test2Activity.this, "onClick", Toast.LENGTH_LONG).show();
            }
        });

        mKChart.setOnDoubleClickListener(new ScrollAndScaleView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                Toast.makeText(Test2Activity.this, "onDoubleClick", Toast.LENGTH_LONG).show();
            }
        });
        mKChart.setOnPressChangeListener(new ScrollAndScaleView.OnPressChangeListener() {
            @Override
            public void onPressChange(View v, boolean onLongpress) {
                Toast.makeText(Test2Activity.this, "onPressChange: " + onLongpress, Toast.LENGTH_LONG).show();
            }
        });
        mKChart.setOnScrollChangeListener(new ScrollAndScaleView.OnScrollChangeListener() {
            @Override
            public void onScrollXChange(int scrollX, int oldScrollX) {
                Log.e("OnScrollChangeListener", "onScrollChange: " + scrollX + " -- " + oldScrollX);
            }

            @Override
            public void onScrollStateChanged(int state) {
                Log.e("OnScrollChangeListener", "onScrollStateChanged: " + state);
            }
        });
        mKChart.getTransformer().setOnViewIndexListener(new Transformer.OnViewIndexListener() {
            @Override
            public void viewIndex(int startIndex, int endIndex) {
                Log.e("OnViewIndexListener", "viewIndex: " + startIndex + " -- " + endIndex);
            }
        });
        mKChart.setOnPaddingListener(new InteractiveKChartView.OnPaddingListener() {
            @Override
            public void rightPadding(boolean inRightPadding) {
                Log.e("OnPaddingListener", "rightPadding: " + inRightPadding);
            }

            @Override
            public void leftPadding(boolean inLeftPadding) {
                Log.e("OnPaddingListener", "leftPadding: " + inLeftPadding);
            }
        });
        mKChart.setOnMarginListener(new InteractiveKChartView.OnMarginListener() {
            @Override
            public void onRightMargin() {
                Log.e("OnMarginListener", "onRightMargin");
            }

            @Override
            public void onLeftMargin() {
                Log.e("OnMarginListener", "onLeftMargin");
            }
        });
        mKChart.getTouchTapManager().setOnSingleSelectedChangeListener(new TouchTapManager.OnSingleSelectedChangeListener<KlineInfo>() {
            @Override
            public void onSingleSelectedChanged(int index, KlineInfo entity) {
                Log.e("TouchTapManager", "on single: " + index);
            }
        });
        mKChart.getTouchTapManager().setOnDoubleSelectedChangeListener(new TouchTapManager.OnDoubleSelectedChangeListener<KlineInfo>() {
            @Override
            public void onDoubleSelectedChanged(int index, KlineInfo entity) {
                Log.e("TouchTapManager", "on double: " + index);
            }
        });
        mKChart.getTouchTapManager().setOnLongSelectedChangeListener(new TouchTapManager.OnLongSelectedChangeListener<KlineInfo>() {
            @Override
            public void onLongleSelectedChanged(int index, KlineInfo entity) {
                Log.e("TouchTapManager", "on long: " + index);
            }
        });

        MainRenderer<KlineInfo> viewRender = mKChart.getMainRenderer();
        mCandleIndexRange = new CandleIndexRange();
        mVolumeIndexRange = new VolumeIndexRange();


        MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1, 3);
        MainRendererCanvas<KlineInfo> mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);
        RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new CandleDrawing(mCandleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new TransitionCandleDrawing(new TransitionIndexRange(mCandleIndexRange), layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        ParalleCandleDrawing paralleCandleDrawing = new ParalleCandleDrawing(new TransitionIndexRange(mCandleIndexRange), layoutParams);
        paralleCandleDrawing.setTag("paralle");
        mKChart.addOnAdapterChangeListener(paralleCandleDrawing);
        mainRenderCanvas.addDrawing(paralleCandleDrawing, true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setWidth(100);
        layoutParams.setHorizontalPosition(RendererCanvas.DrawingLayoutParams.POSITION_LEFT);
        mainRenderCanvas.addDrawing(new LeftPaddingDrawing(layoutParams));

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setWidth(100);
        layoutParams.setHorizontalPosition(RendererCanvas.DrawingLayoutParams.POSITION_RIGHT);
        mainRenderCanvas.addDrawing(new RightPaddingDrawing(layoutParams));

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new HighlightDrawing(mCandleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new HighlightDrawing(mCandleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        HighlightDrawing highlightDrawing = new HighlightDrawing(mCandleIndexRange, layoutParams);
        highlightDrawing.setTag("paralleHigh");
        mainRenderCanvas.addDrawing(highlightDrawing, true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_MAIN);

//        layoutParams = new RendererCanvas.DrawingLayoutParams();
//        layoutParams.setWeight(1);
//        mainRenderCanvas.addDrawing(new TriggerRepeatPointDrawing(layoutParams));
//
//        layoutParams = new RendererCanvas.DrawingLayoutParams();
//        layoutParams.setWeight(1);
//        mainRenderCanvas.addDrawing(new RepeatPointDrawing(layoutParams));

        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 0, 1);
        mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new VolumeDrawing(mVolumeIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        TransitionValumeDrawing drawing = new TransitionValumeDrawing(new TransitionIndexRange(mVolumeIndexRange), layoutParams);
        drawing.setTag("tvalume");
        mainRenderCanvas.addDrawing(drawing, true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new VolumeHighlightDrawing(mVolumeIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new VolumeHighlightDrawing(mVolumeIndexRange, layoutParams), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_BOTTOM);

        mAdapter = new KChartAdapter();
        mKChart.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("快速查看：往后插100个数据\n\n长按显示高亮线\n左右滑动查看数据\n双指放大放小\n滑动K线区域查看手势嵌套\n滑动底部按钮查看更多设置")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    int startIndex = 100;
    int endIndex = 100;

    public void pre10(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        int size = Math.min(startIndex, 10);
        for (int i = 0; i < size; i++) {
            list.add(mDataProvider.mData.get(startIndex - size + i));
        }
        mAdapter.addDatas(list);
        showToast("pre add: " + (startIndex - size) + "-" + (startIndex));
        startIndex -= size;
    }

    public void centerAdd(View view) {
        if (endIndex >= mDataProvider.mData.size() - 1) return;
        KlineInfo klineInfo = mDataProvider.mData.get(endIndex);
        showToast("add index: " + endIndex);
        mAdapter.addData(klineInfo);
        endIndex++;
    }

    public void add10(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        int size = Math.min((mDataProvider.mData.size() - endIndex), 10);
        for (int i = 0; i < size; i++) {
            list.add(mDataProvider.mData.get(endIndex + i));
        }
        mAdapter.addDatas(list);
        showToast("add10: " + (endIndex) + "-" + (endIndex + size));
        endIndex += size;
    }

    public void add100(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        int size = Math.min((mDataProvider.mData.size() - endIndex), 100);
        for (int i = 0; i < size; i++) {
            list.add(mDataProvider.mData.get(endIndex + i));
        }
        mAdapter.addDatas(list);
        showToast("add100: " + (endIndex) + "-" + (endIndex + size));
        endIndex += size;
    }

    public void updateLast(View view) {
        if (mAdapter.getCount() == 0) {
            return;
        }
        KlineInfo item = (KlineInfo) mAdapter.getItem(mAdapter.getCount() - 1);
        double ceil = Math.ceil(Math.log10(item.close_price));
        if (System.currentTimeMillis() % 2 == 0) {
            item.close_price = item.close_price + (float) (Math.random() * ceil) * 0.8f;
        } else {
            item.close_price = item.close_price - (float) (Math.random() * ceil) * 0.8f;
        }
        if (item.close_price < item.min_price) {
            item.min_price = item.close_price;
        }
        if (item.close_price > item.max_price) {
            item.max_price = item.close_price;
        }
        mAdapter.notifyLastUpdated(mAdapter.getCount() - 1);
    }

    public void clearData(View view) {
        startIndex = 100;
        endIndex = 100;
        mAdapter.clear();
    }

    public void reset(View view) {
        mKChart.reset(true);
    }

    public void toViewPort(View view) {
        Log.e("ScrollAndScaleView", mKChart.getMainRenderer().toViewPortString());
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void updateLayout(View view) {
        mKChart.updateRenderPortLayout();
        showToast("更新布局");
    }

    public void toLeft(View view) {
        mKChart.scrollToIndex(0, 0, 0, true);
    }

    public void toCenter(View view) {
        mKChart.scrollToIndex(mAdapter.getCount() / 2, 0.5f, 0.5f, true);
    }

    public void toRight(View view) {
        mKChart.scrollToIndex(mAdapter.getCount() - 1, 1f, 1f, true);
    }

    ClickDrawing mOneDrawing = new ClickDrawing("One", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer<KlineInfo> mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mOneDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    ClickDrawing mTwoDrawing = new ClickDrawing("Two", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer<KlineInfo> mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mTwoDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    ClickDrawing mThreeDrawing = new ClickDrawing("Three", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer<KlineInfo> mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mThreeDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    public void addClickTag(View view) {
        MainRenderer<KlineInfo> mainRenderer = mKChart.getMainRenderer();
        MainRendererCanvas<KlineInfo> renderCanvas = mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN);
        if (mOneDrawing.getLayoutParams() == null) {
            RendererCanvas.DrawingLayoutParams params = new RendererCanvas.DrawingLayoutParams();
            params.setWidth(100);
            params.setHeight(100);
            params.setVerticalPosition(RendererCanvas.DrawingLayoutParams.POSITION_TOP);
            params.setHorizontalPosition(RendererCanvas.DrawingLayoutParams.POSITION_LEFT);
            mOneDrawing.setLayoutParams(params);
        }
        renderCanvas.addDrawing(mOneDrawing);

        if (mTwoDrawing.getLayoutParams() == null) {
            RendererCanvas.DrawingLayoutParams params = new RendererCanvas.DrawingLayoutParams();
            params.setWidth(100);
            params.setHeight(100);
            params.setVerticalPosition(RendererCanvas.DrawingLayoutParams.POSITION_CENTER);
            params.setHorizontalPosition(RendererCanvas.DrawingLayoutParams.POSITION_CENTER);
            mTwoDrawing.setLayoutParams(params);
        }
        renderCanvas.addDrawing(mTwoDrawing);

        if (mThreeDrawing.getLayoutParams() == null) {
            RendererCanvas.DrawingLayoutParams params = new RendererCanvas.DrawingLayoutParams();
            params.setWidth(100);
            params.setHeight(100);
            params.setVerticalPosition(RendererCanvas.DrawingLayoutParams.POSITION_BOTTOM);
            params.setHorizontalPosition(RendererCanvas.DrawingLayoutParams.POSITION_RIGHT);
            mThreeDrawing.setLayoutParams(params);
        }
        renderCanvas.addDrawing(mThreeDrawing);
        mKChart.updateRenderPortLayout();
    }

    public static void startKchart2(Context context) {
        context.startActivity(new Intent(context, Test2Activity.class));
    }


    boolean showMarker = false;

    public void showMarker(View view) {
        if (showMarker) return;
        showMarker = true;
        RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        RepeatPointDrawing drawing = new RepeatPointDrawing(layoutParams);
        drawing.setTag("marker");
        mKChart.getMainRenderer().getRenderCanvas(MainRenderer.POSITION_MAIN).addDrawing(drawing);
        mKChart.updateRenderPortLayout();
    }

    public void hideMarker(View view) {
        if (!showMarker) return;
        showMarker = false;
        mKChart.getMainRenderer()
                .getRenderCanvas(MainRenderer.POSITION_MAIN)
                .removeDrawingByTag("marker");
        mKChart.updateRenderPortLayout();
    }


    boolean isReverseKchart = false;

    public void reverseKchart(View view) {
        MainRendererCanvas<KlineInfo> mainRenderCanvas = mKChart.getMainRenderer()
                .getRenderCanvas(MainRenderer.POSITION_MAIN);
        int paralleIndex = mainRenderCanvas.drawingIndexTag("paralle");

        ParalleCandleDrawing drawing = (ParalleCandleDrawing) mainRenderCanvas.getDrawing(paralleIndex);
        mKChart.removeOnAdapterChangeListener(drawing);
        mainRenderCanvas.removeDrawing(paralleIndex);

        if (isReverseKchart) {
            isReverseKchart = false;

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 1);
            ParalleCandleDrawing paralleCandleDrawing = new ParalleCandleDrawing(new TransitionIndexRange(mCandleIndexRange), layoutParams);
            paralleCandleDrawing.setTag("paralle");
            mKChart.addOnAdapterChangeListener(paralleCandleDrawing);

            mainRenderCanvas.addDrawing(paralleIndex, paralleCandleDrawing, true);

            int paralleHighIndex = mainRenderCanvas.drawingIndexTag("paralleHigh");
            RendererCanvas.DrawingLayoutParams layoutParams2 = new RendererCanvas.DrawingLayoutParams();
            layoutParams2.setWeight(1);
            layoutParams2.setVerticalLinear(true, 2);
            HighlightDrawing highlightDrawing = new HighlightDrawing(mCandleIndexRange, layoutParams2);
            highlightDrawing.setTag("paralleHigh");
            mainRenderCanvas.replaceDrawing(paralleHighIndex, highlightDrawing);

        } else {
            isReverseKchart = true;

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 1);
            ParalleCandleDrawing paralleCandleDrawing = new ParalleCandleDrawing(new TransitionIndexRange(new ReverseIndexRange(mCandleIndexRange)), layoutParams);
            paralleCandleDrawing.setTag("paralle");
            mKChart.addOnAdapterChangeListener(paralleCandleDrawing);

            mainRenderCanvas.addDrawing(paralleIndex, paralleCandleDrawing, true);

            int paralleHighIndex = mainRenderCanvas.drawingIndexTag("paralleHigh");
            RendererCanvas.DrawingLayoutParams layoutParams2 = new RendererCanvas.DrawingLayoutParams();
            layoutParams2.setWeight(1);
            layoutParams2.setVerticalLinear(true, 2);
            HighlightDrawing highlightDrawing = new HighlightDrawing(new ReverseIndexRange(mCandleIndexRange), layoutParams2);
            highlightDrawing.setTag("paralleHigh");
            mainRenderCanvas.replaceDrawing(paralleHighIndex, highlightDrawing);
        }
        mKChart.updateRenderPortLayout();
    }

    boolean isReverseVolume = false;

    public void reverseVolume(View view) {
        if (isReverseVolume) {
            isReverseVolume = false;
            MainRendererCanvas<KlineInfo> mainRenderCanvas = mKChart.getMainRenderer()
                    .getRenderCanvas(MainRenderer.POSITION_BOTTOM);
            int tvalumeIndex = mainRenderCanvas.drawingIndexTag("tvalume");
            mainRenderCanvas.removeDrawing(tvalumeIndex);

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 1);
            TransitionValumeDrawing drawing = new TransitionValumeDrawing(new TransitionIndexRange(mVolumeIndexRange), layoutParams);
            drawing.setTag("tvalume");
            mainRenderCanvas.addDrawing(tvalumeIndex, drawing, true);
        } else {
            isReverseVolume = true;

            MainRendererCanvas<KlineInfo> mainRenderCanvas = mKChart.getMainRenderer()
                    .getRenderCanvas(MainRenderer.POSITION_BOTTOM);
            int tvalumeIndex = mainRenderCanvas.drawingIndexTag("tvalume");
            mainRenderCanvas.removeDrawing(tvalumeIndex);

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 1);
            TransitionValumeDrawing drawing = new TransitionValumeDrawing(new TransitionIndexRange(new ReverseIndexRange(mVolumeIndexRange)), layoutParams);
            drawing.setTag("tvalume");
            mainRenderCanvas.addDrawing(tvalumeIndex, drawing, true);
        }
        mKChart.updateRenderPortLayout();
    }

    boolean showLeft = false;

    public void changeLeft(View view) {
        if (showLeft) {
            showLeft = false;
            mKChart.getMainRenderer().removeRenderCanvas(MainRenderer.POSITION_LEFT);
        } else {
            showLeft = true;
            MainRenderer<KlineInfo> viewRender = mKChart.getMainRenderer();
            MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(100, 0);
            MainRendererCanvas<KlineInfo> mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 2);
            PositionDrawing positionDrawing = new PositionDrawing("左", Color.parseColor("#6200EA"), Color.parseColor("#FFFF8D"), layoutParams);
            mainRenderCanvas.addDrawing(positionDrawing);
            viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_LEFT);
        }
        mKChart.updateRenderPortLayout();
    }

    boolean showRight = false;

    public void changeRight(View view) {
        if (showRight) {
            showRight = false;
            mKChart.getMainRenderer().removeRenderCanvas(MainRenderer.POSITION_RIGHT);
        } else {
            showRight = true;
            MainRenderer<KlineInfo> viewRender = mKChart.getMainRenderer();
            MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(100, 0);
            MainRendererCanvas<KlineInfo> mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 2);
            PositionDrawing positionDrawing = new PositionDrawing("右", Color.parseColor("#6200EA"), Color.parseColor("#B388FF"), layoutParams);
            mainRenderCanvas.addDrawing(positionDrawing);
            viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_RIGHT);
        }
        mKChart.updateRenderPortLayout();
    }

    boolean showTop = false;

    public void changeTop(View view) {
        if (showTop) {
            showTop = false;
            mKChart.getMainRenderer().removeRenderCanvas(MainRenderer.POSITION_TOP);
        } else {
            showTop = true;
            MainRenderer<KlineInfo> viewRender = mKChart.getMainRenderer();
            MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 100);
            MainRendererCanvas<KlineInfo> mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);

            RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
            layoutParams.setWeight(1);
            layoutParams.setVerticalLinear(true, 2);
            PositionDrawing positionDrawing = new PositionDrawing("上", Color.parseColor("#6200EA"), Color.parseColor("#FF8A80"), layoutParams);
            mainRenderCanvas.addDrawing(positionDrawing);
            viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_TOP);
        }
        mKChart.updateRenderPortLayout();
    }

    boolean leftTop = false;

    public void lefttop(View view) {
        leftTop = !leftTop;
        mKChart.getMainRenderer().setCornerRule(leftTop, righttop, rightbottom, leftbottom);
        mKChart.updateRenderPortLayout();
    }

    boolean righttop = false;

    public void righttop(View view) {
        righttop = !righttop;
        mKChart.getMainRenderer().setCornerRule(leftTop, righttop, rightbottom, leftbottom);
        mKChart.updateRenderPortLayout();
    }

    boolean rightbottom = false;

    public void rightbottom(View view) {
        rightbottom = !rightbottom;
        mKChart.getMainRenderer().setCornerRule(leftTop, righttop, rightbottom, leftbottom);
        mKChart.updateRenderPortLayout();
    }

    boolean leftbottom = false;

    public void leftbottom(View view) {
        leftbottom = !leftbottom;
        mKChart.getMainRenderer().setCornerRule(leftTop, righttop, rightbottom, leftbottom);
        mKChart.updateRenderPortLayout();
    }
}