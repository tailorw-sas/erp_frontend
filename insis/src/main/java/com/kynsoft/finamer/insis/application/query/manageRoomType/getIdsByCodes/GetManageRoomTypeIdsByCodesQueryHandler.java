package com.kynsoft.finamer.insis.application.query.manageRoomType.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType.ManageRoomTypeIdsResponse;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetManageRoomTypeIdsByCodesQueryHandler implements IQueryHandler<GetManageRoomTypeIdsByCodesQuery, ManageRoomTypeIdsResponse> {

    private final IManageRoomTypeService service;

    public GetManageRoomTypeIdsByCodesQueryHandler(IManageRoomTypeService service){
        this.service = service;
    }

    @Override
    public ManageRoomTypeIdsResponse handle(GetManageRoomTypeIdsByCodesQuery query) {
        return new ManageRoomTypeIdsResponse(service.findIdsByCodes(query.getCodes(), query.getHotel()));
    }
}
