package com.kynsoft.finamer.settings.application.query.manageRoomCategory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageRoomCategoryQueryHandler implements IQueryHandler<GetSearchManageRoomCategoryQuery, PaginatedResponse> {

    private final IManageRoomCategoryService service;

    public GetSearchManageRoomCategoryQueryHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRoomCategoryQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
