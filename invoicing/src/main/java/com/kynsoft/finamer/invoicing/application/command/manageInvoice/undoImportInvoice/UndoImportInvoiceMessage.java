package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UndoImportInvoiceMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_INVOICE";

    public UndoImportInvoiceMessage() {
    }

}
