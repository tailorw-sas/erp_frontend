package com.kynsoft.finamer.settings.application.command.manageChargeType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerChargeTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;

    public CreateManagerChargeTypeCommand(String code, String description, String name, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static CreateManagerChargeTypeCommand fromRequest(CreateManagerChargeTypeRequest request) {
        return new CreateManagerChargeTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerChargeTypeMessage(id);
    }
}
