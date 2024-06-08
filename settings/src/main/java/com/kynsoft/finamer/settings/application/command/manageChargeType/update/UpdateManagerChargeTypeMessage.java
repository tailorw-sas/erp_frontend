package com.kynsoft.finamer.settings.application.command.manageChargeType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerChargeTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_CHARGE_TYPE";

    public UpdateManagerChargeTypeMessage(UUID id) {
        this.id = id;
    }

}
