package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import java.util.UUID;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CalculateInvoiceAmountCommand implements ICommand {
    private UUID id;

    @Override
    public ICommandMessage getMessage() {
        return new CalculateInvoiceAmountMessage(id);
    }

}
