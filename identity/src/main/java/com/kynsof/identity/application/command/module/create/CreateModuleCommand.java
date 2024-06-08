package com.kynsof.identity.application.command.module.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateModuleCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;

    public CreateModuleCommand(String name,  String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
    }

    public static CreateModuleCommand fromRequest(CreateModuleRequest request) {
        return new CreateModuleCommand(
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateModuleMessage(id);
    }
}
