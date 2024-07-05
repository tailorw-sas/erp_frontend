package com.kynsoft.finamer.payment.application.command.paymentDetail.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePaymentDetailMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PAYMENT";

    public UpdatePaymentDetailMessage(UUID id) {
        this.id = id;
    }

}
