package com.kynsoft.finamer.settings.application.command.manageRegion.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageRegionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRegion.ProducerUpdateManageRegionService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageRegionCommandHandler implements ICommandHandler<UpdateManageRegionCommand> {

    private final IManageRegionService service;

    private final ProducerUpdateManageRegionService producer;

    public UpdateManageRegionCommandHandler(IManageRegionService service, ProducerUpdateManageRegionService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(UpdateManageRegionCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Region ID cannot be null."));

        ManageRegionDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producer.update(new ManageRegionKafka(
                    dto.getId(), dto.getCode(), dto.getName()
            ));
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
