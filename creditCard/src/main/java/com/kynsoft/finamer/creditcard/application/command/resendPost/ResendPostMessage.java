package com.kynsoft.finamer.creditcard.application.command.resendPost;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendPostMessage implements ICommandMessage {

    private final String command = "RESEND_POST_PAYMENT";
    private String result;

    public ResendPostMessage(String result) {
        this.result = result;
    }

}
