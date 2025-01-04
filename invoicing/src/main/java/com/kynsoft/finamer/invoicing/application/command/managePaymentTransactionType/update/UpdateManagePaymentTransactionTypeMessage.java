package com.kynsoft.finamer.invoicing.application.command.managePaymentTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePaymentTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_PAYMENT_TRANSACTION_TYPE";

    public UpdateManagePaymentTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
