package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.core.IncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.core.ProcessCreateIncome;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    private final IManageAttachmentService attachmentService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageEmployeeService employeeService;

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IInvoiceCloseOperationService closeOperationService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CreateAntiToIncomeCommandHandler(IManageAgencyService agencyService,
                                            IManageHotelService hotelService,
                                            IManageInvoiceTypeService invoiceTypeService,
                                            IManageInvoiceStatusService invoiceStatusService,
                                            IManageInvoiceService manageInvoiceService,
                                            IManageAttachmentTypeService attachmentTypeService,
                                            IManageResourceTypeService resourceTypeService,
                                            IInvoiceStatusHistoryService invoiceStatusHistoryService,
                                            IManageAttachmentService attachmentService,
                                            IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                            IManageEmployeeService employeeService,
                                            IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                            IInvoiceCloseOperationService closeOperationService) {
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.invoiceTypeService = invoiceTypeService;
        this.invoiceStatusService = invoiceStatusService;
        this.manageInvoiceService = manageInvoiceService;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.attachmentService = attachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.employeeService = employeeService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(CreateAntiToIncomeCommand command) {

        Map<UUID, InvoiceCloseOperationDto> closeOperationByHotelMap = new HashMap<>();
        Map<UUID, ManageAgencyDto> agencyMap = new HashMap<>();
        Map<UUID, ManageHotelDto> hotelMap = new HashMap<>();
        Map<UUID, ManageInvoiceTypeDto> invoiceTypeMap = new HashMap<>();
        Map<UUID, ManageInvoiceStatusDto> invoiceStatusMap = new HashMap<>();
        Map<UUID, String> employeeFullNameMap = new HashMap<>();
        Map<UUID, String> employeeFullNameMapByIncome = new HashMap<>();
        Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeMap = new HashMap<>();
        ManageAttachmentTypeDto defaultAttachmentTypeDto = this.attachmentTypeService.findAttachInvDefault().orElse(null);
        ResourceTypeDto defaultResourceTypeDto = this.resourceTypeService.findByDefaults();

        Instant before = Instant.now();
        this.getManagerMaps(command.getCreateIncomeCommands(),
                closeOperationByHotelMap,
                agencyMap,
                hotelMap,
                invoiceTypeMap,
                invoiceStatusMap,
                employeeFullNameMap,
                paymentTransactionTypeMap);
        Instant after = Instant.now();
        System.out.println("********************* -> Get Maps: " + Duration.between(before, after).toMillis() + " ms");

        List<ManageInvoiceDto> incomes = new ArrayList<>();

        before = Instant.now();
        for(CreateIncomeCommand incomeCommand : command.getCreateIncomeCommands()){
            InvoiceCloseOperationDto closeOperation = closeOperationByHotelMap.get(incomeCommand.getHotel());
            ManageAgencyDto agencyDto = agencyMap.get(incomeCommand.getAgency());
            ManageHotelDto hotelDto = hotelMap.get(incomeCommand.getHotel());
            ManageInvoiceTypeDto invoiceTypeDto = invoiceTypeMap.get(incomeCommand.getInvoiceType());
            ManageInvoiceStatusDto invoiceStatusDto = invoiceStatusMap.get(incomeCommand.getInvoiceStatus());
            String employeeFullName = employeeFullNameMap.get(UUID.fromString(incomeCommand.getEmployee()));
            List<IncomeAdjustment> incomeAdjustments = this.getIncomeAdjustmentList(incomeCommand.getAdjustments(), paymentTransactionTypeMap);

            ProcessCreateIncome processCreateIncome = new ProcessCreateIncome(incomeCommand.getId(),
                    closeOperation,
                    incomeCommand.getInvoiceDate(),
                    incomeCommand.getDueDate(),
                    incomeCommand.getManual(),
                    agencyDto,
                    hotelDto,
                    incomeCommand.getReSend(),
                    incomeCommand.getReSendDate(),
                    invoiceTypeDto,
                    invoiceStatusDto,
                    employeeFullName,
                    incomeAdjustments);

            ManageInvoiceDto income = processCreateIncome.process();

            incomes.add(income);
            employeeFullNameMapByIncome.put(income.getId(), employeeFullName);

            if (incomeCommand.getAttachments() != null && !incomeCommand.getAttachments().isEmpty()) {
                List<ManageAttachmentDto> attachmentDtoList = this.createManageAttachments(incomeCommand.getAttachments(),
                        defaultAttachmentTypeDto,
                        defaultResourceTypeDto);
                income.setAttachments(attachmentDtoList);
            }
        }
        after = Instant.now();
        System.out.println("********************* -> Process Incomes: " + Duration.between(before, after).toMillis() + " ms");

        for(ManageInvoiceDto income : incomes){
            before = Instant.now();
            this.manageInvoiceService.insert(income);
            after = Instant.now();
            System.out.println("********************* -> Saving Income: " + Duration.between(before, after).toMillis() + " ms");

            before = Instant.now();
            String employeeFullName = employeeFullNameMapByIncome.get(income.getId());
            this.updateInvoiceStatusHistory(income, employeeFullName);
            after = Instant.now();
            System.out.println("********************* -> Updating InvoiceStatusHistory: " + Duration.between(before, after).toMillis() + " ms");

            before = Instant.now();
            this.attachmentService.create(income);
            after = Instant.now();
            System.out.println("********************* -> Creating attachments: " + Duration.between(before, after).toMillis() + " ms");

            before = Instant.now();
            this.updateAttachmentStatusHistory(income, income.getAttachments(), employeeFullName);
            after = Instant.now();
            System.out.println("********************* -> Updating attachment status history: " + Duration.between(before, after).toMillis() + " ms");
        }

        before = Instant.now();
        Map<UUID, ManageInvoiceDto> invoiceDtoMap = incomes.stream()
                .collect(Collectors.toMap(ManageInvoiceDto::getId, income -> income));

        command.getCreateIncomeCommands().forEach(createIncomeCommand -> {
            ManageInvoiceDto income = invoiceDtoMap.get(createIncomeCommand.getId());
            createIncomeCommand.setInvoiceId(income.getInvoiceId());
            createIncomeCommand.setInvoiceNo(income.getInvoiceNo().toString());
            createIncomeCommand.setIncome(income);
        });
        after = Instant.now();
        System.out.println("********************* -> Preparing response: " + Duration.between(before, after).toMillis() + " ms");
    }

    private void getManagerMaps(List<CreateIncomeCommand> createIncomeCommands,
                                Map<UUID, InvoiceCloseOperationDto> closeOperationByHotelMap,
                                Map<UUID, ManageAgencyDto> agencyMap,
                                Map<UUID, ManageHotelDto> hotelMap,
                                Map<UUID, ManageInvoiceTypeDto> invoiceTypeMap,
                                Map<UUID, ManageInvoiceStatusDto> invoiceStatusMap,
                                Map<UUID, String> employeeFullNameMap,
                                Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeMap){
        Set<UUID> hotelSet = new HashSet<>();
        Set<UUID> agencySet = new HashSet<>();
        Set<UUID> invoiceTypeSet = new HashSet<>();
        Set<UUID> invoiceStatusSet = new HashSet<>();
        Set<UUID> employeeIdSet = new HashSet<>();
        Set<UUID> paymentTransactionTypeSet = new HashSet<>();

        for (CreateIncomeCommand command : createIncomeCommands){
            hotelSet.add(command.getHotel());
            agencySet.add(command.getAgency());
            invoiceTypeSet.add(command.getInvoiceType());
            invoiceStatusSet.add(command.getInvoiceStatus());
            employeeIdSet.add(UUID.fromString(command.getEmployee()));
            paymentTransactionTypeSet.addAll(command.getAdjustments().stream().map(CreateIncomeAdjustment::getTransactionType).toList());
        }

        closeOperationByHotelMap.putAll(this.getCloseOperationByHotelMap(new ArrayList<>(hotelSet)));
        agencyMap.putAll(this.getManageAgencyMap(new ArrayList<>(agencySet)));
        hotelMap.putAll(this.getManageHotelMap(new ArrayList<>(hotelSet)));
        invoiceTypeMap.putAll(this.getManageInvoiceTypeMap(new ArrayList<>(invoiceTypeSet)));
        invoiceStatusMap.putAll(this.getManageInvoiceStatusMap(new ArrayList<>(invoiceStatusSet)));
        employeeFullNameMap.putAll(this.getEmployeeFullNameMap(new ArrayList<>(employeeIdSet)));
        paymentTransactionTypeMap.putAll(this.getManagePaymentTransactionTypeMap(new ArrayList<>(paymentTransactionTypeSet)));
    }

    private Map<UUID, InvoiceCloseOperationDto> getCloseOperationByHotelMap(List<UUID> hotelIds){
        if(Objects.isNull(hotelIds) || hotelIds.isEmpty()){
            throw new IllegalArgumentException("The hotel ID list is null or empty for the search of close operations");
        }
        return this.closeOperationService.findByHotelIds(hotelIds).stream()
                .collect(Collectors.toMap(closeOperation -> closeOperation.getHotel().getId(),
                        closeOperation -> closeOperation));
    }

    private Map<UUID, ManageAgencyDto> getManageAgencyMap(List<UUID> agencyIds){
        return this.agencyService.getMapById(agencyIds);
    }

    private Map<UUID, ManageHotelDto> getManageHotelMap(List<UUID> hotelIds){
        return this.hotelService.getMapById(hotelIds);
    }

    private Map<UUID, ManageInvoiceTypeDto> getManageInvoiceTypeMap(List<UUID> invoiceTypeIds){
        return this.invoiceTypeService.getMapById(invoiceTypeIds);
    }

    private Map<UUID, ManageInvoiceStatusDto> getManageInvoiceStatusMap(List<UUID> invoiceStatusIds){
        return this.invoiceStatusService.getMapById(invoiceStatusIds);
    }

    private Map<UUID, String> getEmployeeFullNameMap(List<UUID> employeeIds){
        return this.employeeService.getEmployeeFullNameMapByIds(employeeIds);
    }

    private Map<UUID, ManagePaymentTransactionTypeDto> getManagePaymentTransactionTypeMap(List<UUID> paymentTransactionTypeIds){
        return this.paymentTransactionTypeService.getMapById(paymentTransactionTypeIds);
    }

    private List<IncomeAdjustment> getIncomeAdjustmentList(List<CreateIncomeAdjustment> newIncomeAdjustmentRequests, Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeMap){
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

        this.invoiceStatusHistoryService.create(dto);

    }

    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, List<ManageAttachmentDto> attachments, String employeeFullName) {
        for (ManageAttachmentDto attachment : attachments) {
            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto.setDescription("An attachment to the invoice was inserted. The file name: " + attachment.getFile());
            attachmentStatusHistoryDto.setEmployee(employeeFullName);
            invoice.setAttachments(null);
            attachmentStatusHistoryDto.setInvoice(invoice);
            attachmentStatusHistoryDto.setEmployeeId(attachment.getEmployeeId());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());
            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
        }
    }

    private List<ManageAttachmentDto> createManageAttachments(List<CreateIncomeAttachmentRequest> attachments,
                                                              ManageAttachmentTypeDto defaultAttachmentTypeDto,
                                                              ResourceTypeDto defaultResourceTypeDto) {
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }
        String defaultFilename = "detail.pdf";
        String defaultRemark = "From payment.";

        return attachments.stream().map(attachment -> {
            return this.convertCreateIncomeAttachmentRequestToManageAttachmentDto(attachment, defaultAttachmentTypeDto, defaultResourceTypeDto, defaultFilename, defaultRemark);
        }).collect(Collectors.toList());
    }

    private ManageAttachmentDto convertCreateIncomeAttachmentRequestToManageAttachmentDto(CreateIncomeAttachmentRequest incomeAttachmentRequest,
                                                                                          ManageAttachmentTypeDto defaultAttachmentTypeDto,
                                                                                          ResourceTypeDto defaultResourceTypeDto,
                                                                                          String defaultFilename, String defaultRemark){
        return new ManageAttachmentDto(
                UUID.randomUUID(),
                null,
                incomeAttachmentRequest.getFilename() != null ? incomeAttachmentRequest.getFilename() : defaultFilename,
                incomeAttachmentRequest.getFile(),
                incomeAttachmentRequest.getRemark() != null ? incomeAttachmentRequest.getRemark() : defaultRemark,
                defaultAttachmentTypeDto,
                null,
                incomeAttachmentRequest.getEmployee(),
                incomeAttachmentRequest.getEmployeeId(),
                null,
                defaultResourceTypeDto,
                false
        );
    }
}