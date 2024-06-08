package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateTemplateEntityCommand implements ICommand {
    private UUID id;
    private final String templateCode;
    private final String name;
    private final String description;
    private final UUID MailjetConfigId;


    public UpdateTemplateEntityCommand(UUID id, String templateCode, String name, String description, UUID mailjetConfigId) {
        this.id = id;

        this.templateCode = templateCode;
        this.name = name;
        this.description = description;
        MailjetConfigId = mailjetConfigId;
    }

    public static UpdateTemplateEntityCommand fromRequest(UUID id, UpdateTemplateEntityRequest request) {
        return new UpdateTemplateEntityCommand(id, request.getTemplateCode(), request.getName(),
                request.getDescription(), request.getMailjetConfigId());
    }


    @Override
    public ICommandMessage getMessage() {
        return new UpdateTemplateEntityMessage();
    }
}
