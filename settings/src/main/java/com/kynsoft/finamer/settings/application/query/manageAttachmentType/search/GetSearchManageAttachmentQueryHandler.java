package com.kynsoft.finamer.settings.application.query.manageAttachmentType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageAttachmentQueryHandler implements IQueryHandler<GetSearchManageAttachmentQuery, PaginatedResponse> {

    private final IManageAttachmentTypeService service;

    public GetSearchManageAttachmentQueryHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageAttachmentQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
