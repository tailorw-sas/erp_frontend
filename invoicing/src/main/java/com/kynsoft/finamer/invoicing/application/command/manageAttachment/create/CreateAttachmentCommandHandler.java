package com.kynsoft.finamer.invoicing.application.command.manageAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.*;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IManageAttachmentService attachmentService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final IManageInvoiceService manageInvoiceService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public CreateAttachmentCommandHandler(IManageAttachmentService attachmentService,
            IManageAttachmentTypeService attachmentTypeService, IManageInvoiceService manageInvoiceService,
            IManageResourceTypeService resourceTypeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.attachmentService = attachmentService;
        this.attachmentTypeService = attachmentTypeService;
        this.manageInvoiceService = manageInvoiceService;
        this.resourceTypeService = resourceTypeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {

        ManageAttachmentTypeDto attachmentType = command.getType() != null
                ? this.attachmentTypeService.findById(command.getType())
                : null;
        ManageInvoiceDto invoiceDto = this.manageInvoiceService.findById(command.getInvoice());

        ResourceTypeDto resourceTypeDto = command.getPaymentResourceType() != null
                ? this.resourceTypeService.findById(command.getPaymentResourceType())
                : null;

        if (invoiceDto.getInvoiceType().compareTo(EInvoiceType.INCOME) == 0 && attachmentType.getDefaults() != null
                && attachmentType.getDefaults()) {
            List<ManageAttachmentDto> attachmentDtoList = invoiceDto.getAttachments();
            for (ManageAttachmentDto attachmentDto : attachmentDtoList) {
                if (attachmentDto.getType().getDefaults() != null && attachmentDto.getType().getDefaults()) {
                    throw new BusinessException(DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT,
                            DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase());
                }
            }
        }

        Long attachmentId = attachmentService.create(new ManageAttachmentDto(
                command.getId(),
                null,
                command.getFilename(),
                command.getFile(),
                command.getRemark(),
                attachmentType,
                invoiceDto, command.getEmployee(), command.getEmployeeId(), null, resourceTypeDto));

        this.updateAttachmentStatusHistory(invoiceDto, command.getFilename(), attachmentId, command.getEmployee(),
                command.getEmployeeId());

        if (invoiceDto.getStatus().equals(EInvoiceStatus.PROCECSED)) {
            invoiceDto.setStatus(EInvoiceStatus.RECONCILED);
            this.manageInvoiceService.update(invoiceDto);

            this.updateInvoiceStatusHistory(invoiceDto, command.getEmployee(), command.getFilename());
        }

    }

    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, String fileName, Long attachmentId,
            String employee, UUID employeeId) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto
                .setDescription("An attachment to the invoice was inserted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employee);
        attachmentStatusHistoryDto.setInvoice(invoice);
        attachmentStatusHistoryDto.setEmployeeId(employeeId);
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee, String fileName) {

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("An attachment to the invoice was inserted. The file name: " + fileName);
        dto.setEmployee(employee);

        this.invoiceStatusHistoryService.create(dto);

    }
}
