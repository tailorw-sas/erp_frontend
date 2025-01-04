package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.originalAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceOriginalAmountCommand implements ICommand {

    private UUID id;
    private Double originalAmount;

    public UpdateInvoiceOriginalAmountCommand(UUID id, Double originalAmount) {
        this.id = id;
        this.originalAmount = originalAmount;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceOriginalAmountMessage(id);
    }
}
