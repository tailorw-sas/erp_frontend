package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInvoiceCalculateInvoiceAmountCommand implements ICommand {

    private ManageInvoiceDto invoiceDto;

    public UpdateInvoiceCalculateInvoiceAmountCommand(ManageInvoiceDto invoiceDto) {
        this.invoiceDto = invoiceDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceCalculateInvoiceAmountMessage(invoiceDto.getId());
    }
}
