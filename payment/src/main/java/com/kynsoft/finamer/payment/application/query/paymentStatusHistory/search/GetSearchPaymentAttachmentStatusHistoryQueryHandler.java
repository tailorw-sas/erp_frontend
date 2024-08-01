package com.kynsoft.finamer.payment.application.query.paymentStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;

@Component
public class GetSearchPaymentAttachmentStatusHistoryQueryHandler implements IQueryHandler<GetSearchPaymentAttachmentStatusHistoryQuery, PaginatedResponse> {
    private final IPaymentStatusHistoryService service;
    
    public GetSearchPaymentAttachmentStatusHistoryQueryHandler(IPaymentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentAttachmentStatusHistoryQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
