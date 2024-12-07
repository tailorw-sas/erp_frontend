package com.kynsoft.finamer.invoicing.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageEmployeeCommand implements ICommand {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneExtension;

    public CreateManageEmployeeCommand(UUID id, String firstName, String lastName, String email, String phoneExtension) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneExtension = phoneExtension;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageEmployeeMessage(id);
    }
}
