package com.kynsoft.finamer.payment.application.command.shareFile.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePaymentShareFileMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ATTACHMENT_TYPE";

    public UpdatePaymentShareFileMessage(UUID id) {
        this.id = id;
    }

}
