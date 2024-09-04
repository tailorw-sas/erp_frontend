package com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<UpdateManagePaymentAttachmentStatusCommand> {

    private final IManagePaymentAttachmentStatusService service;

    public UpdateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentAttachmentStatusCommand command) {

        ManagePaymentAttachmentStatusDto dto = this.service.findById(command.getId());
        dto.setName(command.getName());
        dto.setStatus(command.getStatus());
        dto.setDefaults(command.getDefaults());
        service.update(dto);
    }
}
