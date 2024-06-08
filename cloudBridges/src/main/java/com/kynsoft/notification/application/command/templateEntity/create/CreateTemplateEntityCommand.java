package com.kynsoft.notification.application.command.templateEntity.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTemplateEntityCommand implements ICommand {
    private UUID id;
    private final String templateCode;
    private final String name;
    private final String description;
    private final UUID MailjetConfigId;


    public CreateTemplateEntityCommand(String templateCode, String name, String description, UUID mailjetConfigId) {
        this.templateCode = templateCode;
        this.name = name;
        this.description = description;
        MailjetConfigId = mailjetConfigId;
    }

    public static CreateTemplateEntityCommand fromRequest(CreateTemplateEntityRequest request) {
        return new CreateTemplateEntityCommand(request.getTemplateCode(), request.getName(),
                request.getDescription(), request.getMailjetConfigId());
    }


    @Override
    public ICommandMessage getMessage() {
        return new CreateTemplateEntityMessage(id);
    }
}
