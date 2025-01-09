package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCodeAndValueMessage implements ICommandMessage {

    public UpdateCodeAndValueMessage() {
    }
}
