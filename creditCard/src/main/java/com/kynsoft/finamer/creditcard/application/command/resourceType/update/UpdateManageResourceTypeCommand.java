package com.kynsoft.finamer.creditcard.application.command.resourceType.update;

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
public class UpdateManageResourceTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private boolean vcc;
    private Status status;
    private boolean defaults;
    private String description;

    public static UpdateManageResourceTypeCommand fromRequest(UpdateManageResourceTypeRequest request, UUID id) {
        return new UpdateManageResourceTypeCommand(
                id,
                request.getName(),
                request.isVcc(),
                request.getStatus(),
                request.isDefaults(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageResourceTypeMessage(id);
    }
}
