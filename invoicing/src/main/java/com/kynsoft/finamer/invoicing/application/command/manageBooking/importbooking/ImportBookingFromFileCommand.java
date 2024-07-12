package com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingFromFileCommand implements ICommand {

    private ImportBookingRequest request;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
