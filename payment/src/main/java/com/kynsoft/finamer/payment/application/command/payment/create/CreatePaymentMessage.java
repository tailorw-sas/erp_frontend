package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentMessage implements ICommandMessage {

    private PaymentResponse payment;

    public CreatePaymentMessage(PaymentDto payment) {
        this.payment = new PaymentResponse(payment);
    }

}
