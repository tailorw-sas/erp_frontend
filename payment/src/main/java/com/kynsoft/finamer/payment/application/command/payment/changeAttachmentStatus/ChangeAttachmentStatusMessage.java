package com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAttachmentStatusMessage implements ICommandMessage {

    private PaymentResponse payment;
    public ChangeAttachmentStatusMessage(PaymentDto payment) {
        this.payment = new PaymentResponse(payment);
    }

}
