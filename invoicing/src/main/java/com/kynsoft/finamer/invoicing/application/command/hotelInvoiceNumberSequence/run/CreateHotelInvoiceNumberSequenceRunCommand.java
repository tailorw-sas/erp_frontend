package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.run;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CreateHotelInvoiceNumberSequenceRunCommand implements ICommand {

    @Override
    public ICommandMessage getMessage() {
        return new CreateHotelInvoiceNumberSequenceRunMessage();
    }
}
