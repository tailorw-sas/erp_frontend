package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;

import java.time.LocalDate;
import java.util.UUID;

public class ManageInvoiceInvoiceDateInCloseOperationRule extends BusinessRule {

    private final IInvoiceCloseOperationService closeOperationService;

    private final LocalDate invoiceDate;

    private final UUID hotel;

    public ManageInvoiceInvoiceDateInCloseOperationRule(IInvoiceCloseOperationService closeOperationService, LocalDate invoiceDate, UUID hotel) {
        super(
                DomainErrorMessage.INVOICE_CLOSE_OPERATION_OUT_OF_RANGE,
                new ErrorField("invoiceDate", DomainErrorMessage.INVOICE_CLOSE_OPERATION_OUT_OF_RANGE.getReasonPhrase())
        );
        this.closeOperationService = closeOperationService;
        this.invoiceDate = invoiceDate;
        this.hotel = hotel;
    }

    @Override
    public boolean isBroken() {
        InvoiceCloseOperationDto dto = this.closeOperationService.findActiveByHotelId(hotel);
        return invoiceDate.isBefore(dto.getBeginDate()) || invoiceDate.isAfter(dto.getEndDate());
    }
}
