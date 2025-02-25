package com.kynsof.share.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankerRounding {
    public static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static double round(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
