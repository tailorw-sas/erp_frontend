package com.kynsof.share.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankerRounding {

    private BankerRounding() {
    }

    public static double round(double value) {
        return round(value, 2);
    }

    public static double round(double value, int decimalPlaces) {
        return new BigDecimal(Double.toString(value))
                .setScale(decimalPlaces, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    public static BigDecimal round(BigDecimal value) {
        return round(value, 2);
    }

    public static BigDecimal round(BigDecimal value, int decimalPlaces) {
        if (value == null) return BigDecimal.ZERO; // Evita errores con valores nulos
        return value.setScale(decimalPlaces, RoundingMode.HALF_EVEN);
    }
}