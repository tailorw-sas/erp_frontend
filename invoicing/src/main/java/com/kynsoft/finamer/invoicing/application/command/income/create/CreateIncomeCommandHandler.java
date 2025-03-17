package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private final IManageEmployeeService employeeService;

    public CreateIncomeCommandHandler(IManageAgencyService agencyService,
                                        IManageHotelService hotelService,
                                        IManageInvoiceTypeService invoiceTypeService,
                                        IManageInvoiceStatusService invoiceStatusService,
                                        IManageInvoiceService manageInvoiceService, 
                                        IManageAttachmentTypeService attachmentTypeService, 
                                        IManageResourceTypeService resourceTypeService, 
                                        IInvoiceStatusHistoryService invoiceStatusHistoryService, 
                                        IInvoiceCloseOperationService closeOperationService, 
                                        IManageAttachmentService attachmentService, 
                                        IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                        IManageEmployeeService employeeService) {
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
        this.employeeService = employeeService;
    }

    @Override
    public void handle(CreateIncomeCommand command) {

        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getInvoiceDate().toLocalDate()));
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService, command.getInvoiceDate().toLocalDate(), command.getHotel()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        ManageInvoiceTypeDto invoiceTypeDto = this.invoiceTypeService.findByEInvoiceType(EInvoiceType.INCOME);

        ManageInvoiceStatusDto invoiceStatusDto = null;
        try {
            invoiceStatusDto = this.invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        } catch (Exception e) {
        }

        ManageEmployeeDto employee = null;
        String employeeFullName = "";
        try {
            employee = this.employeeService.findById(UUID.fromString(command.getEmployee()));
            employeeFullName = employee.getFirstName() + " " + employee.getLastName();
        } catch (Exception e) {
            employeeFullName = command.getEmployee();
        }

        String invoiceNumber = this.setInvoiceNumber(hotelDto, InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME));

        ManageInvoiceDto income = new ManageInvoiceDto(
                command.getId(),
                0L,
                0L,
                invoiceNumber,
                InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME) + "-" + 0L,
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
                null, 0.0,0
        );
        income.setOriginalAmount(0.0);
        ManageInvoiceDto invoiceDto = this.manageInvoiceService.create(income);
        command.setInvoiceId(invoiceDto.getInvoiceId());
        command.setInvoiceNo(invoiceDto.getInvoiceNumber());

        this.updateInvoiceStatusHistory(invoiceDto, employeeFullName);
        //this.updateInvoiceStatusHistory(invoiceDto, command.getEmployee());
        if (command.getAttachments() != null) {
            List<ManageAttachmentDto> attachmentDtoList = this.createAttachment(command.getAttachments(), invoiceDto);
            invoiceDto.setAttachments(attachmentDtoList);
            this.updateAttachmentStatusHistory(invoiceDto, attachmentDtoList, employeeFullName);
        }

    }

    private String setInvoiceNumber(ManageHotelDto hotel, String invoiceNumber) {
        if (hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotel.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotel.getCode();
        }
        return invoiceNumber;
    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee) {

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("The income data was inserted.");
        dto.setEmployee(employee);
        dto.setInvoiceStatus(invoiceDto.getStatus());

        this.invoiceStatusHistoryService.create(dto);

    }

    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, List<ManageAttachmentDto> attachments, String employeeFullName) {
        for (ManageAttachmentDto attachment : attachments) {
            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto
                    .setDescription("An attachment to the invoice was inserted. The file name: " + attachment.getFile());
            attachmentStatusHistoryDto.setEmployee(employeeFullName);
            //attachmentStatusHistoryDto.setEmployee(attachment.getEmployee());
            invoice.setAttachments(null);
            attachmentStatusHistoryDto.setInvoice(invoice);
            attachmentStatusHistoryDto.setEmployeeId(attachment.getEmployeeId());
            try {
                attachmentStatusHistoryDto.setAttachmentId(this.attachmentService.findById(attachment.getId()).getAttachmentId());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    resourceTypeDto,
                    false
            ));

            if (attachmentType != null && attachmentType.getDefaults() != null && attachmentType.getDefaults()) {
                countDefaults++;
            }
        }

        if (countDefaults > 1) {
            throw new BusinessException(DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT,
                    DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase());
        }
        this.attachmentService.create(dtos);
        return dtos;
    }

    private LocalDateTime invoiceDate(UUID hotel, LocalDateTime invoiceDate) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate(), invoiceDate.toLocalDate())) {
            return invoiceDate;
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }
}
