package com.kynsoft.finamer.creditcard.application.command.manageB2BPartnerType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageB2BPartnerTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Status status;

    public UpdateManageB2BPartnerTypeCommand(UUID id, String description, String name, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static UpdateManageB2BPartnerTypeCommand fromRequest(UpdateManageB2BPartnerTypeRequest request, UUID id) {
        return new UpdateManageB2BPartnerTypeCommand(
                id,
                request.getDescription(),
                request.getName(), 
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageB2BPartnerTypeMessage(id);
    }
}
