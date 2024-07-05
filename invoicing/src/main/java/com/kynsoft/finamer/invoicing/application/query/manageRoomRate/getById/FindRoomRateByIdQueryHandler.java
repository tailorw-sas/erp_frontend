package com.kynsoft.finamer.invoicing.application.query.manageRoomRate.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomRateResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class FindRoomRateByIdQueryHandler implements IQueryHandler<FindRoomRateByIdQuery, ManageRoomRateResponse>  {

    private final IManageRoomRateService service;

    public FindRoomRateByIdQueryHandler(IManageRoomRateService service) {
        this.service = service;
    }

    @Override
    public ManageRoomRateResponse handle(FindRoomRateByIdQuery query) {
        ManageRoomRateDto response = service.findById(query.getId());

        return new ManageRoomRateResponse(response);
    }
}
