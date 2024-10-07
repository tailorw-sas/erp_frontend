package com.kynsoft.finamer.invoicing.application.query.manageInvoiceStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageInvoiceStatusQueryHandler implements IQueryHandler<GetSearchManageInvoiceStatusQuery, PaginatedResponse> {

    private final IManageInvoiceStatusService service;

    public GetSearchManageInvoiceStatusQueryHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageInvoiceStatusQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
