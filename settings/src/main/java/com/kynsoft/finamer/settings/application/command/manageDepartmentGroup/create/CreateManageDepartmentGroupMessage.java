package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageDepartmentGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_DEPARTMENT_GROUP";

    public CreateManageDepartmentGroupMessage(UUID id) {
        this.id = id;
    }
}
