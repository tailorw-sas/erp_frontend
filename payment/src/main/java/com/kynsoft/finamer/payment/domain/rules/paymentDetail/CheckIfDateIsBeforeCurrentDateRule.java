package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;

public class CheckIfDateIsBeforeCurrentDateRule extends BusinessRule {

    private final LocalDate currentDate;
    private final LocalDate dateToValidate;

    public CheckIfDateIsBeforeCurrentDateRule(LocalDate dateToValidate) {
        super(DomainErrorMessage.CHECK_DATE_IS_BEFORE_CURRENT_DATE, new ErrorField("transactionDate", DomainErrorMessage.CHECK_DATE_IS_BEFORE_CURRENT_DATE.getReasonPhrase()));
        this.currentDate = LocalDate.now();
        this.dateToValidate = dateToValidate;
    }

    /**
     * Bank Deposit Date: Fecha de hoy debe visualizarse por defecto, puede ser anterior, no debe darse la
     * posibilidad de seleccionar fechas futuras.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return dateToValidate.isAfter(currentDate);
    }

}
