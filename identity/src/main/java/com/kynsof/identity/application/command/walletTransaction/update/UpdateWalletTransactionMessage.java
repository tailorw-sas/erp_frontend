package com.kynsof.identity.application.command.walletTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateWalletTransactionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_BUSINESS";

    public UpdateWalletTransactionMessage(UUID id) {
        this.id = id;
    }

}
