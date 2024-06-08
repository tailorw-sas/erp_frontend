package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageReconcileTransactionStatusCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;

    public UpdateManageReconcileTransactionStatusCommand(UUID id,  Status status, String name, String description) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public static UpdateManageReconcileTransactionStatusCommand fromRequest(UpdateManageReconcileTransactionStatusRequest request, UUID id){
        return new UpdateManageReconcileTransactionStatusCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageReconcileTransactionStatusMessage(id);
    }
}
