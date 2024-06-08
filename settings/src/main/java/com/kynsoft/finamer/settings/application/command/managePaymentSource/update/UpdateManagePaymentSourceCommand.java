package com.kynsoft.finamer.settings.application.command.managePaymentSource.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentSourceCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private Boolean isBank;

    public UpdateManagePaymentSourceCommand(UUID id, String description, Status status, String name, Boolean isBank) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.name = name;
        this.isBank = isBank;
    }

    public static UpdateManagePaymentSourceCommand fromRequest(UpdateManagePaymentSourceRequest request, UUID id){
        return new UpdateManagePaymentSourceCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getIsBank()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentSourceMessage(id);
    }
}
