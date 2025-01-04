package com.kynsoft.finamer.invoicing.application.query.manageRoomCategory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchRoomCategoryQueryHandler implements IQueryHandler<GetSearchManageRoomCategoryQuery, PaginatedResponse> {

    private final IManageRoomCategoryService service;

    public GetSearchRoomCategoryQueryHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRoomCategoryQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
