package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ImportBookingCheckOutValidator extends ExcelRuleValidator<BookingRow> {

    private final String[] validDateFormat = new String[]{"yyyymmdd", "mm/dd/yyyy"};

    public ImportBookingCheckOutValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {
        String date = obj.getTransactionDate();
        boolean valid = false;
        for (String format : validDateFormat) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                simpleDateFormat.parse(date);
                valid = true;
                break;
            } catch (ParseException e) {
                valid=false;
            }
        }
        if (!valid) {
            sendErrorEvent(obj.getRowNumber(),new ErrorField("CheckOut", "Invalid date format"),obj);
            return false;
        }
        return true;
    }
}
