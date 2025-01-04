package com.kynsof.share.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankerRounding {
    public static double round(double value, int decimalPlaces) {
        // Convertir el valor a BigDecimal
        BigDecimal bd = new BigDecimal(Double.toString(value));
        // Aplicar el redondeo del banquero con la cantidad de decimales
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        // Retornar el valor redondeado como double
        return bd.doubleValue();
    }
}
