package com.kynsoft.finamer.payment.domain.core.attachment;

import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProcessCreateAttachment {

    private final PaymentDto payment;
    private final CreateAttachment createAttachment;

    public ProcessCreateAttachment(PaymentDto payment,
                                   CreateAttachment createAttachment){
        this.payment = payment;
        this.createAttachment = createAttachment;
    }

    public MasterPaymentAttachmentDto create(){
        MasterPaymentAttachmentDto masterPaymentAttachment = this.getMasterPaymentAttachment(this.payment, this.createAttachment);
        this.updatePayment(this.payment, masterPaymentAttachment);

        return masterPaymentAttachment;
        //createAttachment.isSupport() ? attachmentTypeSupport : attachmentTypeOtherDto,
    }

    private MasterPaymentAttachmentDto getMasterPaymentAttachment(PaymentDto payment,
                                                                  CreateAttachment createAttachment){
        return new MasterPaymentAttachmentDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                createAttachment.getResourceTypeDto(),
                createAttachment.getAttachmentTypeDto(),
                createAttachment.getFileName(),
                createAttachment.getFileWeight(),
                createAttachment.getPath(),
                createAttachment.getRemark(),
                0L
        );
    }

    private void updatePayment(PaymentDto payment, MasterPaymentAttachmentDto masterPaymentAttachmentDto){
        List<MasterPaymentAttachmentDto> newMasterPaymentAttachmentList = new ArrayList<>();
        if(Objects.nonNull(payment.getAttachments())){
            newMasterPaymentAttachmentList.addAll(payment.getAttachments());
        }
        newMasterPaymentAttachmentList.add(masterPaymentAttachmentDto);
        payment.setAttachments(newMasterPaymentAttachmentList);
        payment.setHasAttachment(true);
    }
}
