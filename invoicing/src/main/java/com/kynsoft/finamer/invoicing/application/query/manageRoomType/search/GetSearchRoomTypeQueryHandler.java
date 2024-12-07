package com.kynsoft.finamer.invoicing.application.query.manageRoomType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchRoomTypeQueryHandler implements IQueryHandler<GetSearchManageRoomTypeQuery, PaginatedResponse> {

    private final IManageRoomTypeService service;

    public GetSearchRoomTypeQueryHandler(IManageRoomTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRoomTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
