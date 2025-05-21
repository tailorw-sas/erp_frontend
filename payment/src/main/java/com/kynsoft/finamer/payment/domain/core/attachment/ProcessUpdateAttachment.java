package com.kynsoft.finamer.payment.domain.core.attachment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule;
import com.kynsoft.finamer.payment.infrastructure.identity.MasterPaymentAttachment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ProcessUpdateAttachment {

    private final PaymentDto payment;
    private final ManageEmployeeDto employee;
    private final MasterPaymentAttachmentDto masterPaymentAttachmentDto;
    private final AttachmentStatusHistoryDto attachmentStatusHistoryDto;

    public ProcessUpdateAttachment(PaymentDto payment,
                                   ManageEmployeeDto employee,
                                   MasterPaymentAttachmentDto masterPaymentAttachmentDto,
                                   AttachmentStatusHistoryDto attachmentStatusHistoryDto) {
        this.payment = payment;
        this.employee = employee;
        this.masterPaymentAttachmentDto = masterPaymentAttachmentDto;
        this.attachmentStatusHistoryDto = attachmentStatusHistoryDto;
    }

    public void update(Status status,
                       ResourceTypeDto resourceType,
                       AttachmentTypeDto attachmentType,
                       String fileName,
                       String fileWeight,
                       String path,
                       String remark) {
        this.updateMasterPaymentAttachment(this.payment,
                this.masterPaymentAttachmentDto,
                status,
                resourceType,
                attachmentType,
                fileName,
                fileWeight,
                path,
                remark);

        this.createAttachmentStatusHistory(this.payment,
                this.employee,
                fileName,
                this.masterPaymentAttachmentDto.getAttachmentId(),
                this.attachmentStatusHistoryDto);

        this.updatePayment(this.payment, this.masterPaymentAttachmentDto);
    }

    private void updateMasterPaymentAttachment(PaymentDto payment,
                                               MasterPaymentAttachmentDto masterPaymentAttachmentDto,
                                               Status status,
                                               ResourceTypeDto resourceType,
                                               AttachmentTypeDto attachmentType,
                                               String fileName,
                                               String fileWeight,
                                               String path,
                                               String remark) {
        ConsumerUpdate update = new ConsumerUpdate();
        masterPaymentAttachmentDto.setRemark(remark);
        masterPaymentAttachmentDto.setFileName(fileName);
        masterPaymentAttachmentDto.setFileWeight(fileWeight);
        masterPaymentAttachmentDto.setStatus(status);
        masterPaymentAttachmentDto.setResourceType(resourceType);
        masterPaymentAttachmentDto.setAttachmentType(attachmentType);
        masterPaymentAttachmentDto.setStatus(status);
        masterPaymentAttachmentDto.setResourceType(resourceType);
        this.updateAttachmentType(masterPaymentAttachmentDto::setAttachmentType, attachmentType, payment, update::setUpdate);
    }

    private void updateAttachmentType(Consumer<AttachmentTypeDto> setter, AttachmentTypeDto newAttachmentType, PaymentDto payment, Consumer<Integer> update) {
        if (newAttachmentType != null) {
            if (newAttachmentType.getDefaults()) {
                RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(payment));
            }
            setter.accept(newAttachmentType);
            update.accept(1);
        }
    }

    private void createAttachmentStatusHistory(PaymentDto payment,
                                               ManageEmployeeDto employeeDto,
                                               String fileName,
                                               Long attachmentId,
                                               AttachmentStatusHistoryDto attachmentStatusHistoryDto) {
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was update. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        attachmentStatusHistoryDto.setUpdatedAt(LocalDateTime.now());
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);
    }

    private void updatePayment(PaymentDto payment, MasterPaymentAttachmentDto masterPaymentAttachmentDto){
        if (this.countDefaultAttachments(payment.getAttachments()) == 0) {
            payment.setPaymentSupport(false);
        }
    }

    private long countDefaultAttachments(List<MasterPaymentAttachmentDto> attachmentDtoList){
        return attachmentDtoList.stream()
                .filter(masterPaymentAttachmentDto -> masterPaymentAttachmentDto.getAttachmentType() != null
                        && masterPaymentAttachmentDto.getAttachmentType().getDefaults())
                .count();
    }
}
