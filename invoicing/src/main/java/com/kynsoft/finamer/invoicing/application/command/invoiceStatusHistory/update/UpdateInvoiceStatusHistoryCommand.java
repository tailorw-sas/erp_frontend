package com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateInvoiceStatusHistoryCommand  implements ICommand {

    private UUID invoice;
    private String employee;

    @Override
    public UpdateInvoiceStatusHistoryMessage getMessage() {
        return new UpdateInvoiceStatusHistoryMessage();
    }
}
