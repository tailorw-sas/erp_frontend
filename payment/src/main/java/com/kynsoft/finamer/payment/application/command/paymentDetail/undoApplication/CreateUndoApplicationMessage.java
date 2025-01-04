package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplication;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUndoApplicationMessage implements ICommandMessage {

    private UUID id;

    public CreateUndoApplicationMessage(UUID id) {
        this.id = id;
    }

}
