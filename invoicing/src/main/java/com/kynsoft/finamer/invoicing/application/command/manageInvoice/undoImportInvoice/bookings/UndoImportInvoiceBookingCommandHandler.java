package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.UndoImportInvoiceBookingRoomRatersCommand;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceBookingCommandHandler implements ICommandHandler<UndoImportInvoiceBookingCommand> {

    public UndoImportInvoiceBookingCommandHandler() {
    }

    @Override
    public void handle(UndoImportInvoiceBookingCommand command) {
        command.getObjects().forEach(booking -> {
            if (booking.getRoomRates() != null || !booking.getRoomRates().isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceBookingRoomRatersCommand(booking.getRoomRates(), command.getMediator()));
            }
            booking.setDeleteInvoice(true);
        });
    }

}
