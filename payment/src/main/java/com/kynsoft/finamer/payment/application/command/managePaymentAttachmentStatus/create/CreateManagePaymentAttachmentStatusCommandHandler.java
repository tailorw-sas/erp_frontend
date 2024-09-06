package com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<CreateManagePaymentAttachmentStatusCommand> {

    private final IManagePaymentAttachmentStatusService service;

    public CreateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentAttachmentStatusCommand command) {
        service.create(new ManagePaymentAttachmentStatusDto(
                command.getId(), 
                command.getCode(), 
                command.getName(),
                command.getStatus(),
                command.getDefaults()
        ));
    }
}
