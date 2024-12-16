package com.kynsoft.finamer.creditcard.application.command.manageMerchant.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageMerchantMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_MERCHANT";

    public UpdateManageMerchantMessage(UUID id) {
        this.id = id;
    }

}
