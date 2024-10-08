package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class ImportBookingCouponValidator extends ExcelRuleValidator<BookingRow> {
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
       //Manage agency
        return true;
    }


}
