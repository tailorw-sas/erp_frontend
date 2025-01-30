package com.kynsoft.finamer.insis.application.query.innsistHotelRoomType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistHotelRoomType.InnsistHotelRoomTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindInnsistHotelRoomTypeByIdQueryHandler implements IQueryHandler<FindInnsistHotelRoomTypeByIdQuery, InnsistHotelRoomTypeResponse> {

    private final IInnsistHotelRoomTypeService service;

    public FindInnsistHotelRoomTypeByIdQueryHandler(IInnsistHotelRoomTypeService service){
        this.service = service;
    }
    @Override
    public InnsistHotelRoomTypeResponse handle(FindInnsistHotelRoomTypeByIdQuery query) {
        InnsistHotelRoomTypeDto innsistTradingCompanyHotelDto = service.findById(query.getId());
        return new InnsistHotelRoomTypeResponse(innsistTradingCompanyHotelDto);
    }
}
