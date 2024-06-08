package com.kynsoft.finamer.settings.application.command.manageEmployee.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageEmployeeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_EMPLOYEE";

    public DeleteManageEmployeeMessage(UUID id) {
        this.id = id;
    }
}
