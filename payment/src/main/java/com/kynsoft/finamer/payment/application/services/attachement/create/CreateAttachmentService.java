package com.kynsoft.finamer.payment.application.services.attachement.create;

import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.core.attachment.ProcessCreateAttachment;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreateAttachmentService {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    public CreateAttachmentService(IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                   IManageAttachmentTypeService manageAttachmentTypeService,
                                   IManageResourceTypeService manageResourceTypeService){
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
    }

    public MasterPaymentAttachmentDto create(PaymentDto payment,
                                             CreateAttachmentRequest attachmentRequest){
        CreateAttachment createAttachment = this.getCreateAttachment(attachmentRequest);

        ProcessCreateAttachment processCreateAttachment = new ProcessCreateAttachment(payment, createAttachment);
        MasterPaymentAttachmentDto masterPaymentAttachmentDto = processCreateAttachment.create();

        this.saveChanges(masterPaymentAttachmentDto);

        return masterPaymentAttachmentDto;
    }

    public List<MasterPaymentAttachmentDto> createMany(PaymentDto payment,
                                                       List<CreateAttachmentRequest> attachmentRequests){
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
                    ProcessCreateAttachment processCreateAttachment = new ProcessCreateAttachment(payment,
                            this.getCreateAttachment(request, attachmentTypeDtoMap.get(request.getAttachmentType()), resourceTypeDtoMap.get(request.getResourceType())));
                    return processCreateAttachment.create();
                })
                .collect(Collectors.toList());

        this.saveChanges(masterPaymentAttachmentDtoList);

        return masterPaymentAttachmentDtoList;
    }

    private Map<UUID, ResourceTypeDto> getResourceTypeMap(List<UUID> ids){
        return this.manageResourceTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ResourceTypeDto::getId, resourceTypeDto -> resourceTypeDto));
    }

    private Map<UUID, AttachmentTypeDto> getAttachmentTypeMap(List<UUID> ids){
        return this.manageAttachmentTypeService.findAllById(ids).stream()
                .collect(Collectors.toMap(AttachmentTypeDto::getId, attachmentTypeDto -> attachmentTypeDto));
    }

    private CreateAttachment getCreateAttachment(CreateAttachmentRequest request){
        AttachmentTypeDto attachmentTypeDto = this.manageAttachmentTypeService.findById(request.getAttachmentType());
        ResourceTypeDto resourceTypeDto = this.manageResourceTypeService.findById(request.getResourceType());
        return new CreateAttachment(attachmentTypeDto,
                resourceTypeDto,
                request.getFileName(),
                request.getFileWeight(),
                request.getPath(),
                request.getRemark(),
                request.isSupport());
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

    private void saveChanges(MasterPaymentAttachmentDto masterPaymentAttachment){
        Long attachmentId = this.masterPaymentAttachmentService.create(masterPaymentAttachment);
        masterPaymentAttachment.setAttachmentId(attachmentId);
    }

    private void saveChanges(List<MasterPaymentAttachmentDto> masterPaymentAttachments){
        this.masterPaymentAttachmentService.create(masterPaymentAttachments);
    }

}
