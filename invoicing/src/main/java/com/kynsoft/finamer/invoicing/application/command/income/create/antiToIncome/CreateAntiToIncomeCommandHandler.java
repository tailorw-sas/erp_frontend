package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.NewIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.domain.core.IncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.core.ProcessCreateIncome;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IInvoiceCloseOperationService closeOperationService;

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
                                            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
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
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
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

        this.getManagerMaps(command.getCreateIncomeCommands(),
                closeOperationByHotelMap,
                agencyMap,
                hotelMap,
                invoiceTypeMap,
                invoiceStatusMap,
                employeeFullNameMap);

        List<ManageInvoiceDto> incomes = new ArrayList<>();

        for(CreateIncomeCommand incomeCommand : command.getCreateIncomeCommands()){
            InvoiceCloseOperationDto closeOperation = closeOperationByHotelMap.get(incomeCommand.getHotel());
            ManageAgencyDto agencyDto = agencyMap.get(incomeCommand.getAgency());
            ManageHotelDto hotelDto = hotelMap.get(incomeCommand.getHotel());
            ManageInvoiceTypeDto invoiceTypeDto = invoiceTypeMap.get(incomeCommand.getInvoiceType());
            ManageInvoiceStatusDto invoiceStatusDto = invoiceStatusMap.get(incomeCommand.getInvoiceStatus());
            String employeeFullName = employeeFullNameMap.get(UUID.fromString(incomeCommand.getEmployee()));
            List<IncomeAdjustment> incomeAdjustments = this.getIncomeAdjustmentList(incomeCommand.getAdjustments());

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
        }

        //this.manageInvoiceService.create()

        /*
        this.updateInvoiceStatusHistory(invoiceDto, employeeFullName);
        if (command.getAttachments() != null) {
            List<ManageAttachmentDto> attachmentDtoList = this.attachments(command.getAttachments(), invoiceDto);
            invoiceDto.setAttachments(attachmentDtoList);
            this.updateAttachmentStatusHistory(invoiceDto, attachmentDtoList, employeeFullName);
        }
*/
    }

    private void getManagerMaps(List<CreateIncomeCommand> createIncomeCommands,
                                Map<UUID, InvoiceCloseOperationDto> closeOperationByHotelMap,
                                Map<UUID, ManageAgencyDto> agencyMap,
                                Map<UUID, ManageHotelDto> hotelMap,
                                Map<UUID, ManageInvoiceTypeDto> invoiceTypeMap,
                                Map<UUID, ManageInvoiceStatusDto> invoiceStatusMap,
                                Map<UUID, String> employeeFullNameMap){
        Set<UUID> hotelSet = new HashSet<>();
        Set<UUID> agencySet = new HashSet<>();
        Set<UUID> invoiceTypeSet = new HashSet<>();
        Set<UUID> invoiceStatusSet = new HashSet<>();
        Set<UUID> employeeIdSet = new HashSet<>();

        for (CreateIncomeCommand command : createIncomeCommands){
            hotelSet.add(command.getHotel());
            agencySet.add(command.getAgency());
            invoiceTypeSet.add(command.getInvoiceType());
            invoiceStatusSet.add(command.getInvoiceStatus());
            employeeIdSet.add(UUID.fromString(command.getEmployee()));
        }

        closeOperationByHotelMap = this.getCloseOperationByHotelMap(new ArrayList<>(hotelSet));
        agencyMap = this.getManageAgencyMap(new ArrayList<>(agencySet));
        hotelMap = this.getManageHotelMap(new ArrayList<>(hotelSet));
        invoiceTypeMap = this.getManageInvoiceTypeMap(new ArrayList<>(invoiceTypeSet));
        invoiceStatusMap = this.getManageInvoiceStatusMap(new ArrayList<>(invoiceStatusSet));
        employeeFullNameMap = this.getEmployeeFullNameMap(new ArrayList<>(employeeIdSet));
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

    private List<IncomeAdjustment> getIncomeAdjustmentList(List<NewIncomeAdjustmentRequest> newIncomeAdjustmentRequests){
        List<UUID> paymentTransactionTypeIds = newIncomeAdjustmentRequests.stream()
                .map(NewIncomeAdjustmentRequest::getTransactionType).toList();
        Map<UUID, ManagePaymentTransactionTypeDto> paymentTransactionTypeDtoMap = this.getManagePaymentTransactionTypeMap(paymentTransactionTypeIds);

        return newIncomeAdjustmentRequests.stream()
                .map(newIncomeAdjustmentRequest -> {
                    ManagePaymentTransactionTypeDto paymentTransactionTypeDto = paymentTransactionTypeDtoMap.get(newIncomeAdjustmentRequest.getTransactionType());

                    return new IncomeAdjustment(paymentTransactionTypeDto,
                            newIncomeAdjustmentRequest.getAmount(),
                            newIncomeAdjustmentRequest.getDate(),
                            newIncomeAdjustmentRequest.getRemark());
                }).collect(Collectors.toList());
    }

    private Map<UUID, ManagePaymentTransactionTypeDto> getManagePaymentTransactionTypeMap(List<UUID> paymentTransactionTypeIds){
        return this.paymentTransactionTypeService.getMapById(paymentTransactionTypeIds);
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
