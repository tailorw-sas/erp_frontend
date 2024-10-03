package com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateRedirectTransactionPaymentCommand implements ICommand {

    private String Token;
    private String result;

    @Override
    public ICommandMessage getMessage() {
        return new CreateRedirectTransactionPaymentCommandMessage(result);
    }
}

