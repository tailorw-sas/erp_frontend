package com.kynsoft.finamer.payment.application.services.attachement.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.core.attachment.ProcessUpdateAttachment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateAttachmentService {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService manageEmployeeService;
    private final IManageResourceTypeService manageResourceTypeService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;


    public UpdateAttachmentService(IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                   IPaymentService paymentService,
                                   IManageEmployeeService manageEmployeeService,
                                   IManageResourceTypeService manageResourceTypeService,
                                   IManageAttachmentTypeService manageAttachmentTypeService,
                                   IAttachmentStatusHistoryService attachmentStatusHistoryService){
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }


    @Transactional
    public void update(UUID id,
                       Status status,
                       UUID employeeId,
                       UUID resourceTypeId,
                       UUID attachmentTypeId,
                       String fileName,
                       String fileWeight,
                       String path,
                       String remark){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(id, "id", "Master Payment Attachment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(employeeId, "id", "Employee ID cannot be null."));

        MasterPaymentAttachmentDto masterPaymentAttachmentDto = this.masterPaymentAttachmentService.findById(id);
        PaymentDto payment = masterPaymentAttachmentDto.getResource();
        ManageEmployeeDto employee = this.manageEmployeeService.findById(employeeId);
        ResourceTypeDto resourceTypeDto = this.manageResourceTypeService.findById(resourceTypeId);
        AttachmentTypeDto attachmentTypeDto = this.manageAttachmentTypeService.findById(attachmentTypeId);
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();

        ProcessUpdateAttachment processUpdateAttachment = new ProcessUpdateAttachment(payment, employee, masterPaymentAttachmentDto, attachmentStatusHistoryDto);
        processUpdateAttachment.update(status,
                resourceTypeDto,
                attachmentTypeDto,
                fileName,
                fileWeight,
                path,
                remark);

        this.saveChanges(payment,
                masterPaymentAttachmentDto,
                attachmentStatusHistoryDto);
    }

    private void saveChanges(PaymentDto paymentDto,
                             MasterPaymentAttachmentDto masterPaymentAttachmentDto,
                             AttachmentStatusHistoryDto attachmentStatusHistoryDto){
        this.paymentService.update(paymentDto);
        this.masterPaymentAttachmentService.update(masterPaymentAttachmentDto);
        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
