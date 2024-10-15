package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateManageStatusTransactionBlueCommand implements ICommand {
    private UpdateManageStatusTransactionBlueCommandRequest request;
    private String result;

    public ICommandMessage getMessage() {
        return new UpdateManageStatusTransactionBlueCommandMessage(result);
    }
}
