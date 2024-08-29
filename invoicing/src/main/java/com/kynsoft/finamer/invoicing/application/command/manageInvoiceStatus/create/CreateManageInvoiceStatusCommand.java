package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageInvoiceStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean showClone;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceStatusMessage(id);
    }
}
