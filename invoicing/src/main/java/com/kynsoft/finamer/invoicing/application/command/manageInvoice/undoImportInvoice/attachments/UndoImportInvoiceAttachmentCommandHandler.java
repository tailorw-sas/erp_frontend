package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.attachments;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceAttachmentCommandHandler implements ICommandHandler<UndoImportInvoiceAttachmentCommand> {

    public UndoImportInvoiceAttachmentCommandHandler() {
    }

    @Override
    public void handle(UndoImportInvoiceAttachmentCommand command) {
        command.getObjects().forEach(attachment -> attachment.setDeleteInvoice(true));
    }

}
