package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportCommand implements ICommand {

    private final String importProcessId;
    private final String employee;
    private final String employeeId;


    public InvoiceReconcileImportCommand(String importProcessId, String employee, String employeeId) {
        this.importProcessId = importProcessId;
        this.employee = employee;
        this.employeeId = employeeId;
    }

    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
