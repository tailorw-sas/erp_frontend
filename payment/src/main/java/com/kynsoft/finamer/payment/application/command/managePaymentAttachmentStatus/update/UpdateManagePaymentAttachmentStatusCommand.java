package com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentAttachmentStatusCommand implements ICommand {

    private UUID id;
    private String name;
    
    public UpdateManagePaymentAttachmentStatusCommand(UUID id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentAttachmentStatusMessage(id);
    }
}
