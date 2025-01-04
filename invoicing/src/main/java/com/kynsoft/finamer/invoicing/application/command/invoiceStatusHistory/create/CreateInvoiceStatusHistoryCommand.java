package com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateInvoiceStatusHistoryCommand  implements ICommand {

    private UUID invoice;
    private String employee;

    @Override
    public CreateInvoiceStatusHistoryMessage getMessage() {
        return new CreateInvoiceStatusHistoryMessage();
    }
}
