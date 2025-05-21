package com.kynsoft.finamer.payment.application.services.payment.create;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.core.attachment.ProcessCreateAttachment;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.core.payment.ProcessCreatePayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.services.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreatePaymentService {

    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentService paymentService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    public CreatePaymentService(IManagePaymentSourceService sourceService,
                                IManagePaymentStatusService statusService,
                                IManageClientService clientService,
                                IManageAgencyService agencyService,
                                IManageHotelService hotelService,
                                IManageBankAccountService bankAccountService,
                                IManagePaymentAttachmentStatusService attachmentStatusService,
                                IPaymentService paymentService,
                                IPaymentCloseOperationService paymentCloseOperationService,
                                IManageAttachmentTypeService manageAttachmentTypeService,
                                IManageResourceTypeService manageResourceTypeService,
                                IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                IManageEmployeeService manageEmployeeService,
                                IPaymentStatusHistoryService paymentStatusHistoryService,
                                IMasterPaymentAttachmentService masterPaymentAttachmentService){
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.bankAccountService = bankAccountService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentService = paymentService;
        this.hotelService = hotelService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
    }

    @Transactional
    public PaymentDto create(UUID paymentSourceId,
                             UUID paymentStatusId,
                             UUID hotelId,
                             UUID clientId,
                             UUID agencyId,
                             boolean isIgnoreBankAccount,
                             UUID bankAccountId,
                             Double amount,
                             String remark,
                             String reference,
                             UUID employeeId,
                             List<CreateAttachmentRequest> attachments){

        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findById(paymentSourceId);
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(paymentStatusId);
        ManageHotelDto hotelDto = this.hotelService.findById(hotelId);
        ManageClientDto clientDto = this.clientService.findById(clientId);
        ManageAgencyDto agencyDto = this.agencyService.findById(agencyId);
        PaymentCloseOperationDto closeOperationDto = this.getCloseOperacion(hotelId);
        OffsetDateTime transactionDate = this.getTransactionDate(closeOperationDto);
        ManageBankAccountDto bankAccountDto = this.getBankAccount(isIgnoreBankAccount, hotelId, paymentSourceDto, bankAccountId);
        ManagePaymentAttachmentStatusDto managePaymentAttachmentStatusDto = this.attachmentStatusService.findByNonNone();
        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(employeeId);
        List<CreateAttachment> createAttachmentList = this.getAttachments(attachments);
        ManagePaymentAttachmentStatusDto attachmentStatusSupported = this.attachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentStatusOtherSupported = this.attachmentStatusService.findByOtherSupported();

        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();
        List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList = new ArrayList<>();
        List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList = new ArrayList<>();

        ProcessCreatePayment processCreatePayment = new ProcessCreatePayment(paymentSourceDto,
                paymentStatusDto,
                transactionDate,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                managePaymentAttachmentStatusDto,
                amount,
                remark,
                reference,
                isIgnoreBankAccount,
                employeeDto,
                closeOperationDto,
                createAttachmentList,
                attachmentStatusSupported,
                attachmentStatusOtherSupported,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList,
                paymentStatusHistoryDto,
                ImportType.NONE,
                false);
        
        PaymentDto payment = processCreatePayment.create();

        this.saveChanges(payment,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList,
                paymentStatusHistoryDto);

        return payment;
    }

    private PaymentCloseOperationDto getCloseOperacion(UUID hotelId){
        return this.paymentCloseOperationService.findByHotelId(hotelId);
    }

    private OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperationDto) {
        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private ManageBankAccountDto getBankAccount(boolean isIgnoreBankAccount,
                                                UUID hotelId,
                                                ManagePaymentSourceDto paymentSourceDto,
                                                UUID bankAccountId){
        if (!isIgnoreBankAccount || !paymentSourceDto.getExpense()) {
            return this.bankAccountService.findById(bankAccountId);
        }

        return null;
    }

    private List<CreateAttachment> getAttachments(List<CreateAttachmentRequest> attachmentRequests){
        if(attachmentRequests == null || attachmentRequests.isEmpty()){
            return Collections.emptyList();
        }

        Set<UUID> resourceTypeIds = new HashSet<>();
        Set<UUID> attachmentTypeIds = new HashSet<>();

        for(CreateAttachmentRequest attachmentRequest : attachmentRequests){
            resourceTypeIds.add(attachmentRequest.getResourceType());
            attachmentTypeIds.add(attachmentRequest.getAttachmentType());
        }

        Map<UUID, ResourceTypeDto> resourceTypeDtoMap = this.getResourceTypeMap(new ArrayList<>(resourceTypeIds));
        Map<UUID, AttachmentTypeDto> attachmentTypeDtoMap = this.getAttachmentTypeMap(new ArrayList<>(attachmentTypeIds));

        return attachmentRequests.stream()
                .map(attachmentRequest -> {
                    return this.getCreateAttachment(attachmentRequest, attachmentTypeDtoMap.get(attachmentRequest.getAttachmentType()), resourceTypeDtoMap.get(attachmentRequest.getResourceType()));
                })
                .collect(Collectors.toList());
    }

    private Map<UUID, ResourceTypeDto> getResourceTypeMap(List<UUID> ids){
        return this.manageResourceTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ResourceTypeDto::getId, resourceTypeDto -> resourceTypeDto));
    }

    private Map<UUID, AttachmentTypeDto> getAttachmentTypeMap(List<UUID> ids){
        return this.manageAttachmentTypeService.findAllById(ids).stream()
                .collect(Collectors.toMap(AttachmentTypeDto::getId, attachmentTypeDto -> attachmentTypeDto));
    }

    private CreateAttachment getCreateAttachment(CreateAttachmentRequest request, AttachmentTypeDto attachmentTypeDto, ResourceTypeDto resourceTypeDto){
        return new CreateAttachment(attachmentTypeDto,
                resourceTypeDto,
                request.getFileName(),
                request.getFileWeight(),
                request.getPath(),
                request.getRemark(),
                request.isSupport());
    }

    private void saveChanges(PaymentDto paymentDto,
                             List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList,
                             List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList,
                             PaymentStatusHistoryDto paymentStatusHistoryDto){
        PaymentDto newPayment = this.paymentService.create(paymentDto);
        if(Objects.nonNull(masterPaymentAttachmentDtoList)){
            this.masterPaymentAttachmentService.create(masterPaymentAttachmentDtoList);
        }
        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDtoList);
        this.paymentStatusHistoryService.create(paymentStatusHistoryDto);

        paymentDto.setPaymentId(newPayment.getPaymentId());
    }
}
