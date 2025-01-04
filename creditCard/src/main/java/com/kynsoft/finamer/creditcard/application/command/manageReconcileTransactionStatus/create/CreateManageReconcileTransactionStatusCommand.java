package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageReconcileTransactionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;
    private boolean created;
    private boolean cancelled;
    private boolean completed;

    public CreateManageReconcileTransactionStatusCommand(String code, Status status, String name, String description, Boolean requireValidation, List<UUID> navigate, boolean created, boolean cancelled, boolean completed) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.requireValidation = requireValidation;
        this.navigate = navigate;
        this.created = created;
        this.cancelled = cancelled;
        this.completed = completed;
    }

    public static CreateManageReconcileTransactionStatusCommand fromRequest(
            CreateManageReconcileTransactionStatusRequest request) {
        return new CreateManageReconcileTransactionStatusCommand(
                request.getCode(),
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
        return new CreateManageReconcileTransactionStatusMessage(id);
    }
}
