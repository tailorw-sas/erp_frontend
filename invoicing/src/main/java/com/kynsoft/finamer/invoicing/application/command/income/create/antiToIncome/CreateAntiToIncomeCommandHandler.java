package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateAntiToIncomeCommandHandler implements ICommandHandler<CreateAntiToIncomeCommand> {

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

    public CreateAntiToIncomeCommandHandler(IManageAgencyService agencyService,
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
    public void handle(CreateAntiToIncomeCommand command) {

        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getInvoiceDate().toLocalDate()));
        //RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService, command.getInvoiceDate().toLocalDate(), command.getHotel()));

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
                null, 0.0, 0
        );
        income.setOriginalAmount(0.0);
        ManageInvoiceDto invoiceDto = this.manageInvoiceService.create(income);
        command.setInvoiceId(invoiceDto.getInvoiceId());
        command.setInvoiceNo(invoiceDto.getInvoiceNumber());

        this.updateInvoiceStatusHistory(invoiceDto, employeeFullName);
        //this.updateInvoiceStatusHistory(invoiceDto, command.getEmployee());
        if (command.getAttachments() != null) {
            List<ManageAttachmentDto> attachmentDtoList = this.attachments(command.getAttachments(), invoiceDto);
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

    private List<ManageAttachmentDto> attachments(List<CreateAntiToIncomeAttachmentRequest> attachments, ManageInvoiceDto invoiceDto) {
        String filename = "detail.pdf";
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }
        ManageAttachmentTypeDto attachmentTypeDto = this.attachmentTypeService.findAttachInvDefault().orElse(null);
//        ResourceTypeDto resourceTypeDto = null;
        ResourceTypeDto resourceTypeDto = this.resourceTypeService.findByDefaults();

        List<ManageAttachmentDto> attachment = new ArrayList<>();
        attachment.add(new ManageAttachmentDto(
                UUID.randomUUID(),
                null,
                filename,
                attachments.get(0).getFile(),
                "From payment.",
                attachmentTypeDto != null ? attachmentTypeDto : null,
                invoiceDto,
                attachments.get(0).getEmployee(),
                attachments.get(0).getEmployeeId(),
                null,
                resourceTypeDto != null ? resourceTypeDto : null,
                false
        ));
        this.attachmentService.create(attachment);
        return attachment;
    }
}
