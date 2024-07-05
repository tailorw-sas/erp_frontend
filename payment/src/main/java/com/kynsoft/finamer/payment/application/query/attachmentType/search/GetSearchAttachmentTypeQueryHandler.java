package com.kynsoft.finamer.payment.application.query.attachmentType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAttachmentTypeQueryHandler implements IQueryHandler<GetSearchAttachmentTypeQuery, PaginatedResponse> {
    private final IManageAttachmentTypeService service;
    
    public GetSearchAttachmentTypeQueryHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAttachmentTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
