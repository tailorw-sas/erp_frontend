package com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendList;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class SendListInvoiceQueryHandler implements IQueryHandler<SendListInvoiceQuery, PaginatedResponse> {
    private final IManageInvoiceService service;
    
    public SendListInvoiceQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(SendListInvoiceQuery query) {

        return this.service.sendList(query.getPageable(),query.getFilter());
    }
}
