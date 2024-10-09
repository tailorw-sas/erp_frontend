package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageTransactionStatusCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private List<UUID> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

    public UpdateManageTransactionStatusCommand(UUID id, String description, String name, List<UUID> navigate, Boolean enablePayment, Boolean visible, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.navigate = navigate;
        this.enablePayment = enablePayment;
        this.visible = visible;
        this.status = status;
    }

    public static UpdateManageTransactionStatusCommand fromRequest(UpdateManageTransactionStatusRequest request, UUID id) {
        return new UpdateManageTransactionStatusCommand(
                id,
                request.getDescription(),
                request.getName(),
                request.getNavigate(),
                request.getEnablePayment(),
                request.getVisible(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageTransactionStatusMessage(id);
    }
}
