package com.kynsoft.finamer.settings.application.query.managePaymentAttachmentStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentAttachmentStatusQueryHandler implements IQueryHandler<GetSearchPaymentAttachmentStatusQuery, PaginatedResponse> {
    
    private IManagePaymentAttachmentStatusService service;
    
    public GetSearchPaymentAttachmentStatusQueryHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }
    
    
    @Override
    public PaginatedResponse handle(GetSearchPaymentAttachmentStatusQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
