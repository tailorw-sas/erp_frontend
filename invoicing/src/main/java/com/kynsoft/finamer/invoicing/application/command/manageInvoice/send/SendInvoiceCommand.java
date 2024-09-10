package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class SendInvoiceCommand implements ICommand {
    private final UUID invoice;
    private final String employee;
    private boolean result;

    public SendInvoiceCommand(UUID invoice, String employee) {
        this.invoice = invoice;
        this.employee = employee;
    }

    public static SendInvoiceCommand fromRequest(SendInvoiceRequest request) {
        return new SendInvoiceCommand(request.getInvoice(), request.getEmployee());
    }

    @Override
    public ICommandMessage getMessage() {
        return new SendInvoiceMessage(result);
    }
}
