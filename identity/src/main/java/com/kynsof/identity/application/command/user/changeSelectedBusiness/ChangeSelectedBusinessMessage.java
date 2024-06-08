package com.kynsof.identity.application.command.user.changeSelectedBusiness;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class ChangeSelectedBusinessMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "CHANGE_SELECTED_BUSINESS_MESSAGE";

    public ChangeSelectedBusinessMessage(Boolean result) {
        this.result = result;
    }

}
