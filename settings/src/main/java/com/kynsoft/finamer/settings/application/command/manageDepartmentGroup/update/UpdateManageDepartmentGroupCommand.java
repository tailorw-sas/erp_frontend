package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageDepartmentGroupCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;

    public UpdateManageDepartmentGroupCommand(UUID id,  Status status, String name, String description) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public static UpdateManageDepartmentGroupCommand fromRequest(UpdateManageDepartmentGroupRequest request, UUID id){
        return new UpdateManageDepartmentGroupCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageDepartmentGroupMessage(id);
    }
}
