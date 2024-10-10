package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyPaymentDetailMessage implements ICommandMessage {

    private PaymentDto payment;

    public ApplyPaymentDetailMessage(PaymentDto payment) {
        this.payment = payment;
    }

}
