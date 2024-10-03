package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualErrorResponse {
    private String invoiceId;
    private String hotel;
    private String invoiceNo;
    private String agency;
    private LocalDate invoiceDate;
    private Double invoiceAmount;
    private String message;
    private EInvoiceStatus status;
}
