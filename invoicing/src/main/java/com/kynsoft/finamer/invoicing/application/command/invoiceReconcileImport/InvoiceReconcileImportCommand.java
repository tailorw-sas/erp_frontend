package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportCommand implements ICommand {

    private final String importProcessId;

    public InvoiceReconcileImportCommand(String importProcessId) {
        this.importProcessId = importProcessId;
    }

    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
