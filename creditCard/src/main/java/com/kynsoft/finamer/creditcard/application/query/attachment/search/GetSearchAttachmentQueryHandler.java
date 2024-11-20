package com.kynsoft.finamer.creditcard.application.query.attachment.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAttachmentQueryHandler implements IQueryHandler<GetSearchAttachmentQuery, PaginatedResponse> {
    private final IAttachmentService service;
    
    public GetSearchAttachmentQueryHandler(IAttachmentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAttachmentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
