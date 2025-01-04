package com.kynsoft.finamer.settings.application.command.managerTimeZone.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTimeZoneKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTimeZone.ProducerReplicateManageTimeZoneService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerTimeZoneCommandHandler implements ICommandHandler<UpdateManagerTimeZoneCommand> {

    private final IManagerTimeZoneService service;
    private final ProducerReplicateManageTimeZoneService producerReplicateManageTimeZoneService;

    public UpdateManagerTimeZoneCommandHandler(IManagerTimeZoneService service,
            ProducerReplicateManageTimeZoneService producerReplicateManageTimeZoneService) {
        this.producerReplicateManageTimeZoneService = producerReplicateManageTimeZoneService;
        this.service = service;
    }

    @Override
    public void handle(UpdateManagerTimeZoneCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Time Zone ID cannot be null."));

        ManagerTimeZoneDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        UpdateIfNotNull.updateDouble(test::setElapse, command.getElapse(), test.getElapse(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerReplicateManageTimeZoneService.create(new ReplicateManageTimeZoneKafka(
                    test.getId(),
                    test.getCode(),
                    test.getName(),
                    test.getDescription(),
                    test.getElapse(),
                    test.getStatus().name()
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
