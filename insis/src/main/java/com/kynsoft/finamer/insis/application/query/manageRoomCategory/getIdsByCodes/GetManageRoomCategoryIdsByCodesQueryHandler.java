package com.kynsoft.finamer.insis.application.query.manageRoomCategory.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomCategory.ManageRoomCategoryIdsResponse;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class GetManageRoomCategoryIdsByCodesQueryHandler implements IQueryHandler<GetManageRoomCategoryIdsByCodesQuery, ManageRoomCategoryIdsResponse> {

    private final IManageRoomCategoryService service;

    public GetManageRoomCategoryIdsByCodesQueryHandler(IManageRoomCategoryService service){
        this.service = service;
    }

    @Override
    public ManageRoomCategoryIdsResponse handle(GetManageRoomCategoryIdsByCodesQuery query) {
        return new ManageRoomCategoryIdsResponse(service.findIdsByCodes(query.getCodes()));
    }
}
