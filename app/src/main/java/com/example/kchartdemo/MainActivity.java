package com.example.kchartdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benben.kchartlib.InteractiveKChartView;
import com.benben.kchartlib.ScrollAndScaleView;
import com.benben.kchartlib.adapter.DefaultKChartAdapter;
import com.benben.kchartlib.canvas.MainRendererCanvas;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.data.AnimDataSizeChangeHandler;
import com.benben.kchartlib.index.range.VolumeIndexRange;
import com.benben.kchartlib.render.MainRenderer;
import com.benben.kchartlib.utils.ConvertUtils;
import com.example.kchartdemo.Drawing.CandleDrawing;
import com.example.kchartdemo.Drawing.ClickDrawing;
import com.example.kchartdemo.Drawing.LeftPaddingDrawing;
import com.example.kchartdemo.Drawing.RightPaddingDrawing;
import com.example.kchartdemo.Drawing.VolumeDrawing;
import com.example.kchartdemo.data.DragonKLineDataProvider;
import com.example.kchartdemo.data.KlineInfo;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DragonKLineDataProvider mDataProvider;
    private InteractiveKChartView mKChart;
    private DefaultKChartAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKChart = findViewById(R.id.k_chart);
        mDataProvider = new DragonKLineDataProvider();

        mKChart.setPointWidth(ConvertUtils.dp2px(this, 8));
        mKChart.setDataSizeChangeHandler(new AnimDataSizeChangeHandler());
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

        MainRenderer viewRender = mKChart.getMainRenderer();

        MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, ConvertUtils.dp2px(this, 340), 1);
        MainRendererCanvas mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        mainRenderCanvas.addDrawing(new CandleDrawing(layoutParams), true);

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
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_MAIN);

        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, ConvertUtils.dp2px(this, 90), 1);
        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        mainRenderCanvas.addDrawing(new VolumeDrawing(new VolumeIndexRange(), layoutParams));
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_BOTTOM);

        mAdapter = new DefaultKChartAdapter();
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
        mKChart.scrollToIndex(0, 0, true);
    }

    public void toCenter(View view) {
        mKChart.scrollToIndex(mAdapter.getCount() / 2, 0.5f, true);
    }

    public void toRight(View view) {
        mKChart.scrollToIndex(mAdapter.getCount() - 1, 1f, true);
    }

    ClickDrawing mOneDrawing = new ClickDrawing("One", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mOneDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    ClickDrawing mTwoDrawing = new ClickDrawing("Two", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mTwoDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    ClickDrawing mThreeDrawing = new ClickDrawing("Three", new ClickDrawing.OnClickListener() {
        @Override
        public void onClick() {
            MainRenderer mainRenderer = mKChart.getMainRenderer();
            mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN).removeDrawing(mThreeDrawing);
            mKChart.updateRenderPortLayout();
        }
    });

    public void addClickTag(View view) {
        MainRenderer mainRenderer = mKChart.getMainRenderer();
        MainRendererCanvas renderCanvas = mainRenderer.getRenderCanvas(MainRenderer.POSITION_MAIN);
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