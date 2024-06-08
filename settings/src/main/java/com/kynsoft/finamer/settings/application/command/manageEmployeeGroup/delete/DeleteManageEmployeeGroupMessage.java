package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageEmployeeGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_EMPLOYEE_GROUP";

    public DeleteManageEmployeeGroupMessage(UUID id) {
        this.id = id;
    }
}
