package com.kynsoft.finamer.invoicing.application.query.manageHotel.getByCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class FindManageHotelByCodeQueryHandler implements IQueryHandler<FindManageHotelByCodeQuery, ManageHotelResponse>  {

    private final IManageHotelService service;

    public FindManageHotelByCodeQueryHandler(IManageHotelService service) {
        this.service = service;
    }

    @Override
    public ManageHotelResponse handle(FindManageHotelByCodeQuery query) {
        ManageHotelDto response = service.findByCode(query.getCode());

        return new ManageHotelResponse(response);
    }
}
