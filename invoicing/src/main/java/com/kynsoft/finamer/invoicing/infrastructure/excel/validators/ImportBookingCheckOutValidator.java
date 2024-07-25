package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ImportBookingCheckOutValidator extends ExcelRuleValidator<BookingRow> {

    private final String[] validDateFormat = new String[]{"yyyymmdd", "mm/dd/yyyy"};

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
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
            errorFieldList.add(new ErrorField("CheckOut", "CheckOut has invalid date format"));
            return false;
        }
        return true;
    }
}
