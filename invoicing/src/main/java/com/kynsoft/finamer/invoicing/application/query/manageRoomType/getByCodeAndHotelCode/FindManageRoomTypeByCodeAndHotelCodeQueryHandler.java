package com.kynsoft.finamer.invoicing.application.query.manageRoomType.getByCodeAndHotelCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomTypeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRoomTypeByCodeAndHotelCodeQueryHandler implements IQueryHandler<FindManageRoomTypeByCodeAndHotelCodeQuery, ManageRoomTypeResponse> {

    private final IManageRoomTypeService roomTypeService;

    public FindManageRoomTypeByCodeAndHotelCodeQueryHandler(IManageRoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @Override
    public ManageRoomTypeResponse handle(FindManageRoomTypeByCodeAndHotelCodeQuery query) {
        ManageRoomTypeDto dto = this.roomTypeService.findManageRoomTypenByCodeAndHotelCode(query.getCode(), query.getHotelCode());

        return new ManageRoomTypeResponse(dto);
    }
}
