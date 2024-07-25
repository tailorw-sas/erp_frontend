package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class ImportBookingNameValidator extends ExcelRuleValidator<BookingRow> {
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
            String firstName=Objects.nonNull(obj.getFirstName())?obj.getFirstName():"";
            String lastName=Objects.nonNull(obj.getLastName())?obj.getLastName():"";
        if (firstName.isEmpty() && lastName.isEmpty()) {
            errorFieldList.add( new ErrorField("FirstName and LastName", "FirstName and LastName must be not empty"));
            return false;
        }

        return true;
    }
}
