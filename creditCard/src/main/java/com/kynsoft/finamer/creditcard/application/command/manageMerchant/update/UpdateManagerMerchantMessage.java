package com.kynsoft.finamer.creditcard.application.command.manageMerchant.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerMerchantMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_MERCHANT";

    public UpdateManagerMerchantMessage(UUID id) {
        this.id = id;
    }

}
