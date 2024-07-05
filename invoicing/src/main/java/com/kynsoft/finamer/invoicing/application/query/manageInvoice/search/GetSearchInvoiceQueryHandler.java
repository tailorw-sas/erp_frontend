package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchInvoiceQueryHandler implements IQueryHandler<GetSearchInvoiceQuery, PaginatedResponse> {
    private final IManageInvoiceService service;
    
    public GetSearchInvoiceQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchInvoiceQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
