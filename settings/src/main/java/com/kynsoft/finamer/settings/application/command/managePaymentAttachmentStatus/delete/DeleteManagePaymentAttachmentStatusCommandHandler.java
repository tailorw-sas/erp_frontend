package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<DeleteManagePaymentAttachmentStatusCommand> {
    
    private final IManagePaymentAttachmentStatusService service;
    
    public DeleteManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }
    
    @Override
    public void handle(DeleteManagePaymentAttachmentStatusCommand command) {
        ManagePaymentAttachmentStatusDto dto = service.findById(command.getId());
        
        service.delete(dto);
    }
}
