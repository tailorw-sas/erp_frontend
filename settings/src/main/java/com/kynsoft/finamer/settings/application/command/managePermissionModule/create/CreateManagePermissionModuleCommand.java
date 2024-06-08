package com.kynsoft.finamer.settings.application.command.managePermissionModule.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePermissionModuleCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

    public CreateManagePermissionModuleCommand(String code, Status status, String name, String description, Boolean isActive) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public static CreateManagePermissionModuleCommand fromRequest(
            CreateManagePermissionModuleRequest request) {
        return new CreateManagePermissionModuleCommand(
                request.getCode(),

                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getIsActive()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePermissionModuleMessage(id);
    }
}
