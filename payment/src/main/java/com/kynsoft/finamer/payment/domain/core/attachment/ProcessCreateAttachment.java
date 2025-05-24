package com.kynsoft.finamer.payment.domain.core.attachment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProcessCreateAttachment {

    private final PaymentDto payment;
    private final ManageEmployeeDto employee;
    private final AttachmentTypeDto attachmentTypeDto;
    private final ResourceTypeDto resourceTypeDto;
    private final String fileName;
    private final String fileWeight;
    private final String path;
    private final String remark;
    private final ManagePaymentAttachmentStatusDto attachmentStatusSupport;
    private final ManagePaymentAttachmentStatusDto attachmentOtherSupport;
    private final AttachmentStatusHistoryDto attachmentStatusHistoryDto;

    public ProcessCreateAttachment(PaymentDto payment,
                                   ManageEmployeeDto employee,
                                   AttachmentTypeDto attachmentTypeDto,
                                   ResourceTypeDto resourceTypeDto,
                                   String fileName,
                                   String fileWeight,
                                   String path,
                                   String remark,
                                   ManagePaymentAttachmentStatusDto attachmentStatusSupport,
                                   ManagePaymentAttachmentStatusDto attachmentOtherSupport,
                                   AttachmentStatusHistoryDto attachmentStatusHistoryDto){
        this.payment = payment;
        this.employee = employee;
        this.attachmentTypeDto = attachmentTypeDto;
        this.resourceTypeDto = resourceTypeDto;
        this.fileName = fileName;
        this.fileWeight = fileWeight;
        this.path = path;
        this.remark = remark;
        this.attachmentStatusSupport = attachmentStatusSupport;
        this.attachmentOtherSupport = attachmentOtherSupport;
        this.attachmentStatusHistoryDto = attachmentStatusHistoryDto;
    }

    public MasterPaymentAttachmentDto create(){
        boolean paymentSupport = false;
        if(attachmentTypeDto.getDefaults()){
            RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(this.payment));
            paymentSupport = true;
        }

        MasterPaymentAttachmentDto masterPaymentAttachment = this.getMasterPaymentAttachment(this.payment,
                this.attachmentTypeDto,
                this.resourceTypeDto,
                this.fileName,
                this.fileWeight,
                this.path,
                this.remark);

        this.updatePayment(this.payment,
                masterPaymentAttachment,
                paymentSupport,
                this.attachmentStatusSupport,
                this.attachmentOtherSupport);

        String statusHistory = this.getStatusHistoryDescription(payment.getAttachmentStatus());

        this.updateAttachmentStatusHistory(this.payment,
                this.employee,
                this.fileName,
                0L,
                statusHistory,
                this.attachmentStatusHistoryDto);

        return masterPaymentAttachment;
    }

    private MasterPaymentAttachmentDto getMasterPaymentAttachment(PaymentDto payment,
                                                                  AttachmentTypeDto attachmentTypeDto,
                                                                  ResourceTypeDto resourceTypeDto,
                                                                  String fileName,
                                                                  String fileWeight,
                                                                  String path,
                                                                  String remark){
        return new MasterPaymentAttachmentDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                resourceTypeDto,
                attachmentTypeDto,
                fileName,
                fileWeight,
                path,
                remark,
                0L
        );
    }

    private void updatePayment(PaymentDto payment,
                               MasterPaymentAttachmentDto masterPaymentAttachmentDto,
                               boolean paymentSupport,
                               ManagePaymentAttachmentStatusDto attachmentStatusSupport,
                               ManagePaymentAttachmentStatusDto attachmentOtherSupport){
        List<MasterPaymentAttachmentDto> newMasterPaymentAttachmentList = new ArrayList<>();

        if(Objects.nonNull(payment.getAttachments())){
            newMasterPaymentAttachmentList.addAll(payment.getAttachments());
        }
        newMasterPaymentAttachmentList.add(masterPaymentAttachmentDto);
        payment.setAttachments(newMasterPaymentAttachmentList);
        payment.setHasAttachment(true);

        if (paymentSupport) {
            payment.setPaymentSupport(true);
            payment.setAttachmentStatus(attachmentStatusSupport);
        } else {
            payment.setAttachmentStatus(attachmentOtherSupport);
        }
    }

    private String getStatusHistoryDescription(ManagePaymentAttachmentStatusDto paymentAttachmentStatusDto){
        return  paymentAttachmentStatusDto.getCode() + "-" + paymentAttachmentStatusDto.getName();
    }

    private void updateAttachmentStatusHistory(PaymentDto payment,
                                               ManageEmployeeDto employeeDto,
                                               String fileName,
                                               Long attachmentId,
                                               String statusHistory,
                                               AttachmentStatusHistoryDto attachmentStatusHistoryDto) {
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(statusHistory);
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);
    }
}