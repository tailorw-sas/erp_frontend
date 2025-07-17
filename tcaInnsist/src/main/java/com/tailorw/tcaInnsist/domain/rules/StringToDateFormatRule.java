package com.tailorw.tcaInnsist.domain.rules;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringToDateFormatRule extends BusinessRule {

    private final String dateToValidate;
    private final DateTimeFormatter formatter;

    public StringToDateFormatRule(String dateToValidate, DateTimeFormatter formatter){
        super(DomainErrorMessage.TCA_INNSIST_INVOICE_DATE_FORMAT_IS_NOT_VALID, new ErrorField("Invoice Date", DomainErrorMessage.TCA_INNSIST_INVOICE_DATE_FORMAT_IS_NOT_VALID.getReasonPhrase()));
        this.dateToValidate = dateToValidate;
        this.formatter = formatter;
    }

    @Override
    public boolean isBroken() {
        try {
            LocalDate.parse(dateToValidate, formatter);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }
}
