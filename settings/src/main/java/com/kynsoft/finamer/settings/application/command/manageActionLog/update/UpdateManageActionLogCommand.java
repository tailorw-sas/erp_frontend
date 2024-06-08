package com.kynsoft.finamer.settings.application.command.manageActionLog.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageActionLogCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;

    public static UpdateManageActionLogCommand fromRequest(UpdateManageActionLogRequest request, UUID id){
        return new UpdateManageActionLogCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageActionLogMessage(id);
    }
}
