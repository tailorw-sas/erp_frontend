package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UndoImportInvoiceBookingRoomRatersCommand implements ICommand {

    private List<ManageRoomRateDto> objects;
    private IMediator mediator;

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportInvoiceBookingRoomRatersMessage();
    }

}
