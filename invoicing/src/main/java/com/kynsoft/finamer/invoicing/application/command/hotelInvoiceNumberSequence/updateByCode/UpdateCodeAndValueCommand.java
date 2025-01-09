package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCodeAndValueCommand implements ICommand {

    private List<UpdateCodeAndValue> hotels;
    private List<UpdateCodeAndValue> tradings;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCodeAndValueMessage();
    }
}
