package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.List;
import lombok.Getter;

@Getter
public class UndoImportInvoiceMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_INVOICE";
    private final List<UndoImportErrors> errors;

    public UndoImportInvoiceMessage(List<UndoImportErrors> errors) {
        this.errors = errors;
    }

}
