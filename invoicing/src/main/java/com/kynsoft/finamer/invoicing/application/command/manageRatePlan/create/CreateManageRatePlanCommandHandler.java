package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRatePlanCommandHandler implements ICommandHandler<CreateManageRatePlanCommand> {

    private final IManageRatePlanService service;
    private final IManageHotelService hotelService;
    

    public CreateManageRatePlanCommandHandler(IManageRatePlanService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageRatePlanCommand command) {
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotelId());

        service.create(new ManageRatePlanDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                hotelDto
        ));
    }
}
