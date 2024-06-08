package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageDepartmentGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_DEPARTMENT_GROUP";

    public UpdateManageDepartmentGroupMessage(UUID id) {
        this.id = id;
    }
}
