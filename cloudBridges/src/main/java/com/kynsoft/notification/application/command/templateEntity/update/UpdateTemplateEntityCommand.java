package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.notification.domain.dto.EMailjetType;
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
    private String languageCode;
    private EMailjetType type;


    public UpdateTemplateEntityCommand(UUID id, String templateCode, String name, String description, UUID mailjetConfigId, String languageCode, EMailjetType type) {
        this.id = id;

        this.templateCode = templateCode;
        this.name = name;
        this.description = description;
        MailjetConfigId = mailjetConfigId;
        this.languageCode = languageCode;
        this.type = type;
    }

    public static UpdateTemplateEntityCommand fromRequest(UUID id, UpdateTemplateEntityRequest request) {
        return new UpdateTemplateEntityCommand(id, request.getTemplateCode(), request.getName(),
                request.getDescription(), request.getMailjetConfigId(), request.getLanguageCode(), request.getType());
    }


    @Override
    public ICommandMessage getMessage() {
        return new UpdateTemplateEntityMessage();
    }
}
