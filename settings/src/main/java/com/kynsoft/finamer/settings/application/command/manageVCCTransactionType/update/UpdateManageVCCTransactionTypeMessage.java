package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageVCCTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_VCC_TRANSACTION_TYPE";

    public UpdateManageVCCTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
