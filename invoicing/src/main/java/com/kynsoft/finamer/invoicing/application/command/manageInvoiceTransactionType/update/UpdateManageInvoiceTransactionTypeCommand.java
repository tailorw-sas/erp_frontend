package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageInvoiceTransactionTypeCommand implements ICommand {

    private UUID id;

    private String name;

    public UpdateManageInvoiceTransactionTypeCommand(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceTransactionTypeMessage(id);
    }
}
