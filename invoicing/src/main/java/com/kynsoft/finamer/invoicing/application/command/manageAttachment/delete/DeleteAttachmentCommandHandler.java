package com.kynsoft.finamer.invoicing.application.command.manageAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IManageAttachmentService service;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageInvoiceService invoiceService;
    private final IManageEmployeeService employeeService;

    public DeleteAttachmentCommandHandler(IManageAttachmentService service, IAttachmentStatusHistoryService attachmentStatusHistoryService, IManageInvoiceService invoiceService, IManageEmployeeService employeeService) {
        this.service = service;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.invoiceService = invoiceService;
        this.employeeService = employeeService;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        ManageAttachmentDto delete = this.service.findById(command.getId());
        ManageInvoiceDto invoiceDto = this.invoiceService.findById(delete.getInvoice().getId());
        ManageEmployeeDto employeeDto = this.employeeService.findById(command.getEmployee());

        if (delete.getType().isAttachInvDefault()){
            int cont = 0;
            for (ManageAttachmentDto attachmentDto : invoiceDto.getAttachments()){
                cont += attachmentDto.getType().isAttachInvDefault() ? 1 : 0;
            }
            if (cont < 2){
                throw new BusinessException(
                        DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
                        DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase()
                );
            }
        }

        service.delete(delete);
        //this.updateAttachmentStatusHistory(delete.getInvoice(), delete.getFilename(), delete.getAttachmentId(), delete.getEmployee(), delete.getEmployeeId());
        this.updateAttachmentStatusHistory(delete.getInvoice(), delete.getFilename(), delete.getAttachmentId(), employeeDto.getFirstName() + " " + employeeDto.getLastName(), employeeDto.getId());

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
}
