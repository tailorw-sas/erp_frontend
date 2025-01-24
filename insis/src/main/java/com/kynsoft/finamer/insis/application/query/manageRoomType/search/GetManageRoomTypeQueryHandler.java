package com.kynsoft.finamer.insis.application.query.manageRoomType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetManageRoomTypeQueryHandler implements IQueryHandler<GetManageRoomTypeQuery, PaginatedResponse> {

    private final IManageRoomTypeService service;

    public GetManageRoomTypeQueryHandler(IManageRoomTypeService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageRoomTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
