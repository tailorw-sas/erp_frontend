package com.kynsoft.finamer.invoicing.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachment;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateAttachmentStatusHistoryCommandHandler
        implements ICommandHandler<CreateAttachmentStatusHistoryCommand> {

    private final IManageInvoiceService invoiceService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageAttachmentService manageAttachmentService;
    private final IManageEmployeeService employeeService;

    public CreateAttachmentStatusHistoryCommandHandler(IManageInvoiceService invoiceService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IManageAttachmentService manageAttachmentService,
            IManageEmployeeService employeeService) {
        this.invoiceService = invoiceService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.manageAttachmentService = manageAttachmentService;
        this.employeeService = employeeService;
    }

    @Override
    public void handle(CreateAttachmentStatusHistoryCommand command) {

        ManageAttachmentDto attachment = this.manageAttachmentService.findById(command.getId());
        ManageEmployeeDto employee = null;
        String employeeFullName = "";
        try {
            employee = this.employeeService.findById(attachment.getEmployeeId());
            employeeFullName = employee.getFirstName() + " " + employee.getLastName();
        } catch (Exception e) {
            employeeFullName = attachment.getEmployee();
        }

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto
                .setDescription(
                        "An attachment to the invoice was inserted. The file name: " + attachment.getFilename());
        attachmentStatusHistoryDto.setEmployee(employeeFullName);
        //attachmentStatusHistoryDto.setEmployee(attachment.getEmployee());
        attachmentStatusHistoryDto.setInvoice(attachment.getInvoice());
        attachmentStatusHistoryDto.setEmployeeId(attachment.getEmployeeId());
        attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

    }

}
