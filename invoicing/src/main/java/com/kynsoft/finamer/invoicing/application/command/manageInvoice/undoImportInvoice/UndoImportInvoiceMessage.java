package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.List;
import lombok.Getter;

@Getter
public class UndoImportInvoiceMessage implements ICommandMessage {

    private final String command = "UNDO_IMPORT_INVOICE";
    private final List<UndoImportErrors> errors;
    private final int satisfactoryQuantity;

    public UndoImportInvoiceMessage(List<UndoImportErrors> errors, int satisfactoryQuantity) {
        this.errors = errors;
        this.satisfactoryQuantity = satisfactoryQuantity;
    }

}
