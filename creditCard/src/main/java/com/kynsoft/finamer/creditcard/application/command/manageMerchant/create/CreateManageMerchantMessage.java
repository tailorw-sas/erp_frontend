package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageMerchantMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MERCHANT";

    public CreateManageMerchantMessage(UUID id) {
        this.id = id;
    }

}
