package com.kynsoft.finamer.invoicing.application.query.invoiceStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchInvoiceStatusHistoryQueryHandler implements IQueryHandler<GetSearchInvoiceStatusHistoryQuery, PaginatedResponse> {
    private final IInvoiceStatusHistoryService closeOperationService;
    
    public GetSearchInvoiceStatusHistoryQueryHandler(IInvoiceStatusHistoryService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public PaginatedResponse handle(GetSearchInvoiceStatusHistoryQuery query) {

        return this.closeOperationService.search(query.getPageable(),query.getFilter());
    }
}
