package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class ImportBookingAdultsValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if(Objects.isNull(obj.getAdults())){
            errorFieldList.add(new ErrorField("Adults", "Adults can't be empty"));
            return false;
        }
        if (obj.getAdults() <= 0) {
            errorFieldList.add(new ErrorField("Adults", "Adults must be greater than 0"));
            return false;
        }
        return true;
    }


}
