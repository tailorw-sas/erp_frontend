package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile;

import lombok.Getter;

@Getter
public class InvoiceReconcileImportRequest {
    private final String importProcessId;
    private final String employee;
    private final String employeeId;

    public InvoiceReconcileImportRequest(String importProcessId, String employee, String employeeId) {
        this.importProcessId = importProcessId;
        this.employee = employee;
        this.employeeId = employeeId;
    }
}
