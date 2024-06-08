package com.kynsof.identity.application.command.user.deleteAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteAllUserSystemsMessage implements ICommandMessage {

    private final String command = "DELETE";

    public DeleteAllUserSystemsMessage() {}

}
