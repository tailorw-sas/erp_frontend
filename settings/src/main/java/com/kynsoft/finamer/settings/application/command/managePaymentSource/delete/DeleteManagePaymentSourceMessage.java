package com.kynsoft.finamer.settings.application.command.managePaymentSource.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManagePaymentSourceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PAYMENT_SOURCE";

    public DeleteManagePaymentSourceMessage(UUID id) {
        this.id = id;
    }
}
