package com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetTransactionDataResponse;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateManageStatusTransactionCommand implements ICommand {
    private CardNetTransactionDataResponse result;
    private String session;
    private String employee;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageStatusTransactionCommandMessage(result);
    }
}

