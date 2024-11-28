package com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceReconcilePdfMessage implements ICommandMessage {

      private final String[] Ids;
      private final byte[] pdfData;
      private final String command = "CREATE_RECONCILE_PDF";
}
