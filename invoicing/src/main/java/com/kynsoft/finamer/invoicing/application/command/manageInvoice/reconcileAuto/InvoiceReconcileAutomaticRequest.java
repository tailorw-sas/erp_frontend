package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvoiceReconcileAutomaticRequest{
    private String importProcessId;
    private String employeeId;
    private String employeeName;
    private String[] invoiceIds;
    private byte[] fileContent;
}
