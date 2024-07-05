package com.kynsoft.finamer.settings.application.query.manageRoomType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageRoomTypeQueryHandler implements IQueryHandler<GetSearchManageRoomTypeQuery, PaginatedResponse> {

    private final IManageRoomTypeService service;

    public GetSearchManageRoomTypeQueryHandler(IManageRoomTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRoomTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
