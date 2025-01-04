package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePaymentTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_PAYMENT_TRANSACTION_STATUS";

    public CreateManagePaymentTransactionStatusMessage(UUID id) {
        this.id = id;
    }
}
