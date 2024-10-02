package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReconcileManualUploadResponse {
    private final String url;
    private final UUID id;
}
