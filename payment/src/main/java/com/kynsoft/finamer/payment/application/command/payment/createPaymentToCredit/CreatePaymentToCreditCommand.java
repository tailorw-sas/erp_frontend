package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentToCreditCommand implements ICommand {

    private UUID id;
    private ManageInvoiceDto invoiceDto;
    private UUID client;
    private UUID agency;
    private UUID hotel;

    public CreatePaymentToCreditCommand(UUID client, UUID agency, UUID hotel, ManageInvoiceDto invoiceDto) {
        this.id = UUID.randomUUID();
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.invoiceDto = invoiceDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentToCreditMessage();
    }
}
