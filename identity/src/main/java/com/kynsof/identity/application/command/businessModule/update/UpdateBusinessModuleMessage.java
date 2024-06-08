package com.kynsof.identity.application.command.businessModule.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateBusinessModuleMessage implements ICommandMessage {

    private final boolean result;

    private final String command = "UPDATE_BUSINESS_MODULE";

    public UpdateBusinessModuleMessage(boolean result) {
        this.result = result;
    }

}
