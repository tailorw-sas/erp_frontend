package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TotalCloneInvoiceCommand implements ICommand {

        private UUID invoiceToClone;
        private CreateInvoiceCommand invoiceCommand;
        private List<CreateBookingCommand> bookingCommands;
        private List<CreateAttachmentCommand> attachmentCommands;
        private Long invoiceId;
        private String invoiceNo;
        private String employee;

        public TotalCloneInvoiceCommand(UUID invoiceToClone, CreateInvoiceCommand invoiceCommand,
                        List<CreateBookingCommand> bookingCommands,

                        List<CreateAttachmentCommand> attachmentCommands, String employee) {
                this.invoiceCommand = invoiceCommand;
                this.bookingCommands = bookingCommands;
                this.attachmentCommands = attachmentCommands;
                this.employee = employee;
        }

        public static TotalCloneInvoiceCommand fromRequest(TotalCloneInvoiceRequest request) {
                List<CreateBookingCommand> bookingCommands = request.getBookings() != null ? request.getBookings()
                                .stream()
                                .map(req -> CreateBookingCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();

                List<CreateAttachmentCommand> attachmentCommands = request.getAttachments() != null ? request
                                .getAttachments().stream()
                                .map(req -> CreateAttachmentCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();

                return new TotalCloneInvoiceCommand(
                                request.getInvoiceToClone(),
                                CreateInvoiceCommand.fromRequest(request.getInvoice()), bookingCommands,

                                attachmentCommands, request.getEmployee());
        }

        @Override
        public ICommandMessage getMessage() {
                return new TotalCloneInvoiceMessage(invoiceCommand.getId(),
                                bookingCommands.stream().map(e -> new CreateBookingMessage(e.getId()))
                                                .collect(Collectors.toList()),

                                attachmentCommands.stream().map(e -> new CreateAttachmentMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                invoiceId, invoiceNo);
        }
}
