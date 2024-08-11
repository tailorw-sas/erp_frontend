package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CalculateInvoiceAmountCommand implements ICommand {
    private UUID id;
    private List<UUID> bookings;
    private List<UUID> roomRates;

    @Override
    public ICommandMessage getMessage() {
        return new CalculateInvoiceAmountMessage(id);
    }

}

