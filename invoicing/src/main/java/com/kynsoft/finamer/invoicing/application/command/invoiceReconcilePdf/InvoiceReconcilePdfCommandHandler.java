package com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.infrastructure.services.ReportPdfServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReconcilePdfCommandHandler implements ICommandHandler<InvoiceReconcilePdfCommand> {
  private final ReportPdfServiceImpl reportPdfService;

    public InvoiceReconcilePdfCommandHandler(ReportPdfServiceImpl reportPdfService) {
        this.reportPdfService = reportPdfService;
    }

    @Override
    public void handle(InvoiceReconcilePdfCommand command) {
        try {
            reportPdfService.generatePdf(command.getId());
        }catch (Exception e) {
            System.err.println("PDF ERROR:");
        }
    }


}
