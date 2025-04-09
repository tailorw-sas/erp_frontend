package com.kynsof.share.core.application.excel.validator;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

public abstract class ExcelListRuleValidator<T> {

    protected final ApplicationEventPublisher applicationEventPublisher;

    protected ExcelListRuleValidator(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public abstract void validate(List<T> obj, List<RowErrorField> errorRowList);

    protected void sendErrorEvent( Object rowError){
        applicationEventPublisher.publishEvent(rowError);
    }

    public void addErrorsToRowList(List<RowErrorField> rowErrorFieldList, List<Integer> rowNumberList, ErrorField errorField) {
        rowNumberList.forEach(row -> {
            List<ErrorField> errors = new ArrayList<>();
            errors.add(errorField);
            addErrorFieldToRowErrorField(rowErrorFieldList, errors, row);
        });
    }

    protected void addErrorFieldToRowErrorField(List<RowErrorField> rowErrorFieldList, List<ErrorField> errorFieldList, Integer rowNumber){
        RowErrorField rowErrorField = rowErrorFieldList.stream()
                .filter(item -> item.getRowNumber().equals(rowNumber))
                .findFirst()
                .orElse(null);

        if(Objects.nonNull(rowErrorField)){
            if(Objects.isNull(rowErrorField.getErrorFieldList())){
                rowErrorField.setErrorFieldList(new ArrayList<>());
            }

            rowErrorField.getErrorFieldList().addAll(errorFieldList);
        }else{
            RowErrorField newRow = new RowErrorField(rowNumber, errorFieldList);
            rowErrorFieldList.add(newRow);
        }
    }
}

