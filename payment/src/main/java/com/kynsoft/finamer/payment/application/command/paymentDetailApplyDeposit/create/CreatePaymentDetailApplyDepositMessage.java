package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePaymentDetailApplyDepositMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PAYMENT_DETAIL";

    public CreatePaymentDetailApplyDepositMessage(UUID id) {
        this.id = id;
    }

}
