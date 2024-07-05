package com.kynsoft.finamer.payment.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageEmployeeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_EMPLOYEE";

    public CreateManageEmployeeMessage(UUID id) {
        this.id = id;
    }

}
