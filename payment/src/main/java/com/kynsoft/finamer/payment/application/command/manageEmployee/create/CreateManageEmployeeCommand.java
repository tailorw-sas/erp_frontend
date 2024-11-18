package com.kynsoft.finamer.payment.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeResponse;
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

    public CreateManageEmployeeCommand(UUID id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static CreateManageEmployeeCommand fromRequest(ManageEmployeeResponse response) {
        return new CreateManageEmployeeCommand(response.getId(), response.getFirstName(), response.getLastName(), response.getEmail());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageEmployeeMessage(id);
    }
}
