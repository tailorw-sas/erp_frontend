package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String code;

    private String name;

    public CreateManageInvoiceTypeCommand(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTypeMessage(id);
    }
}
