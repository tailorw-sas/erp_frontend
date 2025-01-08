package com.kynsoft.finamer.insis.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomType.ProducerReplicateManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateRoomTypeCommandHandler implements ICommandHandler<CreateRoomTypeCommand> {

    private final IManageRoomTypeService service;
    private final IManageHotelService hotelService;
    private final ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService;

    public CreateRoomTypeCommandHandler(IManageRoomTypeService service,
                                        IManageHotelService hotelService,
                                        ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService){
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageRoomTypeService = producerReplicateManageRoomTypeService;
    }

    @Override
    public void handle(CreateRoomTypeCommand command) {
        ManageHotelDto hotelDto = hotelService.findById(command.getHotelId());

        service.create(new ManageRoomTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.isDeleted(),
                null,
                hotelDto
        ));
    }
}
