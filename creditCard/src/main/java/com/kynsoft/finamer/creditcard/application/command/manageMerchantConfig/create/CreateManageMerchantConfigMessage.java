package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageMerchantConfigMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MERCHANT";

    public CreateManageMerchantConfigMessage(UUID id) {
        this.id = id;
    }

}
