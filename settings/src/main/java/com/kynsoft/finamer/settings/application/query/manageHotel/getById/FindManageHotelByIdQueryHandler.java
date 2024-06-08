package com.kynsoft.finamer.settings.application.query.manageHotel.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class FindManageHotelByIdQueryHandler implements IQueryHandler<FindManageHotelByIdQuery, ManageHotelResponse> {

    private final IManageHotelService service;

    public FindManageHotelByIdQueryHandler(IManageHotelService service) {
        this.service = service;
    }

    @Override
    public ManageHotelResponse handle(FindManageHotelByIdQuery query) {
        ManageHotelDto dto = service.findById(query.getId());
        return new ManageHotelResponse(dto);
    }
}
