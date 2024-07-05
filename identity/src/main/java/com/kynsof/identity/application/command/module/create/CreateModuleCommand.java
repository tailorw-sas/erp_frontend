package com.kynsof.identity.application.command.module.create;

import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
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
    private ModuleStatus status;
    private String code;

    public CreateModuleCommand(String name,  String description, ModuleStatus status, String code) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.status = status;
        this.code = code;
    }

    public static CreateModuleCommand fromRequest(CreateModuleRequest request) {
        return new CreateModuleCommand(
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getCode()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateModuleMessage(id);
    }
}
