package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageEmployeeGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_EMPLOYEE_GROUP";

    public UpdateManageEmployeeGroupMessage(UUID id) {
        this.id = id;
    }
}
