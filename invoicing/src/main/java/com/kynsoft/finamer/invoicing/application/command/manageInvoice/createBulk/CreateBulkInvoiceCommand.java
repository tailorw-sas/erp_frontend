package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import java.util.stream.Collectors;

@Getter
@Setter
public class CreateBulkInvoiceCommand implements ICommand {

        private CreateInvoiceCommand invoiceCommand;
        private List<CreateBookingCommand> bookingCommands;
        private List<CreateRoomRateCommand> roomRateCommands;
        private List<CreateAdjustmentCommand> adjustmentCommands;

        public CreateBulkInvoiceCommand(CreateInvoiceCommand invoiceCommand, List<CreateBookingCommand> bookingCommands,
                        List<CreateRoomRateCommand> roomRateCommands,
                        List<CreateAdjustmentCommand> adjustmentCommands) {
                this.invoiceCommand = invoiceCommand;
                this.bookingCommands = bookingCommands;
                this.roomRateCommands = roomRateCommands;
                this.adjustmentCommands = adjustmentCommands;
        }

        public static CreateBulkInvoiceCommand fromRequest(CreateBulkInvoiceRequest request) {
                List<CreateBookingCommand> bookingCommands = request.getBookings().stream()
                                .map(req -> CreateBookingCommand.fromRequest(req)).collect(Collectors.toList());
                List<CreateRoomRateCommand> roomRateCommands = request.getRoomRates().stream()
                                .map(req -> CreateRoomRateCommand.fromRequest(req)).collect(Collectors.toList());
                List<CreateAdjustmentCommand> adjustmentCommands = request.getAdjustments().stream()
                                .map(req -> CreateAdjustmentCommand.fromRequest(req)).collect(Collectors.toList());

                return new CreateBulkInvoiceCommand(
                                CreateInvoiceCommand.fromRequest(request.getInvoice()), bookingCommands,
                                roomRateCommands,
                                adjustmentCommands);
        }

        @Override
        public ICommandMessage getMessage() {
                return new CreateBulkInvoiceMessage(invoiceCommand.getId(),
                                bookingCommands.stream().map(e -> new CreateBookingMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                roomRateCommands.stream().map(e -> new CreateRoomRateMessage(e.getId()))
                                                .collect(Collectors.toList()),
                                adjustmentCommands.stream().map(e -> new CreateAdjustmentMessage(e.getId()))
                                                .collect(Collectors.toList()));
        }
}
