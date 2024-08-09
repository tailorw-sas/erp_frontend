package com.kynsoft.finamer.invoicing.application.excel;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.error.ImportBookingRowErrorEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ValidatorFactory<T> {

    private final ApplicationEventPublisher applicationEventPublisher;
    protected List<ErrorField> errorFieldList;
    protected Map<String, ExcelRuleValidator<BookingRow>> validators;

    protected ValidatorFactory(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.errorFieldList = new ArrayList<>();
        this.validators =  new LinkedHashMap<>();
    }

    abstract public void createValidators(String importType);

    abstract public boolean validate(T toValidate);

    protected void sendErrorEvent(BookingRow bookingRow){
        if (!errorFieldList.isEmpty()) {
            BookingRowError rowError = new BookingRowError(null, bookingRow.getRowNumber(), bookingRow.getImportProcessId(), errorFieldList, bookingRow);
            ImportBookingRowErrorEvent excelRowErrorEvent = new ImportBookingRowErrorEvent(rowError);
            applicationEventPublisher.publishEvent(excelRowErrorEvent);
        }
    }

    protected void clearErrors(){
        this.errorFieldList.clear();
    }
    public  void removeValidators(){
        if (!validators.isEmpty()){
            validators.clear();
        }
    }
}
