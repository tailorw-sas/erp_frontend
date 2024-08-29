package com.kynsoft.finamer.payment.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagePaymentStatusCommandHandler implements ICommandHandler<UpdateManagePaymentStatusCommand> {
    private final IManagePaymentStatusService service;

    public UpdateManagePaymentStatusCommandHandler(final IManagePaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentStatusCommand command) {
        ManagePaymentStatusDto update = this.service.findById(command.getId());
        update.setName(command.getName());
        update.setStatus(command.getStatus());
        update.setApplied(command.getApplied());
        service.update(update);
    }
}
