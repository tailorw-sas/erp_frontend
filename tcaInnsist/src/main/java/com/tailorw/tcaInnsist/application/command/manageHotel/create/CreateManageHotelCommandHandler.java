package com.tailorw.tcaInnsist.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService hotelService;

    @Override
    public void handle(CreateManageHotelCommand command) {
        ManageHotelDto hotel = new ManageHotelDto(command.getId(),
                command.getCode(),
                command.getName(),
                command.getRoomType(),
                command.getTradingCompanyId());
        hotelService.create(hotel);
    }
}
