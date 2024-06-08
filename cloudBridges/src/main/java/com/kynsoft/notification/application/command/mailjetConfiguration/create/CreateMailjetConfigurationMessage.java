package com.kynsoft.notification.application.command.mailjetConfiguration.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateMailjetConfigurationMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MAILJET_CONFIG";

    public CreateMailjetConfigurationMessage(UUID id) {
        this.id = id;
    }

}
