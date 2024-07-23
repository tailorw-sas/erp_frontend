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
    private String status;
    
    public UpdateManagePaymentAttachmentStatusCommand(UUID id, final String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentAttachmentStatusMessage(id);
    }
}
