package com.kynsoft.finamer.insis.application.query.manageHotel.getByCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GetManageHotelByCodeQueryHandler implements IQueryHandler<GetManageHotelByCodeQuery, ManageHotelResponse> {

    private final IManageHotelService service;

    public GetManageHotelByCodeQueryHandler(IManageHotelService service){
        this.service = service;
    }

    @Override
    public ManageHotelResponse handle(GetManageHotelByCodeQuery query) {
        ManageHotelDto dto = service.findByCode(query.getCode());
        if(Objects.nonNull(dto)){
            return new ManageHotelResponse(dto);
        }

        return null;
    }
}
