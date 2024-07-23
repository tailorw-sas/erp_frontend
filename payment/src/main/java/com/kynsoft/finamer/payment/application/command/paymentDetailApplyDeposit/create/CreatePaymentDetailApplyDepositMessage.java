package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;

@Getter
public class CreatePaymentDetailApplyDepositMessage implements ICommandMessage {

    private final PaymentDto payment;

    public CreatePaymentDetailApplyDepositMessage(PaymentDto payment) {
        this.payment = payment;
    }

}
