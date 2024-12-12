package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private boolean defaults;
    private boolean cloneAdjustmentDefault;

    public CreateManageInvoiceTransactionTypeCommand(UUID id, String code, String name, boolean defaults, boolean cloneAdjustmentDefault) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaults = defaults;
        this.cloneAdjustmentDefault = cloneAdjustmentDefault;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTransactionTypeMessage(id);
    }
}
