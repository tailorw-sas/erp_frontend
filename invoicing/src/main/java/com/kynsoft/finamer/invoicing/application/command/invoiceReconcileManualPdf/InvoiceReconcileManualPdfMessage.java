package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceReconcileManualPdfMessage implements ICommandMessage {

      private List<UUID> invoices;
      private  byte[] pdfData;
      private String command = "CREATE_RECONCILE_PDF";

      public InvoiceReconcileManualPdfMessage(List<UUID> invoices, byte[] pdfData){
            this.invoices = invoices;
            this.pdfData = pdfData;
      }
}
