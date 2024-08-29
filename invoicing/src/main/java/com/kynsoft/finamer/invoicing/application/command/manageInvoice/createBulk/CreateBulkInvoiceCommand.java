package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CreateBulkInvoiceCommand implements ICommand {

        private CreateInvoiceCommand invoiceCommand;
        private List<CreateBookingCommand> bookingCommands;
        private List<CreateRoomRateCommand> roomRateCommands;
        private List<CreateAdjustmentCommand> adjustmentCommands;
        private List<CreateAttachmentCommand> attachmentCommands;
        private Long invoiceId;
        private String invoiceNo;
        private String employee;

        public CreateBulkInvoiceCommand(CreateInvoiceCommand invoiceCommand, List<CreateBookingCommand> bookingCommands,
                        List<CreateRoomRateCommand> roomRateCommands,
                        List<CreateAdjustmentCommand> adjustmentCommands,
                        List<CreateAttachmentCommand> attachmentCommands, String employee) {
                this.invoiceCommand = invoiceCommand;
                this.bookingCommands = bookingCommands;
                this.roomRateCommands = roomRateCommands;
                this.adjustmentCommands = adjustmentCommands;
                this.attachmentCommands = attachmentCommands;
                this.employee = employee;
        }

        public static CreateBulkInvoiceCommand fromRequest(CreateBulkInvoiceRequest request) {
                List<CreateBookingCommand> bookingCommands = request.getBookings() != null ? request.getBookings()
                                .stream()
                                .map(req -> CreateBookingCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();
                List<CreateRoomRateCommand> roomRateCommands = request.getRoomRates() != null ? request.getRoomRates()
                                .stream()
                                .map(req -> CreateRoomRateCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();
                List<CreateAdjustmentCommand> adjustmentCommands = request.getAdjustments() != null ? request
                                .getAdjustments().stream()
                                .map(req -> CreateAdjustmentCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();
                List<CreateAttachmentCommand> attachmentCommands = request.getAttachments() != null ? request
                                .getAttachments().stream()
                                .map(req -> CreateAttachmentCommand.fromRequest(req)).collect(Collectors.toList())
                                : new LinkedList<>();

                return new CreateBulkInvoiceCommand(
                                CreateInvoiceCommand.fromRequest(request.getInvoice()), bookingCommands,
                                roomRateCommands,
                                adjustmentCommands, attachmentCommands, request.getEmployee());
        }

        @Override
        public ICommandMessage getMessage() {
                return new CreateBulkInvoiceMessage(invoiceCommand.getId(),
                                bookingCommands.stream().map(e -> new CreateBookingMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                roomRateCommands.stream().map(e -> new CreateRoomRateMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                adjustmentCommands.stream().map(e -> new CreateAdjustmentMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                attachmentCommands.stream().map(e -> new CreateAttachmentMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                invoiceId, invoiceNo);
        }
}
