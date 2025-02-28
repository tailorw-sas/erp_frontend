package com.kynsoft.finamer.insis.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRatePlan.ProducerReplicateManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class CreateRatePlanCommandHandler implements ICommandHandler<CreateRatePlanCommand> {

    private final IManageRatePlanService service;
    private final IManageHotelService hotelService;
    private final ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService;

    public CreateRatePlanCommandHandler(IManageRatePlanService service, IManageHotelService hotelService,
                                        ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService){
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageRatePlanService = producerReplicateManageRatePlanService;
    }

    @Override
    public void handle(CreateRatePlanCommand command) {
        ManageHotelDto hotelDto = hotelService.findById(command.getHotelId());
        //Validar si el code del rateplan y el id del hotel ya existe
        ManageRatePlanDto dto = new ManageRatePlanDto(command.getId(),command.getCode(), command.getName(), command.getStatus(), command.isDeleted(), null, hotelDto);
        service.create(dto);
    }
}
