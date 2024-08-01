package com.kynsoft.finamer.invoicing.application.query.invoicecloseoperation.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchInvoiceCloseOperationQueryHandler implements IQueryHandler<GetSearchInvoiceCloseOperationQuery, PaginatedResponse> {
    private final IInvoiceCloseOperationService invoiceStatusHistoryService;
    
    public GetSearchInvoiceCloseOperationQueryHandler(IInvoiceCloseOperationService invoiceStatusHistoryService) {
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public PaginatedResponse handle(GetSearchInvoiceCloseOperationQuery query) {

        return this.invoiceStatusHistoryService.search(query.getPageable(),query.getFilter());
    }
}
