package com.kynsoft.notification.application.command.mailjetConfiguration.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateMailjetConfigurationMessage implements ICommandMessage {


    private final String command = "UPDATE_MAILJET_CONFIG";

    public UpdateMailjetConfigurationMessage() {

    }

}
