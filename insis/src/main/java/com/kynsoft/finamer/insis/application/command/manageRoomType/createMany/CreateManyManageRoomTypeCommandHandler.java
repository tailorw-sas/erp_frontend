package com.kynsoft.finamer.insis.application.command.manageRoomType.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomType.ProducerReplicateManageRoomTypeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManyManageRoomTypeCommandHandler implements ICommandHandler<CreateManyManageRoomTypeCommand> {

    private final IManageRoomTypeService service;
    private final IManageHotelService hotelService;
    private final ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService;

    public CreateManyManageRoomTypeCommandHandler(IManageRoomTypeService service,
                                                  IManageHotelService hotelService,
                                                  ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService){
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageRoomTypeService = producerReplicateManageRoomTypeService;
    }

    @Override
    public void handle(CreateManyManageRoomTypeCommand command) {
        ManageHotelDto hotelDto = hotelService.findById(command.getHotel());

        List<ManageRoomTypeDto> newRoomTypesDto = command.getRoomTypeCommandList().stream()
                .map(roomType -> new ManageRoomTypeDto(
                        roomType.getId(),
                        roomType.getCode(),
                        roomType.getName(),
                        roomType.getStatus(),
                        false,
                        null,
                        hotelDto
                )).toList();

        List<ManageRoomTypeDto> newRoomTypeList = service.createMany(newRoomTypesDto);

        for(ManageRoomTypeDto newRoomTypeDto : newRoomTypeList){
            ReplicateManageRoomTypeKafka replicate = new ReplicateManageRoomTypeKafka(
                    newRoomTypeDto.getId(),
                    newRoomTypeDto.getCode(),
                    newRoomTypeDto.getName(),
                    "ACTIVE",
                    newRoomTypeDto.getHotel().getId()
            );
            producerReplicateManageRoomTypeService.create(replicate);
        }
    }
}
