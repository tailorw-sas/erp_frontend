package com.kynsoft.finamer.creditcard.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageBankAccountCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID manageBank;
    private UUID manageHotel;
    private UUID manageAccountType;
    private String description;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageBankAccountMessage(id);
    }
}
