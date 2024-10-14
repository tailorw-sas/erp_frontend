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
    private Boolean defaults;
    private boolean nonNone;
    private boolean patWithAttachment;
    private boolean pwaWithOutAttachment;
    private boolean supported;
    
    public CreateManagePaymentAttachmentStatusCommand(UUID id, final String code, final String name, String status, Boolean defaults, boolean nonNone, 
                                                      boolean patWithAttachment, boolean pwaWithOutAttachment, boolean supported) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.defaults = defaults;
        this.nonNone = nonNone;
        this.patWithAttachment = patWithAttachment;
        this.pwaWithOutAttachment = pwaWithOutAttachment;
        this.supported = supported;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentAttachmentStatusMessage(id);
    }
}
