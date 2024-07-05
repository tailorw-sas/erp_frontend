package com.kynsoft.finamer.payment.application.command.managePaymentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePaymentStatusMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_PAYMENT_STATUS";
    
    public CreateManagePaymentStatusMessage(final UUID id) {
        this.id = id;
    }
}
