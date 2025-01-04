package com.kynsoft.finamer.payment.application.command.replicate.delete.objects;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteReplicateMessage implements ICommandMessage {

    private final String command = "TO_DELETE";

    public DeleteReplicateMessage() {
    }

}
