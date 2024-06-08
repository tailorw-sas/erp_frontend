package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManagePaymentTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PAYMENT_TRANSACTION_TYPE";

    public DeleteManagePaymentTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
