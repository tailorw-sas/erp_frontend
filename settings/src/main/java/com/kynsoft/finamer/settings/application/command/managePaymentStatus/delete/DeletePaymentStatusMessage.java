package com.kynsoft.finamer.settings.application.command.managePaymentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePaymentStatusMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_PAYMENT_STATUS";

    public DeletePaymentStatusMessage(final UUID id) {
        this.id = id;
    }
}
