package com.kynsof.share.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ScaleAmount {

    public static Double scaleAmount(Double amount) {
        BigDecimal bd = new BigDecimal(Double.toString(amount));
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static String DecimalFormat(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        String numeroFormateado = df.format(value);

        return numeroFormateado;
    }
}
