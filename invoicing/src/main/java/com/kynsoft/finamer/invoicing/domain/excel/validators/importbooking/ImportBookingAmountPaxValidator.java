package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingAmountPaxValidator extends ExcelRuleValidator<BookingRow> {


    public ImportBookingAmountPaxValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (obj.getAmountPAX()==0){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Amount PAX", "Amount PAX must be not 0"),obj);
            return false;
        }
        else if (obj.getAdults() + obj.getChildren() != obj.getAmountPAX()){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Amount PAX","Amount PAX is different from amount of the people in the reservation "),obj);
            return false;
        }
        return true;
    }


}
