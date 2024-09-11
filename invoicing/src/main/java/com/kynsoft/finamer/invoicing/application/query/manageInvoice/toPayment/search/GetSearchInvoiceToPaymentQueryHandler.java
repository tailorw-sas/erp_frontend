package com.kynsoft.finamer.invoicing.application.query.manageInvoice.toPayment.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchInvoiceToPaymentQueryHandler implements IQueryHandler<GetSearchInvoiceToPaymentQuery, PaginatedResponse> {
    private final IManageInvoiceService service;
    
    public GetSearchInvoiceToPaymentQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchInvoiceToPaymentQuery query) {

        return this.service.searchToPayment(query.getPageable(),query.getFilter());
    }
}
