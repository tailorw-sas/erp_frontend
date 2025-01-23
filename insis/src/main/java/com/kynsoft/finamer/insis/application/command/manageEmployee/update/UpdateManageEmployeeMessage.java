package com.kynsoft.finamer.insis.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageEmployeeMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_MANAGE_EMPLOYEE_COMMAND";

    public UpdateManageEmployeeMessage(UUID id){
        this.id = id;
    }
}
