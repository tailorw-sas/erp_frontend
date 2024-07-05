package com.kynsoft.finamer.settings.application.query.manageRoomType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRoomTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRoomTypeByIdQueryHandler implements IQueryHandler<FindManageRoomTypeByIdQuery, ManageRoomTypeResponse> {

    private final IManageRoomTypeService service;

    public FindManageRoomTypeByIdQueryHandler(IManageRoomTypeService service) {
        this.service = service;
    }

    @Override
    public ManageRoomTypeResponse handle(FindManageRoomTypeByIdQuery query) {
        ManageRoomTypeDto dto = service.findById(query.getId());
        return new ManageRoomTypeResponse(dto);
    }
}
