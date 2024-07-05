package com.kynsoft.finamer.settings.application.command.manageMerchant.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerMerchantMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_MERCHANT";

    public DeleteManagerMerchantMessage(UUID id) {
        this.id = id;
    }

}
