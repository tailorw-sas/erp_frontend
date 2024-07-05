package com.kynsoft.finamer.creditcard.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageHotelCommandHandler implements ICommandHandler<UpdateManageHotelCommand> {

    private final IManageHotelService service;


    public UpdateManageHotelCommandHandler(IManageHotelService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageHotelCommand command) {
        ManageHotelDto dto = service.findById(command.getId());
        dto.setName(command.getName());
        dto.setIsApplyByVCC(command.getIsApplyByVCC());
        this.service.update(dto);
    }
}
