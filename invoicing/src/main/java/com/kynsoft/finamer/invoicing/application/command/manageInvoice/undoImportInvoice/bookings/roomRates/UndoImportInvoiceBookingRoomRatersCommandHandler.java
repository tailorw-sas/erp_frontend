package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.adjustment.UndoImportInvoiceBookingRoomRatersAdjustmentCommand;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceBookingRoomRatersCommandHandler implements ICommandHandler<UndoImportInvoiceBookingRoomRatersCommand> {

    public UndoImportInvoiceBookingRoomRatersCommandHandler() {
    }

    @Override
    public void handle(UndoImportInvoiceBookingRoomRatersCommand command) {
        command.getObjects().forEach(rate -> {
            if (rate.getAdjustments() != null || !rate.getAdjustments().isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceBookingRoomRatersAdjustmentCommand(rate.getAdjustments()));
            }
            rate.setDeleteInvoice(true);
        });
    }

}
