package com.kynsof.identity.application.command.businessModule.deleteall;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteAllBusinessModuleMessage implements ICommandMessage {

    private final boolean result;

    private final String command = "DELETE_BUSINESS_MODULE";

    public DeleteAllBusinessModuleMessage(boolean result) {
        this.result = result;
    }

}
