package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

@Data
public class CreateRedirectCommandMessage implements ICommandMessage {
    private final String result;

    public CreateRedirectCommandMessage(String result) {
        this.result = result;
    }
}
