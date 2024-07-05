package com.kynsoft.finamer.settings.application.command.manageNightType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageNightTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private final String description;

    public CreateManageNightTypeCommand(String code, String name, Status status, String description) {
        this.description = description;
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public static CreateManageNightTypeCommand fromRequest(CreateManageNightTypeRequest request) {
        return new CreateManageNightTypeCommand(
                request.getCode(),
                request.getName(),
                request.getStatus(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageNightTypeMessage(id);
    }
}
