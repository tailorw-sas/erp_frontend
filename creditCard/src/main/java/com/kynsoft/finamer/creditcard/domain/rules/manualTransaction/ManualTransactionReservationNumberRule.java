package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManualTransactionReservationNumberRule extends BusinessRule {

    private final String reservationNumber;

    public ManualTransactionReservationNumberRule(String reservationNumber) {
        super(
                DomainErrorMessage.RESERVATION_NUMBER,
                new ErrorField("reservationNumber", DomainErrorMessage.RESERVATION_NUMBER.getReasonPhrase())
        );
        this.reservationNumber = reservationNumber;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(reservationNumber);
    }

    private boolean validateCode(String reservationNumber) {
        Pattern pattern = Pattern.compile("^(I|G)\\s\\d{3}\\s\\d{2}$");
        Matcher matcher = pattern.matcher(reservationNumber);

        return matcher.matches();
    }
}
