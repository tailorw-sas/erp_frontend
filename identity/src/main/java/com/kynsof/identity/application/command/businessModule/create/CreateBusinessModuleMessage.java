package com.kynsof.identity.application.command.businessModule.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateBusinessModuleMessage implements ICommandMessage {

    private final boolean result;

    private final String command = "CREATE_BUSINESS_MODULE";

    public CreateBusinessModuleMessage(boolean result) {
        this.result = result;
    }

}
