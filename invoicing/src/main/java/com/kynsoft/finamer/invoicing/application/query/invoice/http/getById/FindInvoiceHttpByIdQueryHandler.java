package com.kynsoft.finamer.invoicing.application.query.invoice.http.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class FindInvoiceHttpByIdQueryHandler implements IQueryHandler<FindInvoiceHttpByIdQuery, InvoiceHttp> {

    private final IManageInvoiceService service;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    public FindInvoiceHttpByIdQueryHandler(IManageInvoiceService service, BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        this.service = service;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public InvoiceHttp handle(FindInvoiceHttpByIdQuery query) {
        ManageInvoiceDto response = service.findById(query.getId());

        return this.bookingImportAutomaticeHelperServiceImpl.createInvoiceHttp(response);
    }
}
