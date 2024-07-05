package com.kynsoft.finamer.payment.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePaymentStatusMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_MANAGE_PAYMENT_STATUS";
    
    public UpdateManagePaymentStatusMessage(final UUID id) {
        this.id = id;
    }
}
