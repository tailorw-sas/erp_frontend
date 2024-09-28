package com.kynsoft.finamer.invoicing.application.query.report;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvoiceReportQuery implements IQuery {
    private InvoiceReportRequest invoiceReportRequest;
}
