package com.example.kchartdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benben.kchartlib.InteractiveKChartView;
import com.benben.kchartlib.canvas.MainRendererCanvas;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.index.range.VolumeIndexRange;
import com.benben.kchartlib.render.MainRenderer;
import com.example.kchartdemo.data.Data;
import com.example.kchartdemo.data.KlineInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private InteractiveKChartView mKChart;
    private Data mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKChart = findViewById(R.id.k_chart);
        init();
    }

    private void init() {
        mData = new Data();

        mKChart.setPointWidth((int) (getResources().getDisplayMetrics().density * 8 + 0.5f));
        mKChart.setPadding(60, 60, 60, 60);
        MainRenderer viewRender = mKChart.getViewRender();

        MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 3);
        MainRendererCanvas mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        mainRenderCanvas.addDrawing(new BGDrawing());

        RendererCanvas.DrawingLayoutParams params = new RendererCanvas.DrawingLayoutParams();
        params.setVerticalPercent(0.3f);
        params.setVerticalPosition(RendererCanvas.DrawingLayoutParams.POSITION_BOTTOM);
        VolumeIndexRange valume = new VolumeIndexRange();
        mainRenderCanvas.addDrawing(new VolumeDrawing(valume, params), true);

        params = new RendererCanvas.DrawingLayoutParams();
        mainRenderCanvas.addDrawing(new CandleDrawing(params), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_MAIN);

        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1);
        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        params = new RendererCanvas.DrawingLayoutParams();
        params.setWeight(1);
        mainRenderCanvas.addDrawing(new VolumeDrawing(valume, params), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_TOP);

        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1);
        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        params = new RendererCanvas.DrawingLayoutParams();
        params.setWeight(1);
        mainRenderCanvas.addDrawing(new VolumeDrawing(valume, params), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_BOTTOM);

//        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1);
//        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
//        mainRenderCanvas.addDrawing(new BGDrawing());
//        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_LEFT);
//
//        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, 0, 1);
//        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
//        mainRenderCanvas.addDrawing(new BGDrawing());
//        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_RIGHT);
    }


    public void updateLayout(View view) {
        mKChart.updateRenderPortLayout();
        showToast("更新布局");
    }

    public void toViewPort(View view) {
        Log.e("ScrollAndScaleView", mKChart.getViewRender().toViewPortString());
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void add100(View view) {
        ArrayList<KlineInfo> klineInfos = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            klineInfos.add(mData.mData.get(i));
        }
        KChartAdapter adapter = new KChartAdapter();
        adapter.setData(klineInfos);
        mKChart.setAdapter(adapter);
    }

    public void scroll0(View view) {
        mKChart.scrollToIndex(0, 0.25f, true);
    }

    public void scroll10(View view) {
        mKChart.scrollToIndex(50, 0.5f, false);
    }

    public void scroll90(View view) {
        mKChart.scrollToIndex(99, 0.25f, true);
    }
}