package com.dongxin.scm.utils;

import java.math.BigDecimal;

/**
 * @author ：melon
 * @date ：Created in 2021/3/31 19:02
 */
public class BigDecimalUtils {

    /**
     * 默认保留两位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiply(BigDecimal d1, BigDecimal d2) {
        return d1.multiply(d2).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留两位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiply(BigDecimal d1, Double d2) {
        return d1.multiply(BigDecimal.valueOf(d2)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留i位小数
     *
     * @param d1
     * @param d2
     * @param i
     * @return
     */
    public static BigDecimal multiply(BigDecimal d1, Double d2,int i) {
        return d1.multiply(BigDecimal.valueOf(d2)).setScale(i, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留两位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiply(Double d1, BigDecimal d2) {
        return d2.multiply(BigDecimal.valueOf(d1)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留两位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiplyKeep3DecimalPlaces(Double d1, Double d2) {
        return BigDecimal.valueOf(d1).multiply(BigDecimal.valueOf(d2)).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留三位位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiply(Double d1, Double d2) {
        return BigDecimal.valueOf(d1).multiply(BigDecimal.valueOf(d2)).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留两位小数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal multiply(BigDecimal d1, String d2) {
        return d1.multiply(new BigDecimal(d2)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留整数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal divide(BigDecimal d1, Double d2) {
        return d1.divide(BigDecimal.valueOf(d2),2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 默认保留整数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal divide(BigDecimal d1, BigDecimal d2) {
        return d1.divide(d2,2, BigDecimal.ROUND_HALF_UP);
    }


}
