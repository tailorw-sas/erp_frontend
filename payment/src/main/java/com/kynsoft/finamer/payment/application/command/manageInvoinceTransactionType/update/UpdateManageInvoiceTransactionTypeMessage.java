package com.kynsoft.finamer.payment.application.command.manageInvoinceTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    public UpdateManageInvoiceTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
