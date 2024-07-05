package com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CloneManageEmployeeCommand implements ICommand {

    private UUID id;
    private List<UUID> targetEmployees;

    public static CloneManageEmployeeCommand fromRequest(CloneManageEmployeeRequest request, UUID id){
        return new CloneManageEmployeeCommand(
                id, request.getTargetEmployees()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CloneManageEmployeeMessage(id, targetEmployees);
    }
}
