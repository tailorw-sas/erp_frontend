package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingInvoiceAmountValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingInvoiceAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (obj.getInvoiceAmount() == 0) {
            sendErrorEvent(obj.getRowNumber(), new ErrorField("Invoice Amount", "Invoice Amount must be greater than 0"),obj);
            return false;
        }
        return true;
    }


}
