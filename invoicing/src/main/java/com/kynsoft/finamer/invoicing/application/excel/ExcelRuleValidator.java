package com.kynsoft.finamer.invoicing.application.excel;

import com.kynsof.share.core.domain.response.ErrorField;

import java.util.List;

public abstract class ExcelRuleValidator<T> {
    public abstract boolean validate(T obj,List<ErrorField> errorFieldList);

    public boolean validate(T obj,List<ErrorField> errorFieldList,String importType){
        return true;
    }
}

