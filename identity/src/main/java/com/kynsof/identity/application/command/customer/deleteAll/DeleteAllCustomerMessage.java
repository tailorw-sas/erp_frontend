package com.kynsof.identity.application.command.customer.deleteAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteAllCustomerMessage implements ICommandMessage {

    private final String command = "DELETE";

    public DeleteAllCustomerMessage() {}

}
