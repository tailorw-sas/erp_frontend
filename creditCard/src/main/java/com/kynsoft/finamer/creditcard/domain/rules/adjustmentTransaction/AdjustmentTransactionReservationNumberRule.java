package com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdjustmentTransactionReservationNumberRule extends BusinessRule {

    private final String reservationNumber;

    private final String bookingCouponFormat;

    public AdjustmentTransactionReservationNumberRule(String reservationNumber, String bookingCouponFormat) {
        super(
                DomainErrorMessage.RESERVATION_NUMBER,
                new ErrorField("reservationNumber", DomainErrorMessage.RESERVATION_NUMBER.getReasonPhrase())
        );
        this.reservationNumber = reservationNumber;
        this.bookingCouponFormat = bookingCouponFormat;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(reservationNumber, bookingCouponFormat);
    }

    private boolean validateCode(String reservationNumber, String bookingCouponFormat) {
        // Primera parte: Verificar que empiece con "I" o "G" seguido de un espacio
        String firstPart = reservationNumber.substring(0, 2);
        Pattern pattern = Pattern.compile("^([IG])\\s$");
        Matcher matcher = pattern.matcher(firstPart);

        if (!matcher.matches()) {
            return false;
        }

        // Si bookingCouponFormat es vacío o null permitir cualquier formato de números
        if(bookingCouponFormat == null || bookingCouponFormat.isEmpty()){
            return true;
        }

        // Segunda parte: Validar el número según la expresión regular proporcionada
        String numberPart = reservationNumber.substring(2); // Ignora el primer caracter y el espacio
        Pattern numberPattern = Pattern.compile(bookingCouponFormat);
        Matcher numberMatcher = numberPattern.matcher(numberPart);
        return numberMatcher.matches();
    }
}
