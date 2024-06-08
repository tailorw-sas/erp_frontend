package com.kynsoft.finamer.settings.application.command.managerTimeZone.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerTimeZoneCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Double elapse;
    private Status status;

    public CreateManagerTimeZoneCommand(String code, String description, String name, Double elapse, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.elapse = elapse;
        this.status = status;
    }

    public static CreateManagerTimeZoneCommand fromRequest(CreateManagerTimeZoneRequest request) {
        return new CreateManagerTimeZoneCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getElapse(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerTimeZoneMessage(id);
    }
}
