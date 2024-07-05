package com.kynsoft.finamer.payment.application.command.manageResourceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageResourceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;

    public CreateManageResourceTypeCommand(String code, String description, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public static CreateManageResourceTypeCommand fromRequest(CreateManageResourceTypeRequest request) {
        return new CreateManageResourceTypeCommand(
               request.getCode(),  request.getDescription(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageResourceTypeMessage(id);
    }
}
