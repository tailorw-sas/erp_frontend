package com.kynsoft.finamer.settings.application.command.managerTimeZone.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerTimeZoneCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Double elapse;
    private Status status;

    public UpdateManagerTimeZoneCommand(UUID id, String description, String name, Double elapse, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.elapse = elapse;
        this.status = status;
    }

    public static UpdateManagerTimeZoneCommand fromRequest(UpdateManagerTimeZoneRequest request, UUID id) {
        return new UpdateManagerTimeZoneCommand(
                id,
                request.getDescription(),
                request.getName(),
                request.getElapse(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerTimeZoneMessage(id);
    }
}
