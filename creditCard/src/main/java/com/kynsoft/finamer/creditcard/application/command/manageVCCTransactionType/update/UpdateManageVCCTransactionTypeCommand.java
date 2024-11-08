package com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update;

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
public class UpdateManageVCCTransactionTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private Boolean isDefault;
    private Boolean subcategory;
    private boolean manual;
    private Status status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageVCCTransactionTypeMessage(id);
    }
}
