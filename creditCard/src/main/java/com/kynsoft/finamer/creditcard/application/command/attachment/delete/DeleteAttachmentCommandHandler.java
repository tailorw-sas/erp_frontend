package com.kynsoft.finamer.creditcard.application.command.attachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IAttachmentService service;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public DeleteAttachmentCommandHandler(IAttachmentService service, IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.service = service;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        AttachmentDto delete = this.service.findById(command.getId());

        this.service.delete(delete);
        this.attachmentStatusHistoryService.create(delete, "deleted");
    }
}
