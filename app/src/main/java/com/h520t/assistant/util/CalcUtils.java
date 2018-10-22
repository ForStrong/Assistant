package com.h520t.assistant.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 *
 * 作者： 卢卫成 时间： 2017年9月2日 功能描述： 用于对数值的精确计算工具类
 *
 */
public class CalcUtils {

    private static final int TYPE_ADD = 0x00; // 加法
    private static final int TYPE_MULTIPLY = 0x01; // 乘法
    private static final int TYPE_DIVIDE = 0x02; // 除法
    private static final int TYPE_SUBTRACT = 0x03; // 减法

    public static Double add(Double a, Double b) {
        return calc(a, b, -1, TYPE_ADD, null);
    }


    public static Double sub(Double a, Double b) {
        return calc(a, b, -1, TYPE_SUBTRACT, null);
    }


    public static Double multiply(Double a, Double b) {
        return calc(a, b, -1, TYPE_MULTIPLY, null);
    }


    public static Double divide(Double a, Double b) {
        return calc(a, b, -1, TYPE_DIVIDE, null);
    }


    public static Double multiply(Double a, Double b, int scale, RoundingMode mode) {

        return calc(a, b, scale, TYPE_MULTIPLY, mode);
    }

    public static Double divide(Double a, Double b, int scale, RoundingMode mode) {

        return calc(a, b, scale, TYPE_DIVIDE, mode);
    }

    private static Double calc(Double a, Double b, int scale, int type, RoundingMode mode) {
        BigDecimal result = null;

        BigDecimal bgA = new BigDecimal(String.valueOf(a));
        BigDecimal bgB = new BigDecimal(String.valueOf(b));
        switch (type) {
            case TYPE_ADD:
                result = bgA.add(bgB);
                break;
            case TYPE_MULTIPLY:
                result = bgA.multiply(bgB);
                break;
            case TYPE_DIVIDE:
                try {
                    result = bgA.divide(bgB);
                } catch (ArithmeticException e) {// 防止无限循环而报错  采用四舍五入保留3位有效数字
                    result = bgA.divide(bgB,3, RoundingMode.HALF_DOWN);
                }

                break;
            case TYPE_SUBTRACT:
                result = bgA.subtract(bgB);
                break;

        }
        if (mode==null) {
            if(scale!=-1){
                result = Objects.requireNonNull(result).setScale(scale);
            }
        }else{
            if(scale!=-1){
                result = Objects.requireNonNull(result).setScale(scale,mode);
            }
        }
        return Objects.requireNonNull(result).doubleValue();
    }

}

