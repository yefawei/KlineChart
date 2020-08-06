package com.example.kchartdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benben.kchartlib.InteractiveKChartView;
import com.benben.kchartlib.canvas.RendererCanvas;
import com.benben.kchartlib.impl.ICanvasPortLayout;
import com.benben.kchartlib.impl.IDrawingPortLayout;
import com.benben.kchartlib.render.ViewRenderer;
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

        mKChart.setPointWidth((int)(getResources().getDisplayMetrics().density * 24 + 0.5f));
        ICanvasPortLayout.CanvasLayoutParams canvasLayoutParams = new ICanvasPortLayout.CanvasLayoutParams(0, 0, 1);
        RendererCanvas mainRenderCanvas = new RendererCanvas(canvasLayoutParams);
        mKChart.setPadding(60,60,60,60);
        IDrawingPortLayout.DrawingLayoutParams params = new IDrawingPortLayout.DrawingLayoutParams();
        params.setWeight(1);
        params.setVerticalPosition(IDrawingPortLayout.DrawingLayoutParams.POSITION_BOTTOM);
        mainRenderCanvas.addDrawing(new CandleDrawing(params));
        mKChart.getViewRender().addRenderCanvas(mainRenderCanvas, ViewRenderer.POSITION_MAIN);
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
}