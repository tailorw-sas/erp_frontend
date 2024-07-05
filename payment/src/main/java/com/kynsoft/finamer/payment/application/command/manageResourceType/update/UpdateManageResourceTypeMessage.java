package com.kynsoft.finamer.payment.application.command.manageResourceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageResourceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_RESOURCE_TYPE";

    public UpdateManageResourceTypeMessage(UUID id) {
        this.id = id;
    }

}
