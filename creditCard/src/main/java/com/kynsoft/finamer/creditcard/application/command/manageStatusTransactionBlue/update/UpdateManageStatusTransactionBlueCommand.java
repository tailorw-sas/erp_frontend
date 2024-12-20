package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateManageStatusTransactionBlueCommand implements ICommand {
    private UpdateManageStatusTransactionBlueCommandRequest request;
    private String result;
    private String employee;
    private UUID employeeId;

    public ICommandMessage getMessage() {
        return new UpdateManageStatusTransactionBlueCommandMessage(result);
    }
}
