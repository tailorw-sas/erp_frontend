package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.IReportPdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoiceReconcileManualPdfCommandHandler implements ICommandHandler<InvoiceReconcileManualPdfCommand> {

    private final IReportPdfService pdfService;

    public InvoiceReconcileManualPdfCommandHandler(IReportPdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Override
    public void handle(InvoiceReconcileManualPdfCommand command) {
        try {
            command.getRequest().setPdfData(pdfService.concatenateManualPDFs(command.getRequest()));
        } catch (Exception e) {
            log.error(e.toString());
        }

    }

}