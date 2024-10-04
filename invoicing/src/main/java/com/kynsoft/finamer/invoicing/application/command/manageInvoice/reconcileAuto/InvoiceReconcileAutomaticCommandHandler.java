package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;


@Component
public class InvoiceReconcileAutomaticCommandHandler implements ICommandHandler<InvoiceReconcileAutomaticCommand> {

    @Override
    public void handle(InvoiceReconcileAutomaticCommand command) {

    }
}
