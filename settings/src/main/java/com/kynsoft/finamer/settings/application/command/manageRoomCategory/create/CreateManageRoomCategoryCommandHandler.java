package com.kynsoft.finamer.settings.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.settings.domain.rules.manageRoomCategory.ManageRoomCategoryCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomCategory.ProducerReplicateManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomCategoryCommandHandler implements ICommandHandler<CreateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    private final ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService;

    public CreateManageRoomCategoryCommandHandler(IManageRoomCategoryService service, ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService) {
        this.service = service;
        this.producerReplicateManageRoomCategoryService = producerReplicateManageRoomCategoryService;
    }

    @Override
    public void handle(CreateManageRoomCategoryCommand command) {
//        RulesChecker.checkRule(new ManageAgencyTypeCodeSizeRule(command.getCode()));
//        RulesChecker.checkRule(new ManageAgencyTypeNameMustBeNull(command.getName()));
        RulesChecker.checkRule(new ManageRoomCategoryCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        service.create(new ManageRoomCategoryDto(
                command.getId(),
                command.getCode(),
                command.getStatus(),
                command.getName(),
                command.getDescription()
        ));

       this.producerReplicateManageRoomCategoryService.create(new ReplicateManageRoomCategoryKafka(command.getId(), command.getCode(), command.getName()));
    }
}
