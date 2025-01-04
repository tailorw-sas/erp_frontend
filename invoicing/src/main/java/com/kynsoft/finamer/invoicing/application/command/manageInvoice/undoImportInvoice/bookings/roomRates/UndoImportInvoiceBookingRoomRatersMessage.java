package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UndoImportInvoiceBookingRoomRatersMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_BOOKING_ROOM_RATES";

    public UndoImportInvoiceBookingRoomRatersMessage() {
    }

}
