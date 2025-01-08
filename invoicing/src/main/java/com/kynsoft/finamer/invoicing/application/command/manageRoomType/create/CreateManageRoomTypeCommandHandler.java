package com.kynsoft.finamer.invoicing.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomTypeCommandHandler implements ICommandHandler<CreateManageRoomTypeCommand> {

    private final IManageRoomTypeService service;
    private final IManageHotelService hotelService;


    public CreateManageRoomTypeCommandHandler(IManageRoomTypeService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageRoomTypeCommand command) {
        ManageHotelDto hotel = this.hotelService.findById(command.getHotelId());

        service.create(new ManageRoomTypeDto(
                command.getId(), command.getCode(),  command.getName(), command.getStatus(), hotel
        ));
    }
}
