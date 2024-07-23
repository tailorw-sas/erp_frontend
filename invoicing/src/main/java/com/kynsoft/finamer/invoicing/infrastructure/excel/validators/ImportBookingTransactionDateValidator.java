package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ImportBookingTransactionDateValidator extends ExcelRuleValidator<BookingRow> {

    public ImportBookingTransactionDateValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {

         return  DateUtil.validateDateFormat(obj.getTransactionDate());

        //TODO
        //Validar close operation
        //Que es el close operation y donde esta?
    }
}
