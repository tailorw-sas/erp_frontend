package com.kynsoft.finamer.payment.application.command.managePaymentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentStatusCommandHandler implements ICommandHandler<CreateManagePaymentStatusCommand> {
    private final IManagePaymentStatusService service;

    public CreateManagePaymentStatusCommandHandler(final IManagePaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentStatusCommand command) {
        service.create(new ManagePaymentStatusDto(
                command.getId(), 
                command.getCode(), 
                command.getName(),
                command.getStatus(),
                command.getApplied()
        ));
    }
}
