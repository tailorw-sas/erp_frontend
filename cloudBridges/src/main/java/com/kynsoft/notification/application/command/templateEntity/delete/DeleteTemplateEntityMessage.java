package com.kynsoft.notification.application.command.templateEntity.delete;



import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTemplateEntityMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MAILJET_CONFIG";

    public DeleteTemplateEntityMessage(UUID id) {
        this.id = id;
    }

}
