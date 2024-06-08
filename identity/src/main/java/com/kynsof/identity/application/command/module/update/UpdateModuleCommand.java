package com.kynsof.identity.application.command.module.update;

import com.kynsof.identity.application.command.module.create.CreateModuleRequest;
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

    public UpdateModuleCommand(UUID id, String name,  String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static UpdateModuleCommand fromRequest(CreateModuleRequest request, UUID id) {
        return new UpdateModuleCommand(
                id, 
                request.getName(),
                request.getDescription());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateModuleMessage(id);
    }
}
