package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInvoiceCalculateDueAmountCommand implements ICommand {

    private ManageInvoiceDto invoiceDto;
    private Double dueAmount;

    public UpdateInvoiceCalculateDueAmountCommand(ManageInvoiceDto invoiceDto, Double dueAmount) {
        this.invoiceDto = invoiceDto;
        this.dueAmount = dueAmount;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceCalculateDueAmountMessage(invoiceDto.getId());
    }
}
