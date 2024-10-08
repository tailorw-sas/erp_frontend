package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import java.util.List;

public class ReconcileAutomaticCouponNumberValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {
    private final IManageBookingService manageBookingService;

    public ReconcileAutomaticCouponNumberValidator(IManageBookingService manageBookingService) {
        this.manageBookingService = manageBookingService;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        return false;
    }
}
