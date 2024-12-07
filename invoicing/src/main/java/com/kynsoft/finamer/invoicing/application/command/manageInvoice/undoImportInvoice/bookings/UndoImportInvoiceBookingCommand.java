package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UndoImportInvoiceBookingCommand implements ICommand {

    private List<ManageBookingDto> objects;
    private IMediator mediator;

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportInvoiceBookingMessage();
    }

}
