package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageDepartmentGroupMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_DEPARTMENT_GROUP";

    public DeleteManageDepartmentGroupMessage(UUID id) {
        this.id = id;
    }
}
