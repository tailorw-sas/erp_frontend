package com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetTransactionDataResponse;
import lombok.Data;

@Data
public class UpdateManageStatusTransactionCommandMessage implements ICommandMessage {
    private final CardNetTransactionDataResponse result;

    public UpdateManageStatusTransactionCommandMessage(CardNetTransactionDataResponse result) {
        this.result = result;
    }
}
