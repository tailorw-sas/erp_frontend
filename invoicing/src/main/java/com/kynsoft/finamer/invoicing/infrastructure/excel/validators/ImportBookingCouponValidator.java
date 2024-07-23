package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingCouponValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingCouponValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }
    @Override
    public boolean validate(BookingRow obj) {
       //Manage agency
        return true;
    }


}
