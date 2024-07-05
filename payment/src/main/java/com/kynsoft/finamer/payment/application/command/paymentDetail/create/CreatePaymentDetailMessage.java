package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDetailMessage implements ICommandMessage {

    private PaymentDto payment;

    public CreatePaymentDetailMessage(PaymentDto payment) {
        this.payment = payment;
    }

}
