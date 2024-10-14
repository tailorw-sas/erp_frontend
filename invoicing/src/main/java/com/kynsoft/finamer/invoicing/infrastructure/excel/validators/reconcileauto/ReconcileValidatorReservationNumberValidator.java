package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReconcileValidatorReservationNumberValidator extends ExcelRuleValidator<InvoiceReconcileAutomaticRow> {

    private final IManageBookingService manageBookingService;
    private final String[] selectedInvoiceIds;

    public ReconcileValidatorReservationNumberValidator(IManageBookingService manageBookingService, String[] selectedInvoiceIds) {
        this.manageBookingService = manageBookingService;
        this.selectedInvoiceIds = selectedInvoiceIds;
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {

        if (validateColumnWIsNotEmpty(obj, errorFieldList)) {
            return validateIfBookingExistByReservationNumber(obj, errorFieldList);
        }
        return false;
    }

    private boolean validateColumnWIsNotEmpty(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getColumnW())) {
            errorFieldList.add(new ErrorField("Column W", "The column W can't be empty"));
            return false;
        }
        return true;
    }

    private boolean validateIfBookingExistByReservationNumber(InvoiceReconcileAutomaticRow obj, List<ErrorField> errorFieldList) {
        Optional<ManageBookingDto> booking = manageBookingService.findManageBookingByBookingNumber(obj.getColumnW());
        if (booking.isEmpty()) {
            errorFieldList.add(new ErrorField("Reservation Number", "There is not booking with the reservation Number"));
            return false;
        }
        boolean isInTheSelectedInvoiceList = Arrays.stream(selectedInvoiceIds)
                .anyMatch(id-> id.equals(booking.get().getInvoice().getId().toString()));
        if (!isInTheSelectedInvoiceList){
            errorFieldList.add(new ErrorField("Reservation Number","There isn't invoice with booking with this reservation number in th selected list"));
            return false;
        }
        obj.setInvoiceIds(Arrays.stream(selectedInvoiceIds).filter(id->id.equals(booking.get().getInvoice().getId().toString())).findAny().get());
        return true;
    }
}
