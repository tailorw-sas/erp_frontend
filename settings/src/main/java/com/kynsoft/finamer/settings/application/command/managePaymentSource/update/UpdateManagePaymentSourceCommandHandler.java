package com.kynsoft.finamer.settings.application.command.managePaymentSource.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentSourceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentSource.ProducerUpdateManagePaymentSourceService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagePaymentSourceCommandHandler implements ICommandHandler<UpdateManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;
    private final ProducerUpdateManagePaymentSourceService producerUpdateManagePaymentSourceService;

    public UpdateManagePaymentSourceCommandHandler(IManagePaymentSourceService service,
                                                   ProducerUpdateManagePaymentSourceService producerUpdateManagePaymentSourceService) {
        this.service = service;
        this.producerUpdateManagePaymentSourceService = producerUpdateManagePaymentSourceService;
    }

    @Override
    public void handle(UpdateManagePaymentSourceCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManagePaymentSourceDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsBank, command.getIsBank(), dto.getIsBank(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setExpense, command.getExpense(), dto.getExpense(), update::setUpdate);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManagePaymentSourceService.update(new UpdateManagePaymentSourceKafka(dto.getId(), dto.getName(), command.getStatus().name(), command.getExpense()));
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
