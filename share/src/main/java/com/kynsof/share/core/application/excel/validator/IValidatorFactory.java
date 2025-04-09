package com.kynsof.share.core.application.excel.validator;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;
import java.util.stream.Collectors;

public abstract class IValidatorFactory<T> {
    protected final ApplicationEventPublisher applicationEventPublisher;
    protected List<ErrorField> errorFieldList;
    protected List<RowErrorField> rowErrorFieldList;
    protected Map<String, ExcelRuleValidator<T>> validators;

    protected IValidatorFactory(ApplicationEventPublisher applicationEventPublisher,
                                ICache cache) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.errorFieldList = new ArrayList<>();
        this.rowErrorFieldList = new ArrayList<>();
    }

    protected IValidatorFactory(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.errorFieldList = new ArrayList<>();
        this.rowErrorFieldList = new ArrayList<>();
    }

    public abstract void createValidators();

    public void createValidators(ICache cache){
        createValidators();
    }

    public abstract boolean validate(T toValidate);

    public boolean validate(List<T> toValidateList) {
        for (T entity : toValidateList) {
            if (!validate(entity)) {
                return false;
            }
        }
        return true;
    }


    protected void clearErrors(){
        this.errorFieldList.clear();
    }

    protected void clearRowErrors(){
        this.rowErrorFieldList.clear();
    }

    protected void sendErrorEvent(Object event){
        if (!errorFieldList.isEmpty() || !rowErrorFieldList.isEmpty()) {
            applicationEventPublisher.publishEvent(event);
        }
    }


    protected boolean hasErrors(){
        return !errorFieldList.isEmpty();
    }
    public  void removeValidators(){
        if (!validators.isEmpty()){
            validators.clear();
        }
    }

    protected boolean hasListErrors(){
        return !rowErrorFieldList.isEmpty();
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
