package com.kynsoft.finamer.creditcard.application.command.resourceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageResourceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private boolean vcc;
    private Status status;
    private boolean defaults;
    private String description;

    public static CreateManageResourceTypeCommand fromRequest(CreateManageResourceTypeRequest request, UUID id) {
        return new CreateManageResourceTypeCommand(
                id, 
                request.getCode(), 
                request.getName(), 
                request.isVcc(), 
                request.getStatus(), 
                request.isDefaults(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageResourceTypeMessage(id);
    }
}
