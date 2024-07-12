package com.kynsoft.finamer.invoicing.domain.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.identity.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingRowErrorEvent;
import org.springframework.context.ApplicationEventPublisher;

public abstract class ExcelRuleValidator<T> {

    protected final ApplicationEventPublisher applicationEventPublisher;

    protected ExcelRuleValidator(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public abstract boolean validate(T obj);

    protected void sendErrorEvent(int rowNumber, ErrorField errorField, BookingRow bookingRow){
        BookingRowError rowError = new BookingRowError(null,rowNumber, bookingRow.getImportProcessId(),errorField,bookingRow);
        ImportBookingRowErrorEvent excelRowErrorEvent = new ImportBookingRowErrorEvent(rowError);
        applicationEventPublisher.publishEvent(excelRowErrorEvent);
    }
}

