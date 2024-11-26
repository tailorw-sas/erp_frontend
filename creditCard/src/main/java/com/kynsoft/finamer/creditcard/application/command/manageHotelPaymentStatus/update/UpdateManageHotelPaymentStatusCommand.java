package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageHotelPaymentStatusCommand implements ICommand {

    private UUID id;
    private String name;
    private Status status;
    private String description;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;

    public static UpdateManageHotelPaymentStatusCommand fromRequest(UpdateManageHotelPaymentStatusRequest request, UUID id) {
        return new UpdateManageHotelPaymentStatusCommand(
                id, request.getName(), request.getStatus(), request.getDescription(),
                request.isInProgress(), request.isCompleted(), request.isCancelled(), request.isApplied()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageHotelPaymentStatusMessage(id);
    }
}
