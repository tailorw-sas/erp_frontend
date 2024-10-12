package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
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
    private boolean defaults;

    public UpdateManageInvoiceTransactionTypeCommand(UUID id, String name, boolean defaults) {
        this.id = id;
        this.name = name;
        this.defaults = defaults;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceTransactionTypeMessage(id);
    }
}
