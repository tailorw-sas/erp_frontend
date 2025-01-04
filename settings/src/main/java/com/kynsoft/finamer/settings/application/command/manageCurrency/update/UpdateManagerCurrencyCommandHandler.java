package com.kynsoft.finamer.settings.application.command.manageCurrency.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import java.util.function.Consumer;

import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCurrency.ProducerUpdateManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerCurrencyCommandHandler implements ICommandHandler<UpdateManagerCurrencyCommand> {

    private final IManagerCurrencyService service;
    private final ProducerUpdateManageCurrencyService producer;

    public UpdateManagerCurrencyCommandHandler(IManagerCurrencyService service, ProducerUpdateManageCurrencyService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(UpdateManagerCurrencyCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Currency ID cannot be null."));

        ManagerCurrencyDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producer.update(new ReplicateManageCurrencyKafka(
                    test.getId(),
                    test.getCode(),
                    test.getName(),
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
