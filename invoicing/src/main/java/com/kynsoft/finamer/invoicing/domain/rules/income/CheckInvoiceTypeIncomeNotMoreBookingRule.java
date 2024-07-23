package com.kynsoft.finamer.invoicing.domain.rules.income;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

public class CheckInvoiceTypeIncomeNotMoreBookingRule extends BusinessRule {

    private final EInvoiceType invoiceType;

    public CheckInvoiceTypeIncomeNotMoreBookingRule(EInvoiceType invoiceType) {
        super(DomainErrorMessage.CHECK_INVOICE_OF_TYPE_INCOME, new ErrorField("invoiceType", DomainErrorMessage.CHECK_INVOICE_OF_TYPE_INCOME.getReasonPhrase()));
        this.invoiceType = invoiceType;
    }

    @Override
    public boolean isBroken() {
        return this.invoiceType.equals(EInvoiceType.INCOME);
    }

}
