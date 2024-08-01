package com.kynsoft.finamer.settings.application.command.manageClient.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageClientCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean isNightType;

    public CreateManageClientCommand(String code, String description, String name, Status status, Boolean isNightType) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
        this.isNightType = isNightType;
    }

    public static CreateManageClientCommand fromRequest(CreateManageClientRequest request) {
        return new CreateManageClientCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getStatus(),
                request.getIsNightType()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageClientMessage(id);
    }
}
