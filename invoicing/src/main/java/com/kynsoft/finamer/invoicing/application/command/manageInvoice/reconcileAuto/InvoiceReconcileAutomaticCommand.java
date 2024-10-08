package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;


@Getter
public class InvoiceReconcileAutomaticCommand implements ICommand {

    private final InvoiceReconcileAutomaticRequest request;

    public InvoiceReconcileAutomaticCommand(InvoiceReconcileAutomaticRequest request) {
        this.request = request;
    }

    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
