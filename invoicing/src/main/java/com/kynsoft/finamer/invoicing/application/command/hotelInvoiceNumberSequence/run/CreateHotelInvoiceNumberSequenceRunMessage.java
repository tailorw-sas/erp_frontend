package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.run;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHotelInvoiceNumberSequenceRunMessage implements ICommandMessage {

    public CreateHotelInvoiceNumberSequenceRunMessage() {
    }
}
