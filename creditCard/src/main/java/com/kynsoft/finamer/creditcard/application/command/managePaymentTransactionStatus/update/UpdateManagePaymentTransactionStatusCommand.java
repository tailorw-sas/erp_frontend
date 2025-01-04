package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update;

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
public class UpdateManagePaymentTransactionStatusCommand implements ICommand {

    private UUID id;
    private String name;
    private Status status;
    private String description;
    private boolean requireValidation;
    private List<UUID> navigate;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;

    public static UpdateManagePaymentTransactionStatusCommand fromRequest(UpdateManagePaymentTransactionStatusRequest request, UUID id) {
        return new UpdateManagePaymentTransactionStatusCommand(
                id, request.getName(), request.getStatus(), request.getDescription(), request.isRequireValidation(), request.getNavigate(),
                request.isInProgress(), request.isCompleted(), request.isCancelled(), request.isApplied()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentTransactionStatusMessage(id);
    }
}
