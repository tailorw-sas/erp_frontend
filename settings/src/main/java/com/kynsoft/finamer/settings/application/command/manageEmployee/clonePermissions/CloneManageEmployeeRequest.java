package com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CloneManageEmployeeRequest {
    private List<UUID> targetEmployees;
}
