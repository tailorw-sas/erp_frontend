package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.other;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateRoomRateOtherMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_ROOMRATE";

    public CreateRoomRateOtherMessage(UUID id) {
        this.id = id;
    }

}
