package com.kynsof.share.core.application.excel.validator;

import com.kynsof.share.core.domain.response.ErrorField;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IValidatorFactory<T> {
    protected final ApplicationEventPublisher applicationEventPublisher;
    protected List<ErrorField> errorFieldList;
    protected Map<String, ExcelRuleValidator<T>> validators;

    protected IValidatorFactory(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.errorFieldList = new ArrayList<>();

    }

     public abstract void createValidators();

    public abstract boolean validate(T toValidate);

    protected void clearErrors(){
        this.errorFieldList.clear();
    }

    protected void sendErrorEvent(Object event){
        if (!errorFieldList.isEmpty()) {
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
}
