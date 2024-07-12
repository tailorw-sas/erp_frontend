package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingAdultsValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingAdultsValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }
    @Override
    public boolean validate(BookingRow obj) {
            if (obj.getAdults()==0){
                sendErrorEvent(obj.getRowNumber(),new ErrorField("Adults","Adults must be greater than 0"),obj);
                return false;
            }
          return true;
    }


}
