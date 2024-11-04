package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualMessage implements ICommandMessage {
    private final String command = "MANUAL_RECONCILE";
    List<ReconcileManualErrorResponse> errorsResponse;
    private int totalInvoicesRec;
    private int totalInvoices;
}
