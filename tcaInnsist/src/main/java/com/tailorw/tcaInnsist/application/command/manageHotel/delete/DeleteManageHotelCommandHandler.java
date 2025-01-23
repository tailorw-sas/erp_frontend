package com.tailorw.tcaInnsist.application.command.manageHotel.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteManageHotelCommandHandler implements ICommandHandler<DeleteManageHotelCommand> {

    private final IManageHotelService hotelService;


    @Override
    public void handle(DeleteManageHotelCommand command) {
        hotelService.delete(command.getId());
    }
}
