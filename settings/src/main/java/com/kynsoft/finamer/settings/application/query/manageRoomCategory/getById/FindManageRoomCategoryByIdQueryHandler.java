package com.kynsoft.finamer.settings.application.query.manageRoomCategory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyTypeResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRoomCategoryResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRoomCategoryByIdQueryHandler implements IQueryHandler<FindManageRoomCategoryByIdQuery, ManageRoomCategoryResponse> {

    private final IManageRoomCategoryService service;

    public FindManageRoomCategoryByIdQueryHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public ManageRoomCategoryResponse handle(FindManageRoomCategoryByIdQuery query) {
        ManageRoomCategoryDto dto = service.findById(query.getId());
        return new ManageRoomCategoryResponse(dto);
    }
}
