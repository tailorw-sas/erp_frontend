package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UndoImportInvoiceRequest {

    private List<UUID> ids;
}
