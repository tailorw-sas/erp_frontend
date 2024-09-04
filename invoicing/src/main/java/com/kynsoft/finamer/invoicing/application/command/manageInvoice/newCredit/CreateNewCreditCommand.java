package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CreateNewCreditCommand implements ICommand {

    private LocalDateTime invoiceDate;
    private UUID invoice;
    private String employee;
    private UUID credit;
    private List<CreateNewCreditBookingRequest> bookings;
    private List<CreateAttachmentCommand> attachmentCommands;
    private Long invoiceId;

    public CreateNewCreditCommand(LocalDateTime invoiceDate, UUID invoice, String employee,
                                  List<CreateNewCreditBookingRequest> bookings,
                                  List<CreateAttachmentCommand> attachmentCommands) {
        this.invoiceDate = invoiceDate;
        this.invoice = invoice;
        this.employee = employee;
        this.bookings = bookings;
        this.attachmentCommands = attachmentCommands;
    }

    public static CreateNewCreditCommand fromRequest(CreateNewCreditRequest request) {
        List<CreateAttachmentCommand> attachments = request.getAttachments()
                .stream().map(CreateAttachmentCommand::fromRequest).collect(Collectors.toList());
        return new CreateNewCreditCommand(
                request.getInvoiceDate(), request.getInvoice(), request.getEmployee(),
                request.getBookings(), attachments
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateNewCreditMessage(
                invoice, credit
        );
    }
}