package com.example.kchartdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kchartdemo.Drawing.CandleDrawing;
import com.example.kchartdemo.Drawing.ClickDrawing;
import com.example.kchartdemo.Drawing.HighlightDrawing;
import com.example.kchartdemo.Drawing.LeftPaddingDrawing;
import com.example.kchartdemo.Drawing.ParalleCandleDrawing;
import com.example.kchartdemo.Drawing.RightPaddingDrawing;
import com.example.kchartdemo.Drawing.TransitionCandleDrawing;
import com.example.kchartdemo.Drawing.TransitionValumeDrawing;
import com.example.kchartdemo.Drawing.VolumeDrawing;
import com.example.kchartdemo.Drawing.VolumeHighlightDrawing;
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
import com.yfw.kchartcore.index.range.TransitionIndexRange;
import com.yfw.kchartcore.render.MainRenderer;
import com.yfw.kchartcore.touch.TouchTapManager;
import com.yfw.kchartext.adapter.DefaultKChartAdapter;
import com.yfw.kchartext.index.range.VolumeIndexRange;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DragonKLineDataProvider mDataProvider;
    private InteractiveKChartView<KlineInfo> mKChart;
    private ScrollView mScrollView;
    private DefaultKChartAdapter<KlineInfo> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_LONG).show();
            }
        });

        mKChart.setOnDoubleClickListener(new ScrollAndScaleView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                Toast.makeText(MainActivity.this, "onDoubleClick", Toast.LENGTH_LONG).show();
            }
        });
        mKChart.setOnPressChangeListener(new ScrollAndScaleView.OnPressChangeListener() {
            @Override
            public void onPressChange(View v, boolean onLongpress) {
                Toast.makeText(MainActivity.this, "onPressChange: " + onLongpress, Toast.LENGTH_LONG).show();
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
        CandleIndexRange candleIndexRange = new CandleIndexRange();
        VolumeIndexRange volumeIndexRange = new VolumeIndexRange();


        MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1, 3);
        MainRendererCanvas<KlineInfo> mainRenderCanvas = new MainRendererCanvas<>(canvasLayoutParams);
        RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new CandleDrawing(candleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new TransitionCandleDrawing(new TransitionIndexRange(candleIndexRange), layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        ParalleCandleDrawing paralleCandleDrawing = new ParalleCandleDrawing(new TransitionIndexRange(candleIndexRange), layoutParams);
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
        mainRenderCanvas.addDrawing(new HighlightDrawing(candleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new HighlightDrawing(candleIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new HighlightDrawing(candleIndexRange, layoutParams), true);
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
        mainRenderCanvas.addDrawing(new VolumeDrawing(volumeIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 1);
        mainRenderCanvas.addDrawing(new TransitionValumeDrawing(new TransitionIndexRange(volumeIndexRange), layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new VolumeHighlightDrawing(volumeIndexRange, layoutParams), true);

        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        layoutParams.setVerticalLinear(true, 2);
        mainRenderCanvas.addDrawing(new VolumeHighlightDrawing(volumeIndexRange, layoutParams), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_BOTTOM);

        mAdapter = new DefaultKChartAdapter<>(new DefaultKChartAdapter.OnPrepareIndexDataListener() {
            @Override
            public void prepareIndexData(List<String> indexTags) {
                Log.e("OnPrepareIndexData", indexTags.toString());
            }
        });
        mKChart.setAdapter(mAdapter);
    }

    int startIndex = 100;
    int endIndex = 100;

    public void pre10(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(mDataProvider.mData.get(startIndex - 10 + i));
        }
        mAdapter.addDatas(list);
        showToast("pre add: " + (startIndex - 10) + "-" + (startIndex + 9));
        startIndex -= 10;
    }

    public void centerAdd(View view) {
        KlineInfo klineInfo = mDataProvider.mData.get(endIndex);
        showToast("add index: " + endIndex);
        mAdapter.addData(klineInfo);
        endIndex++;
    }

    public void add10(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(mDataProvider.mData.get(endIndex + i));
        }
        mAdapter.addDatas(list);
        showToast("add10: " + (endIndex) + "-" + (endIndex + 9));
        endIndex += 10;
    }

    public void add100(View view) {
        ArrayList<KlineInfo> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(mDataProvider.mData.get(endIndex + i));
        }
        mAdapter.addDatas(list);
        showToast("add100: " + (endIndex) + "-" + (endIndex + 99));
        endIndex += 100;
    }

    public void line(View view) {
        KlineInfo item = (KlineInfo) mAdapter.getItem(mAdapter.getCount() - 1);
        double ceil = Math.ceil(Math.log10(item.close_price));
        if (System.currentTimeMillis() % 2 == 0) {
            item.close_price =item.close_price + (float) (Math.random() * ceil) * 0.8f;
        } else {
            item.close_price =item.close_price - (float) (Math.random() * ceil) * 0.8f;
        }
        if (item.close_price < item.min_price) {
            item.min_price = item.close_price;
        }
        if (item.close_price > item.max_price) {
            item.max_price = item.close_price;
        }
        mAdapter.notifyLastUpdated(mAdapter.getCount() - 1);
    }

    public void candle(View view) {

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
            MainRenderer<KlineInfo>mainRenderer = mKChart.getMainRenderer();
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
}