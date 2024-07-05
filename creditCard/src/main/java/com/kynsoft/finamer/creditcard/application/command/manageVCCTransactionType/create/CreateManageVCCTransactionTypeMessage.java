package com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageVCCTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_VCC_TRANSACTION_TYPE";

    public CreateManageVCCTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
