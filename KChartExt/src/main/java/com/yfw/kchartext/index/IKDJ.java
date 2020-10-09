package com.yfw.kchartext.index;

/**
 * @日期 : 2020/7/9
 * @描述 : KDJ 指标接口
 */
public interface IKDJ {

    /**
     * KDJ k值
     */
    float getK();

    /**
     * KDJ d值
     */
    float getD();

    /**
     * KDJ j值
     */
    float getJ();
}
