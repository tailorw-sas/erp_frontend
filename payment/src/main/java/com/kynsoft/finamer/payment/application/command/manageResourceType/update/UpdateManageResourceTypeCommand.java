package com.kynsoft.finamer.payment.application.command.manageResourceType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageResourceTypeCommand implements ICommand {
    private UUID id;
    private String description;
    private Status status;

    public UpdateManageResourceTypeCommand(UUID id, String description, Status status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public static UpdateManageResourceTypeCommand fromRequest(UpdateManageResourceTypeRequest request, UUID id) {
        return new UpdateManageResourceTypeCommand(
                id,
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageResourceTypeMessage(id);
    }
}
