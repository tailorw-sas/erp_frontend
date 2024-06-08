package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageVCCTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_VCC_TRANSACTION_TYPE";

    public DeleteManageVCCTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
