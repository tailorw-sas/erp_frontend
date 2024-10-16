package com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageVCCTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean isDefault;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageVCCTransactionTypeMessage(id);
    }
}
