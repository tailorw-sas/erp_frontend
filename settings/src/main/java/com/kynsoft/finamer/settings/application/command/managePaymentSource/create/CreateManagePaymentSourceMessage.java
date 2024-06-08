package com.kynsoft.finamer.settings.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePaymentSourceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_PAYMENT_SOURCE";

    public CreateManagePaymentSourceMessage(UUID id) {
        this.id = id;
    }
}
