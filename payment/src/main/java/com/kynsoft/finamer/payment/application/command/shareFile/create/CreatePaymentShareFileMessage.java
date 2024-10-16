package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentShareFileMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_ATTACHMENT_TYPE";

    public CreatePaymentShareFileMessage(UUID id) {
        this.id = id;
    }
}
