package com.kynsoft.finamer.settings.application.command.manageChargeType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerChargeTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_CHARGE_TYPE";

    public CreateManagerChargeTypeMessage(UUID id) {
        this.id = id;
    }

}
