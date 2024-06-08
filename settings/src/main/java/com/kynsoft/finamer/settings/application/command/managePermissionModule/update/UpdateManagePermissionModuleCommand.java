package com.kynsoft.finamer.settings.application.command.managePermissionModule.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePermissionModuleCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

    public UpdateManagePermissionModuleCommand(UUID id,  Status status, String name, String description, Boolean isActive) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public static UpdateManagePermissionModuleCommand fromRequest(UpdateManagePermissionModuleRequest request, UUID id){
        return new UpdateManagePermissionModuleCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getIsActive()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePermissionModuleMessage(id);
    }
}
