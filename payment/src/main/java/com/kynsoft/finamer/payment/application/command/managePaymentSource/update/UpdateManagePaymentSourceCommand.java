package com.kynsoft.finamer.payment.application.command.managePaymentSource.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentSourceCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;

    public UpdateManagePaymentSourceCommand(UUID id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentSourceMessage(id);
    }
}
