package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplication;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateUndoApplicationMessage implements ICommandMessage {

    private UUID id;

    public CreateUndoApplicationMessage(UUID id) {
        this.id = id;
    }

}
