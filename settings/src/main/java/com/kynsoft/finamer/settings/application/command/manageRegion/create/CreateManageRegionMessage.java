package com.kynsoft.finamer.settings.application.command.manageRegion.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRegionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_REGION";

    public CreateManageRegionMessage(UUID id) {
        this.id = id;
    }

}
