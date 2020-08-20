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
import com.benben.kchartlib.utils.ConvertUtils;
import com.example.kchartdemo.Drawing.CandleDrawing;
import com.example.kchartdemo.Drawing.VolumeDrawing;
import com.example.kchartdemo.data.DataProvider;
import com.example.kchartdemo.data.KlineInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private InteractiveKChartView mKChart;
    private DataProvider mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKChart = findViewById(R.id.k_chart);
        mData = new DataProvider();

        mKChart.setPointWidth(ConvertUtils.dp2px(this, 8));

        MainRenderer viewRender = mKChart.getMainRenderer();

        MainRenderer.CanvasLayoutParams canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, ConvertUtils.dp2px(this, 340), 1);
        MainRendererCanvas mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);

        RendererCanvas.DrawingLayoutParams layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        mainRenderCanvas.addDrawing(new CandleDrawing(layoutParams), true);
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_MAIN);
        canvasLayoutParams = new MainRenderer.CanvasLayoutParams(0, ConvertUtils.dp2px(this, 90), 1);

        mainRenderCanvas = new MainRendererCanvas(canvasLayoutParams);
        layoutParams = new RendererCanvas.DrawingLayoutParams();
        layoutParams.setWeight(1);
        mainRenderCanvas.addDrawing(new VolumeDrawing(new VolumeIndexRange(), layoutParams));
        viewRender.addRenderCanvas(mainRenderCanvas, MainRenderer.POSITION_BOTTOM);
    }

    public void pre10(View view) {

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

    public void add1(View view) {

    }

    public void line(View view) {

    }

    public void candle(View view) {

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

}