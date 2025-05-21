package com.kynsoft.finamer.payment.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.attachement.create.CreateAttachmentService;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final CreateAttachmentService createAttachmentService;

    public CreateAttachmentCommandHandler(CreateAttachmentService createAttachmentService) {
        this.createAttachmentService = createAttachmentService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {

        List<MasterPaymentAttachmentDto> masterPaymentAttachments = this.createAttachmentService.createMany(command.getPaymentDto().getId(),
                command.getAttachments());
        command.setDtos(masterPaymentAttachments);
    }

}
