package com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentStatusHistoryCommandHandler implements ICommandHandler<CreateAttachmentStatusHistoryCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public CreateAttachmentStatusHistoryCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService, IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(CreateAttachmentStatusHistoryCommand command) {
        List<MasterPaymentAttachmentDto> list = masterPaymentAttachmentService.findAllByPayment(command.getPayment().getId());
        for (MasterPaymentAttachmentDto attachment : list) {

            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(command.getId());
            attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + attachment.getFileName());
            attachmentStatusHistoryDto.setEmployee(command.getEmployeeDto());
            attachmentStatusHistoryDto.setPayment(command.getPayment());
            attachmentStatusHistoryDto.setStatus(command.getPayment().getAttachmentStatus().getCode() + "-" + command.getPayment().getAttachmentStatus().getName());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

        }
    }

}
