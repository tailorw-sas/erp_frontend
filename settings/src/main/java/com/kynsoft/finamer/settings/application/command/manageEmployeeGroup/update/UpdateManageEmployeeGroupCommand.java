package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageEmployeeGroupCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;

    public UpdateManageEmployeeGroupCommand(UUID id,  Status status, String name, String description) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public static UpdateManageEmployeeGroupCommand fromRequest(UpdateManageEmployeeGroupRequest request, UUID id){
        return new UpdateManageEmployeeGroupCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageEmployeeGroupMessage(id);
    }
}
