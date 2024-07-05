package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePaymentDetailMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PAYMENT_DETAIL";

    public DeletePaymentDetailMessage(UUID id) {
        this.id = id;
    }

}
