package com.kynsoft.finamer.invoicing.application.query.manageInvoiceTransactionType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageInvoiceTransactionTypeQueryHandler implements IQueryHandler<GetSearchManageInvoiceTransactionTypeQuery, PaginatedResponse> {

    private final IManageLanguageService service;

    public GetSearchManageInvoiceTransactionTypeQueryHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageInvoiceTransactionTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
