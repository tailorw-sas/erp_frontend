package com.kynsoft.finamer.payment.application.command.manageClient.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageClientMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_BANK";

    public CreateManageClientMessage(UUID id) {
        this.id = id;
    }

}
