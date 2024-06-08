package com.kynsof.identity.application.command.customer.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCustomerMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_CUSTOMER";

    public UpdateCustomerMessage(UUID id) {
        this.id = id;
    }

}
