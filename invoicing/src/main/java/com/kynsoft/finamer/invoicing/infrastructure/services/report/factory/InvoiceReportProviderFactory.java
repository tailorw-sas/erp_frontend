package com.kynsoft.finamer.invoicing.infrastructure.services.report.factory;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InvoiceReportProviderFactory {
    private final Map<String, IInvoiceReport> paymentReportServices;

    public InvoiceReportProviderFactory(Map<String, IInvoiceReport> invoiceReportServices) {
        this.paymentReportServices = invoiceReportServices;
    }

    public IInvoiceReport getInvoiceReportService(EInvoiceReportType paymentReportType){
        return paymentReportServices.getOrDefault(paymentReportType.name(),null);
    }
}
