package com.kynsoft.finamer.invoicing.application.query.manageAttachment.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAttachmentQueryHandler implements IQueryHandler<GetSearchAttachmentQuery, PaginatedResponse> {
    private final IManageAttachmentService service;
    
    public GetSearchAttachmentQueryHandler(IManageAttachmentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAttachmentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
