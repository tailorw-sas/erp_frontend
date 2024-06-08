package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageEmployeeGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_EMPLOYEE_GROUP";

    public CreateManageEmployeeGroupMessage(UUID id) {
        this.id = id;
    }
}
