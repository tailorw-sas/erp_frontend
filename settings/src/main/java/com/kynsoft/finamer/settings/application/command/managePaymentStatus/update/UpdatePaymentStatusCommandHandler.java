package com.kynsoft.finamer.settings.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentStatusKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentStatus.ProducerUpdateManagePaymentStatusService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdatePaymentStatusCommandHandler implements ICommandHandler<UpdatePaymentStatusCommand> {
    
    private final IManagerPaymentStatusService service;
    private final ProducerUpdateManagePaymentStatusService producerUpdateManagePaymentStatusService;
    
    public UpdatePaymentStatusCommandHandler(final IManagerPaymentStatusService service,
                                            ProducerUpdateManagePaymentStatusService producerUpdateManagePaymentStatusService) {
        this.service = service;
        this.producerUpdateManagePaymentStatusService = producerUpdateManagePaymentStatusService;
    }

    @Override
    public void handle(UpdatePaymentStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));
        ManagerPaymentStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCollected, command.getCollected(), dto.getCollected(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDefaults, command.getDefaults(), dto.getDefaults(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setApplied, command.getApplied(), dto.getApplied(), update::setUpdate);

        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManagePaymentStatusService.update(new UpdateManagePaymentStatusKafka(dto.getId(), dto.getName(), command.getStatus().name(), command.getApplied()));
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
