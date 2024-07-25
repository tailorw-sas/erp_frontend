package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class ImportBookingAmountPaxValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (obj.getAmountPAX()==0){
            errorFieldList.add(new ErrorField("Amount PAX", "Amount PAX must be not 0"));
            return false;
        }
        else if (obj.getAdults() + obj.getChildren() != obj.getAmountPAX()){
            errorFieldList.add(new ErrorField("Amount PAX","Amount PAX is different from amount of the people in the reservation"));
            return false;
        }
        return true;
    }


}
