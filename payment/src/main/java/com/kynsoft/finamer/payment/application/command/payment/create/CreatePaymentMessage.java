package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentMessage implements ICommandMessage {

    private PaymentDto payment;

    public CreatePaymentMessage(PaymentDto payment) {
        this.payment = payment;
    }

}
