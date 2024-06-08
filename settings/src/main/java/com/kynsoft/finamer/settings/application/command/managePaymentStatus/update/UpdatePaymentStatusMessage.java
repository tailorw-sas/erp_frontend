package com.kynsoft.finamer.settings.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePaymentStatusMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_MANAGE_PAYMENT_STATUS";
    
    public UpdatePaymentStatusMessage(final UUID id) {
        this.id = id;
    }
}
