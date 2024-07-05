package com.kynsoft.finamer.settings.application.command.manageAgencyType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private final String description;

    public CreateManageAgencyTypeCommand(String code, Status status, String name, String description) {
        this.description = description;
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
    }

    public static CreateManageAgencyTypeCommand fromRequest(CreateManageAgencyTypeRequest request){
        return new CreateManageAgencyTypeCommand(
                request.getCode(), request.getStatus(), request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyTypeMessage(id);
    }
}
