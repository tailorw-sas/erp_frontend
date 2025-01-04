package com.kynsoft.finamer.invoicing.infrastructure.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AgencyCouponFormatUtils {

    public static boolean agencyCouponFormatIsValid(String couponFormat) {
        // Comprobar si el regex es válido
        try{
            Pattern.compile(couponFormat);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public static boolean validateCode(String hotelBookingNumber, String bookingCouponFormat) {
        try {
            // Primera parte: Verificar que empiece con "I" o "G" seguido de un espacio
            String firstPart = hotelBookingNumber.substring(0, 2);
            Pattern pattern = Pattern.compile("^([IG])\\s$");
            Matcher matcher = pattern.matcher(firstPart);

            if (!matcher.matches()) {
                return false;
            }

            // Si bookingCouponFormat es vacío o null permitir cualquier formato de números
            if (bookingCouponFormat == null || bookingCouponFormat.isEmpty()) {
                return true;
            }

            // Segunda parte: Validar el número según la expresión regular proporcionada
            String numberPart = hotelBookingNumber.substring(2); // Ignora el primer caracter y el espacio
            Pattern numberPattern = Pattern.compile(bookingCouponFormat);
            Matcher numberMatcher = numberPattern.matcher(numberPart);

            return numberMatcher.matches();
        } catch (Exception e) {
            return false;
        }
    }
}
