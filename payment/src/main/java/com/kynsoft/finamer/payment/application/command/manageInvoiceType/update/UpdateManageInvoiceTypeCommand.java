package com.kynsoft.finamer.payment.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String name;


    public UpdateManageInvoiceTypeCommand(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceTypeMessage(id);
    }
}
