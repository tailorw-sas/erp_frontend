package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ReconcileManualCommand implements ICommand {

    private List<UUID> invoices;
    Map<UUID, String> errors;
    private UUID attachInvDefault;
    private UUID resourceType;
    private String employeeName;
    private UUID employeeId;

    public ReconcileManualCommand(List<UUID> invoices, UUID attachInvDefault,
                                  String employeeName, UUID employeeId, UUID resourceType) {
        this.invoices = invoices;
        this.attachInvDefault = attachInvDefault;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.resourceType = resourceType;
    }

    public static ReconcileManualCommand fromRequest(ReconcileManualRequest request){
        return new ReconcileManualCommand(
                request.getInvoices(),
                request.getAttachInvDefault(),
                request.getEmployeeName(),
                request.getEmployeeId(),
                request.getResourceType()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ReconcileManualMessage(errors);
    }
}
