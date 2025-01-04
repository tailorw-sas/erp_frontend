package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UndoImportErrors {
    private UUID invoiceId;
    private Long InvoiceNo;
    private String status;
}
