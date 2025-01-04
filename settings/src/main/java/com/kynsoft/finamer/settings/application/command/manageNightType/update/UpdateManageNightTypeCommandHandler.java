package com.kynsoft.finamer.settings.application.command.manageNightType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageNightTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageNightType.ProducerReplicateManageNightTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageNightTypeCommandHandler implements ICommandHandler<UpdateManageNightTypeCommand> {

    private final IManageNightTypeService service;
    private final ProducerReplicateManageNightTypeService producerReplicateManageNightTypeService;

    public UpdateManageNightTypeCommandHandler(IManageNightTypeService service,
                                               ProducerReplicateManageNightTypeService producerReplicateManageNightTypeService) {
        this.service = service;
        this.producerReplicateManageNightTypeService = producerReplicateManageNightTypeService;
    }

    @Override
    public void handle(UpdateManageNightTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Night Type ID cannot be null."));

        ManageNightTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setDescription(command.getDescription());

        service.update(dto);
        this.producerReplicateManageNightTypeService.create(new ReplicateManageNightTypeKafka(dto.getId(), dto.getCode(), dto.getName(), dto.getStatus().name()));
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }
}
