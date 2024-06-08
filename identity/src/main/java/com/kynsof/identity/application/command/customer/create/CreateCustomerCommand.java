package com.kynsof.identity.application.command.customer.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCustomerCommand implements ICommand {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public CreateCustomerCommand(String firstName, String lastName, String email) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.email = email;
        this.lastName = lastName;
    }

    public static CreateCustomerCommand fromRequest(CreateCustomerRequest request) {
        return new CreateCustomerCommand(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCustomerMessage(id);
    }
}
