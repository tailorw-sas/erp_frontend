package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingNameValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingNameValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {

        if (obj.getFirstName().isEmpty() && obj.getLastName().isEmpty()) {
            sendErrorEvent(obj.getRowNumber(), new ErrorField("FirstName and LastName", "FirstName and LastName must be not empty"), obj);
            return false;
        }

        return true;
    }
}
