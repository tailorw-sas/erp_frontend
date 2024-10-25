package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageReconcileTransactionStatusCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;

    public UpdateManageReconcileTransactionStatusCommand(UUID id,  Status status, String name, String description, Boolean requireValidation,List<UUID> navigate) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.navigate = navigate;
        this.requireValidation = requireValidation;
    }

    public static UpdateManageReconcileTransactionStatusCommand fromRequest(UpdateManageReconcileTransactionStatusRequest request, UUID id){
        return new UpdateManageReconcileTransactionStatusCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getRequireValidation(),
                request.getNavigate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageReconcileTransactionStatusMessage(id);
    }
}
