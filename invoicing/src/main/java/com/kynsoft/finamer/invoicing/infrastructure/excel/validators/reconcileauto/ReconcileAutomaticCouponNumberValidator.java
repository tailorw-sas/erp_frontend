package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReconcileAutomaticCouponNumberValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {
    private final IManageBookingService manageBookingService;

    public ReconcileAutomaticCouponNumberValidator(IManageBookingService manageBookingService) {
        this.manageBookingService = manageBookingService;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        if (validateIfColumnEIsNotEmpty(obj,errorFieldList)){
           return validateCouponNumber(obj,errorFieldList);
        }
        return false;
    }

    private boolean validateIfColumnEIsNotEmpty(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList){
        if (Objects.isNull(obj.getColumnE())){
            errorFieldList.add(new ErrorField("Column E","The column E can't be empty"));
            return false;
        }

        if (Objects.isNull(obj.getColumnL())){
            errorFieldList.add(new ErrorField("Column L","The column L can't be empty"));
            return false;
        }
        return true;
    }

    private boolean validateCouponNumber(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList){
        Optional<ManageBookingDto> bookingDto = manageBookingService.findManageBookingByReservationNumber(obj.getColumnW());
        if (!obj.getColumnE().concat(obj.getColumnL()).equals(bookingDto.get().getCouponNumber())){
            errorFieldList.add(new ErrorField("Cupon Number","The coupon number is not valid"));
            return false;
        }
        return true;
    }
}
