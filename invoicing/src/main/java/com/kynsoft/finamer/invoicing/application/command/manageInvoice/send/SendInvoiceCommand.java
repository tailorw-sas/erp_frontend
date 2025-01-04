package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SendInvoiceCommand implements ICommand {
    private final List<UUID> invoice;
    private final String employee;
    private final boolean groupByClient;
    private final boolean withAttachment;
    private boolean result;

    public SendInvoiceCommand(List<UUID>  invoice, String employee, boolean groupByClient, boolean withAttachment) {
        this.invoice = invoice;
        this.employee = employee;
        this.groupByClient = groupByClient;
        this.withAttachment = withAttachment;
    }

    public static SendInvoiceCommand fromRequest(SendInvoiceRequest request) {
        return new SendInvoiceCommand(request.getInvoice(), request.getEmployee(),
                request.isGroupByClient(), request.isWithAttachment() );
    }

    @Override
    public ICommandMessage getMessage() {
        return new SendInvoiceMessage(result);
    }
}
