package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReconcileManualRequest {
    private List<UUID> invoices;
    private UUID attachInvDefault;
    private UUID resourceType;
    private String employeeName;
    private UUID employeeId;
}
