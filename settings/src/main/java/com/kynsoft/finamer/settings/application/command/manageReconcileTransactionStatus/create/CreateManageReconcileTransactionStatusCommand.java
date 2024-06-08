package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageReconcileTransactionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;

    public CreateManageReconcileTransactionStatusCommand(String code, Status status, String name, String description) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public static CreateManageReconcileTransactionStatusCommand fromRequest(
            CreateManageReconcileTransactionStatusRequest request) {
        return new CreateManageReconcileTransactionStatusCommand(
                request.getCode(),

                request.getStatus(),
                request.getName(),
                request.getDescription()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageReconcileTransactionStatusMessage(id);
    }
}
