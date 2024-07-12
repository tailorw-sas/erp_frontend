package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import org.springframework.stereotype.Component;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;

@Component
public class CalculateInvoiceAmountCommandHandler implements ICommandHandler<CalculateInvoiceAmountCommand> {

    private final IManageInvoiceService invoiceService;

    public CalculateInvoiceAmountCommandHandler(IManageInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public void handle(CalculateInvoiceAmountCommand command) {

        ManageInvoiceDto invoiceDto = this.invoiceService.findById(command.getId());

        Double InvoiceAmount = 0.00;

        System.out.println(invoiceDto);

        if (invoiceDto.getBookings() != null) {

            for (int i = 0; i < invoiceDto.getBookings().size(); i++) {

                InvoiceAmount += invoiceDto.getBookings().get(i).getInvoiceAmount();

            }

            invoiceDto.setInvoiceAmount(InvoiceAmount);

            this.invoiceService.update(invoiceDto);
        }

    }

}
