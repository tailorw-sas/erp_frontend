package com.kynsoft.finamer.creditcard.application.query.attachmentStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAttachmentStatusHistoryQueryHandler implements IQueryHandler<GetSearchAttachmentStatusHistoryQuery, PaginatedResponse> {
    private final IAttachmentStatusHistoryService service;
    
    public GetSearchAttachmentStatusHistoryQueryHandler(IAttachmentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAttachmentStatusHistoryQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
