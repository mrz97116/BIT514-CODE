package com.dongxin.scm.utils;

import java.math.BigDecimal;

/**
 * @author ：melon
 * @date ：Created in 2021/3/6 11:22
 */
public class ScmNumberUtils {

    public static double setScale(double d) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String delRedundantZero(Double d) {
        BigDecimal bd = BigDecimal.valueOf(ifNullZero(d));
        return bd.stripTrailingZeros().toPlainString();
    }

    public static double ifNullZero(Double d) {
        if (d == null) {
            return 0d;
        } else {
            return d;
        }
    }


    public static BigDecimal ifNullZero(BigDecimal d) {
        if (d == null) {
            return BigDecimal.ZERO;
        } else {
            return d;
        }
    }


}
