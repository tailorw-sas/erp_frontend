package com.kynsoft.finamer.payment.application.command.managePaymentSource.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePaymentSourceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_PAYMENT_SOURCE";

    public UpdateManagePaymentSourceMessage(UUID id) {
        this.id = id;
    }
}
