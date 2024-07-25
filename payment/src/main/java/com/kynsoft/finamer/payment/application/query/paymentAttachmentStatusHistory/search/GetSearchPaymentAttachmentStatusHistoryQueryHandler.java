package com.kynsoft.finamer.payment.application.query.paymentAttachmentStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentAttachmentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentAttachmentStatusHistoryQueryHandler implements IQueryHandler<GetSearchPaymentAttachmentStatusHistoryQuery, PaginatedResponse> {
    private final IPaymentAttachmentStatusHistoryService service;
    
    public GetSearchPaymentAttachmentStatusHistoryQueryHandler(IPaymentAttachmentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentAttachmentStatusHistoryQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
