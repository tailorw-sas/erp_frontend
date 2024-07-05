package com.kynsoft.finamer.settings.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMerchantMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_MERCHANT";

    public CreateManagerMerchantMessage(UUID id) {
        this.id = id;
    }

}
