package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUndoReverseTransactionMessage implements ICommandMessage {

    private Long reverseFromParentId;

    public CreateUndoReverseTransactionMessage(Long reverseFromParentId) {
        this.reverseFromParentId = reverseFromParentId;
    }

}
