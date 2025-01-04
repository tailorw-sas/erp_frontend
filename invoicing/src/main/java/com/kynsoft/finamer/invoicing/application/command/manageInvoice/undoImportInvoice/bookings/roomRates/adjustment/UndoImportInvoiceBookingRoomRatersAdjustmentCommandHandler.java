package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.adjustment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceBookingRoomRatersAdjustmentCommandHandler implements ICommandHandler<UndoImportInvoiceBookingRoomRatersAdjustmentCommand> {

    public UndoImportInvoiceBookingRoomRatersAdjustmentCommandHandler() {
    }

    @Override
    public void handle(UndoImportInvoiceBookingRoomRatersAdjustmentCommand command) {
        command.getObjects().forEach(object -> object.setDeleteInvoice(true));
    }

}
