package com.kynsoft.finamer.creditcard.application.command.sendMail;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMailMessage implements ICommandMessage {

    private final String command = "MAIL SENDING";
    private boolean result;

    public SendMailMessage( boolean result) {
        this.result = result;
    }

}
