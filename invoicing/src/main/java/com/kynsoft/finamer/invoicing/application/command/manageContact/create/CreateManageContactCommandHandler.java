package com.kynsoft.finamer.invoicing.application.command.manageContact.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageContactDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.services.IManageContactService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageContactCommandHandler implements ICommandHandler<CreateManageContactCommand> {

    private final IManageContactService service;

    private final IManageHotelService hotelService;

    public CreateManageContactCommandHandler(IManageContactService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageContactCommand command) {
        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        service.create(new ManageContactDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getName(), hotelDto,
                command.getEmail(), command.getPhone(), command.getPosition()
        ));
    }
}
