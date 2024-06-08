package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateTemplateEntityMessage implements ICommandMessage {


    private final String command = "UPDATE_TEMPLATE";

    public UpdateTemplateEntityMessage() {

    }

}
