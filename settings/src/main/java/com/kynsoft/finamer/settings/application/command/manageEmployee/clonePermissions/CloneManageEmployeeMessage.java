package com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CloneManageEmployeeMessage implements ICommandMessage {

    private final UUID sourceEmployee;
    private final List<UUID> targetEmployees;

    private final String command = "CLONE_MANAGE_EMPLOYEE";

    public CloneManageEmployeeMessage(UUID sourceEmployee, List<UUID> targetEmployees) {
        this.sourceEmployee = sourceEmployee;
        this.targetEmployees = targetEmployees;
    }
}
