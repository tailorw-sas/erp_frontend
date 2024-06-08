package com.kynsoft.notification.application.command.mailjetConfiguration.delete;



import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteMailjetConfigurationMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MAILJET_CONFIG";

    public DeleteMailjetConfigurationMessage(UUID id) {
        this.id = id;
    }

}
