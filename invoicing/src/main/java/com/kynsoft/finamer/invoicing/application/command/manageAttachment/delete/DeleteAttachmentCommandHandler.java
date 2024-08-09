package com.kynsoft.finamer.invoicing.application.command.manageAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IManageAttachmentService service;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public DeleteAttachmentCommandHandler(IManageAttachmentService service, IAttachmentStatusHistoryService attachmentStatusHistoryService, IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.service = service;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        ManageAttachmentDto delete = this.service.findById(command.getId());

        service.delete(delete);
        this.updateAttachmentStatusHistory(delete.getInvoice(), delete.getFilename(), delete.getAttachmentId(), delete.getEmployee(), delete.getEmployeeId());

    }

     private void updateAttachmentStatusHistory( ManageInvoiceDto invoice, String fileName, Long attachmentId, String employee, UUID employeeId) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the invoice was deleted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employee);
        attachmentStatusHistoryDto.setInvoice(invoice);
        attachmentStatusHistoryDto.setEmployeeId(employeeId);
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee, String fileName){

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("An attachment to the invoice was deleted. The file name: " + fileName);
        dto.setEmployee(employee);

        this.invoiceStatusHistoryService.create(dto);

    }

}
