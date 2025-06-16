package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.core.IncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.core.ProcessCreateIncome;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
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

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        Instant before = Instant.now();
        String employeeFullName = this.employeeService.getEmployeeFullName(command.getEmployee());
        Instant after = Instant.now();
        System.out.println("Get employee full name: " + Duration.between(before, after).toMillis() + " ms");

        List<IncomeAdjustment> incomeAdjustments = this.getIncomeAdjustmentList(command.getAdjustments());

        ProcessCreateIncome processCreateIncome = new ProcessCreateIncome(command.getId(),
                closeOperationDto,
                command.getInvoiceDate(),
                command.getDueDate(),
                command.getManual(),
                agencyDto,
                hotelDto,
                command.getReSend(),
                command.getReSendDate(),
                invoiceTypeDto,
                invoiceStatusDto,
                employeeFullName,
                incomeAdjustments
        );
        ManageInvoiceDto income = processCreateIncome.process();

        if (command.getAttachments() != null && !command.getAttachments().isEmpty()) {
            this.createAttachment(command.getAttachments(), income);
        }

        before = Instant.now();
        //income = this.manageInvoiceService.create(income);
        this.manageInvoiceService.insert(income);
        after = Instant.now();
        System.out.println("Insert invoice - booking: " + Duration.between(before, after).toMillis() + " ms");

        this.updateInvoiceStatusHistory(income, employeeFullName);
        this.attachmentService.create(income);
        this.updateAttachmentStatusHistory(income, income.getAttachments(), employeeFullName);

        if(command.getManual()){
            try {
                this.producerReplicateManageInvoiceService.create(income, null, null);
            } catch (Exception e) {
                Logger.getLogger(CreateIncomeCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating new Income", e);
            }
        }

        command.setInvoiceId(income.getInvoiceId());
        command.setInvoiceNo(income.getInvoiceNumber());
        command.setIncome(income);
    }

    private List<IncomeAdjustment> getIncomeAdjustmentList(List<CreateIncomeAdjustment> newIncomeAdjustmentRequests){
        Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeMap = this.getPaymentTransactionTypeMap(newIncomeAdjustmentRequests);
        return newIncomeAdjustmentRequests.stream()
                .map(newIncomeAdjustmentRequest -> {
                    ManagePaymentTransactionTypeDto paymentTransactionTypeDto = paymentTransactionTypeMap.get(newIncomeAdjustmentRequest.getTransactionType());

                    return new IncomeAdjustment(paymentTransactionTypeDto,
                            newIncomeAdjustmentRequest.getAmount(),
                            LocalDate.parse(newIncomeAdjustmentRequest.getDate(), formatter),
                            newIncomeAdjustmentRequest.getRemark());
                }).collect(Collectors.toList());
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
            attachmentStatusHistoryDto.setDescription("An attachment to the invoice was inserted. The file name: " + attachment.getFile());
            attachmentStatusHistoryDto.setEmployee(employeeFullName);
            attachmentStatusHistoryDto.setInvoice(invoice);
            attachmentStatusHistoryDto.setEmployeeId(attachment.getEmployeeId());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());
            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
        }
    }

    private List<ManageAttachmentDto> createAttachment(List<CreateIncomeAttachmentRequest> attachments, ManageInvoiceDto invoiceDto) {
        Set<UUID> attachmentTypeIdSet = new HashSet<>();
        Set<UUID> resourceTypeIdSet = new HashSet<>();
        for(CreateIncomeAttachmentRequest attachmentRequest : attachments){
            if(attachmentRequest.getType() != null) attachmentTypeIdSet.add(attachmentRequest.getType());
            if(attachmentRequest.getPaymentResourceType() != null) resourceTypeIdSet.add(attachmentRequest.getPaymentResourceType());
        }

        Map<UUID, ManageAttachmentTypeDto> attachmentTypeDtoMap = this.getAttachmentTypeMap(new ArrayList<>(attachmentTypeIdSet));
        Map<UUID, ResourceTypeDto> resourceTypeDtoMap = this.getResourceTypeMap(new ArrayList<>(resourceTypeIdSet));

        List<ManageAttachmentDto> attachmentDtos = new ArrayList<>();
        int countDefaults = 0;
        for (CreateIncomeAttachmentRequest attachment : attachments) {
            ManageAttachmentTypeDto attachmentType = attachment.getType() != null
                    ? attachmentTypeDtoMap.get(attachment.getType())
                    : null;
            ResourceTypeDto resourceTypeDto = attachment.getPaymentResourceType() != null
                    ? resourceTypeDtoMap.get(attachment.getPaymentResourceType())
                    : null;

            attachmentDtos.add(new ManageAttachmentDto(
                    UUID.randomUUID(),
                    null,
                    attachment.getFilename(),
                    attachment.getFile(),
                    attachment.getRemark(),
                    attachmentType,
                    null,
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
        invoiceDto.setAttachments(attachmentDtos);

        if (countDefaults > 1) {
            throw new BusinessException(DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT,
                    DomainErrorMessage.INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase());
        }
        return attachmentDtos;
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

    private Map<UUID, ManagePaymentTransactionTypeDto> getPaymentTransactionTypeMap(List<CreateIncomeAdjustment> adjustmentRequests){
        List<UUID> ids = adjustmentRequests.stream()
                .map(CreateIncomeAdjustment::getTransactionType)
                .filter(Objects::nonNull)
                .toList();

        return this.transactionTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ManagePaymentTransactionTypeDto::getId, transactionType -> transactionType));
    }

    private Map<UUID, ManageAttachmentTypeDto> getAttachmentTypeMap(List<UUID> attachmentTypeIds){
        return this.attachmentTypeService.getMapById(attachmentTypeIds);
    }

    private Map<UUID, ResourceTypeDto> getResourceTypeMap(List<UUID> resourceTypeIds){
                return this.resourceTypeService.getMapById(resourceTypeIds);
    }

    private ManagePaymentTransactionTypeDto getPaymentTransactionTypeFromMap(Map<UUID, ManagePaymentTransactionTypeDto> transactionTypeDtoMap, UUID transactionTypeId){
        if(transactionTypeDtoMap.containsKey(transactionTypeId)){
            return transactionTypeDtoMap.get(transactionTypeId);
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND.getReasonPhrase())));
    }
}
