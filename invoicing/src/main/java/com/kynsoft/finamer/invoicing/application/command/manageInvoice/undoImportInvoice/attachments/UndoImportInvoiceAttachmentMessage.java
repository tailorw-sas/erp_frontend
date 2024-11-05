package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.attachments;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UndoImportInvoiceAttachmentMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_INVOICE_ATTACHMENT";

    public UndoImportInvoiceAttachmentMessage() {
    }

}
