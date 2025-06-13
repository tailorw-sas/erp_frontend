package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeAttachmentRequest;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.core.domain.http.entity.income.NewIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckAmountNotZeroRule;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
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
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IManagePaymentTransactionTypeService transactionTypeService;

    private final IManageRoomRateService roomRateService;

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
                                      IManageEmployeeService employeeService,
                                      IManagePaymentTransactionTypeService transactionTypeService,
                                      ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
                                      IManageRoomRateService roomRateService) {
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
        this.transactionTypeService = transactionTypeService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.roomRateService = roomRateService;
    }

    @Override
    public void handle(CreateIncomeCommand command) {
        InvoiceCloseOperationDto closeOperationDto = this.getHotelCloseOperation(command.getHotel());

        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getInvoiceDate().toLocalDate()));
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService,
                command.getInvoiceDate().toLocalDate(), command.getHotel()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        ManageInvoiceTypeDto invoiceTypeDto = this.invoiceTypeService.findByEInvoiceType(EInvoiceType.INCOME);
        ManageInvoiceStatusDto invoiceStatusDto = this.invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        String employeeFullName = this.employeeService.getEmployeeFullName(command.getEmployee());
      
        String invoiceNumber = this.setInvoiceNumber(hotelDto, InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME));

        ManageInvoiceDto income = this.generateNewManageInvoice(command.getId(),
                invoiceNumber,
                command.getInvoiceDate(),
                command.getDueDate(),
                command.getManual(),
                hotelDto,
                agencyDto,
                command.getReSend(),
                command.getReSendDate(),
                invoiceTypeDto,
                invoiceStatusDto);
        income.setOriginalAmount(0.0);

        this.createAdjustments(income, command.getAdjustments(), employeeFullName, closeOperationDto);

        //ManageInvoiceDto invoiceDto = this.manageInvoiceService.create(income);
        Instant before = Instant.now();
        this.manageInvoiceService.insert(income);
        Instant after = Instant.now();
        System.out.println("Insert invoice - booking: " + Duration.between(before, after).toMillis() + " ms");

        command.setInvoiceId(income.getInvoiceId());
        command.setInvoiceNo(income.getInvoiceNumber());
        command.setIncome(income);

        this.updateInvoiceStatusHistory(income, employeeFullName);
        if (command.getAttachments() != null) {
            List<ManageAttachmentDto> attachmentDtoList = this.createAttachment(command.getAttachments(), income);
            income.setAttachments(attachmentDtoList);
            this.updateAttachmentStatusHistory(income, attachmentDtoList, employeeFullName);
        }

        if(command.getManual()){
            try {
                this.producerReplicateManageInvoiceService.create(income, null, null);
            } catch (Exception e) {
                Logger.getLogger(CreateIncomeCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating new Income", e);
            }
        }

    }

    private String setInvoiceNumber(ManageHotelDto hotel, String invoiceTypeCode) {
        if (hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()) {
            return invoiceTypeCode + "-" + hotel.getManageTradingCompanies().getCode();
        } else {
            return invoiceTypeCode + "-" + hotel.getCode();
        }
    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee) {

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("The income data was inserted.");
        dto.setEmployee(employee);
        dto.setInvoiceStatus(invoiceDto.getStatus());

        Instant before = Instant.now();
        this.invoiceStatusHistoryService.create(dto);
        Instant after = Instant.now();
        System.out.println("updateInvoiceStatusHistory: " + Duration.between(before, after).toMillis()+ " ms");
    }

    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, List<ManageAttachmentDto> attachments, String employeeFullName) {
        for (ManageAttachmentDto attachment : attachments) {
            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto
                    .setDescription("An attachment to the invoice was inserted. The file name: " + attachment.getFile());
            attachmentStatusHistoryDto.setEmployee(employeeFullName);
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

    private LocalDateTime invoiceDate(InvoiceCloseOperationDto closeOperationDto, LocalDateTime invoiceDate) {
        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate(), invoiceDate.toLocalDate())) {
            return invoiceDate;
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

    private InvoiceCloseOperationDto getHotelCloseOperation(UUID hotel){
        return this.closeOperationService.findActiveByHotelId(hotel);
    }

    private void createAdjustments(ManageInvoiceDto incomeDto,
                                   List<NewIncomeAdjustmentRequest> adjustmentRequests,
                                   String employeeFullName,
                                   InvoiceCloseOperationDto closeOperationDto){
        if(Objects.nonNull(adjustmentRequests) && !adjustmentRequests.isEmpty()){
            Double invoiceAmount = 0.0;
            List<ManageAdjustmentDto> adjustmentDtos = new ArrayList<>();
            Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeDtoMap = this.getPaymentTransactionTypeMap(adjustmentRequests);

            for (NewIncomeAdjustmentRequest adjustment : adjustmentRequests) {
                // Puede ser + y -, pero no puede ser 0
                RulesChecker.checkRule(new CheckAmountNotZeroRule(adjustment.getAmount()));
                RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(adjustment.getDate()));
                ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.getPaymentTransactionTypeFromMap(paymentTransactionTypeDtoMap, adjustment.getTransactionType());

                adjustmentDtos.add(new ManageAdjustmentDto(
                        UUID.randomUUID(),
                        0L,
                        adjustment.getAmount(),
                        invoiceDate(closeOperationDto, adjustment.getDate().atStartOfDay()),
                        adjustment.getRemark(),
                        null,
                        paymentTransactionTypeDto,
                        null,
                        employeeFullName,
                        false
                ));
                invoiceAmount += adjustment.getAmount();
            }
            invoiceAmount = BankerRounding.round(invoiceAmount);

            ManageRoomRateDto roomRateDto = this.generateNewManageRoomRate();
            roomRateDto.setInvoiceAmount(invoiceAmount);
            roomRateDto.setAdjustments(adjustmentDtos);

            List<ManageRoomRateDto> roomRates = new ArrayList<>();
            roomRates.add(roomRateDto);

            ManageBookingDto bookingDto = this.generateNewManageBooking(incomeDto, invoiceAmount, roomRates);

            incomeDto.setInvoiceAmount(invoiceAmount);
            incomeDto.setDueAmount(invoiceAmount);
            incomeDto.setOriginalAmount(invoiceAmount);
            incomeDto.setBookings(List.of(bookingDto));

            // this.manageAdjustmentService.create(adjustmentDtos);
        }
    }

    private Map<UUID, ManagePaymentTransactionTypeDto> getPaymentTransactionTypeMap(List<NewIncomeAdjustmentRequest> adjustmentRequests){
        List<UUID> ids = adjustmentRequests.stream()
                .map(NewIncomeAdjustmentRequest::getTransactionType)
                .filter(Objects::nonNull)
                .toList();

        return this.transactionTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ManagePaymentTransactionTypeDto::getId, transactionType -> transactionType));
    }

    private ManagePaymentTransactionTypeDto getPaymentTransactionTypeFromMap(Map<UUID, ManagePaymentTransactionTypeDto> transactionTypeDtoMap, UUID transactionTypeId){
        if(transactionTypeDtoMap.containsKey(transactionTypeId)){
            return transactionTypeDtoMap.get(transactionTypeId);
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    private ManageInvoiceDto generateNewManageInvoice(UUID id,
                                                      String invoiceNumber,
                                                      LocalDateTime invoiceDate,
                                                      LocalDate invoiceDueDate,
                                                      boolean manual,
                                                      ManageHotelDto hotelDto,
                                                      ManageAgencyDto agencyDto,
                                                      boolean isReSend,
                                                      LocalDate reSendDate,
                                                      ManageInvoiceTypeDto invoiceTypeDto,
                                                      ManageInvoiceStatusDto invoiceStatusDto){
        return  new ManageInvoiceDto(
                id,//command.getId(),
                0L,
                0L,
                invoiceNumber,
                InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME) + "-" + 0L,
                invoiceDate,//command.getInvoiceDate(),
                invoiceDueDate,//command.getDueDate(),
                manual,//command.getManual(),
                0.0,
                0.0,
                hotelDto,
                agencyDto,
                EInvoiceType.INCOME,
                EInvoiceStatus.SENT,
                Boolean.FALSE,
                null,
                null,
                isReSend,//command.getReSend(),
                reSendDate,//command.getReSendDate(),
                invoiceTypeDto,
                invoiceStatusDto,
                null,
                false,
                null, 0.0,0
        );
    }

    private ManageRoomRateDto generateNewManageRoomRate(){
        return new ManageRoomRateDto(
                UUID.randomUUID(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0.0,
                null,
                null,
                null,
                null,
                null,
                0.00,
                "",
                null,
                null,
                null,
                false,
                null
        );
    }

    private ManageBookingDto generateNewManageBooking(ManageInvoiceDto incomeDto, Double invoiceAmount, List<ManageRoomRateDto> roomRates){
        return new ManageBookingDto(
                UUID.randomUUID(),
                0L,
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                invoiceAmount,
                invoiceAmount,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0.00,
                null,
                null,//incomeDto,
                null,
                null,
                null,
                null,
                roomRates,
                null,
                null,
                null,
                false,
                null
        );
    }
}
