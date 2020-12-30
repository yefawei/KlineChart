package com.example.kchartdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotokchart1(View view) {
        TestActivity.startKchart(this);
    }

    public void gotokchart2(View view) {
        Test2Activity.startKchart2(this);
    }
}