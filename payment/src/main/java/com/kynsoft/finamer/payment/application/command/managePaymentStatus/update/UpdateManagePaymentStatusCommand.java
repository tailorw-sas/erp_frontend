package com.kynsoft.finamer.payment.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentStatusCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;
    private Boolean applied;

    public UpdateManagePaymentStatusCommand(UUID id, String name, String status, Boolean applied) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.applied = applied;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentStatusMessage(id);
    }
}
