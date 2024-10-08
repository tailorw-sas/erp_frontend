package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReconcileAutomaticPriceValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {

    private final IManageBookingService manageBookingService;

    public ReconcileAutomaticPriceValidator(IManageBookingService manageBookingService) {
        this.manageBookingService = manageBookingService;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        if (validateIfColumANIsNotEmpty(obj,errorFieldList)){
            return validateIfPriceIsEqualToBookingAmount(obj,errorFieldList);
        }
        return false;
    }

    private boolean validateIfColumANIsNotEmpty(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList){
        if (Objects.isNull(obj.getColumnAN())){
            errorFieldList.add(new ErrorField("Column AN","The column AN can't be empty"));
            return false;
        }
        return true;
    }

    private boolean validateIfPriceIsEqualToBookingAmount(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList){
        Optional<ManageBookingDto> bookingDtoOptional= manageBookingService.findManageBookingByReservationNumber(obj.getColumnW());
         String[] priceString=obj.getColumnAN().split(":");
         double price= Double.parseDouble(priceString[1]);
        if (price != bookingDtoOptional.get().getDueAmount()){
            errorFieldList.add(new ErrorField("Booking amount","The price is not equals to  booking amount"));
            return false;
        }
        return true;
    }
}
