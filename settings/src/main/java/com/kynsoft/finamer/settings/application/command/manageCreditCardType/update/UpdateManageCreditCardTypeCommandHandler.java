package com.kynsoft.finamer.settings.application.command.manageCreditCardType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCreditCardTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManageCreditCardTypeFirstDigitMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCreditCardType.ProducerUpdateManageCreditCardTypeService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCreditCardTypeCommandHandler implements ICommandHandler<UpdateManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    private final ProducerUpdateManageCreditCardTypeService producerUpdateManageCreditCardTypeService;

    public UpdateManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service,
                                                    ProducerUpdateManageCreditCardTypeService producerUpdateManageCreditCardTypeService) {
        this.service = service;
        this.producerUpdateManageCreditCardTypeService = producerUpdateManageCreditCardTypeService;
    }

    @Override
    public void handle(UpdateManageCreditCardTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Credit Card Type ID cannot be null."));

        ManageCreditCardTypeDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        if (UpdateIfNotNull.updateInteger(test::setFirstDigit, command.getFirstDigit(), test.getFirstDigit(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageCreditCardTypeFirstDigitMustBeUniqueRule(this.service, command.getFirstDigit(), command.getId()));
        }

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerUpdateManageCreditCardTypeService.update(new UpdateManageCreditCardTypeKafka(test.getId(), test.getName(), test.getDescription(), test.getFirstDigit(), test.getStatus().name()));
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
