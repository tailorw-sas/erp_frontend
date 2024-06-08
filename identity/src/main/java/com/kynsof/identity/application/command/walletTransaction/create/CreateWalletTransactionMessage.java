package com.kynsof.identity.application.command.walletTransaction.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateWalletTransactionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_BUSINESS";

    public CreateWalletTransactionMessage(UUID id) {
        this.id = id;
    }

}
