package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePaymentTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_PAYMENT_TRANSACTION_STATUS";

    public UpdateManagePaymentTransactionStatusMessage(UUID id) {
        this.id = id;
    }
}
