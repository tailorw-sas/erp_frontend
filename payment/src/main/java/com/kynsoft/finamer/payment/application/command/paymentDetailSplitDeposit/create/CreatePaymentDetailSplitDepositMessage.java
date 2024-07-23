package com.kynsoft.finamer.payment.application.command.paymentDetailSplitDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;

@Getter
public class CreatePaymentDetailSplitDepositMessage implements ICommandMessage {

    private final PaymentDto payment;

    public CreatePaymentDetailSplitDepositMessage(PaymentDto payment) {
        this.payment = payment;
    }

}
