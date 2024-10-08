package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import java.util.List;
import java.util.Objects;

public class ReconcileValidatorReservationNumberValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {

    private final IManageBookingService manageBookingService;

    public ReconcileValidatorReservationNumberValidator(IManageBookingService manageBookingService) {
        this.manageBookingService = manageBookingService;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {

        if (validateColumnWIsNotEmpty(obj,errorFieldList)){
            return validateIfBookingExistByReservationNumber(obj,errorFieldList);
        }
        return false;
    }
    private boolean validateColumnWIsNotEmpty(InvoiceReconcileAutomaticRow obj,List<ErrorField> errorFieldList){
            if (Objects.isNull(obj.getColumnW()) || obj.getColumnW().isEmpty()){
                errorFieldList.add(new ErrorField("Column W","The column W can't be empty"));
                return false;
            }
            return true;
    }

    private boolean validateIfBookingExistByReservationNumber(InvoiceReconcileAutomaticRow obj,List<ErrorField> errorFieldList){
        if (manageBookingService.findManageBookingByReservationNumber(obj.getColumnW()).isEmpty()){
            errorFieldList.add(new ErrorField("Reservation Number","There is not booking with the reservation Number"));
            return false;
        }
        return true;
    }
}
