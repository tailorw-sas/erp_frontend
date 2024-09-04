package com.kynsoft.finamer.payment.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    public CreateAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {
        List<MasterPaymentAttachmentDto> dtos = new ArrayList<>();
        for (CreateAttachmentRequest attachment : command.getAttachments()) {
            dtos.add(new MasterPaymentAttachmentDto(
                    UUID.randomUUID(),
                    Status.ACTIVE,
                    command.getPaymentDto(),
                    null,
                    null,
                    attachment.getFileName(),
                    attachment.getFileWeight(),
                    attachment.getPath(),
                    attachment.getRemark(),
                    0L
            ));
        }

        this.masterPaymentAttachmentService.create(dtos);
        command.setDtos(dtos);
    }

}
