package com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateHotelInvoiceNumberSequenceCommand implements ICommand {

    private UUID id;
    private String codeHotel;
    private String codeTradingCompanies;
    private EInvoiceType invoiceType;
    private Long invoiceNo;

    public static CreateHotelInvoiceNumberSequenceCommand fromRequest(CreateHotelInvoiceNumberSequenceRequest request) {
        return new CreateHotelInvoiceNumberSequenceCommand(
                UUID.randomUUID(),
                request.getCodeHotel(),
                request.getCodeTradingCompanies(),
                request.getInvoiceType(),
                request.getInvoiceNo()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateHotelInvoiceNumberSequenceMessage(id);
    }
}
