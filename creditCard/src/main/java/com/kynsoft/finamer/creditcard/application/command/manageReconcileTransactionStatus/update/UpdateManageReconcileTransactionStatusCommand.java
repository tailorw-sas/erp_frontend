package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageReconcileTransactionStatusCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;
    private boolean created;
    private boolean cancelled;
    private boolean completed;

    public static UpdateManageReconcileTransactionStatusCommand fromRequest(UpdateManageReconcileTransactionStatusRequest request, UUID id){
        return new UpdateManageReconcileTransactionStatusCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getRequireValidation(),
                request.getNavigate(),
                request.isCreated(),
                request.isCancelled(),
                request.isCompleted()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageReconcileTransactionStatusMessage(id);
    }
}
