package com.kynsoft.finamer.invoicing.application.command.manageBooking.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class DeleteBookingCommandHandler implements ICommandHandler<DeleteBookingCommand> {

    private final IManageBookingService service;

    public DeleteBookingCommandHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteBookingCommand command) {
        ManageBookingDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
