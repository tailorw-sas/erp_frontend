package com.kynsof.identity.application.command.customer.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCustomerCommand implements ICommand {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public UpdateCustomerCommand(UUID id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.lastName = lastName;
    }

    public static UpdateCustomerCommand fromRequest(UpdateCustomerRequest request, UUID id) {
        return new UpdateCustomerCommand(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCustomerMessage(id);
    }
}
