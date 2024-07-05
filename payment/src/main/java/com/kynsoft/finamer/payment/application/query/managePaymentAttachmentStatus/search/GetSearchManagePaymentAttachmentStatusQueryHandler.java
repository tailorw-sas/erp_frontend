package com.kynsoft.finamer.payment.application.query.managePaymentAttachmentStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentAttachmentStatusQueryHandler implements IQueryHandler<GetSearchManagePaymentAttachmentStatusQuery, PaginatedResponse> {
    private final IManagePaymentAttachmentStatusService service;
    
    public GetSearchManagePaymentAttachmentStatusQueryHandler(IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagePaymentAttachmentStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
