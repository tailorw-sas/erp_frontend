package com.kynsoft.finamer.settings.application.command.manageNightType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageNightTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private Status status;
    private final String description;

    public UpdateManageNightTypeCommand(UUID id, String name, Status status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public static UpdateManageNightTypeCommand fromRequest(UpdateManageNightTypeRequest request, UUID id) {
        return new UpdateManageNightTypeCommand(
                id,
                request.getName(), 
                request.getStatus(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageNightTypeMessage(id);
    }
}
