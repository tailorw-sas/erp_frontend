package com.kynsoft.finamer.settings.application.command.manageActionLog.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageActionLogCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;

    public CreateManageActionLogCommand(String code, String description, Status status, String name) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
    }

    public static CreateManageActionLogCommand fromRequest(CreateManageActionLogRequest request){
        return new CreateManageActionLogCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageActionLogMessage(id);
    }
}
