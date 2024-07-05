package com.kynsof.identity.application.command.module.update;

import com.kynsof.identity.application.command.module.create.CreateModuleRequest;
import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateModuleCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private ModuleStatus status;

    public UpdateModuleCommand(UUID id, String name,  String description, ModuleStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public static UpdateModuleCommand fromRequest(CreateModuleRequest request, UUID id) {
        return new UpdateModuleCommand(
                id, 
                request.getName(),
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateModuleMessage(id);
    }
}
