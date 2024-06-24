package com.bin.webmonitor.common.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

    private static final int ONE_HUNDRED = 100;

    private static final Integer DEFAULT_SCALE = 2;

    /**
     * percent 2 scale double.
     */
    public static double percent(int dividedInt, int divisorInt) {
        BigDecimal divide = divided(dividedInt, divisorInt);
        return divide.multiply(new BigDecimal(ONE_HUNDRED)).doubleValue();
    }

    /**
     * percent int value.
     */
    public static int percentInt(int dividedInt, int divisorInt) {
        BigDecimal divide = divided(dividedInt, divisorInt);
        return divide.multiply(new BigDecimal(ONE_HUNDRED)).intValue();
    }

    /**
     * dividedInt / divisorInt.
     */
    public static BigDecimal divided(int dividedInt, int divisorInt) {
        return dividedByScale(dividedInt, divisorInt, DEFAULT_SCALE);
    }

    /**
     * set scale divide.
     */
    public static BigDecimal dividedByScale(int dividedInt, int divisorInt, Integer scale) {
        if (null == scale) {
            return divided(dividedInt, divisorInt);
        }
        BigDecimal divided = new BigDecimal(dividedInt);
        BigDecimal divisor = new BigDecimal(divisorInt);
        return divided.divide(divisor, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将金额单位(元)转换为(分).
     */
    public static int multiplyHundred(BigDecimal yuan) {
        BigDecimal multiplicand = new BigDecimal(ONE_HUNDRED);
        return yuan.multiply(multiplicand).intValue();
    }

    /**
     * double 转换精度.
     */
    public static Double setScaleTwo(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal bigDecimalRet = bigDecimal.setScale(DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        return Double.parseDouble(bigDecimalRet.toString());
    }

    /**
     * BigDecimal 加一.
     */
    public static BigDecimal plusOne(BigDecimal value) {
        if (null == value || value.compareTo(BigDecimal.ONE) < 0) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ONE.add(value).setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
