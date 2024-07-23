package com.kynsoft.finamer.invoicing.application.command.manageAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;

import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;

import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IManageAttachmentService attachmentService;
    private final IManageAttachmentTypeService attachmentTypeService;

    private final IManageInvoiceService manageInvoiceService;

    public CreateAttachmentCommandHandler(IManageAttachmentService attachmentService,
            IManageAttachmentTypeService attachmentTypeService, IManageInvoiceService manageInvoiceService) {
        this.attachmentService = attachmentService;
        this.attachmentTypeService = attachmentTypeService;
        this.manageInvoiceService = manageInvoiceService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {

        ManageAttachmentTypeDto attachmentType = this.attachmentTypeService.findById(command.getType());
        ManageInvoiceDto invoiceDto = this.manageInvoiceService.findById(command.getInvoice());

        attachmentService.create(new ManageAttachmentDto(
                command.getId(),
                null,
                command.getFilename(),
                command.getFile(),
                command.getRemark(),
                attachmentType,
                invoiceDto, command.getEmployee(), command.getEmployeeId(), null));
    }
}
