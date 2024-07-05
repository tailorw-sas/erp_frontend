package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePaymentDetailMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PAYMENT_DETAIL";

    public CreatePaymentDetailMessage(UUID id) {
        this.id = id;
    }

}
