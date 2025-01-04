package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UndoImportInvoiceBookingMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_BOOKING";

    public UndoImportInvoiceBookingMessage() {
    }

}
