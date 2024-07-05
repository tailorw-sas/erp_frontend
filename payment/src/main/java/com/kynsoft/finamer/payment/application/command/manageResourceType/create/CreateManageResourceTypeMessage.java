package com.kynsoft.finamer.payment.application.command.manageResourceType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageResourceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_RESOURCE_TYPE";

    public CreateManageResourceTypeMessage(UUID id) {
        this.id = id;
    }
}
