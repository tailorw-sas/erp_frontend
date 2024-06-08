package com.kynsoft.notification.application.command.templateEntity.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTemplateEntityMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_TEMPLATE";

    public CreateTemplateEntityMessage(UUID id) {
        this.id = id;
    }

}
