package com.kynsoft.finamer.invoicing.domain.services;



import java.util.Optional;

public interface IInvoiceReport {

    Optional<byte[]> generateReport(String invoiceId);
}
