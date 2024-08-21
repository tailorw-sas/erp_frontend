package com.kynsoft.finamer.payment.application.query.manageInvoice.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageInvoiceQueryHandler implements IQueryHandler<GetSearchManageInvoiceQuery, PaginatedResponse> {
    private final IManageInvoiceService service;
    
    public GetSearchManageInvoiceQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageInvoiceQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
