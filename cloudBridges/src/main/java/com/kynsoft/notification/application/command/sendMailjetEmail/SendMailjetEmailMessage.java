package com.kynsoft.notification.application.command.sendMailjetEmail;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class SendMailjetEmailMessage implements ICommandMessage {

    private final boolean result;



    public SendMailjetEmailMessage(boolean result) {
        this.result = result;
    }

}
