package com.kynsoft.finamer.settings.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomCategory.ProducerReplicateManageRoomCategoryService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageRoomCategoryCommandHandler implements ICommandHandler<UpdateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;
    private final ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService;

    public UpdateManageRoomCategoryCommandHandler(IManageRoomCategoryService service,
            ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService) {
        this.service = service;
        this.producerReplicateManageRoomCategoryService = producerReplicateManageRoomCategoryService;
    }

    @Override
    public void handle(UpdateManageRoomCategoryCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Room Category ID cannot be null."));

        ManageRoomCategoryDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setDescription(command.getDescription());
        this.service.update(dto);
        this.producerReplicateManageRoomCategoryService.create(new ReplicateManageRoomCategoryKafka(dto.getId(), dto.getCode(), dto.getName(), dto.getStatus().name()));

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }
}
