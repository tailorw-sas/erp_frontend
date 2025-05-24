package com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule extends BusinessRule {

    private final PaymentDto paymentDto;

    public MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(PaymentDto paymentDto) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.paymentDto = paymentDto;
    }

    @Override
    public boolean isBroken() {
        if(Objects.isNull(this.paymentDto.getAttachments()) || this.paymentDto.getAttachments().isEmpty()){
            return false;
        }

        return this.countDefaultAttachments(this.paymentDto.getAttachments()) > 0;
    }

    private long countDefaultAttachments(List<MasterPaymentAttachmentDto> attachmentDtoList){
        return attachmentDtoList.stream()
                .filter(masterPaymentAttachmentDto -> masterPaymentAttachmentDto.getAttachmentType() != null
                        && masterPaymentAttachmentDto.getAttachmentType().getDefaults())
                .count();
    }

}
