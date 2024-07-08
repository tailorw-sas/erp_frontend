package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteMasterPaymentAttachmentCommand implements ICommand {

    private UUID id;
    private UUID employee;

    @Override
    public ICommandMessage getMessage() {
        return new DeleteMasterPaymentAttachmentMessage(id);
    }

}
