package com.kynsoft.finamer.insis.application.query.innsistHotelRoomType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetInnsistHotelRoomTypeQueryHandler implements IQueryHandler<GetInnsistHotelRoomTypeQuery, PaginatedResponse> {

    private final IInnsistHotelRoomTypeService service;

    public GetInnsistHotelRoomTypeQueryHandler(IInnsistHotelRoomTypeService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetInnsistHotelRoomTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
