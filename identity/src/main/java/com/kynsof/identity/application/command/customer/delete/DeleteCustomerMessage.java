package com.kynsof.identity.application.command.customer.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteCustomerMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_CUSTOMER";

    public DeleteCustomerMessage(UUID id) {
        this.id = id;
    }

}
