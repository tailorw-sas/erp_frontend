package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import org.springframework.stereotype.Component;


@Component
public class InvoiceReconcileAutomaticCommandHandler implements ICommandHandler<InvoiceReconcileAutomaticCommand> {

    private final IInvoiceReconcileAutomaticService reconcileAutomaticService;

    public InvoiceReconcileAutomaticCommandHandler(IInvoiceReconcileAutomaticService reconcileAutomaticService) {
        this.reconcileAutomaticService = reconcileAutomaticService;
    }

    @Override
    public void handle(InvoiceReconcileAutomaticCommand command) {
        reconcileAutomaticService.reconcileAutomatic(command.getRequest());
    }
}
