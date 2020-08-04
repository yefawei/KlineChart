package com.benben.kchartlib.impl;

/**
 * @日期 : 2020/7/7
 * @描述 :
 */
public interface IMainCanvasPort {

    int getMainCanvasLeft();

    int getMainCanvasTop();

    int getMainCanvasRight();

    int getMainCanvasBottom();

    int getMainCanvasWidth();

    int getMainCanvasHeight();


    boolean mainCanvasValid();
}
