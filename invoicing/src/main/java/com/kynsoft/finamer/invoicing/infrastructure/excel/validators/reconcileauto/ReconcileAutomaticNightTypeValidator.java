package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;

import java.util.List;
import java.util.Objects;

public class ReconcileAutomaticNightTypeValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {

    private final IManageNightTypeService nightTypeService;

    public ReconcileAutomaticNightTypeValidator(IManageNightTypeService nightTypeService) {
        this.nightTypeService = nightTypeService;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        if (validateIfNightTypeIsNotEmpty(obj,errorFieldList)){

        }
    }

    private boolean validateIfNightTypeIsNotEmpty(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList){
         if (Objects.isNull(obj.getColumnN())){
             errorFieldList.add(new ErrorField("Column N","The column N can't be empty"));
             return false;
         }
         return true;
    }

    private boolean validateIfNightTypeExist(InvoiceReconcileAutomaticRow obj,List<ErrorField> errorFieldList){
       if(!nightTypeService.existNightTypeByCode(obj.getColumnN())){
           errorFieldList.add(new ErrorField("Column N","The night type not exist"));
           return false;
       }
       return true;
    }

}
