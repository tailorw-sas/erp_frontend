package com.kynsoft.finamer.insis.application.command.manageRoomCategory.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomCategory.ProducerReplicateManageRoomCategoryService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManyManageRoomCategoryCommandHandler implements ICommandHandler<CreateManyManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;
    private final ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService;

    public CreateManyManageRoomCategoryCommandHandler(IManageRoomCategoryService service,
                                                      ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService){
        this.service = service;
        this.producerReplicateManageRoomCategoryService = producerReplicateManageRoomCategoryService;
    }

    @Override
    public void handle(CreateManyManageRoomCategoryCommand command) {

        List<ManageRoomCategoryDto> dtos = command.getRoomCategoryCommands().stream()
                .map(roomCategory -> new ManageRoomCategoryDto(
                        roomCategory.getId(),
                        roomCategory.getCode(),
                        roomCategory.getName(),
                        roomCategory.getStatus(),
                        roomCategory.getUpdatedAt()
                ))
                .toList();

        List<ManageRoomCategoryDto> newRoomCategories = service.createMany(dtos);

        for(ManageRoomCategoryDto newRoomCategory : newRoomCategories){
            ReplicateManageRoomCategoryKafka replicate = new ReplicateManageRoomCategoryKafka(
                    newRoomCategory.getId(),
                    newRoomCategory.getCode(),
                    newRoomCategory.getName(),
                    newRoomCategory.getStatus()
            );
            producerReplicateManageRoomCategoryService.create(replicate);
        }
    }
}
