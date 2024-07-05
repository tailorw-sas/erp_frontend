package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateBulkInvoiceCommandHandler implements ICommandHandler<CreateBulkInvoiceCommand> {

    private final IMediator mediator;

    public CreateBulkInvoiceCommandHandler(
            IMediator mediator) {

        this.mediator = mediator;
    }

    @Override
    @Transactional
    public void handle(CreateBulkInvoiceCommand command) {

        this.mediator.send(command.getInvoiceCommand());

        for (int i = 0; i < command.getBookingCommands().size(); i++) {

            CreateBookingCommand bookingCommand = command.getBookingCommands().get(i);

            this.mediator.send(bookingCommand);

        }

        for (int i = 0; i < command.getRoomRateCommands().size(); i++) {

            CreateRoomRateCommand roomRateCommand = command.getRoomRateCommands().get(i);

            this.mediator.send(roomRateCommand);

        }

        for (int i = 0; i < command.getAdjustmentCommands().size(); i++) {

            CreateAdjustmentCommand adjustmentCommand = command.getAdjustmentCommands().get(i);

            this.mediator.send(adjustmentCommand);

        }

    }
}
