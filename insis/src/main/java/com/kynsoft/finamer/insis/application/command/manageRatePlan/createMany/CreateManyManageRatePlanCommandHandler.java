package com.kynsoft.finamer.insis.application.command.manageRatePlan.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRatePlan.ProducerReplicateManageRatePlanService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CreateManyManageRatePlanCommandHandler implements ICommandHandler<CreateManyManageRatePlanCommand> {

    private final IManageRatePlanService service;
    private final IManageHotelService hotelService;
    private final ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService;

    public CreateManyManageRatePlanCommandHandler(IManageRatePlanService service,
                                                  IManageHotelService hotelService,
                                                  ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService){
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageRatePlanService = producerReplicateManageRatePlanService;
    }

    @Override
    public void handle(CreateManyManageRatePlanCommand command) {

        ManageHotelDto hotelDto = hotelService.findById(command.getHotel());
        if(Objects.nonNull(hotelDto)){
            List<ManageRatePlanDto> ratePlanDtoList = command.getRatePlanCommands()
                    .stream()
                    .map(ratePlan -> new ManageRatePlanDto(
                            ratePlan.getId(),
                            ratePlan.getCode(),
                            ratePlan.getName(),
                            ratePlan.getStatus(),
                            false,
                            null,
                            hotelDto
                            ))
                    .toList();
            List<ManageRatePlanDto> newRatePlanDtoList = service.createMany(ratePlanDtoList);

            for(ManageRatePlanDto newManageRatePlanDto : newRatePlanDtoList){
                ReplicateManageRatePlanKafka replicate = new ReplicateManageRatePlanKafka(
                        newManageRatePlanDto.getId(),
                        newManageRatePlanDto.getHotel().getId(),
                        newManageRatePlanDto.getCode(),
                        newManageRatePlanDto.getName(),
                        "ACTIVE");
                producerReplicateManageRatePlanService.create(replicate);
            }
        }
    }
}
