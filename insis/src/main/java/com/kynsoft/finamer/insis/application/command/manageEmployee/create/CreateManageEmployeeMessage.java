package com.kynsoft.finamer.insis.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageEmployeeMessage implements ICommandMessage {

    private final UUID id;
    public String command = "CREATE_MANAGE_EMPLOYEE_COMMAND";

    public CreateManageEmployeeMessage(UUID id){
        this.id = id;
    }
}
