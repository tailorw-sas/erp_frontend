package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageHotelPaymentStatusCommandHandler implements ICommandHandler<DeleteManageHotelPaymentStatusCommand> {

    private final IManageHotelPaymentStatusService service;

    public DeleteManageHotelPaymentStatusCommandHandler(IManageHotelPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageHotelPaymentStatusCommand command) {
        this.service.delete(command.getId());
    }
}
