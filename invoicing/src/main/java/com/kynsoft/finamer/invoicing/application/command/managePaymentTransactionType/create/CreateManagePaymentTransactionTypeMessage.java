package com.kynsoft.finamer.invoicing.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePaymentTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_PAYMENT_TRANSACTION_TYPE";

    public CreateManagePaymentTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
