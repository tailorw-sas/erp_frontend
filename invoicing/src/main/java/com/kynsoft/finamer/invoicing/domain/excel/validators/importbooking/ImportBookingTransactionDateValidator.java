package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ImportBookingTransactionDateValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingTransactionDateValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {
        String date = obj.getTransactionDate();
        String validDateFormat = "yyyymmdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(validDateFormat);
        try {
            simpleDateFormat.parse(date);
        } catch (ParseException e) {
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Transaction Date", "Invalid date format"),obj);
            return false;
        }
        return true;
        //TODO
        //Validar close operation
        //Que es el close operation y donde esta?
    }
}
