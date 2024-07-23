package com.kynsoft.finamer.payment.application.command.manageInvoinceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    public CreateManageInvoiceTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
