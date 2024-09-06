package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateIncomeCommandHandler implements ICommandHandler<CreateIncomeCommand> {

    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService invoiceTypeService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final IManageInvoiceService manageInvoiceService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    private final IInvoiceCloseOperationService closeOperationService;

    private final IManageAttachmentService attachmentService;

    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public CreateIncomeCommandHandler(IManageAgencyService agencyService,
                                      IManageHotelService hotelService,
                                      IManageInvoiceTypeService invoiceTypeService,
                                      IManageInvoiceStatusService invoiceStatusService,
                                      IManageInvoiceService manageInvoiceService, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService, IInvoiceStatusHistoryService invoiceStatusHistoryService, IInvoiceCloseOperationService closeOperationService, IManageAttachmentService attachmentService, IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.invoiceTypeService = invoiceTypeService;
        this.invoiceStatusService = invoiceStatusService;
        this.manageInvoiceService = manageInvoiceService;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.closeOperationService = closeOperationService;
        this.attachmentService = attachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(CreateIncomeCommand command) {

        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getInvoiceDate().toLocalDate()));
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService, command.getInvoiceDate().toLocalDate(), command.getHotel()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        ManageInvoiceTypeDto invoiceTypeDto = null;
        try {
            invoiceTypeDto = this.invoiceTypeService.findById(command.getInvoiceType());
        } catch (Exception e) {
        }

        ManageInvoiceStatusDto invoiceStatusDto = null;
        try {
            invoiceStatusDto = this.invoiceStatusService.findById(command.getInvoiceStatus());
        } catch (Exception e) {
        }

        ManageInvoiceDto invoiceDto = this.manageInvoiceService.create(new ManageInvoiceDto(
                command.getId(), 
                0L, 
                0L, 
                InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME), 
                command.getInvoiceDate(), 
                command.getDueDate(), 
                command.getManual(), 
                0.0, 
                0.0, 
                hotelDto, 
                agencyDto, 
                EInvoiceType.INCOME, 
                EInvoiceStatus.SENT, 
                Boolean.FALSE, 
                null, 
                null, 
                command.getReSend(), 
                command.getReSendDate(), 
                invoiceTypeDto, 
                invoiceStatusDto,
                null,
                false,
                null, 0.0
        ));
        command.setInvoiceId(invoiceDto.getInvoiceId());

        this.updateInvoiceStatusHistory(invoiceDto, command.getEmployee());
        if(command.getAttachments() != null){
            List<ManageAttachmentDto> attachmentDtoList = this.createAttachment(command.getAttachments(), invoiceDto);
            invoiceDto.setAttachments(attachmentDtoList);
            this.updateAttachmentStatusHistory(invoiceDto, attachmentDtoList);
        }

    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee){

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("The income data was inserted.");
        dto.setEmployee(employee);
        dto.setInvoiceStatus(invoiceDto.getStatus());

        this.invoiceStatusHistoryService.create(dto);

    }

    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, List<ManageAttachmentDto> attachments){
        for(ManageAttachmentDto attachment : attachments){
            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto
                    .setDescription("An attachment to the invoice was inserted. The file name: " + attachment.getFile());
            attachmentStatusHistoryDto.setEmployee(attachment.getEmployee());
            invoice.setAttachments(null);
            attachmentStatusHistoryDto.setInvoice(invoice);
            attachmentStatusHistoryDto.setEmployeeId(attachment.getEmployeeId());
            attachmentStatusHistoryDto.setAttachmentId(this.attachmentService.findById(attachment.getId()).getAttachmentId());

            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
        }
    }

    private List<ManageAttachmentDto> createAttachment(List<CreateIncomeAttachmentRequest> attachments, ManageInvoiceDto invoiceDto) {
        List<ManageAttachmentDto> dtos = new ArrayList<>();
        int countDefaults = 0;
        for (CreateIncomeAttachmentRequest attachment : attachments) {
            ManageAttachmentTypeDto attachmentType = attachment.getType() != null
                    ? this.attachmentTypeService.findById(attachment.getType())
                    : null;
            ResourceTypeDto resourceTypeDto = attachment.getPaymentResourceType() != null
                    ? this.resourceTypeService.findById(attachment.getPaymentResourceType())
                    : null;

            dtos.add(new ManageAttachmentDto(
                    UUID.randomUUID(),
                    null,
                    attachment.getFilename(),
                    attachment.getFile(),
                    attachment.getRemark(),
                    attachmentType,
                    invoiceDto,
                    attachment.getEmployee(),
                    attachment.getEmployeeId(),
                    null,
                    resourceTypeDto));

            if (attachmentType != null && attachmentType.getDefaults() != null && attachmentType.getDefaults()) {
                countDefaults++;
            }
        }

        if(countDefaults > 1){
            throw new BusinessException(DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT,
                    DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase());
        }
        this.attachmentService.create(dtos);
        return dtos;
    }
}
