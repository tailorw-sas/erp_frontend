package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.attachments.UndoImportInvoiceAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.UndoImportInvoiceBookingCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceCommandHandler implements ICommandHandler<UndoImportInvoiceCommand> {

    private final IManageInvoiceService service;

    public UndoImportInvoiceCommandHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public void handle(UndoImportInvoiceCommand command) {
        for (UUID id : command.getIds()) {
            ManageInvoiceDto delete = this.service.findById(id);
            if (delete.getBookings() != null || !delete.getBookings().isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceBookingCommand(delete.getBookings(), command.getMediator()));
            }
            if (delete.getAttachments() != null || !delete.getAttachments().isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceAttachmentCommand(delete.getAttachments()));
            }
            this.service.deleteInvoice(delete);
        }
    }

}
