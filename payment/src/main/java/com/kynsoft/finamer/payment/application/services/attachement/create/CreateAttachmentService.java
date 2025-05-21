package com.kynsoft.finamer.payment.application.services.attachement.create;

import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.core.attachment.ProcessCreateAttachment;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreateAttachmentService {

    private final IPaymentService paymentService;
    private final IManageEmployeeService manageEmployeeService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public CreateAttachmentService(IPaymentService paymentService,
                                   IManageEmployeeService manageEmployeeService,
                                   IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                   IManageAttachmentTypeService manageAttachmentTypeService,
                                   IManageResourceTypeService manageResourceTypeService,
                                   IManagePaymentAttachmentStatusService attachmentStatusService,
                                   IAttachmentStatusHistoryService attachmentStatusHistoryService){
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.attachmentStatusService = attachmentStatusService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    public MasterPaymentAttachmentDto create(UUID paymentId,
                                             CreateAttachmentRequest attachmentRequest){
        PaymentDto payment = this.paymentService.findByIdCustom(paymentId);
        ManageEmployeeDto employee = this.manageEmployeeService.findById(attachmentRequest.getEmployee());
        AttachmentTypeDto attachmentTypeDto = this.manageAttachmentTypeService.findById(attachmentRequest.getAttachmentType());
        ResourceTypeDto resourceTypeDto = this.manageResourceTypeService.findById(attachmentRequest.getResourceType());
        ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.attachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.attachmentStatusService.findByOtherSupported();
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();

        ProcessCreateAttachment processCreateAttachment = new ProcessCreateAttachment(payment,
                employee,
                attachmentTypeDto,
                resourceTypeDto,
                attachmentRequest.getFileName(),
                attachmentRequest.getFileWeight(),
                attachmentRequest.getPath(),
                attachmentRequest.getRemark(),
                attachmentStatusSupport,
                attachmentOtherSupport,
                attachmentStatusHistoryDto);
        MasterPaymentAttachmentDto masterPaymentAttachmentDto = processCreateAttachment.create();

        this.saveChanges(masterPaymentAttachmentDto,
                payment,
                attachmentStatusHistoryDto);

        return masterPaymentAttachmentDto;
    }

    public List<MasterPaymentAttachmentDto> createMany(UUID paymentId,
                                                       List<CreateAttachmentRequest> attachmentRequests){
        PaymentDto payment = this.paymentService.findByIdCustom(paymentId);
        ManageEmployeeDto employee = this.getEmployeeMap(attachmentRequests);
        ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.attachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.attachmentStatusService.findByOtherSupported();
        List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList = new ArrayList<>();

        if(attachmentRequests == null || attachmentRequests.isEmpty()){
            return Collections.emptyList();
        }

        Set<UUID> resourceTypeIds = new HashSet<>();
        Set<UUID> attachmentTypeIds = new HashSet<>();

        for(CreateAttachmentRequest attachmentRequest : attachmentRequests){
            resourceTypeIds.add(attachmentRequest.getResourceType());
            attachmentTypeIds.add(attachmentRequest.getAttachmentType());
        }

        Map<UUID, AttachmentTypeDto> attachmentTypeDtoMap = this.getAttachmentTypeMap(new ArrayList<>(attachmentTypeIds));
        Map<UUID, ResourceTypeDto> resourceTypeDtoMap = this.getResourceTypeMap(new ArrayList<>(resourceTypeIds));

        List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList = attachmentRequests.stream()
                .map(request -> {
                    AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
                    ProcessCreateAttachment processCreateAttachment = new ProcessCreateAttachment(payment,
                            employee,
                            attachmentTypeDtoMap.get(request.getAttachmentType()),
                            resourceTypeDtoMap.get(request.getResourceType()),
                            request.getFileName(),
                            request.getFileWeight(),
                            request.getPath(),
                            request.getRemark(),
                            attachmentStatusSupport,
                            attachmentOtherSupport,
                            attachmentStatusHistoryDto);
                    MasterPaymentAttachmentDto masterPaymentAttachmentDto = processCreateAttachment.create();
                    attachmentStatusHistoryDtoList.add(attachmentStatusHistoryDto);
                    return masterPaymentAttachmentDto;
                })
                .collect(Collectors.toList());

        this.saveChanges(payment,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList);

        return masterPaymentAttachmentDtoList;
    }

    private ManageEmployeeDto getEmployeeMap(List<CreateAttachmentRequest> attachmentRequests){
        if(Objects.isNull(attachmentRequests) || !attachmentRequests.isEmpty()){
            return null;
        }
        return this.manageEmployeeService.findById(attachmentRequests.get(0).getEmployee());
    }

    private Map<UUID, ResourceTypeDto> getResourceTypeMap(List<UUID> ids){
        return this.manageResourceTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ResourceTypeDto::getId, resourceTypeDto -> resourceTypeDto));
    }

    private Map<UUID, AttachmentTypeDto> getAttachmentTypeMap(List<UUID> ids){
        return this.manageAttachmentTypeService.findAllById(ids).stream()
                .collect(Collectors.toMap(AttachmentTypeDto::getId, attachmentTypeDto -> attachmentTypeDto));
    }

    private void saveChanges(MasterPaymentAttachmentDto masterPaymentAttachment,
                             PaymentDto paymentDto,
                             AttachmentStatusHistoryDto attachmentStatusHistoryDto){
        Long attachmentId = this.masterPaymentAttachmentService.create(masterPaymentAttachment);
        masterPaymentAttachment.setAttachmentId(attachmentId);
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.paymentService.update(paymentDto);
        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private void saveChanges(PaymentDto paymentDto,
                             List<MasterPaymentAttachmentDto> masterPaymentAttachments,
                             List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList){
        this.masterPaymentAttachmentService.create(masterPaymentAttachments);
        this.paymentService.update(paymentDto);

        Map<String, Long> attachmentIdMap = masterPaymentAttachments.stream()
                .collect(Collectors.toMap(MasterPaymentAttachmentDto::getFileName, MasterPaymentAttachmentDto::getAttachmentId));

        attachmentStatusHistoryDtoList.forEach(attachmentStatusHistoryDto ->
                attachmentStatusHistoryDto.setAttachmentId(attachmentIdMap.get(getFileNameFromDescription(attachmentStatusHistoryDto.getDescription()))));

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDtoList);
    }

    private String getFileNameFromDescription(String description){
        String keyword = "The file name: ";
        int startIndex = description.indexOf(keyword) + keyword.length();
        return description.substring(startIndex);
    }
}
