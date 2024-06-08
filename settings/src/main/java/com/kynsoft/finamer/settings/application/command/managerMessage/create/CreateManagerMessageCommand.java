package com.kynsoft.finamer.settings.application.command.managerMessage.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMessageCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private String type;
    private UUID language;

    public CreateManagerMessageCommand(String code, String description, Status status, String name, String type, UUID language) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.type = type;
        this.language = language;
    }

    public static CreateManagerMessageCommand fromRequest(CreateManagerMessageRequest request){
        return new CreateManagerMessageCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getType(),
                request.getLanguage()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerMessageMessage(id);
    }
}
