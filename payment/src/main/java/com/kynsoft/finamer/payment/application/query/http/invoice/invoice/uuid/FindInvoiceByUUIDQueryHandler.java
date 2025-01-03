package com.kynsoft.finamer.payment.application.query.http.invoice.invoice.uuid;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class FindInvoiceByUUIDQueryHandler implements IQueryHandler<FindInvoiceByUUIDQuery, InvoiceHttp> {

    private final InvoiceHttpUUIDService service;
    private final InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl;

    public FindInvoiceByUUIDQueryHandler(InvoiceHttpUUIDService service,
                                         InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl) {
        this.service = service;
        this.invoiceImportAutomaticeHelperServiceImpl = invoiceImportAutomaticeHelperServiceImpl;
    }

    @Override
    public InvoiceHttp handle(FindInvoiceByUUIDQuery query) {
        InvoiceHttp response = service.sendGetBookingHttpRequest(query.getId());

        this.invoiceImportAutomaticeHelperServiceImpl.createInvoice(response);

        return response;
    }

}
