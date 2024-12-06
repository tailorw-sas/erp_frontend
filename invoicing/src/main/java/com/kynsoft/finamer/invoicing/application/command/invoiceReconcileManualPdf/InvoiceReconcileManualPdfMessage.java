package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceReconcileManualPdfMessage implements ICommandMessage {

      private final byte[] pdfData;

      private final String command = "CREATE_RECONCILE_MANUAL";

      public InvoiceReconcileManualPdfMessage(byte[] pdfData) {
            this.pdfData = pdfData;
      }
}
