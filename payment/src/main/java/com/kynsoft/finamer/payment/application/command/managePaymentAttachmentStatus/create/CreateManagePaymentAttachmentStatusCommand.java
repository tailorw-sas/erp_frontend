package com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentAttachmentStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    
    public CreateManagePaymentAttachmentStatusCommand(UUID id, final String code, final String name, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentAttachmentStatusMessage(id);
    }
}
