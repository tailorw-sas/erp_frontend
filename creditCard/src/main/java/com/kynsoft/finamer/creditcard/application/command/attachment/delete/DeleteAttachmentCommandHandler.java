package com.kynsoft.finamer.creditcard.application.command.attachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IAttachmentService service;

    public DeleteAttachmentCommandHandler(IAttachmentService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        AttachmentDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

//     private void updateAttachmentStatusHistory( ManageInvoiceDto invoice, String fileName, Long attachmentId, String employee, UUID employeeId) {
//
//        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription("An attachment to the invoice was deleted. The file name: " + fileName);
//        attachmentStatusHistoryDto.setEmployee(employee);
//        attachmentStatusHistoryDto.setInvoice(invoice);
//        attachmentStatusHistoryDto.setEmployeeId(employeeId);
//        attachmentStatusHistoryDto.setAttachmentId(attachmentId);
//
//        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }
}
