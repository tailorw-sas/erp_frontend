package com.kynsoft.finamer.payment.application.command.shareFile.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePaymentShareFileMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_ATTACHMENT_TYPE";

    public DeletePaymentShareFileMessage(UUID id) {
        this.id = id;
    }

}
