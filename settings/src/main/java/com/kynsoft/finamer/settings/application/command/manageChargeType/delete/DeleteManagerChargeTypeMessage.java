package com.kynsoft.finamer.settings.application.command.manageChargeType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerChargeTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_CHARGE_TYPE";

    public DeleteManagerChargeTypeMessage(UUID id) {
        this.id = id;
    }

}
